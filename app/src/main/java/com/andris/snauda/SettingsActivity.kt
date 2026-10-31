package com.andris.snauda

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ScrollView

/** Atveras tikai pēc paroles (no MainActivity). */
class SettingsActivity : Activity() {

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val col = column()

        col.addView(text("Dienas limits (minūtēs)"))
        val limit = numberField(Store.limitMin(this)).also { col.addView(it) }

        col.addView(text("Jauna diena sākas plkst. (0–23)"))
        val wake = numberField(Store.wakeHour(this)).also { col.addView(it) }

        col.addView(text("\nAplikācijas, kuras NEKAD neguļ:", 20f))
        col.addView(text("(zvani, SMS, iestatījumi un sākuma ekrāns jau ir atļauti)", 14f))

        val always = Store.alwaysAllowed(this)
        val chosen = Store.exceptions(this)
        val pm = packageManager
        val apps = pm.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0
        ).map { it.activityInfo.packageName to it.loadLabel(pm).toString() }
            .distinctBy { it.first }
            .filter { it.first !in always }
            .sortedBy { it.second.lowercase() }

        val boxes = apps.map { (pkg, label) ->
            CheckBox(this).apply {
                text = label; tag = pkg; isChecked = pkg in chosen
                col.addView(this)
            }
        }

        col.addView(button("Saglabāt") {
            val l = limit.text.toString().toIntOrNull()
            val w = wake.text.toString().toIntOrNull()
            if (l == null || l < 1 || w == null || w !in 0..23) {
                toast("Pārbaudi skaitļus"); return@button
            }
            Store.save(this, l, w, boxes.filter { it.isChecked }.map { it.tag as String }.toSet())
            toast("Saglabāts"); finish()
        })
        col.addView(button("Mainīt paroli") { setNewPassword(cancelable = true) })

        setContentView(ScrollView(this).apply { addView(col) })
    }
}
