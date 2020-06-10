package com.vjapp.writest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vjapp.writest.R
import com.vjapp.writest.domain.model.TestEntity
import com.vjapp.writest.domain.utils.toFullString
import kotlinx.android.synthetic.main.item_test.view.*

class TestsAdapter(private val listener: OnTestsSelectionListener) : RecyclerView.Adapter<TestsAdapter.ListViewHolder>() {

    interface OnTestsSelectionListener {
        fun onTestSelection(id: Int, element: TestEntity)
        fun onEmptyList() {}
    }

    private var testsList: MutableList<TestEntity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(testsList[position],position)
    }

    override fun getItemCount(): Int {
        return this.testsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_test
    }

    fun checkListState() {
        if (testsList.size == 0) listener.onEmptyList()
    }

    fun updateData(data: List<TestEntity>) {
        this.testsList = data.toMutableList()
        notifyDataSetChanged()
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: TestEntity, position:Int) {
            itemView.tv_send_date.text = String.format("%s - %s" ,data.sendDate.toFullString(),data.token) //TODO
            itemView.tv_school.text = String.format("%s - %s",data.school, data.classType)
            itemView.tv_diagnosi.text = "no"

            itemView.setOnClickListener {
                data?.apply { listener.onTestSelection(data.idTest!!, data)}
            }

        }
    }

}
