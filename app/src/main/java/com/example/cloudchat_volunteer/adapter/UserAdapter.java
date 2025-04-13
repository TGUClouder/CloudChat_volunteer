package com.example.cloudchat_volunteer.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudchat_volunteer.R;

import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<Map<String, String>> userList;
    private int selectedPosition = RecyclerView.NO_POSITION; // 选中的位置

    public UserAdapter(List<Map<String, String>> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Map<String, String> user = userList.get(position);

        holder.textViewName.setText(user.get("first_name") + " " + user.get("last_name"));
        holder.textViewGrade.setText("年级: " + user.get("grade"));
        holder.textViewHobby.setText("兴趣: " + user.get("hobby"));

        String diaryContent = user.get("diary");
        if (diaryContent == null || diaryContent.isEmpty() || diaryContent.equals("null")) {
            holder.textViewDiary.setText("日记：暂无内容");
        } else {
            holder.textViewDiary.setText("日记：" + diaryContent);
        }

        // **点击事件**
        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // 获取实时位置
            if (currentPosition != RecyclerView.NO_POSITION) {
                selectedPosition = currentPosition;
                notifyDataSetChanged(); // 刷新选中状态
            }
        });

        // **设置选中状态样式**
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // **定义 ViewHolder 内部类**
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewGrade, textViewHobby, textViewDiary;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.tv_user_name);
            textViewGrade = itemView.findViewById(R.id.tv_user_grade);
            textViewHobby = itemView.findViewById(R.id.tv_user_hobby);
            textViewDiary = itemView.findViewById(R.id.tv_user_diary);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
