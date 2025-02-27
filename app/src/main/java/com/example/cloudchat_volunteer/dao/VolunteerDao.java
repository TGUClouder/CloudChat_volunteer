package com.example.cloudchat_volunteer.dao;

import androidx.annotation.NonNull;

import com.example.cloudchat_volunteer.json.ErrorResponse;
import com.example.cloudchat_volunteer.json.IdResponse;
import com.example.cloudchat_volunteer.json.NullMessageResponse;
import com.example.cloudchat_volunteer.json.PasswordResponse;
import com.example.cloudchat_volunteer.json.StatusResponse;
import com.example.cloudchat_volunteer.json.UserInfoResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class VolunteerDao {

    static OkHttpClient okHttpClient = new OkHttpClient();
    static String baseURL = "http://47.94.207.38:9999/cloudchat_db_server-1.2/";
    static String tableUrl_info = "user_info";
    static String tableUrl_account = "user_account";

    private VolunteerDao(){}


    // 验证密码
    public static String get_password(String username) throws IOException {
        String appendURL = "user_account?accountType=volunteer&accountName="+username;
        Request request = new Request.Builder()
                .url(baseURL+appendURL)
                .build();
        try(Response response = okHttpClient.newCall(request).execute()){
            if (response.isSuccessful() && response.body() != null) {
                String string = response.body().string();
                Gson gson = new Gson();
                if(string.contains("error")){
                    ErrorResponse errorResponse = gson.fromJson(string, ErrorResponse.class);
                    return errorResponse.getError();
                }else{
                    PasswordResponse passwordResponse = gson.fromJson(string, PasswordResponse.class);

                    return passwordResponse.getMessage().getPassword();
                }

            } else {
                System.out.println("Request failed: " + response.code());
            }
        }
        return "ConnectionFailed";
    }

    // 获取用户信息
    public static ArrayList<String> get_userinfo(String username)throws IOException{
        ArrayList<String> arrayList = new ArrayList<>();
        String appendURL = "user_info?accountType=volunteer&accountName="+username;
        Request request = new Request.Builder()
                .url(baseURL+appendURL)
                .build();
        try(Response response = okHttpClient.newCall(request).execute()){
            if (response.isSuccessful() && response.body() != null) {
                String string = response.body().string();
                Gson gson = new Gson();
                if(string.contains("error")){
                    ErrorResponse errorResponse = gson.fromJson(string, ErrorResponse.class);
                    arrayList.add(errorResponse.getError());
                    return arrayList;
                }else{
                    UserInfoResponse userInfoResponse = gson.fromJson(string, UserInfoResponse.class);
                    UserInfoResponse.Message  message = userInfoResponse.getMessage();
                    arrayList.add(message.getId());
                    arrayList.add(message.getFirst_name());
                    arrayList.add(message.getLast_name());
                    arrayList.add(message.getAge());
                    arrayList.add(message.getEmail());
                    arrayList.add(message.getGender());
                    arrayList.add(message.getDegree());
                    arrayList.add(message.getBirthday());
                    arrayList.add(message.getHobby());
                    arrayList.add(message.getGrade());

                    return arrayList;
                }

            } else {
                System.out.println("Request failed: " + response.code());
            }
        }
        arrayList.add("ConnectionFailed");
        return arrayList;

    }

    // 删除帐号
    public static String delete_account(String username) throws IOException {
        String appendURL = "user_account?accountType=volunteer&accountName="+username;
        Request request = new Request.Builder()
                .url(baseURL+appendURL)
                .delete()
                .build();
        try(Response response = okHttpClient.newCall(request).execute()){
            if (response.isSuccessful() && response.body() != null) {
                String string = response.body().string();
                Gson gson = new Gson();
                if(string.contains("error")){
                    ErrorResponse errorResponse = gson.fromJson(string, ErrorResponse.class);
                    return errorResponse.getError();
                }else{
                    StatusResponse statusResponse = gson.fromJson(string, StatusResponse.class);

                    return statusResponse.getMessage().getStatus();
                }

            } else {
                System.out.println("Request failed: " + response.code());
            }
        }
        return "ConnectionFailed";


    }


    // 更新密码
    public static String update_password(String username, String new_password) throws IOException{
        String appendURL = "accountType=volunteer&accountName="+username+"&password="+new_password;
        Request request = new Request.Builder()
                .url(baseURL+tableUrl_account+"?"+appendURL)
                .put(RequestBody.create("",null))
                .build();
        try(Response response = okHttpClient.newCall(request).execute()){
            if (response.isSuccessful() && response.body() != null) {
                String string = response.body().string();
                Gson gson = new Gson();
                if(string.contains("error")){
                    ErrorResponse errorResponse = gson.fromJson(string, ErrorResponse.class);
                    return errorResponse.getError();
                }else{
                    StatusResponse statusResponse = gson.fromJson(string, StatusResponse.class);

                    return statusResponse.getMessage().getStatus();
                }

            } else {
                System.out.println("Request failed: " + response.code());
            }
        }
        return "ConnectionFailed";

    }

    // 注册帐号
    public static String signup(ArrayList<String> arrayList) throws IOException {
        String appendURL = getString(arrayList);
        MediaType mediaType = MediaType.get("application/x-www-form-urlencoded");
        RequestBody requestBody = RequestBody.create(appendURL.getBytes(StandardCharsets.UTF_8), mediaType);
        Request request = new Request.Builder()
                .url(baseURL+tableUrl_info)
                .post(requestBody)
                .build();
        try(Response response = okHttpClient.newCall(request).execute()){
            if (response.isSuccessful() && response.body() != null) {
                String string = response.body().string();
                Gson gson = new Gson();
                if(string.contains("error")){
                    ErrorResponse errorResponse = gson.fromJson(string, ErrorResponse.class);
                    return errorResponse.getError();
                }else{
                    IdResponse idResponse = gson.fromJson(string, IdResponse.class);

                    return idResponse.getMessage().getId();
                }

            } else {
                System.out.println("Request failed: " + response.code());
            }
        }
        return "ConnectionFailed";

    }

    @NonNull
    private static String getString(ArrayList<String> arrayList) {
        String name = arrayList.get(0);
        String password = arrayList.get(1);
        String f_name = arrayList.get(2);
        String l_name = arrayList.get(3);
        String age = arrayList.get(4);
        String email = arrayList.get(5);
        String gender = arrayList.get(6);
        String degree = arrayList.get(7);
        String birthday = arrayList.get(8);
        String hobby = arrayList.get(9);
        String grade = arrayList.get(10);
        return "name="+name+"&password="+password+"&accountType=volunteer&f="+f_name+"&l="+l_name+"&a="+age+"&e="+email+"&g="+gender+"&d="+degree+"&b="+birthday+"&h="+hobby+"&grade="+grade;
    }

    // 更新用户信息
    public static String update_userinfo(ArrayList<String> arrayList) throws IOException {
        String appendURL = getAppendURL(arrayList);
//        MediaType mediaType = MediaType.get("application/x-www-form-urlencoded");
//        RequestBody requestBody = RequestBody.create(appendURL.getBytes(StandardCharsets.UTF_8), mediaType);
        Request request = new Request.Builder()
                .url(baseURL+tableUrl_info+"?"+appendURL)
                .put(RequestBody.create("", null))
                .build();
        try(Response response = okHttpClient.newCall(request).execute()){
            if (response.isSuccessful() && response.body() != null) {
                String string = response.body().string();
                Gson gson = new Gson();
                if(string.contains("error")){
                    ErrorResponse errorResponse = gson.fromJson(string, ErrorResponse.class);
                    return errorResponse.getError();
                }else{
                    NullMessageResponse statusResponse = gson.fromJson(string, NullMessageResponse.class);

                    return statusResponse.getMessage();
                }

            } else {
                System.out.println("Request failed: " + response.code());
            }
        }
        return "ConnectionFailed";
    }

    @NonNull
    private static String getAppendURL(ArrayList<String> arrayList) {
        String username = arrayList.get(0);
        String f_name = arrayList.get(1);
        String l_name = arrayList.get(2);
        String age = arrayList.get(3);
        String email = arrayList.get(4);
        String gender = arrayList.get(5);
        String degree = arrayList.get(6);
        String birthday = arrayList.get(7);
        String hobby = arrayList.get(8);
        String grade = arrayList.get(9);

        return "name="+username+"&type=volunteer&f="+f_name+"&l="+l_name+"&a="+age+"&e="+email+"&g="+gender+"&d="+degree+"&b="+birthday+"&h="+hobby+"&grade="+grade;

    }

    public static void shutdown(){}

    
}
