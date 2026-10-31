package com.andris.snauda

import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telecom.TelecomManager
import android.view.inputmethod.InputMethodManager
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Calendar

/** Visi iestatījumi un skaitītājs. Viss glabājas telefonā. */
object Store {
    private fun p(c: Context) = c.getSharedPreferences("limits", 0)

    // ---------- Iestatījumi ----------
    fun limitMin(c: Context) = p(c).getInt("limitMin", 240)
    fun wakeHour(c: Context) = p(c).getInt("wakeHour", 6)
    fun exceptions(c: Context): Set<String> = p(c).getStringSet("exceptions", emptySet())!!

    fun save(c: Context, limitMin: Int, wakeHour: Int, exceptions: Set<String>) =
        p(c).edit().putInt("limitMin", limitMin).putInt("wakeHour", wakeHour)
            .putStringSet("exceptions", exceptions).apply()

    // ---------- Parole ----------
    fun hasPassword(c: Context) = p(c).contains("pwHash")

    fun setPassword(c: Context, pw: String) {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }.toHex()
        p(c).edit().putString("salt", salt).putString("pwHash", hash(salt + pw)).apply()
    }

    fun checkPassword(c: Context, pw: String) =
        hash(p(c).getString("salt", "")!! + pw) == p(c).getString("pwHash", null)

    private fun hash(s: String) =
        MessageDigest.getInstance("SHA-256").digest(s.toByteArray()).toHex()

    private fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }

    // ---------- Dienas skaitītājs ----------
    /** "Diena" sākas pamošanās stundā, nevis pusnaktī. */
    private fun dayKey(c: Context): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.HOUR_OF_DAY, -wakeHour(c))
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
    }

    private fun ensureDay(c: Context) {
        val k = dayKey(c)
        if (p(c).getString("day", "") != k)
            p(c).edit().putString("day", k).putLong("usedMs", 0).putLong("unlockUntil", 0).apply()
    }

    fun usedMs(c: Context): Long { ensureDay(c); return p(c).getLong("usedMs", 0) }

    fun addUsage(c: Context, ms: Long) {
        if (ms <= 0 || ms > 60 * 60_000) return   // aizsardzība pret dīvainām vērtībām
        ensureDay(c)
        p(c).edit().putLong("usedMs", usedMs(c) + ms).apply()
    }

    fun unlockFor(c: Context, minutes: Int) =
        p(c).edit().putLong("unlockUntil", System.currentTimeMillis() + minutes * 60_000L).apply()

    fun isSleeping(c: Context): Boolean {
        ensureDay(c)
        return usedMs(c) >= limitMin(c) * 60_000L &&
               System.currentTimeMillis() >= p(c).getLong("unlockUntil", 0)
    }

    // ---------- Kuras aplikācijas neguļ ----------
    /** Šīs nekad neskaitās un netiek aizvērtas. */
    fun alwaysAllowed(c: Context): Set<String> {
        val s = mutableSetOf(
            c.packageName, "android", "com.android.systemui", "com.android.settings",
            "com.android.phone", "com.android.server.telecom", "com.android.emergency"
        )
        // Sākuma ekrāns
        c.packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0
        ).forEach { s += it.activityInfo.packageName }
        // Zvani un SMS (jebkura ražotāja)
        c.getSystemService(TelecomManager::class.java)?.defaultDialerPackage?.let { s += it }
        Telephony.Sms.getDefaultSmsPackage(c)?.let { s += it }
        // Klaviatūras
        c.getSystemService(InputMethodManager::class.java)
            ?.enabledInputMethodList?.forEach { s += it.packageName }
        return s
    }

    fun isAllowed(c: Context, pkg: String, always: Set<String>) =
        pkg in always || pkg in exceptions(c)
}
