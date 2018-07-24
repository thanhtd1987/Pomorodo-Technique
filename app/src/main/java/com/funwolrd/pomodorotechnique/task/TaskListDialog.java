package com.funwolrd.pomodorotechnique.task;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.funwolrd.pomodorotechnique.R;
import com.funwolrd.pomodorotechnique.common.Utils;

import java.util.List;

/**
 * Created by ThanhTD on 7/22/2018.
 */
public class TaskListDialog extends Dialog implements View.OnClickListener {

    private RecyclerView rcvTasks;
    private TasksAdapter mAdapter;
    TextView tvAdd, tvClearAll, tvOk;
    ImageView ivClose;
    EditText etAddTask;

    List<Task> tasks;
    TasksDialogListener mListener;

    public TaskListDialog(@NonNull Context context) {
        super(context);
    }

    public TaskListDialog setListener(TasksDialogListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDialogProperties();
        initView();
        initAction();
    }

    private void initDialogProperties() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.dialog_tasks);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;
        getWindow().setAttributes(layoutParams);
    }

    void initView() {
        rcvTasks = findViewById(R.id.rcv_tasks);
        tvAdd = findViewById(R.id.tv_add_task);
        tvClearAll = findViewById(R.id.tv_clear_tasks);
        tvOk = findViewById(R.id.btn_select_task);
        ivClose = findViewById(R.id.iv_close);
        etAddTask = findViewById(R.id.et_add_task_name);

        tasks = Task.getSavedTasks(getContext());
        mAdapter = new TasksAdapter(tasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvTasks.setLayoutManager(layoutManager);
        rcvTasks.setAdapter(mAdapter);

        etAddTask.setImeActionLabel("Add", EditorInfo.IME_ACTION_DONE);
        etAddTask.setOnEditorActionListener((textView, imeAction, keyEvent) -> {
            if (imeAction == EditorInfo.IME_ACTION_DONE) {
                addTask(textView.getText().toString());
                return true;
            }

            return false;
        });
    }

    void initAction() {
        tvAdd.setOnClickListener(this);
        tvClearAll.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        ivClose.setOnClickListener(this);
    }

    private void addTask(String taskName) {
        if (taskName.trim().isEmpty())
            return;
        Utils.hideKeyboard(getContext(), etAddTask);
        Task task = new Task(taskName);
        mAdapter.addTask(task);
        etAddTask.setText("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_task:
                addTask(etAddTask.getText().toString());
                break;
            case R.id.tv_clear_tasks:
                mAdapter.clearAllTasks();
                break;
            case R.id.btn_select_task:
                mListener.onDismiss(mAdapter.getSelectedTask());
                Task.saveTaskList(getContext(), tasks);
                dismiss();
                break;
            case R.id.iv_close:
                dismiss();
                break;
        }

    }

    public interface TasksDialogListener {
        void onDismiss(Task task);
    }
}
