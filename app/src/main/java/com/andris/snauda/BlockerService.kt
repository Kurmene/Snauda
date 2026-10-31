package com.andris.snauda

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager

/**
 * Seko, kura aplikācija ir priekšā, skaita laiku un,
 * kad limits izlietots, aizver aplikāciju un parāda "guļ" ekrānu.
 */
class BlockerService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var always = emptySet<String>()
    private var current: String? = null      // skaitāmā aplikācija priekšā
    private var since = 0L

    private val tick = object : Runnable {
        override fun run() {
            flush(); enforce()
            always = Store.alwaysAllowed(this@BlockerService)   // ja nomainīts zvanu/SMS aplikācija
            handler.postDelayed(this, 15_000)
        }
    }

    private val screenOff = object : BroadcastReceiver() {
        override fun onReceive(c: Context, i: Intent) { flush(); current = null }
    }

    override fun onServiceConnected() {
        always = Store.alwaysAllowed(this)
        registerReceiver(screenOff, IntentFilter(Intent.ACTION_SCREEN_OFF))
        handler.post(tick)
    }

    override fun onAccessibilityEvent(e: AccessibilityEvent) {
        if (e.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = e.packageName?.toString() ?: return
        // Paziņojumu josla, sistēmas logi un klaviatūra neko nemaina
        if (pkg == "com.android.systemui" || pkg == "android" || pkg in imes()) return

        flush()
        current = if (Store.isAllowed(this, pkg, always)) null else pkg
        enforce()
    }

    private fun imes(): Set<String> =
        getSystemService(InputMethodManager::class.java)
            ?.enabledInputMethodList?.map { it.packageName }?.toSet() ?: emptySet()

    /** Pieskaita laiku, kas pagājis kopš pēdējās reizes. */
    private fun flush() {
        val now = System.currentTimeMillis()
        if (current != null) Store.addUsage(this, now - since)
        since = now
    }

    private fun enforce() {
        if (current == null || !Store.isSleeping(this)) return
        current = null
        performGlobalAction(GLOBAL_ACTION_HOME)
        startActivity(
            Intent(this, BlockActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        handler.removeCallbacks(tick)
        runCatching { unregisterReceiver(screenOff) }
        super.onDestroy()
    }
}
