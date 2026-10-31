package com.andris.snauda

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity

/** "Aplikācijas guļ" ekrāns ar iespēju atbloķēt ar paroli. */
class BlockActivity : Activity() {

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val col = column().apply { gravity = Gravity.CENTER }

        col.addView(text("😴", 64f).apply { gravity = Gravity.CENTER })
        col.addView(text("Šodienas limits izlietots", 24f).apply { gravity = Gravity.CENTER })
        col.addView(text(
            "Aplikācijas guļ līdz %02d:00.\nZvani un SMS strādā.".format(Store.wakeHour(this))
        ).apply { gravity = Gravity.CENTER })

        val pw = passwordField("Parole").also { col.addView(it) }
        listOf(15, 60).forEach { m ->
            col.addView(button("Atbloķēt uz $m min") {
                if (Store.checkPassword(this, pw.text.toString())) {
                    Store.unlockFor(this, m); toast("Atbloķēts uz $m min"); finish()
                } else toast("Nepareiza parole")
            })
        }
        col.addView(button("Uz sākuma ekrānu") { goHome() })
        setContentView(col)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() = goHome()

    private fun goHome() {
        startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()
    }
}
