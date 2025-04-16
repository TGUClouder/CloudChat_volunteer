package com.app.cloudchat_volunteer.ui.science_workshop;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cloudchat_volunteer.R;

import com.app.cloudchat_volunteer.adapter.VoteAdapter;
import com.app.cloudchat_volunteer.dao.VotesDao;

import com.app.cloudchat_volunteer.databinding.FragmentWorkshopBinding;
import com.app.cloudchat_volunteer.itemdecoration.VoteDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class WorkshopFragment extends Fragment {


    private FragmentWorkshopBinding binding;
    private AlertDialog voteListDialog;
    private final HashMap<String, ArrayList<String>> voteMap = new HashMap<>();
    private WorkshopViewModel workshopViewModel;
    private VoteAdapter adapter;
    private String USERNAME = "vote_admin";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        workshopViewModel =
                new ViewModelProvider(this).get(WorkshopViewModel.class);

        adapter = new VoteAdapter(voteMap, workshopViewModel);

        // 页面
        binding = FragmentWorkshopBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // 组件

        final Button button_vote = binding.voteButton;
        final TextView textView = binding.textPushing;
        final ImageView imageView_live = binding.liveImageview;



        // 动作绑定
        GetPushingMessage("暂无消息");

        button_vote.setOnClickListener(this::ShowVotes);

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // 数据获取
        workshopViewModel.getVoteLiveData().observe(getViewLifecycleOwner(), new Observer<HashMap<String, ArrayList<String>>>() {
            @Override
            public void onChanged(HashMap<String, ArrayList<String>> hashMap) {
                for(String key:hashMap.keySet()){
                    voteMap.put(key,hashMap.get(key));
                    adapter.addRow(key, hashMap.get(key));
                }
            }
        });

        workshopViewModel.getDetail_switch().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)&&voteListDialog!=null){
                   ShowVoteDetail();
                }
            }
        });

        workshopViewModel.getRemove_switch().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean.equals(true)&&voteListDialog!=null){
                    deleteDialog();
                }
            }
        });

        workshopViewModel.getChange_switch().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)&&voteListDialog!=null){
                    changeDialog();
                }
            }
        });
    }

    private void ShowVotes(View v) {
        if(voteMap.isEmpty()){
            Toast.makeText(getContext(), "请等待数据加载！", Toast.LENGTH_LONG).show();
            return;
        }
        View voteListLayout = getLayoutInflater().inflate(R.layout.dialog_vote_list, null);

        // 设置 RecyclerView 和适配器
        RecyclerView recyclerView = voteListLayout.findViewById(R.id.recycler_table);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        recyclerView.addItemDecoration(new VoteDecoration(2));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        FloatingActionButton fab = voteListLayout.findViewById(R.id.faButton);
        if (fab != null) {
            fab.setOnClickListener(this::SetVoteDialog);
        }

        voteListDialog = new AlertDialog.Builder(getContext())
                .setIcon(R.mipmap.app_icon)
                .setView(voteListLayout)
                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        voteListDialog.dismiss();
                    }
                })
                .create();

        voteListDialog.show();
    }

    // GetTextPushingFromDatabase
    private void GetPushingMessage(String message){
        this.binding.textPushing.setText(message);
    }


    // 设置新投票
    private void SetVoteDialog(View v) {
        voteListDialog.dismiss();
        View view = getLayoutInflater().inflate(R.layout.dialog_vote_submit, null);
        final EditText editText1 = view.findViewById(R.id.vote_name);
        final EditText editText2 = view.findViewById(R.id.vote_detail);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setIcon(R.mipmap.app_icon)//设置标题的图片
                .setTitle("新建话题")//设置对话框的标题
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
                        String vote_name = editText1.getText().toString();
                        String vote_detail = editText2.getText().toString();
                        final String[] id = {null};
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    id[0] = VotesDao.set_vote(USERNAME,vote_name,vote_detail);
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(id[0].contains("ConnectionFailed")){
                                                Toast.makeText(getContext(),"网络出错，请重试！", Toast.LENGTH_SHORT).show();
                                                return;
                                            } else if (id[0].contains("error")) {
                                                Toast.makeText(getContext(),"输入出错，请重试！", Toast.LENGTH_SHORT).show();
                                                return;
                                            }else{
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            workshopViewModel.setVoteLiveData(VotesDao.get_all_votes());
                                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(getContext(),"设置成功！话题id:"+id[0], Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        } catch (IOException | JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }).start();
                                            }

                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        dialog.dismiss();
                    }
                }).create();
        dialog.show();

    }

    // 投票详情页
    private void ShowVoteDetail(){
        View view = getLayoutInflater().inflate(R.layout.dialog_vote_detail, null);
        TextView topicTV = view.findViewById(R.id.detail_topic);
        TextView creatorTV = view.findViewById(R.id.detail_creator);
        TextView prosTV = view.findViewById(R.id.detail_pros);
        TextView startTV = view.findViewById(R.id.detail_start_time);
        TextView endTV = view.findViewById(R.id.detail_end_time);
        TextView detailTV = view.findViewById(R.id.detail_content);

        if (workshopViewModel.getDetails_list().getValue()!=null){
        topicTV.setText(workshopViewModel.getDetails_list().getValue().get(0));
        creatorTV.setText(workshopViewModel.getDetails_list().getValue().get(1));
        prosTV.setText(workshopViewModel.getDetails_list().getValue().get(2));
        startTV.setText(workshopViewModel.getDetails_list().getValue().get(3));
        endTV.setText(workshopViewModel.getDetails_list().getValue().get(4));
        detailTV.setText(workshopViewModel.getDetails_list().getValue().get(5));}

        AlertDialog voteDetailDialog = new AlertDialog.Builder(getContext())
                .setIcon(R.mipmap.app_icon)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        workshopViewModel.setDetail_switch(false);
                    }
                })
                .create();
        voteDetailDialog.show();
    }


    private void deleteDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("删除话题")
                .setIcon(R.mipmap.app_icon)
                .setMessage("确认删除该话题吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        workshopViewModel.setRemove_switch(false);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!USERNAME.equals(workshopViewModel.getVote_owner().getValue())){
                            Toast.makeText(getContext(),"对不起，您无权删除！", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        new Thread(new Runnable() {
                            String response;
                            @Override
                            public void run() {
                                try {
                                    response = VotesDao.delete_vote(workshopViewModel.getVote_id().getValue());
                                } catch (IOException e) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), "删除失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return;
                                }
                                if(response.equals("true")){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                workshopViewModel.setVoteLiveData(VotesDao.get_all_votes());
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getContext(), "投票已删除！", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } catch (IOException | JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }

                                // 在主线程中显示 Toast
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(response.equals("true"));
                                        else if (response.equals("ConnectionFailed")) {Toast.makeText(getContext(), "网络连接出错，请稍后重试！", Toast.LENGTH_SHORT).show();}
                                        else{Toast.makeText(getContext(), "服务器内部出错！", Toast.LENGTH_SHORT).show();}
                                    }
                                });
                            }
                        }).start();
                        workshopViewModel.setRemove_switch(false);
                        voteListDialog.dismiss();
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void changeDialog(){
        View changeVoteLayout = getLayoutInflater().inflate(R.layout.dialog_vote_change,null);
        TextView topicTV = changeVoteLayout.findViewById(R.id.change_topic);
        EditText detailET = changeVoteLayout.findViewById(R.id.change_detail);
        topicTV.setText(workshopViewModel.getVote_title().getValue());
        detailET.setText(workshopViewModel.getVote_detail().getValue());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setIcon(R.mipmap.app_icon)
                .setTitle("更改话题描述")
                .setView(changeVoteLayout)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        workshopViewModel.setChange_switch(false);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String detail = String.valueOf(detailET.getText());
                        new Thread(new Runnable() {
                            String response;
                            @Override
                            public void run() {
                                if(!USERNAME.equals(workshopViewModel.getVote_owner().getValue())){
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), "对不起,你无权修改！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                try {
                                    response = VotesDao.update_details(workshopViewModel.getVote_id().getValue(),detail,USERNAME);
                                } catch (IOException e) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), "修改失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return;
                                }

                                if(response.equals("true")){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                workshopViewModel.setVoteLiveData(VotesDao.get_all_votes());
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getContext(), "投票已更改！", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } catch (IOException | JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(response.equals("true"));
                                        else if (response.equals("ConnectionFailed")) {Toast.makeText(getContext(), "网络连接出错，请稍后重试！", Toast.LENGTH_SHORT).show();}
                                        else{Toast.makeText(getContext(), "服务器内部出错！", Toast.LENGTH_SHORT).show();}
                                    }
                                });
                            }
                        }).start();
                        workshopViewModel.setChange_switch(false);
                        voteListDialog.dismiss();
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
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