package com.example.mac.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public class MainActivity extends AppCompatActivity {
    private  static  final String TAG = "MainActivity";
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private  static final  int REQUEST_SETTINGS_CODE = 999;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.snackbar_container);

        requestPermission();

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)) {
                showPermissionRationale("需要打开通讯录权限，方便您的操作");
            }else {





                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }else {
            init();
        }
    }

    /**
     * 提示用户申请权限说明
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void showPermissionRationale(String rationale) {
        Snackbar.make(coordinatorLayout, rationale, Snackbar.LENGTH_LONG)
                .setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestPermission();
                    }
                }).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    init();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                        // 给用于予以权限解释, 对于已经拒绝过的情况，先提示申请理由，再进行申请
                        showPermissionRationale("需要打开通讯录权限，方便您的操作");
                    } else {
                        // 用户勾选了不再提醒，引导用户进入设置界面进行开启权限
                        Snackbar.make(coordinatorLayout, "需要打开权限才能使用该功能，您也可以前往设置->应用。。。开启权限",
                                Snackbar.LENGTH_LONG)
                                .setAction("确定",new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view){
                                        Uri uri = new Uri.Builder()
                                                .scheme("package")
                                                .opaquePart(getPackageName())
                                                .build();
                                        startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS,uri),REQUEST_SETTINGS_CODE);
                                    }
                                }).show();
                    }

                }
                break;
            }
            default:{
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);
                break;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void init(){
        List<PhoneBookEntity> list = ContactHelper.getInstance().getContacts(this);
        for (PhoneBookEntity pbe : list) {
            Log.d(TAG,"name:"+pbe.name+",phone:"+pbe.phoneNum);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://webhooks.mongodb-stitch.com/api/client/v2.0/app/passbook-ssizi/service/")
                .build();

        GitHubService service = retrofit.create(GitHubService.class);
        Call<ResponseBody>  responseBody= service.listRepos(getContacts());
        responseBody.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.d("Call",response.body().toString());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail",call.toString());
            }
        });

    }

    private Map<String,String> getContacts() {
        Map<String,String> maps=new HashMap<>();
        List<PhoneBookEntity> list = ContactHelper.getInstance().getContacts(this);

        for (PhoneBookEntity pbe : list) {

            maps.put(pbe.name,pbe.phoneNum);

            Log.d(TAG,"name:"+pbe.name+",phone:"+pbe.phoneNum);
        }
        return  maps;
    }

    public interface GitHubService {
        @FormUrlEncoded
        @Headers("Content-Type:application/x-www-form-urlencoded")
        @POST("addData/incoming_webhook/webhook0")
        Call<ResponseBody> listRepos(@FieldMap Map<String,String> list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTINGS_CODE) {
            requestPermission();
        }
    }

}
