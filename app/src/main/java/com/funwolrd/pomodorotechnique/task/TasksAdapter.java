package com.funwolrd.pomodorotechnique.task;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.funwolrd.pomodorotechnique.R;

import java.util.List;

/**
 * Created by ThanhTD on 7/21/2018.
 */
public class TasksAdapter extends RecyclerView.Adapter {

    List<Task> mTasks;
    //    TaskListener mListener;
    TaskViewHolder mSelectedView;

    public TasksAdapter(List<Task> mTasks) {
        this.mTasks = mTasks;
//        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TaskViewHolder) holder).bind(mTasks.get(position));
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public boolean autoSelectNextTask() {
        for (int i = 0; i < mTasks.size(); i++) {
            if (!mTasks.get(i).isDone()) {
                mTasks.get(i).setOnDoing(true);
                return true;
            }
        }
        mSelectedView = null;
        return false;
    }

    public void addTask(Task task) {
        if (mSelectedView == null)
            task.setOnDoing(true);
        mTasks.add(task);
        notifyDataSetChanged();
    }

    public void clearAllTasks() {
        mTasks.clear();
        mSelectedView = null;
        notifyDataSetChanged();
    }

    public Task getSelectedTask() {
        for (Task task : mTasks) {
            if (task.isOnDoing())
                return task;
        }
        return null;
    }

    private class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTaskName;
        private CheckBox cbTaskStatus;
        private ImageView ivDoingTask;
        private ImageView ivDelete;

        private Task mTask;

        public TaskViewHolder(View itemView) {
            super(itemView);

            tvTaskName = itemView.findViewById(R.id.tv_task_name);
            cbTaskStatus = itemView.findViewById(R.id.cb_done);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            ivDoingTask = itemView.findViewById(R.id.iv_doing_task);

            int color = Color.TRANSPARENT;
            Drawable background = itemView.getBackground();
            if (background instanceof ColorDrawable)
                color = ((ColorDrawable) background).getColor();
            final int bgColor = color;

            itemView.setOnClickListener(v -> {
//                mListener.onTaskClick();
                if (!mTask.isDone()) {
                    if (mSelectedView != null)
                        mSelectedView.setSelectedTask(false);
                    setSelectedTask(true);
                    mSelectedView = this;
                }
            });

            cbTaskStatus.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                mTask.setDone(isChecked);
                itemView.setBackgroundColor(isChecked ? itemView.getContext().getResources().getColor(R.color.disable_color) : bgColor);
                if (isChecked && mTask.isOnDoing()) {
                    setSelectedTask(false);
                    if (autoSelectNextTask()) {
                        notifyDataSetChanged();
                    }
                }
            });

            ivDelete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setMessage("Delete task?");
                builder.setPositiveButton("OK", (dialogInterface, j) -> {
                    int pos = getAdapterPosition();
                    if (!mTask.isOnDoing() || !autoSelectNextTask()) {
                        mTasks.remove(pos);
                        notifyItemRemoved(pos);
                    } else {
                        mTasks.remove(pos);
                        notifyDataSetChanged();
                    }
                    dialogInterface.dismiss();
                });
                builder.setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.cancel()));
                builder.create().show();
            });
        }

        void bind(Task task) {
            mTask = task;
            tvTaskName.setText(task.getTaskName());
            cbTaskStatus.setChecked(task.isDone());
            if (task.isOnDoing())
                mSelectedView = this;
            setSelectedTask(task.isOnDoing());
        }

        void setSelectedTask(boolean isSelected) {
            ivDoingTask.setImageResource(isSelected ? R.drawable.ic_selected : android.R.color.transparent);
            mTask.setOnDoing(isSelected);
        }
    }

//    interface TaskListener {
//        void onTaskClick();
//    }
}
