package com.vjapp.writest

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vjapp.writest.presentation.NotificationViewModel
import com.vjapp.writest.presentation.Resource
import com.vjapp.writest.presentation.ResourceState
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    val notificationViewModel : NotificationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnNuovoInvio.setOnClickListener {
            onBtnNuovoInvioClick()
        }
        if (supportActionBar!=null) (supportActionBar as ActionBar).setDisplayHomeAsUpEnabled(true)

        initializeHandlers()
    }

    fun initializeHandlers() {
        notificationViewModel.notificationSubscriptionLiveData.observe(this, Observer { response ->
            response?.let { handleSendFileComplete(response) }
        })
    }

    private fun handleSendFileComplete(response: Resource<String>) {
        when (response.status) {
            ResourceState.SUCCESS -> {
                val firebaseToken = response.data
            }
            ResourceState.LOADING -> {}
            ResourceState.ERROR -> {}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.mi_progetto -> {
                val implicit = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.h-writools.it/progetto-h-writools/")
                )
                startActivity(implicit)

                return true
            }
            R.id.mi_storico -> {
                startActivity(Intent(this, TestsHistoryActivity::class.java))
                return true
            }
            R.id.mi_help -> {
                val implicit = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.h-writools.it/help/")
                )
                startActivity(implicit)

                return true
            }
            R.id.mi_logout -> {
                logout()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Disconnettersi?")
            .setMessage("Vuoi eseguire la disconnessione ed inserire la password al prossimo accesso ?")
            .setPositiveButton("Esci") {dialog, which -> dialog.dismiss();finish() }
            .setNegativeButton("Esci e disconnetti") {dialog, which -> Firebase.auth.signOut();dialog.dismiss();finish() }
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        logout()
        return true
    }

    private fun onBtnNuovoInvioClick() {
        startActivity(Intent(this,UploadFilesActivity::class.java))
    }
}
