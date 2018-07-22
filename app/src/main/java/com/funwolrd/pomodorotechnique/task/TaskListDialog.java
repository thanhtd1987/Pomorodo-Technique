package com.funwolrd.pomodorotechnique.task;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.funwolrd.pomodorotechnique.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThanhTD on 7/22/2018.
 */
public class TaskListDialog extends Dialog implements View.OnClickListener {

    private RecyclerView rcvTasks;
    private TasksAdapter mAdapter;
    TextView tvAdd, tvClearAll, tvOk;
    ImageView ivClose;

    List<Task> tasks;

    public TaskListDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tasks);

        initView();
        initAction();
    }

    void initView() {
        rcvTasks = findViewById(R.id.rcv_tasks);
        tvAdd = findViewById(R.id.tv_add_task);
        tvClearAll = findViewById(R.id.tv_clear_tasks);
        tvOk = findViewById(R.id.btn_select_task);
        ivClose = findViewById(R.id.iv_close);

        tasks = getSavedTasks();
        mAdapter = new TasksAdapter(tasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvTasks.setLayoutManager(layoutManager);
        rcvTasks.setAdapter(mAdapter);
    }

    void initAction() {
        tvAdd.setOnClickListener(this);
        tvClearAll.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        ivClose.setOnClickListener(this);
    }

    private List<Task> getSavedTasks() {
        List<Task> list = new ArrayList<>();
        //TODO: get from SharePreference as String & parse
        return list;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_task:
                //TODO: add task with simple dialog
                break;
            case R.id.tv_clear_tasks:
                //TODO: clear all of tasks
                break;
            case R.id.btn_select_task:
                //TODO: return the selected task name
                break;
            case R.id.iv_close:
                //TODO: close dialog
                break;
        }

    }
}
