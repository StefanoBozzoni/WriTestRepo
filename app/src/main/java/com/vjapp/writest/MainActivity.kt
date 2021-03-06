package com.vjapp.writest

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vjapp.writest.presentation.MainViewModel
import com.vjapp.writest.presentation.NotificationViewModel
import com.vjapp.writest.presentation.Resource
import com.vjapp.writest.presentation.ResourceState
import com.vjapp.writest.services.MyFirebaseMsgService.Companion.UPLOAD_TOKEN
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    val notificationViewModel : NotificationViewModel by viewModel()
    val mainViewModel : MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uploadToken : String? = intent.getStringExtra(UPLOAD_TOKEN)
        uploadToken?.apply { Log.d("UPLOADTOKEN", uploadToken) }

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
        mainViewModel.syncDBLiveData.observe(this, Observer { response ->
            response?.let { handleSyncDBComplete(response) }
        })
    }

    private fun handleSyncDBComplete(response: Resource<Boolean>) {
        when (response.status) {
            ResourceState.SUCCESS -> {
                mainViewFlipper.displayedChild=1
                //TODO: Check to see if there are Records in the Queue Table and go to the History Section, in the case,
                //or detail section, first find the token to Display
            }
            ResourceState.LOADING -> {
                mainViewFlipper.displayedChild=0
            }
            ResourceState.ERROR -> {
                mainViewFlipper.displayedChild=2
            }
        }
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
                startActivity(Intent(this, TestsListActivity::class.java))
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
