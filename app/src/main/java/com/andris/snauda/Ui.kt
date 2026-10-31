package com.andris.snauda

import android.app.Activity
import android.app.AlertDialog
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

/** Mazi palīgi, lai nevajadzētu XML izkārtojumus. */
fun Activity.column() = LinearLayout(this).apply {
    orientation = LinearLayout.VERTICAL
    setPadding(48, 48, 48, 48)
}

fun Activity.text(s: String, size: Float = 18f) = TextView(this).apply {
    text = s; textSize = size; setPadding(0, 12, 0, 12)
}

fun Activity.button(s: String, onClick: () -> Unit) = Button(this).apply {
    text = s; setOnClickListener { onClick() }
}

fun Activity.passwordField(hint: String) = EditText(this).apply {
    this.hint = hint
    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
}

fun Activity.numberField(value: Int) = EditText(this).apply {
    setText(value.toString())
    inputType = InputType.TYPE_CLASS_NUMBER
}

fun Activity.toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()

/** Prasa paroli; ja pareiza — izpilda onOk. */
fun Activity.askPassword(onOk: () -> Unit) {
    val f = passwordField("Parole")
    AlertDialog.Builder(this)
        .setTitle("Ievadi paroli")
        .setView(f)
        .setPositiveButton("OK") { _, _ ->
            if (Store.checkPassword(this, f.text.toString())) onOk()
            else toast("Nepareiza parole")
        }
        .setNegativeButton("Atcelt", null)
        .show()
}

/** Pirmā reize vai paroles maiņa. */
fun Activity.setNewPassword(cancelable: Boolean, done: () -> Unit = {}) {
    val box = column()
    val a = passwordField("Jaunā parole").also { box.addView(it) }
    val b = passwordField("Atkārto paroli").also { box.addView(it) }
    val d = AlertDialog.Builder(this)
        .setTitle("Uzstādi paroli")
        .setMessage("Ar to varēs mainīt iestatījumus un atbloķēt aplikācijas.")
        .setView(box)
        .setPositiveButton("Saglabāt", null)
        .setCancelable(cancelable)
        .apply { if (cancelable) setNegativeButton("Atcelt", null) }
        .show()
    d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
        val p = a.text.toString()
        when {
            p.length < 4 -> toast("Vismaz 4 simboli")
            p != b.text.toString() -> toast("Paroles nesakrīt")
            else -> { Store.setPassword(this, p); toast("Parole saglabāta"); d.dismiss(); done() }
        }
    }
}
