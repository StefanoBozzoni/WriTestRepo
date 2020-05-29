package com.vjapp.writest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (supportActionBar!=null) (supportActionBar as ActionBar).setDisplayHomeAsUpEnabled(true)


        btnNuovoInvio.setOnClickListener {
            onBtnNuovoInvioClick()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        AlertDialog.Builder(this)
            .setTitle("Disconnettersi?")
            .setMessage("Vuoi eseguire la disconnessione ed inserire la password al prossimo accesso ?")
            .setPositiveButton("Esci") {dialog, which -> dialog.dismiss();finish() }
            .setNegativeButton("Esci e disconnetti") {dialog, which -> Firebase.auth.signOut();dialog.dismiss();finish() }
            .show()
        //onBackPressed()
        return true
    }

    private fun onBtnNuovoInvioClick() {
        startActivity(Intent(this,UploadFilesActivity::class.java))
    }
}
