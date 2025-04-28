package com.app.cloudchat_volunteer.ui.interactive_lecture;

import static com.app.cloudchat_volunteer.dao.MaterialDao.get_all_res;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.app.cloudchat_volunteer.R;
import com.app.cloudchat_volunteer.dao.MaterialDao;
import com.app.cloudchat_volunteer.ui.live.InteractiveActivity;
import com.app.cloudchat_volunteer.ui.live.LiveActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LectureFragment extends Fragment {

    private ListView orderList;
    private TextView countdownText;
    private List<Map<String, String>> orderData;
    private OrderAdapter orderAdapter;
    private String newLink = "https://www.newlink.com"; // 新链接

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecture, container, false);

        orderList = view.findViewById(R.id.orderList);
        countdownText = view.findViewById(R.id.countdownText);
        orderData = new ArrayList<>();

        // 添加头部
        View headerView = inflater.inflate(R.layout.list_item, null);
        ((TextView) headerView.findViewById(R.id.idText)).setText("ID");
        ((TextView) headerView.findViewById(R.id.linkText)).setText("链接");
        ((TextView) headerView.findViewById(R.id.gradeText)).setText("年级");
        ((TextView) headerView.findViewById(R.id.subjectText)).setText("科目");
        ((TextView) headerView.findViewById(R.id.statusText)).setText("状态");
        headerView.findViewById(R.id.acceptButton).setVisibility(View.GONE);
        orderList.addHeaderView(headerView);

        // 获取数据
        new Thread(() -> {
            try {
                HashMap<String, ArrayList<String>> res = get_all_res();
                Log.d("LectureFragment", "Received data: " + res);

                getActivity().runOnUiThread(() -> {
                    for (Map.Entry<String, ArrayList<String>> entry : res.entrySet()) {
                        Map<String, String> order = new HashMap<>();
                        ArrayList<String> details = entry.getValue();

                        order.put("id", entry.getKey());
                        order.put("link", details.get(0));

                        String remark = details.get(1); // eg: 高中 - 高一 - 语文
                        String[] parts = remark.split(" - ");
                        order.put("school", parts.length > 0 ? parts[0] : "");
                        order.put("grade", parts.length > 1 ? parts[1] : "");
                        order.put("subject", parts.length > 2 ? parts[2] : "");

                        order.put("status", details.get(2));
                        orderData.add(order);
                    }
                    orderAdapter = new OrderAdapter(getActivity(), orderData);
                    orderList.setAdapter(orderAdapter);
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.e("LectureFragment", "数据获取失败", e);
            }
        }).start();

        return view;
    }

    private class OrderAdapter extends SimpleAdapter {

        public OrderAdapter(FragmentActivity context, List<? extends Map<String, ?>> data) {
            super(context, data, R.layout.list_item,
                    new String[]{"id", "link", "school", "grade", "subject", "status"},
                    new int[]{R.id.idText, R.id.linkText, R.id.schoolText, R.id.gradeText, R.id.subjectText, R.id.statusText});

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            TextView linkTextView = view.findViewById(R.id.linkText);
            Button acceptButton = view.findViewById(R.id.acceptButton);
            TextView statusText = view.findViewById(R.id.statusText);

            String link = orderData.get(position).get("link");
            String status = orderData.get(position).get("status");

            // 确保链接不会太长，截断并添加省略号
            String simplifiedLink = link;
            if (link.length() > 30) {
                simplifiedLink = link.substring(0, 30) + "...";
            }

            // 设置原始链接可点击
            SpannableString linkSpannable = new SpannableString(simplifiedLink);
            linkSpannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    openLink(link); // 点击后打开原始链接
                }
            }, 0, simplifiedLink.length(), 0);
            linkSpannable.setSpan(new UnderlineSpan(), 0, simplifiedLink.length(), 0);

            linkTextView.setText(linkSpannable);
            linkTextView.setMovementMethod(LinkMovementMethod.getInstance());

            // 若已接单，按钮禁用且变灰
            if ("已接单".equals(status)) {
                acceptButton.setEnabled(false);
                acceptButton.setText("已接单");
                acceptButton.setAlpha(0.5f); // 半透明表示不可用
            } else {
                acceptButton.setEnabled(true);
                acceptButton.setAlpha(1f);
                acceptButton.setText("接单");

                acceptButton.setOnClickListener(v -> {
                    // 显示链接、倒计时
                    linkTextView.setVisibility(View.VISIBLE);
                    countdownText.setVisibility(View.VISIBLE);
                    startCountdown();

                    // 设置新链接
                    SpannableString newLinkSpannable = new SpannableString(newLink);
                    newLinkSpannable.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            openLink(newLink);
                        }
                    }, 0, newLink.length(), 0);
                    newLinkSpannable.setSpan(new UnderlineSpan(), 0, newLink.length(), 0);
                    linkTextView.setText(newLinkSpannable);
                    linkTextView.setMovementMethod(LinkMovementMethod.getInstance());

                    // 显示加载中样式
                    acceptButton.setEnabled(false);
                    acceptButton.setText("接单中...");
                    acceptButton.setAlpha(0.5f);

                    // 后台线程更新状态
                    new Thread(() -> {
                        try {
                            String rowKey = orderData.get(position).get("id");
                            String newStatus = MaterialDao.update_status(rowKey);
                            String displayStatus = "true".equals(newStatus) ? "已接单" : newStatus;

                            getActivity().runOnUiThread(() -> {
                                orderData.get(position).put("status", displayStatus);
                                orderAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "接单成功，状态更新为：" + displayStatus, Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "接单失败，请重试", Toast.LENGTH_SHORT).show();
                                acceptButton.setEnabled(true);
                                acceptButton.setAlpha(1f);
                                acceptButton.setText("接单");
                            });
                        }
                    }).start();
                });
            }

            return view;
        }

    }

    private void startCountdown() {
        new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownText.setText("自动倒计时 " + millisUntilFinished / 1000 + " 秒后开始");
            }

            @Override
            public void onFinish() {
                countdownText.setText("开始");
                Intent intent = new Intent(getActivity(), InteractiveActivity.class);
                intent.putExtra("url", newLink);
                startActivity(intent);
            }
        }.start();
    }

    private void openLink(String url) {
        try {
            if (url.equals(newLink)) {
                Intent intent = new Intent(getActivity(), LiveActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "无法打开链接，请检查网络或链接", Toast.LENGTH_LONG).show();
        }
    }
}
