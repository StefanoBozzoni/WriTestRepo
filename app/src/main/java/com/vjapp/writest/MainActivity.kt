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


class MainActivity : AppCompatActivity(), CoroutineScope, pinRequest by PinActivity() {

    private val sendAssetsVM : SendFilesViewModel by viewModel()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()

        btn_addFile.setOnClickListener { btnChoosePhotoFile() }
        btn_addFile2.setOnClickListener { btnChooseVideoFile() }
        btn_cancelFile.setOnClickListener { v -> btnCancelFileClick(v) }
        btn_cancelFile2.setOnClickListener { v -> btnCancelFileClick(v) }
        btn_add_photo.setOnClickListener { btnChoosePhotoClick()}
        btn_add_photo2.setOnClickListener { btnChooseVideoClick()}
        btnSendFiles.setOnClickListener { sendFiles() }
        checkEnableButton()

        sendAssetsVM.httpBinDemo()

        sendAssetsVM.httpBinLiveData.observe(this, Observer { response ->
            response?.let { handlehttpResultData(response) }
        })

        sendAssetsVM.httpBinLiveData2.observe(this, Observer { response ->
            response?.let { handlehttpResultData(response) }
        })
    }

    private fun sendFiles() {
        val token    = UUID.randomUUID().toString() //TODO: generazione token
        launch {
            val def1 = async(Dispatchers.IO) {sendAssetsVM.getFilePathFromUri(sendAssetsVM.selectedPhotoUri!!, "prova.jpg")}
            val def2 = async(Dispatchers.IO) {sendAssetsVM.getFilePathFromUri(sendAssetsVM.selectedVideoUri!!, "") }
            val fileImg   = def1.await()
            val fileVideo = def2.await()
            fileImg?.apply {
                fileVideo?.apply {
                    sendAssetsVM.sendFiles(UploadFilesRequestEntity(fileImg.absolutePath, fileVideo.absolutePath, token))
                }
            }
        }
    }

    private fun handlehttpResultData(resp:String) {
        Log.d("RISULTATO", resp)
    }

    private fun btnChoosePhotoClick() {
        if (!isCameraPermissionGranted(this)) return

        val outputDir: File = this.cacheDir
        val outputFile = File.createTempFile("snapshot", ".jpg", outputDir)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val mPhotoURI = FileProvider.getUriForFile(this, "${this.packageName}.fileprovider", outputFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI)

        sendAssetsVM.selectedPhotoUri=mPhotoURI
        //sendAssetsVM.mOutputFile = outputFile.absolutePath
        startActivityForResult(intent, PICK_PHOTO)
    }

    private fun btnChooseVideoClick() {
        if (!isCameraPermissionGranted(this)) return
        val outputDir: File = this.cacheDir
        val outputFile = File.createTempFile("videosnapshot", ".mp4", outputDir)

        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)

        val mVideoURI = FileProvider.getUriForFile(this, "${this.packageName}.fileprovider", outputFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoURI)
        sendAssetsVM.selectedVideoUri=mVideoURI
        //sendAssetsVM.mOutputFile = outputFile.absolutePath
        if (takeVideoIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takeVideoIntent,PICK_VIDEO)
        }
    }

    private fun isCameraPermissionGranted(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.CAMERA),
                    1
                )
                false
            }
        } else {
            true
        }
    }

    private fun btnCancelFileClick(v: View?) {
            val dialog = VJDialog(this, R.layout.dialog_generic_confirm)

            dialog.show()

            dialog.iv_DialogIcon.setImageResource(R.drawable.documenti_popup_icona_90x80_alert_cancella)
            dialog.tv_DialogTitle.text = getString(R.string.question_delete_confirm_str)

            dialog.btnCancel.setOnClickListener {
                dialog.cancel()
            }

            dialog.btnNext.setOnClickListener {
                dialog.cancel()
                if (v?.id==R.id.btn_cancelFile) {
                    //sendAssetsVM.mOutputFile = ""
                    sendAssetsVM.photoPage = 0
                    sendAssetsVM.selectedPhotoUri = null
                    viewFlipper.displayedChild = 0
                    viewFlipper.tv_nomefile.text=""
                }
                if (v?.id==R.id.btn_cancelFile2) {
                    //sendAssetsVM.mOutputFile = ""
                    sendAssetsVM.videoPage = 0
                    sendAssetsVM.selectedVideoUri = null
                    viewFlipper2.displayedChild = 0
                    viewFlipper2.tv_nomefile2.text=""
                }
                checkEnableButton()
            }
    }

    private fun btnChoosePhotoFile() {
        val mimeTypes = arrayOf("image/*", "application/pdf")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType("image/* | application/pdf")
            .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, PICK_FILE)
    }

    private fun btnChooseVideoFile() {
        val mimeTypes = arrayOf("video/mp4")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType("video/mp4")
            .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, PICK_FILE_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode in setOf(PICK_FILE, PICK_FILE_VIDEO) && resultCode == Activity.RESULT_OK && null != data)
        {
            val selectedUri = data.data
            if (selectedUri != null) {
                val displayName = getFileNameFromCursor(selectedUri).first

                if (requestCode== PICK_FILE) {
                   sendAssetsVM.selectedPhotoUri=selectedUri
                    tv_nomefile.text = displayName
                    viewFlipper.displayedChild = 1
                }
                if (requestCode== PICK_FILE_VIDEO) {
                    sendAssetsVM.selectedVideoUri=selectedUri
                    tv_nomefile2.text = displayName
                    viewFlipper2.displayedChild = 1
                }
                checkEnableButton()
            }
        }

        if (requestCode == PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            val file = File(sendAssetsVM.selectedPhotoUri?.path)
            tv_nomefile.text = file.name
            sendAssetsVM.photoPage = 1
            viewFlipper.displayedChild = sendAssetsVM.photoPage
            checkEnableButton()
        }

        if (requestCode == PICK_VIDEO && resultCode == Activity.RESULT_OK) {
            val file = File(sendAssetsVM.selectedVideoUri?.path)
            tv_nomefile2.text = file.name
            sendAssetsVM.videoPage = 1
            viewFlipper2.displayedChild = sendAssetsVM.videoPage
            checkEnableButton()
        }
    }

    private fun checkEnableButton() {
        btnSendFiles.isEnabled = (tv_nomefile.text.isNotEmpty()) && (tv_nomefile2.text.isNotEmpty())
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun getFileNameFromCursor(uri: Uri): Pair<String?, Long> {
        return sendAssetsVM.getFileNameFromCursor(uri)
    }

    companion object {
        const val PICK_FILE = 2
        const val PICK_FILE_VIDEO = 3
        const val PICK_PHOTO = 4
        const val PICK_VIDEO = 5
    }
}