package com.vjapp.writest

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.vjapp.writest.components.VJDialog
import com.vjapp.writest.data.remote.model.SchoolsResponse
import com.vjapp.writest.domain.model.ClassesEntity
import com.vjapp.writest.domain.model.UploadFilesRequestEntity
import com.vjapp.writest.domain.model.UploadFilesResponseEntity
import com.vjapp.writest.presentation.Resource
import com.vjapp.writest.presentation.ResourceState
import com.vjapp.writest.presentation.UploadFilesViewModel
import kotlinx.android.synthetic.main.activity_upload_files.*
import kotlinx.android.synthetic.main.activity_upload_files.view.*
import kotlinx.android.synthetic.main.basic_spinner_view.view.*
import kotlinx.android.synthetic.main.dialog_generic_confirm.*
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.coroutines.CoroutineContext

class UploadFilesActivity : AppCompatActivity(), CoroutineScope {

    private val uploadAssetsVM: UploadFilesViewModel by viewModel()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job
    lateinit var mToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_files)
        job = Job()

        uploadAssetsVM.init()

        btn_addFile.setOnClickListener { btnChoosePhotoFile() }
        btn_addFile2.setOnClickListener { btnChooseVideoFile() }
        btn_cancelFile.setOnClickListener { v -> btnCancelFileClick(v) }
        btn_cancelFile2.setOnClickListener { v -> btnCancelFileClick(v) }
        btn_add_photo.setOnClickListener { btnChoosePhotoClick() }
        btn_add_photo2.setOnClickListener { btnChooseVideoClick() }
        btnSendFiles.setOnClickListener { btnSendFilesClick() }
        checkEnableButton()
        //val randomToken = UUID.randomUUID().toString()

        val savedToken = uploadAssetsVM.sharedpreferences.getString("token", "")
        if (savedToken?.isEmpty() ?: false) {
            uploadAssetsVM.generateNewToken()
        } else {
            mToken = savedToken!!
            tvToken.text = mToken
        }

        tvInfotext.movementMethod = ScrollingMovementMethod()
        if (supportActionBar != null) (supportActionBar as ActionBar).setDisplayHomeAsUpEnabled(true)

        /*
        sendAssetsVM.httpBinLiveData.observe(this, Observer { response ->
            response?.let { handlehttpResultData(response) }
        })

        sendAssetsVM.httpBinLiveData2.observe(this, Observer { response ->
            response?.let { handlehttpResultData(response) }
        })
        */

        uploadAssetsVM.sendFilesLivData.observe(this, Observer { response ->
            response?.let { handleSendFileComplete(response) }
        })

        uploadAssetsVM.getConfigLivData.observe(this, Observer { response ->
            response?.let { handleConfigResultData(response) }
        })

        uploadAssetsVM.newTokenLivData.observe(this, Observer { response ->
            response?.let { handleNewTokenResultData(response) }
        })

        uploadAssetsVM.getConfig()
    }

    private fun handleNewTokenResultData(response: Resource<String>) {
        if (response.status==ResourceState.SUCCESS) {
            tvToken.text = response.data!!
            mToken = response.data
        }
    }

    //tvToken.text = randomToken

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun btnSendFilesClick() {
        val token = tvToken.text.toString()

        if (sp_scuola.selectedItemPosition == 0) {
            sp_scuola.selectedView.spTextView.error = "dato obbligatorio"
            sp_scuola.selectedView.spTextView.setTextColor(Color.parseColor("#FFFF0000"))
            return
        }
        if (sp_classe.selectedItemPosition == 0) {
            sp_classe.selectedView.spTextView.error = "dato obbligatorio"
            sp_scuola.selectedView.spTextView.setTextColor(Color.parseColor("#FFFF0000"))
            //(sp_classe.selectedView as TextView).error="dato obbligatorio"
            return
        }
        if (token.isEmpty()) {
            tvToken.error = "dato obbligatorio"
            return
        }

        //uploadFilesUsingLegacyServer(token)
        uploadAssetsVM.uploadAllFilesUsingFirebaseStorage(token, sp_classe.spTextView.text.toString(),sp_scuola.spTextView.text.toString())
    }


    fun uploadFilesUsingLegacyServer(token: String) {
        //it should work, not tested tought
        launch {
            val def1 = async(Dispatchers.IO) {
                uploadAssetsVM.getFilePathFromUri(
                    uploadAssetsVM.selectedPhotoUri!!,
                    "provaimmagine.jpg"
                )
            }
            val def2 = async(Dispatchers.IO) {
                uploadAssetsVM.getFilePathFromUri(
                    uploadAssetsVM.selectedVideoUri!!,
                    "provavideo.mp4"
                )
            }
            val fileImg = def1.await()
            val fileVideo = def2.await()

            fileImg?.apply {
                fileVideo?.apply {
                    uploadAssetsVM.sendFiles(
                        UploadFilesRequestEntity(
                            fileImg.absolutePath,
                            fileVideo.absolutePath,
                            token
                        )
                    )
                }
            }
        }
    }

    private fun handlehttpResultData(resp: String) {
        Log.d("RISULTATO", resp)
    }

    private fun handleSendFileComplete(resource: Resource<UploadFilesResponseEntity>) {

        if (resource.status == ResourceState.LOADING) {
            mainViewFlipper.displayedChild = 0
        }

        if (resource.status == ResourceState.SUCCESS) {
            mainViewFlipper.displayedChild = 1
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.invio_completato_title))
                .setMessage(getString(R.string.invio_completato_message_str))
                .setPositiveButton(getString(R.string.chiudi_str)) { d, _ -> d.dismiss();finish() }
                .show()
        }

        if (resource.status == ResourceState.ERROR) {
            mainViewFlipper.displayedChild = 1
            AlertDialog.Builder(this)
                .setTitle("Errore nell'invio")
                .setMessage("L'invio non è stato completato a causa di errori. Ritenta ed assicurati di avere una connessione WIFI stabile.")
                .setPositiveButton("Chiudi") { d, _ -> d.dismiss(); }
                .show()
        }
    }

    private fun handleConfigResultData(resource: Resource<Any>) {

        if (resource.status == ResourceState.SUCCESS && resource.data is SchoolsResponse) {
            Log.d("CONFIGURAZIONE", resource.data.toString())
            val listSchools =
                listOf("Scegliere una scuola:") + resource.data.schoolList.map { s -> s.nameSchool }
            listSchools.also {
                val adapterSpinner = ArrayAdapter(this, R.layout.basic_spinner_view, it)
                sp_scuola.adapter = adapterSpinner
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            mainViewFlipper.displayedChild = 1 //schools are the last data loaded
        }

        if (resource.status == ResourceState.SUCCESS && resource.data is ClassesEntity) {
            Log.d("CONFIGURAZIONE", resource.data.toString())
            val listClasses =
                listOf("Scegliere la classe elementare:") + resource.data.classList.map { c -> c.type }
            listClasses.also {
                val adapterSpinner = ArrayAdapter(this, R.layout.basic_spinner_view, it)
                sp_classe.adapter = adapterSpinner
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }

        if (resource.status == ResourceState.ERROR) {
            //TODO: Mostrare maschera di errore
        }

    }

    private fun btnChoosePhotoClick() {
        if (!isCameraPermissionGranted(this)) return

        val outputDir: File = this.cacheDir
        val outputFile = File.createTempFile("snapshot", ".jpg", outputDir)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val mPhotoURI =
            FileProvider.getUriForFile(this, "${this.packageName}.fileprovider", outputFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI)

        /*
        contentResolver.takePersistableUriPermission(
            mPhotoURI,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
       */
        uploadAssetsVM.selectedPhotoUri = mPhotoURI

        //sendAssetsVM.mOutputFile = outputFile.absolutePath
        startActivityForResult(intent, PICK_PHOTO)
    }

    private fun btnChooseVideoClick() {
        if (!isCameraPermissionGranted(this)) return
        val outputDir: File = this.cacheDir
        val outputFile = File.createTempFile("videosnapshot", ".mp4", outputDir)

        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)

        val mVideoURI =
            FileProvider.getUriForFile(this, "${this.packageName}.fileprovider", outputFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoURI)
        uploadAssetsVM.selectedVideoUri = mVideoURI

        //sendAssetsVM.mOutputFile = outputFile.absolutePath
        if (takeVideoIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takeVideoIntent, PICK_VIDEO)
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
            if (v?.id == R.id.btn_cancelFile) {
                //sendAssetsVM.mOutputFile = ""
                uploadAssetsVM.photoPage = 0
                uploadAssetsVM.selectedPhotoUri = null
                viewFlipper.displayedChild = 0
                viewFlipper.tv_nomefile.text = ""
            }
            if (v?.id == R.id.btn_cancelFile2) {
                //sendAssetsVM.mOutputFile = ""
                uploadAssetsVM.videoPage = 0
                uploadAssetsVM.selectedVideoUri = null
                viewFlipper2.displayedChild = 0
                viewFlipper2.tv_nomefile2.text = ""
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

        if (requestCode in setOf(
                PICK_FILE,
                PICK_FILE_VIDEO
            ) && resultCode == Activity.RESULT_OK && null != data
        ) {
            val selectedUri = data.data
            if (selectedUri != null) {
                val displayName = getFileNameFromCursor(selectedUri).first

                if (requestCode == PICK_FILE) {
                    uploadAssetsVM.selectedPhotoUri = selectedUri
                    tv_nomefile.text = displayName
                    viewFlipper.displayedChild = 1
                }
                if (requestCode == PICK_FILE_VIDEO) {
                    uploadAssetsVM.selectedVideoUri = selectedUri
                    tv_nomefile2.text = displayName
                    viewFlipper2.displayedChild = 1
                }
                checkEnableButton()
            }
        }

        if (requestCode == PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            val file = File(uploadAssetsVM.selectedPhotoUri?.path)
            tv_nomefile.text = file.name
            uploadAssetsVM.photoPage = 1
            viewFlipper.displayedChild = uploadAssetsVM.photoPage
            checkEnableButton()
        }

        if (requestCode == PICK_VIDEO && resultCode == Activity.RESULT_OK) {
            val file = File(uploadAssetsVM.selectedVideoUri?.path)
            tv_nomefile2.text = file.name
            uploadAssetsVM.videoPage = 1
            viewFlipper2.displayedChild = uploadAssetsVM.videoPage
            uploadAssetsVM.selectedVideoUri = data?.data
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
        return uploadAssetsVM.getFileNameFromCursor(uri)
    }

    companion object {
        const val TAG = "UploadFiles"
        const val PICK_FILE = 2
        const val PICK_FILE_VIDEO = 3
        const val PICK_PHOTO = 4
        const val PICK_VIDEO = 5
    }
}