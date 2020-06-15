package com.vjapp.writest

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vjapp.writest.adapters.TestsAdapter
import com.vjapp.writest.domain.model.TestEntity
import com.vjapp.writest.presentation.Resource
import com.vjapp.writest.presentation.ResourceState
import com.vjapp.writest.presentation.TestListViewModel
import kotlinx.android.synthetic.main.activity_tests_list.*
import org.koin.android.viewmodel.ext.android.viewModel


class TestsListActivity : AppCompatActivity(),TestsAdapter.OnTestsSelectionListener {

    lateinit var testsAdapter: TestsAdapter
    private val testsViewModel: TestListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests_list)

        if (supportActionBar!=null) (supportActionBar as ActionBar).setDisplayHomeAsUpEnabled(true)

        testsAdapter = TestsAdapter(this)
        recycler_view_tests.adapter = testsAdapter
        recycler_view_tests.layoutManager = LinearLayoutManager(this)
        initLisners()
        testsViewModel.init()
    }

    private fun initLisners() {
        testsViewModel.testsLiveData.observe(this, Observer { response ->
            response?.let { handleSendFileComplete(response) }
        })

    }

    private fun handleSendFileComplete(response: Resource<List<TestEntity>>) {
        when (response.status) {
            ResourceState.LOADING -> {
                view_flipper_tests.displayedChild=0
            }
            ResourceState.SUCCESS -> {
                view_flipper_tests.displayedChild=1
                testsAdapter.updateData(response.data!!)
            }
            ResourceState.ERROR -> {
                view_flipper_tests.displayedChild=2
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onTestSelection(uploadToken: String, element: TestEntity) {
        startActivity(TestDetailActivity.newIntent(this,uploadToken))
    }
}
