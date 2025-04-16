package com.app.cloudchat_volunteer.ui.science_workshop;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.cloudchat_volunteer.dao.VotesDao;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class WorkshopViewModel extends ViewModel {

    private final MutableLiveData<HashMap<String, ArrayList<String>>> voteLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> detail_switch = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<String>> details_list = new MutableLiveData<>();
    private final MutableLiveData<Boolean> change_switch = new MutableLiveData<>();
    private final MutableLiveData<String> vote_title = new MutableLiveData<>();
    private final MutableLiveData<String> vote_detail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> remove_switch = new MutableLiveData<>();
    private final MutableLiveData<String> vote_id = new MutableLiveData<>();
    private final MutableLiveData<String> vote_owner = new MutableLiveData<>();


    private HashMap<String, ArrayList<String>> arrayListMap = null;

    public WorkshopViewModel(){
        this.detail_switch.setValue(false);
        this.change_switch.setValue(false);
        this.remove_switch.setValue(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    arrayListMap = VotesDao.get_all_votes();
                    voteLiveData.postValue(arrayListMap);
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public MutableLiveData<Boolean> getDetail_switch() {
        return this.detail_switch;
    }
    public void setDetail_switch(Boolean flag){
        this.detail_switch.postValue(flag);
    }

    public MutableLiveData<Boolean> getChange_switch() {
        return this.change_switch;
    }
    public void setChange_switch(Boolean flag){
        this.change_switch.postValue(flag);
    }

    public MutableLiveData<Boolean> getRemove_switch() {
        return this.remove_switch;
    }
    public void setRemove_switch(Boolean flag){
        this.remove_switch.postValue(flag);
    }


    public MutableLiveData<HashMap<String, ArrayList<String>>> getVoteLiveData() {
        return this.voteLiveData;
    }

    public void setVoteLiveData(HashMap<String, ArrayList<String>> hashMap){
        this.voteLiveData.postValue(hashMap);
    }


    public MutableLiveData<ArrayList<String>> getDetails_list() {
        return this.details_list;
    }

    public void setDetails_list(ArrayList<String> arrayList) {
        this.details_list.postValue(arrayList);
    }

    public void setVote_id(String id){
        this.vote_id.postValue(id);
    }
    public MutableLiveData<String> getVote_id(){
        return this.vote_id;
    }

    public void setVote_owner(String owner){
        this.vote_owner.postValue(owner);
    }
    public MutableLiveData<String> getVote_owner(){
        return this.vote_owner;
    }

    public void setVote_title(String title){
        this.vote_title.postValue(title);
    }

    public MutableLiveData<String> getVote_title(){
        return this.vote_title;
    }

    public void setVote_detail(String detail){
        this.vote_detail.postValue(detail);
    }

    public MutableLiveData<String> getVote_detail(){
        return this.vote_detail;
    }


}