package com.gt.gzclaimapp.ui.hr

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.databinding.TodoListData
import com.gt.gzclaimapp.gson.ToDoList
import com.gt.gzclaimapp.manager.IoMainScheduler
import com.gt.gzclaimapp.manager.RetrofitManager
import com.gt.gzclaimapp.ui.adapter.TodoListAdapter
import com.gt.gzclaimapp.ui.base.BaseActivity
import com.gt.gzclaimapp.utils.ConstantsUtils
import com.gt.gzclaimapp.utils.PreferenceUtils
import com.gt.gzclaimapp.view.CustomLoadMoreView
import kotlinx.android.synthetic.main.activity_single_recyclerview.*
import kotlinx.android.synthetic.main.common_title_bar.*
import java.util.*

class TodoListActivity : BaseActivity(), BaseQuickAdapter.RequestLoadMoreListener {

    private lateinit var todoListAdapter: TodoListAdapter

    private var mCurrentPageSize = 0

    private var sessionId by PreferenceUtils(this, ConstantsUtils.SAVE_SESSION_ID, "")

    companion object {
        private const val PAGE_SIZE = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_recyclerview)
        init()
    }

    override fun onResume() {
        super.onResume()
        callAPI(0)
    }

    private fun init() {
        todoListAdapter = TodoListAdapter()
        todoListAdapter.setLoadMoreView(CustomLoadMoreView())
        todoListAdapter.setOnLoadMoreListener(this@TodoListActivity, singleRecyclerView)

        singleRecyclerView.layoutManager = LinearLayoutManager(this)
        singleRecyclerView.adapter = todoListAdapter

        leftBack.visibility = View.VISIBLE
        leftBack.setOnClickListener({ finish() })
        tv_title.setText(R.string.hr_todo_title)
    }

    private fun callAPI(searchDate: Long) {
        if (searchDate == 0L) {
            showLoading()
        }
        val disposable = RetrofitManager.service.checkInHrToDolist(sessionId, searchDate)
                .compose(IoMainScheduler())
                .arrayResult()
                .map({ toDoList ->
                    mapData(toDoList)
                })
                .subscribe({ toDoListDataList ->
                    hideLoading()
                    if (todoListAdapter.isLoading) {
                        toDoListDataList?.let { todoListAdapter.addData(it) }
                        todoListAdapter.loadMoreComplete()
                    } else {
                        todoListAdapter.setNewData(toDoListDataList)
                    }
                    mCurrentPageSize = toDoListDataList?.size ?: 0
                }, { error ->
                    hideLoading()
                    error.printStackTrace()
                })
        addSubscription(disposable)
    }

    private fun mapData(toDoList: List<ToDoList>): List<TodoListData>? {
        val todoListDataList = ArrayList<TodoListData>()
        for (detail: ToDoList in toDoList) {
            val data = TodoListData(detail, this@TodoListActivity)
            todoListDataList.add(data)
        }
        return todoListDataList
    }

    override fun onLoadMoreRequested() {
        if (mCurrentPageSize < PAGE_SIZE) {
            todoListAdapter.loadMoreEnd()
        } else {
            callAPI(todoListAdapter.data.last().toDoListDetail.checkInDate)
        }
    }

}