package com.example.cloudchat_volunteer.ui.mind_sanctuary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudchat_volunteer.R;
import com.example.cloudchat_volunteer.adapter.UserAdapter;
import com.example.cloudchat_volunteer.dao.DiaryDao;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Map<String, String>> userList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tree, container, false);

        recyclerView = view.findViewById(R.id.rv_user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // **初始化 Adapter**
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        // **调用 DAO 方法，获取用户数据**
        new Thread(() -> {
            try {
                List<HashMap<String, String>> data = DiaryDao.get_all_diary();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        userList.clear();
                        userList.addAll(data);
                        userAdapter.notifyDataSetChanged();
                    });
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();

        // **滚动事件**
        ScrollView scrollView = view.findViewById(R.id.sv_content);
        LinearLayout inputArea = view.findViewById(R.id.ll_input_area);
        scrollView.post(() -> {
            scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
                int scrollY = scrollView.getScrollY();
                int height = scrollView.getHeight();
                int contentHeight = scrollView.getChildAt(0).getHeight();
                int tolerance = 10;

                inputArea.setVisibility((scrollY + height >= contentHeight - tolerance) ? View.VISIBLE : View.GONE);
            });
        });

        // **提交按钮事件**
        Button btnSubmit = view.findViewById(R.id.btn_submit_tree);
        btnSubmit.setOnClickListener(v -> {
            int selectedPosition = userAdapter.getSelectedPosition();
            if (selectedPosition == -1) {
                Toast.makeText(getContext(), "请先选择一个用户", Toast.LENGTH_SHORT).show();
                return;
            }

            // **获取 HashMap 结构的用户信息**
            Map<String, String> selectedUser = userList.get(selectedPosition);
            String message = "选择的用户: " + selectedUser.get("first_name") + " " + selectedUser.get("last_name")
                    + ", 年级: " + selectedUser.get("grade") + ", 兴趣: " + selectedUser.get("hobby") + " 提交成功";
            Navigation.findNavController(v).navigate(R.id.action_treeFragment_to_chatFragment);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
