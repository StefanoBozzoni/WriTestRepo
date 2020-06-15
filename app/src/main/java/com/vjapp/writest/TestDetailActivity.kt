package com.vjapp.writest

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.Observer
import com.vjapp.writest.domain.model.TestEntity
import com.vjapp.writest.domain.utils.toFullString
import com.vjapp.writest.presentation.Resource
import com.vjapp.writest.presentation.ResourceState
import com.vjapp.writest.presentation.TestDetailViewModel
import com.vjapp.writest.services.MyFirebaseMsgService.Companion.BY_NOTIFICATION
import com.vjapp.writest.services.MyFirebaseMsgService.Companion.UPLOAD_TOKEN
import kotlinx.android.synthetic.main.activity_test_detail.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class TestDetailActivity : AppCompatActivity() {
    private val detailViewModel: TestDetailViewModel by viewModel()
    //private var mTestId: Int = 0
    private var mTestToken: String? = null
    private lateinit var mTest: TestEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_detail)
        if (supportActionBar != null) (supportActionBar as ActionBar).setDisplayHomeAsUpEnabled(true)

        //mTestId = intent.getIntExtra(TEST_ID, -1)
        mTestToken = intent.getStringExtra(UPLOAD_TOKEN)

        initLisners()

        detailViewModel.init(mTestToken?:"")
    }

    private fun initLisners() {
        detailViewModel.testsLiveData.observe(this, Observer { response ->
            response?.let { handleGetSingleTestComplete(response) }
        })

        detailViewModel.diagnosisLiveData.observe(this, Observer { response ->
            response?.let { handlegetDisagnosisComplete(response) }
        })
    }

    private fun handlegetDisagnosisComplete(response: Resource<Uri>) {
        when (response.status) {
            ResourceState.LOADING -> {
                view_flipper_detail.displayedChild = 0
            }
            ResourceState.SUCCESS -> {
                view_flipper_detail.displayedChild = 1
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(response.data, "image/*")
                startActivity(
                    Intent.createChooser(
                        intent,
                        "Scegli una applicazione per aprire l'immagine:"
                    )
                )
            }

            ResourceState.ERROR -> {
                view_flipper_detail.displayedChild = 1
            }
        }

    }

    private fun handleGetSingleTestComplete(response: Resource<TestEntity>) {
        when (response.status) {
            ResourceState.LOADING -> {
                view_flipper_detail.displayedChild = 0
            }
            ResourceState.SUCCESS -> {
                view_flipper_detail.displayedChild = 1
                setViewForData(response.data!!)
                //view_flipper_detail.updateData(response.data!!)
            }

            ResourceState.ERROR -> {
                view_flipper_detail.displayedChild = 2
            }
        }
    }

    fun setViewForData(data: TestEntity) {
        mTest           = data
        tvData.text     = data.sendDate.toFullString()
        tvToken.text    = data.token
        tvSchool.text   = data.school
        tvClass.text    = data.classType
        tvDiagnosi.text = data.diagnosis

        //if (intent.getBooleanExtra(BY_NOTIFICATION,false))
        //    tvDiagnosi.text = "Si"

        if (data.diagnosis.toUpperCase(Locale.getDefault())=="SI") {
            btnShowDiagnosis.visibility=View.VISIBLE
        }
    }

    fun onBtnShowVideoClick(view: View) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(mTest.videoUri), "video/mp4")
        startActivity(Intent.createChooser(intent, "Scegli una applicazione per aprire il video:"))
    }

    fun onBtnShowImageClick(view: View) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(mTest.imgUri), "image/*")
        startActivity(
            Intent.createChooser(
                intent,
                "Scegli una applicazione per aprire l'immagine:"
            )
        )
    }

    fun onBtnShowDiagnosisClick(view: View) {
        detailViewModel.showDiagnosis(tvToken.text.toString())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        //const val TEST_ID = "tesst_id"
        fun newIntent(c: Context, uploadToken: String): Intent {
            val intent = Intent(c, TestDetailActivity::class.java)
            intent.putExtra(UPLOAD_TOKEN, uploadToken)
            return intent
        }
    }


}
