package com.andris.snauda

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var warning: TextView

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val col = column()
        col.addView(text("Snauda", 28f))
        status = text("").also { col.addView(it) }
        warning = text("", 16f).also { col.addView(it) }

        col.addView(button("Ieslēgt / pārbaudīt servisu") {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        })
        col.addView(button("Iestatījumi 🔒") {
            askPassword { startActivity(Intent(this, SettingsActivity::class.java)) }
        })
        setContentView(col)

        if (!Store.hasPassword(this)) setNewPassword(cancelable = false)
    }

    override fun onResume() {
        super.onResume()
        val used = Store.usedMs(this) / 60_000
        val limit = Store.limitMin(this)
        val left = (limit - used).coerceAtLeast(0)
        status.text = buildString {
            append("Šodien izmantots: ${hm(used)}\n")
            append("Dienas limits: ${hm(limit.toLong())}\n")
            append("Atlicis: ${hm(left)}\n")
            append("Jauna diena sākas: %02d:00\n".format(Store.wakeHour(this@MainActivity)))
            if (Store.isSleeping(this@MainActivity)) append("\n😴 Aplikācijas guļ")
        }
        warning.text = if (serviceOn()) ""
            else "⚠ Serviss nav ieslēgts. Spied pogu zemāk → Snauda → Ieslēgt."
    }

    private fun serviceOn(): Boolean {
        val s = Settings.Secure.getString(contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
        return s.contains("$packageName/")
    }
}

fun hm(min: Long) = "${min / 60} h ${min % 60} min"
