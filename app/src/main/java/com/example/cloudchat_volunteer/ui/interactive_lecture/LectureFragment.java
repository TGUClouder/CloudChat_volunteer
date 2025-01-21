package com.example.cloudchat_volunteer.ui.interactive_lecture;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.cloudchat_volunteer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LectureFragment extends Fragment {

    private ListView orderList;
    private TextView linkText;
    private TextView countdownText;
    private List<Map<String, String>> orderData;
    private OrderAdapter orderAdapter;
    private String newLink = "https://www.newlink.com"; // 新链接的URL


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecture, container, false);

        orderList = view.findViewById(R.id.orderList);
        linkText = view.findViewById(R.id.linkText);
        countdownText = view.findViewById(R.id.countdownText);

        orderData = new ArrayList<>();
        Map<String, String> order = new HashMap<>();
        order.put("id", "1");
        order.put("link", "链接");
        order.put("grade", "一年级");
        order.put("subject", "数学");
        orderData.add(order);

        orderAdapter = new OrderAdapter(getActivity(), orderData);
        orderList.setAdapter(orderAdapter);

        // 添加头视图
        View headerView = inflater.inflate(R.layout.list_item, null);
        TextView idText = headerView.findViewById(R.id.idText);
        TextView linkText = headerView.findViewById(R.id.linkText);
        TextView gradeText = headerView.findViewById(R.id.gradeText);
        TextView subjectText = headerView.findViewById(R.id.subjectText);
        TextView acceptText = headerView.findViewById(R.id.acceptText);
        Button acceptButton = headerView.findViewById(R.id.acceptButton);

        idText.setText("ID");
        linkText.setText("链接");
        gradeText.setText("年级");
        subjectText.setText("科目");
        acceptText.setText("接单");
        acceptText.setVisibility(View.VISIBLE);
        acceptButton.setVisibility(View.GONE);

        orderList.addHeaderView(headerView);

        return view;
    }

    private class OrderAdapter extends SimpleAdapter {

        public OrderAdapter(FragmentActivity context, List<? extends Map<String, ?>> data) {
            super(context, data, R.layout.list_item, new String[]{"id", "link", "grade", "subject"}, new int[]{R.id.idText, R.id.linkText, R.id.gradeText, R.id.subjectText});
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            Button acceptButton = view.findViewById(R.id.acceptButton);
            TextView linkText = view.findViewById(R.id.linkText);

            // 设置链接文本为可点击
            String link = orderData.get(position).get("link");
            SpannableString spannableString = new SpannableString(link);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com"));
                    startActivity(intent);
                }
            };
            spannableString.setSpan(clickableSpan, 0, link.length(), 0);
            spannableString.setSpan(new UnderlineSpan(), 0, link.length(), 0);
            linkText.setText(spannableString);
            linkText.setMovementMethod(LinkMovementMethod.getInstance()); // 使文本可点击

            acceptButton.setOnClickListener(v -> {
                linkText.setVisibility(View.VISIBLE);
                linkText.setText("新链接: " + newLink);
                countdownText.setVisibility(View.VISIBLE);
                startCountdown();
                // 设置新链接文本为可点击
                SpannableString newLinkSpannableString = new SpannableString("新链接");
                ClickableSpan newLinkClickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newLink));
                        startActivity(intent);
                    }
                };
                newLinkSpannableString.setSpan(newLinkClickableSpan, 0, newLinkSpannableString.length(), 0);
                newLinkSpannableString.setSpan(new UnderlineSpan(), 0, newLinkSpannableString.length(), 0);
                linkText.setText(newLinkSpannableString);
                linkText.setMovementMethod(LinkMovementMethod.getInstance()); // 使文本可点击
            });


            return view;
        }
    }

    private void startCountdown() {
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownText.setText("自动倒计时 " + millisUntilFinished / 1000 + " 秒后开始");
            }

            @Override
            public void onFinish() {
                countdownText.setText("开始");
                // 倒计时结束后自动跳转到新链接
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newLink));
                startActivity(intent);
            }
        }.start();
    }
}
