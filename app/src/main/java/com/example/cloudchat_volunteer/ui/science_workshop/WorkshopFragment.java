package com.example.cloudchat_volunteer.ui.science_workshop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cloudchat_volunteer.R;
import com.example.cloudchat_volunteer.databinding.FragmentWorkshopBinding;

public class WorkshopFragment extends Fragment {

    private FragmentWorkshopBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        WorkshopViewModel workshopViewModel =
                new ViewModelProvider(this).get(WorkshopViewModel.class);

        binding = FragmentWorkshopBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button button_vote = binding.voteButton;
        final TextView textView = binding.textPushing;
        final ImageView imageView_live = binding.liveImageview;

        GetPushingMessage();


        button_vote.setOnClickListener(this::ShowVoteDialog);
        imageView_live.setOnClickListener(this::ShowLive);
        textView.setOnClickListener(this::ShowPushing);


//        final TextView textView = binding.textWorkshop;
//        workshopViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    // GetTextPushingFromDatabase

    private void GetPushingMessage(){
        String message = "后续实现消息获取";
        this.binding.textPushing.setText(message);
    }

    private void ShowVoteDialog(View v) {
        View view = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_edit);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setIcon(R.mipmap.app_icon)//设置标题的图片
                .setTitle("半自定义对话框")//设置对话框的标题
                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = editText.getText().toString();
                        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();

    }

    private void ShowLive(View v) {
        Toast.makeText(getContext(), "后续实现跳转直播", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void ShowPushing(View view) {
        Toast.makeText(getContext(), this.binding.textPushing.getText(), Toast.LENGTH_SHORT).show();
    }

}