package com.vjapp.writest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
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
        onBackPressed()
        return true
    }

    private fun onBtnNuovoInvioClick() {
        startActivity(Intent(this,UploadFilesActivity::class.java))
    }
}
