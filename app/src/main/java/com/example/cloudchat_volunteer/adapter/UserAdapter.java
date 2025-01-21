package com.example.cloudchat_volunteer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudchat_volunteer.R;
import com.example.cloudchat_volunteer.data.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private int selectedPosition = -1; // 记录当前选中项的位置

    public UserAdapter(List<User> userList) {
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
        User user = userList.get(position);

        // 设置显示内容：序号 + 姓名 - 年级 - 爱好
        String displayText = (position + 1) + ". " + user.getName() + " - " + user.getGrade() + " - " + user.getHobby();
        holder.tvUserInfo.setText(displayText);
        //设置点击颜色
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.selected_item_color)); // 已选中
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.default_item_color)); // 默认
        }
        // 设置选中状态
        holder.itemView.setSelected(position == selectedPosition);
        holder.radioButton.setChecked(position == selectedPosition);

        // 点击整行时
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // 刷新前一项和当前选中项
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        });

        // 点击单选按钮时
        holder.radioButton.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // 刷新前一项和当前选中项
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // 获取当前选中项的位置
    public int getSelectedPosition() {
        return selectedPosition;
    }

    // ViewHolder 类
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserInfo; // 合并显示内容到一个 TextView
        RadioButton radioButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserInfo = itemView.findViewById(R.id.tv_user_info);
            radioButton = itemView.findViewById(R.id.rb_select_user);
        }
    }
}
