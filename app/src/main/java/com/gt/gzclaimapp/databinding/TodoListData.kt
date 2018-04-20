package com.gt.gzclaimapp.databinding

import android.content.Context
import com.blankj.utilcode.util.TimeUtils
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.gson.ToDoList
import java.text.SimpleDateFormat
import java.util.*

class TodoListData() {

    lateinit var name: String
    lateinit var applyDate: String
    lateinit var clockTime: String
    lateinit var type: String
    lateinit var toDoListDetail: ToDoList

    private val typeString = arrayOf(R.string.overview_type_none, R.string.overview_type_none, R.string.hr_todo_rest_half, R.string.hr_todo_rest_full)

    constructor(toDoListDetail: ToDoList, context: Context) : this() {
        name = toDoListDetail.userName
        applyDate = context.getString(R.string.hr_todo_apply_title2, TimeUtils.millis2String(toDoListDetail.restDate, SimpleDateFormat("MM/dd", Locale.CHINESE)))
        clockTime = context.getString(R.string.hr_todo_clock_time, TimeUtils.millis2String(toDoListDetail.checkInDate, SimpleDateFormat("MM/dd HH:mm", Locale.CHINESE)))
        type = context.getString(typeString[toDoListDetail.checkInType - 1])

        this.toDoListDetail = toDoListDetail
    }

}