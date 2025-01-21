package com.example.cloudchat_volunteer.ui.mind_sanctuary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText; // 确保导入 EditText
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudchat_volunteer.R;
import com.example.cloudchat_volunteer.adapter.UserAdapter;
import com.example.cloudchat_volunteer.data.User;

import java.util.ArrayList;
import java.util.List;

public class TreeFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tree, container, false);

        // 设置 RecyclerView
        recyclerView = view.findViewById(R.id.rv_user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化数据
        userList = new ArrayList<>();
        userList.add(new User("张三", "一年级", "篮球"));
        userList.add(new User("李四", "二年级", "足球"));
        userList.add(new User("王五", "三年级", "编程"));
        userList.add(new User("赵六", "四年级", "羽毛球"));
        userList.add(new User("孙七", "一年级", "乒乓球"));

        // 设置适配器
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        // 获取 ScrollView 和输入区域
        ScrollView scrollView = view.findViewById(R.id.sv_content);
        LinearLayout inputArea = view.findViewById(R.id.ll_input_area);
        // 添加滑动监听
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            View contentView = scrollView.getChildAt(0);
            int tolerance = 10; // 容差值，避免浮点误差导致重复判断
            boolean isAtBottom = scrollView.getScrollY() + scrollView.getHeight() >= contentView.getHeight() - tolerance;

            if (isAtBottom && inputArea.getVisibility() != View.VISIBLE) {
                inputArea.setVisibility(View.VISIBLE); // 显示输入区域
            } else if (!isAtBottom && inputArea.getVisibility() != View.GONE) {
                inputArea.setVisibility(View.GONE); // 隐藏输入区域
            }
        });

        // 获取输入框和提交按钮
        EditText etInput = view.findViewById(R.id.et_chat_input); // 确保类型是 EditText
        Button btnSubmit = view.findViewById(R.id.btn_submit_tree);

        // 提交按钮点击事件
        btnSubmit.setOnClickListener(v -> {
            // 获取输入框内容
            String inputContent = etInput.getText().toString().trim(); // getText() 返回 Editable 对象，再调用 toString()

            // 检查输入框是否为空
            if (inputContent.isEmpty()) {
                Toast.makeText(getContext(), "请输入内容", Toast.LENGTH_SHORT).show();
                return; // 结束点击事件
            }

            // 检查是否选择了用户
            int selectedPosition = userAdapter.getSelectedPosition();
            if (selectedPosition == -1) {
                Toast.makeText(getContext(), "请先选择一个用户", Toast.LENGTH_SHORT).show();
                return; // 结束点击事件
            }

            // 获取选中的用户信息
            User selectedUser = userList.get(selectedPosition);
            String message = "选择的用户: " + selectedUser.getName() + ", 年级: " + selectedUser.getGrade() + ", 兴趣: " + selectedUser.getHobby() +
                    "\n输入内容: " + inputContent + "   提交成功";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

            // 清空输入框内容
            etInput.setText("");
        });

        return view;
    }
}
