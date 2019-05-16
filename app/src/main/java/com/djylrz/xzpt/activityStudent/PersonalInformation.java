package com.djylrz.xzpt.activityStudent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.djylrz.xzpt.R;
import com.djylrz.xzpt.activity.BaseActivity;
import com.djylrz.xzpt.bean.PostResult;
import com.djylrz.xzpt.bean.TempResponseData;
import com.djylrz.xzpt.bean.User;
import com.djylrz.xzpt.utils.PostParameterName;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Calendar;

public class PersonalInformation extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PersonalInformation";

    private EditText name;//姓名
    private Spinner sex;//性别
    private EditText age;//年龄
    private EditText phoneNum;//电话号码
    private EditText mailAddress;//邮箱
    private EditText currentCity;//居住城市
    private EditText school;//毕业院校
    private Spinner highestEducation;//最高学历
    private EditText major;//主修专业
    private EditText startTime;//教育开始时间
    private EditText endTime;//教育结束时间
    private ArrayAdapter<String> sexAdapter;
    private ArrayAdapter<String> highestEducationAdapter;
    private String[] sexArray=new String[]{"默认","男","女"};
    private String[] highestEducationArray=new String[]{"学历不限","大专","本科","硕士","博士及以上"};

    private User user = new User();//用户实体对象
    private String token;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pernoal_information);

        name =  (EditText) findViewById(R.id.info_name);
        age = (EditText) findViewById(R.id.info_age);
        phoneNum = (EditText) findViewById(R.id.info_phonenum);
        mailAddress = (EditText) findViewById(R.id.info_mail);
        currentCity = (EditText) findViewById(R.id.info_currentcity);
        school = (EditText) findViewById(R.id.info_school);
        major = (EditText) findViewById(R.id.info_major);
        startTime = (EditText) findViewById(R.id.info_start_time);
        endTime = (EditText) findViewById(R.id.info_end_time);
        sex = (Spinner) findViewById(R.id.sex_spinner);
        highestEducation = (Spinner) findViewById(R.id.highestEducation);
        //性别下拉框
        sexAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,sexArray);
        sexAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sex.setAdapter(sexAdapter);
        //性别下拉框点击事件
        sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PersonalInformation.this,"性别"+sexArray[position], Toast.LENGTH_SHORT).show();
                Log.d(TAG, "性别："+position);
                user.setSex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //最高学历下拉框
        highestEducationAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,highestEducationArray);
        highestEducationAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        highestEducation.setAdapter(highestEducationAdapter);
        //点击事件
        highestEducation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PersonalInformation.this,"最高学历："+highestEducationArray[position], Toast.LENGTH_SHORT).show();
                Log.d(TAG, "最高学历："+position);
                user.setHighestEducation(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button next = (Button)findViewById(R.id.info_next_button);//保存按钮
        next.setOnClickListener(this);

        getStudentInfo();



        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //保存按钮
            case R.id.info_next_button:
                //保存参数
                user.setUserName(name.getText().toString());//名字
                user.setSpecialty(major.getText().toString());//专业
                user.setUserName(name.getText().toString());//名字
                user.setAge(Integer.parseInt(age.getText().toString()));//年龄
                user.setEmail(mailAddress.getText().toString());//邮件
                user.setPresentCity(currentCity.getText().toString());//当前城市
                user.setSchool(school.getText().toString());//学校
                user.setTelephone(phoneNum.getText().toString());//电话，没有限定输入格式

                Calendar calendar = Calendar.getInstance();
                if (!startTime.getText().toString().equals("")){
                    calendar.set(Calendar.YEAR,Integer.parseInt(startTime.getText().toString()));
                    user.setStartTime(new java.sql.Date(calendar.getTime().getTime()));//教育开始时间
                }
                if (!endTime.getText().toString().equals("")){
                    calendar.set(Calendar.YEAR,Integer.parseInt(endTime.getText().toString()));
                    user.setEndTime(new java.sql.Date(calendar.getTime().getTime()));//教育结束时间，string->Date,没有限定输入格式                ;
                }

                //发送修改个人信息请求
                Log.d(TAG, "onClick: "+PostParameterName.POST_URL_UPDATE_USER_INRO+user.getToken());
                try {
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .create();
                    Log.d(TAG, "onClick: "+new Gson().toJson(user));
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(PostParameterName.POST_URL_UPDATE_USER_INRO + user.getToken(), new JSONObject(gson.toJson(user)),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    final PostResult postResult = new Gson().fromJson(response.toString(),PostResult.class);
                                    Log.d(TAG, "onResponse: 修改个人信息"+response.toString());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            switch(postResult.getResultCode()){
                                                case "200":{
                                                    Toast.makeText(PersonalInformation.this, "修改个人信息成功", Toast.LENGTH_SHORT).show();
                                                    getStudentInfo();
                                                    finish();//保存成功，结束当前页面
                                                }break;
                                                default:{
                                                    Toast.makeText(PersonalInformation.this, "修改个人信息失败", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            PersonalInformation.this.finish();
                                        }
                                    });
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("TAG", error.getMessage(), error);
                        }});
                    requestQueue.add(jsonObjectRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }
    //初始化页面可用这个函数
    private void initpage(User user) {
        setEditTextSaveEnableFalse();
        name.setText(user.getUserName());
        age.setText(String.valueOf(user.getAge()));
        phoneNum.setText(user.getTelephone());
        mailAddress.setText(user.getEmail());
        currentCity.setText(user.getPresentCity());
        school.setText(user.getSchool());
        major.setText(user.getSpecialty());
        highestEducation.setSelection((int)user.getHighestEducation());
        sex.setSelection((int)user.getSex());
        Calendar calendar = Calendar.getInstance();
        if (user.getStartTime()!=null){
            calendar.setTime(new Date(user.getStartTime().getTime()));
            startTime.setText(calendar.get(Calendar.YEAR) + "");
        }else{
            startTime.setText("");
        }
        if (user.getEndTime()!=null){
            calendar.setTime(new Date(user.getEndTime().getTime()));
            endTime.setText(calendar.get(Calendar.YEAR) + "");
        }else{
            endTime.setText("");
        }
        Log.d(TAG, "initpage: -----");

    }

    private void setEditTextSaveEnableFalse(){
        name.setSaveEnabled(true);
        age.setSaveEnabled(false);
        phoneNum.setSaveEnabled(false);
        mailAddress.setSaveEnabled(false);
        currentCity.setSaveEnabled(false);
        school.setSaveEnabled(false);
        major.setSaveEnabled(false);
    }


    private void getStudentInfo(){
        //用户已经登录，查询个人信息并显示
        requestQueue = Volley.newRequestQueue(getApplicationContext()); //把上下文context作为参数传递进去
        SharedPreferences userToken = getSharedPreferences("token",0);
        token = userToken.getString(PostParameterName.STUDENT_TOKEN,null);
        if (token != null){
            Log.d(TAG, "onCreate: TOKEN is "+token);

            user.setToken(token);

            try {
                Log.d(TAG, "onCreate: 获取个人信息，只填了token"+new Gson().toJson(user));
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(PostParameterName.POST_URL_GET_USER_BY_TOKEN+user.getToken(),new JSONObject(new Gson().toJson(user)),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "onResponse: 返回"+response.toString());
                                Type jsonType = new TypeToken<TempResponseData<User>>() {}.getType();

                                Gson gson = new GsonBuilder()
                                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .create();
                                final TempResponseData<User> postResult = gson.fromJson(response.toString(), jsonType);
                                Log.d(TAG, "onResponse: "+postResult.getResultCode());
                                user = postResult.getResultObject();
                                user.setToken(token);

                                //获取用户信息，存储到本地。
                                SharedPreferences sharedPreferences = getSharedPreferences("user", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                try {
                                    Log.d(TAG, "用户信息存储到本地SharedPreferences：："+response.getJSONObject(PostParameterName.RESPOND_RESULTOBJECT).toString());
                                    editor.putString("student", new Gson().toJson(user));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                editor.commit();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        initpage(user);
                                        Log.d(TAG, "run: ------");
                                    }
                                });
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }});
                requestQueue.add(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

