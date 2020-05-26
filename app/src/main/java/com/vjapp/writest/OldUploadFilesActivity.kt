package com.vjapp.writest

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.vjapp.writest.components.VJDialog
import com.vjapp.writest.domain.model.UploadFilesRequestEntity
import com.vjapp.writest.presentation.SendFilesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.dialog_generic_confirm.*
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.util.*
import kotlin.coroutines.CoroutineContext


class OldUploadFilesActivity : AppCompatActivity(), CoroutineScope, pinRequest by PinActivity() {

    private val sendAssetsVM : SendFilesViewModel by viewModel()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}