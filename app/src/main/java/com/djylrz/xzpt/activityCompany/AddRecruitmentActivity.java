package com.djylrz.xzpt.activityCompany;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.djylrz.xzpt.R;
import com.djylrz.xzpt.activityStudent.SelectTagActivity;
import com.djylrz.xzpt.bean.Recruitment;
import com.djylrz.xzpt.bean.TempResponseData;
import com.djylrz.xzpt.utils.PostParameterName;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityPicker;
import com.vondear.rxui.view.dialog.RxDialogLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.DoublePicker;
import cn.qqtheme.framework.picker.SinglePicker;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class AddRecruitmentActivity extends AppCompatActivity implements View.OnClickListener{
    private Recruitment recruitment;//用于接收intent传过来的岗位信息对象
    private static final String TAG = "AddRecruitmentActivity";
    private Toolbar toolbar;
    private Activity activity;
    private EditText editTextJobName;
    private EditText editTextDescription;
    private EditText editTextDeliveryRequest;
    private EditText editTextContact;
    private RelativeLayout layoutLocation;
    private TextView textViewLocation;
    private RelativeLayout layoutSalary;
    private TextView textViewSalary;
    private RelativeLayout layoutDegree;
    private TextView textViewDegree;
    private RelativeLayout layoutWorkTime;
    private TextView textViewWorkTime;
    private RelativeLayout layoutIndustryLabel;
    private TextView textViewIndustryLabel;
    private RelativeLayout layoutStationLabel;
    private TextView textViewStationLabel;
    private RelativeLayout layoutType;
    private TextView textViewType;
    List<String> data = new ArrayList<>();
    private String[] mVals = new String[]
            {"C++","Javascript","金融","直播","电商","Java","移动互联网","分布式","C","服务器端","社交",
                    "带薪年假","银行","云计算","MySQL","Linux/Unix","旅游","绩效奖金","工具软件","大数据",
                    "系统架构","互联网金融","J2EE","Hadoop","中间件","支付","顶尖团队","医疗健康","牛人多",
                    "ERP","新零售","平台","数据库","企业服务","PHP","汽车","五险一金","节日礼物","本地生活",
                    "软件开发","移动开发","招聘","Python","广告营销","Yii","保险","SOA","Node.js","IOS",
                    "Golang","嵌入式","Phalcon","Laravel","硬件制造","HTML","客户端","美女CEO","数据处理",
                    "股票期权","无限量零食","抓取","docker","前端开发","数据挖掘","高级技术管理","GO","Web前端",
                    "架构师","文体活动","信息安全","Ruby","运维","爬虫工程师","自动化","图像处理","MFC","游戏",
                    "即时通讯","C#/.NET","通信","媒体","发展空间大","Windows","视频","两次年度旅游","通信/网络设备",
                    "定期体检","年底双薪","教育","JS","现象级产品","地图","算法","视觉","品牌","用户体验","理财",
                    "UI","UED","画册","动画","平面","网店","原画","创意","UE","App设计","Flash","3D","包装","借贷",
                    "美工","广告","交互设计专家","餐补交通补","福利优厚","工程师文化","超豪华办公室","专项奖金",
                    "岗位晋升","Android","全栈","HTML5","爬虫","领导力","团建旅游","Shell","扁平管理","年度旅游",
                    "爬虫架构","搜索","技能培训","音视频","QT","视频流转码","视频编解码","简单有爱文化","福利倍儿好",
                    "免费班车","年终分红","语音处理","机器学习","移动交互","电商美工","网页","手绘","场景","UX",
                    "部门管理","Scala","交通补助","过节费","弹性工作","午餐补助","创业公司范儿","网络爬虫","区块链",
                    "团队建设","美味晚餐","领导好","音频编解码","弹性工作制","年终奖丰厚","Q版","2D","多媒体","目标管理"
            };
    //动画控件
    private SubData subData;
    private RequestQueue requestQueue;
    private RxDialogLoading rxDialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recruitment);
        //初始化数据
        data.add("开发|测试|运维类");
        data.add("产品|需求|项目类");
        data.add("运营|编辑|客服类");
        data.add("市场|商务类");
        data.add("销售类");
        data.add("综合职能|高级管理类");
        data.add("金融类");
        data.add("文娱|传媒|艺术|体育类");
        data.add("教育|培训类");
        data.add("商业服务|专业服务类");
        data.add("贸易|批发|零售|租赁业类");
        data.add("交通|运输|物流|仓储类");
        data.add("房地产|建筑|物业类");
        data.add("生产|加工|制造类");
        data.add("能源矿产|农林牧渔类");
        data.add("化工|生物|制药|医护类");
        data.add("公务员|其他类");
        //获取传递过来的岗位信息
        Intent intent = getIntent();
        recruitment = (Recruitment) intent.getSerializableExtra("recruitment");
        activity = this;
        //获取布局控件
        toolbar = (Toolbar)findViewById(R.id.asa_toolbar);
        layoutLocation = findViewById(R.id.job_location_layout);
        textViewLocation = findViewById(R.id.job_location_result);
        layoutSalary = findViewById(R.id.job_salary_layout);
        textViewSalary = findViewById(R.id.job_salary_result);
        layoutDegree = findViewById(R.id.job_degree_layout);
        textViewDegree = findViewById(R.id.job_degree_result);
        layoutWorkTime = findViewById(R.id.job_workTime_layout);
        textViewWorkTime = findViewById(R.id.job_workTime_result);
        layoutIndustryLabel = findViewById(R.id.job_industryLabel_layout);
        textViewIndustryLabel = findViewById(R.id.job_industryLabel_result);
        layoutStationLabel = findViewById(R.id.job_stationLabel_layout);
        textViewStationLabel = findViewById(R.id.job_stationLabel_result);
        layoutType = findViewById(R.id.job_type_layout);
        textViewType = findViewById(R.id.job_type_result);

        editTextJobName = findViewById(R.id.job_name_et);
        editTextDescription = findViewById(R.id.job_description_et);
        editTextDeliveryRequest = findViewById(R.id.job_deliveryRequest_et);
        editTextContact = findViewById(R.id.job_contact_et);

        rxDialogLoading = new RxDialogLoading(this);
        rxDialogLoading.setLoadingText("正在发布岗位");
        rxDialogLoading.setLoadingColor(R.color.colorPrimary);
        //设置标题栏
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitle("发布岗位");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.done_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_menu_done:
                        //发布岗位
                        //开始加载动画
                        rxDialogLoading.show();
                        //检查是否填写完整
                        if(checkData()){
                            //提交数据至服务器
                            submitData();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        //添加监听
        layoutLocation.setOnClickListener(this);
        layoutDegree.setOnClickListener(this);
        layoutSalary.setOnClickListener(this);
        layoutWorkTime.setOnClickListener(this);
        layoutIndustryLabel.setOnClickListener(this);
        layoutStationLabel.setOnClickListener(this);
        layoutType.setOnClickListener(this);

        subData = new SubData();
        requestQueue = Volley.newRequestQueue(getApplicationContext()); //把上下文context作为参数传递进去

        //如果从intent获取recruitment不为空，则是编辑岗位
        if(recruitment != null){
            toolbar.setTitle("编辑岗位");
            editTextJobName.setSaveEnabled(false);
            editTextJobName.setText(recruitment.getJobName());
            editTextDescription.setSaveEnabled(false);
            editTextDescription.setText(recruitment.getDescription());
            editTextContact.setSaveEnabled(false);
            editTextContact.setText(recruitment.getContact());
            editTextDeliveryRequest.setSaveEnabled(false);
            editTextDeliveryRequest.setText(recruitment.getDeliveryRequest());
            textViewLocation.setText(recruitment.getLocation());
            subData.setLocation(recruitment.getLocation());
            textViewSalary.setText(recruitment.getSalary());
            subData.setSalary(recruitment.getSalary());
            textViewDegree.setText(recruitment.getDegree());
            subData.setDegree(recruitment.getDegree());
            String strWorkTime;
            switch ((int) recruitment.getWorkTime()){
                case 0:
                    strWorkTime = "面议";
                    break;
                case 1:
                    strWorkTime = "955";
                    break;
                case 2:
                    strWorkTime = "965";
                    break;
                case 3:
                    strWorkTime = "956";
                    break;
                case 4:
                    strWorkTime = "996";
                    break;
                default:
                    strWorkTime = "请选择工作时间";
                    break;
            }

            textViewWorkTime.setText(strWorkTime);
            subData.setWorkTime(recruitment.getWorkTime()+"");
            textViewIndustryLabel.setText(data.get((int)recruitment.getIndustryLabel()));
            subData.setIndustryLabel(recruitment.getIndustryLabel()+"");
            String strJobType;
            switch ((int) recruitment.getJobType()){
                case 1:
                    strJobType = "招聘";
                    break;
                case 2:
                    strJobType = "实习";
                    break;
                case 3:
                    strJobType = "兼职";
                    break;
                default:
                    strJobType = "请选择岗位类型";
                    break;
            }
            textViewType.setText(strJobType);
            subData.setJobType(recruitment.getJobType()+"");
            String strStationLabels = recruitment.getStationLabel();
            String[] strStationLabel = strStationLabels.split(",");
            strStationLabels = "";
            for (String stationLabel:strStationLabel) {
                strStationLabels = strStationLabels +mVals[Integer.parseInt(stationLabel)]  + ",";
            }
            textViewStationLabel.setText(strStationLabels);
            subData.setStationLabel(recruitment.getStationLabel());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //地点
            case R.id.job_location_layout:
                JDCityPicker cityPicker = new JDCityPicker();
                cityPicker.init(activity);
                cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
                    //回调函数
                    @Override
                    public void onSelected(ProvinceBean province, com.lljjcoder.bean.CityBean city, DistrictBean district) {
                        textViewLocation.setText(province.getName() + city.getName() + district.getName());
                        subData.setLocation(province.getName() + city.getName() + district.getName());
                    }

                    @Override
                    public void onCancel() {
                    }
                });
                cityPicker.showCityPicker();
                break;
            case R.id.job_degree_layout:
                onDegreePicker(this.getWindow().getDecorView());
                break;
            case R.id.job_salary_layout:
                onSalaryPicker(this.getWindow().getDecorView());
                break;
            case R.id.job_workTime_layout:
                onWorkTimePicker(this.getWindow().getDecorView());
                break;
            case R.id.job_industryLabel_layout:
                onIndustryLabelPicker(this.getWindow().getDecorView());
                break;
            case R.id.job_stationLabel_layout:
                Intent intent = new Intent(AddRecruitmentActivity.this, SelectTagActivity.class);
                startActivityForResult(intent,0);
                break;
            case R.id.job_type_layout:
                onTypePicker(this.getWindow().getDecorView());
                break;
        }
    }

    /**
     *
     * @param view
     */
    public void onDegreePicker(View view) {
        List<String> data = new ArrayList<>();
        data.add("无要求");
        data.add("初中及以上");
        data.add("中专/中技及以上");
        data.add("高中及以上");
        data.add("大专及以上");
        data.add("本科及以上");
        data.add("硕士及以上");
        data.add("博士及以上");
        SinglePicker<String> picker = new SinglePicker<>(this, data);
        picker.setSelectedIndex(1);
        picker.setCycleDisable(true);
        picker.setCanceledOnTouchOutside(true);
        picker.setTextSizeAutoFit(true);
        picker.setLabelTextColor(R.color.colorPrimary);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                textViewDegree.setText(item);
                Toast.makeText(activity, item, Toast.LENGTH_SHORT).show();
                subData.setDegree(item);
            }
        });
        picker.show();
    }
    /**
     *
     * @param view
     */
    public void onSalaryPicker(View view) {
        final ArrayList<String> firstData = new ArrayList<>();
        for(int i = 0; i <= 100; i++){
            firstData.add(i+"");
        }

        final ArrayList<String> secondData = new ArrayList<>();
        for(int i = 0; i <= 100; i++){
            secondData.add(i+"");
        }
        final DoublePicker picker = new DoublePicker(this, firstData, secondData);
        picker.setDividerVisible(true);
        picker.setCycleDisable(true);
        picker.setSelectedIndex(0, 0);
        picker.setCanceledOnTouchOutside(true);
        picker.setFirstLabel(null, "K");
        picker.setSecondLabel("-", "K");
        picker.setTextSizeAutoFit(true);
        picker.setContentPadding(15, 10);
        picker.setLabelTextColor(R.color.colorPrimary);
        picker.setOnPickListener(new DoublePicker.OnPickListener() {
            @Override
            public void onPicked(int selectedFirstIndex, int selectedSecondIndex) {
                if(selectedFirstIndex <= selectedSecondIndex){
                    textViewSalary.setText(firstData.get(selectedFirstIndex) + "K - " + secondData.get(selectedSecondIndex)+"K");
                    subData.setSalary(firstData.get(selectedFirstIndex) + "K - " + secondData.get(selectedSecondIndex)+"K");
                    Toast.makeText(activity, firstData.get(selectedFirstIndex) + "K - " + secondData.get(selectedSecondIndex)+"K", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, "薪资区间应该从小到大，请重新设置", Toast.LENGTH_SHORT).show();

                }

            }
        });
        picker.show();
    }

    /**
     *
     * @param view
     */
    public void onWorkTimePicker(View view) {
        List<String> data = new ArrayList<>();
        data.add("面议");
        data.add("955");
        data.add("965");
        data.add("956");
        data.add("996");
        SinglePicker<String> picker = new SinglePicker<>(this, data);
        picker.setSelectedIndex(1);
        picker.setCycleDisable(true);
        picker.setCanceledOnTouchOutside(true);
        picker.setTextSizeAutoFit(true);
        picker.setLabelTextColor(R.color.colorPrimary);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                textViewWorkTime.setText(item);
                subData.setWorkTime(index+"");
                Toast.makeText(activity, item, Toast.LENGTH_SHORT).show();
            }
        });
        picker.show();
    }

    /**
     *
     * @param view
     */
    public void onIndustryLabelPicker(View view) {

        SinglePicker<String> picker = new SinglePicker<>(this, data);
        picker.setSelectedIndex(1);
        picker.setCycleDisable(true);
        picker.setCanceledOnTouchOutside(true);
        picker.setTextSizeAutoFit(true);
        picker.setLabelTextColor(R.color.colorPrimary);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                textViewIndustryLabel.setText(item);
                subData.setIndustryLabel((index+1)+"");
                Toast.makeText(activity, index+" "+item, Toast.LENGTH_SHORT).show();
            }
        });
        picker.show();
    }
    /**
     *
     * @param view
     */
    public void onTypePicker(View view) {
        List<String> data = new ArrayList<>();
        data.add("招聘");
        data.add("实习");
        data.add("兼职");

        SinglePicker<String> picker = new SinglePicker<>(this, data);
        picker.setSelectedIndex(1);
        picker.setCycleDisable(true);
        picker.setCanceledOnTouchOutside(true);
        picker.setTextSizeAutoFit(true);
        picker.setLabelTextColor(R.color.colorPrimary);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                textViewType.setText(item);
                subData.setJobType((index+1)+"");
                Toast.makeText(activity, index + " " + item, Toast.LENGTH_SHORT).show();
            }
        });
        picker.show();
    }

    /**
     * 重复次数、提醒时间、提醒方式的回调函数
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 0){//识别码
            textViewStationLabel.setText(data.getStringExtra("names"));
            subData.setStationLabel(data.getStringExtra("number"));

        }

    }

    /**
     *
     * @return
     */
    private boolean checkData(){
        //获取用户填写的数据
        subData.setJobName(editTextJobName.getText().toString());
        subData.setDescription(editTextDescription.getText().toString());
        subData.setDeliveryRequest(editTextDeliveryRequest.getText().toString());
        subData.setContact(editTextContact.getText().toString());
        Log.d(TAG, "checkData: "+ subData.getJobName() + "," + subData.getDescription() + "," + subData.getDeliveryRequest() + "," + subData.getContact());
        if(subData.getJobName().equals("") ||subData.getJobName() == null || subData.getDescription() == null || subData.getDescription().equals("") || subData.getContact() == null || subData.getContact().equals("") ||
                subData.getLocation() == null || subData.getDeliveryRequest() == null || subData.getDeliveryRequest().equals("") || subData.getDegree() == null
                || subData.getWorkTime() == null || subData.getIndustryLabel() == null || subData.getStationLabel() == null || subData.getJobType() == null){
            Toast.makeText(activity, "除薪资外，其他项请完整填写", Toast.LENGTH_SHORT).show();
            //结束加载动画
            rxDialogLoading.hide();
            return false;
        }else{
            return true;
        }
    }
    private void submitData(){
        //获取token
        SharedPreferences preferences = getSharedPreferences("token",0);
        String token = preferences.getString(PostParameterName.TOKEN,null);
        Recruitment subDataNew = new Recruitment();
        try {
        //组装URL
        String url;
        if(recruitment != null){
            url = PostParameterName.POST_URL_COMPANY_UPDATE_RESRUITMENT + token + "&recruitmentId=" + recruitment.getRecruitmentId();
            subDataNew = recruitment;
            subDataNew.setJobName(this.subData.getJobName());
            subDataNew.setDescription(this.subData.getDescription());
            subDataNew.setPublishTime(null);
            subDataNew.setDeliveryRequest(this.subData.getDeliveryRequest());
            subDataNew.setContact(this.subData.getContact());
            subDataNew.setLocation(this.subData.getLocation());
            subDataNew.setSalary(this.subData.getSalary());
            subDataNew.setDegree(this.subData.getDegree());
            subDataNew.setWorkTime(Integer.parseInt(this.subData.getWorkTime()));
            subDataNew.setIndustryLabel(Integer.parseInt(this.subData.getIndustryLabel()));
            subDataNew.setJobType(Integer.parseInt(this.subData.getJobType()));
            subDataNew.setStationLabel(this.subData.getStationLabel());
        }else{
            url = PostParameterName.POST_URL_COMPANY_RELEASE_RECRUITMENT + token;
        }

            Log.d(TAG, "onCreate: 开始发送json请求"+ url);
            JSONObject jsonObject = new JSONObject(new Gson().toJson(subData));
            if(recruitment != null){
                jsonObject = new JSONObject(new Gson().toJson(subDataNew));
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: 返回"+response.toString());
                            Type jsonType = new TypeToken<TempResponseData<Object>>() {}.getType();
                            final TempResponseData<Object> postResult = new Gson().fromJson(response.toString(), jsonType);
                            Log.d(TAG, "onResponse: "+postResult.getResultCode());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //结束加载动画
                                    rxDialogLoading.hide();
                                    if(postResult.getResultCode() == 200){
                                        Toast.makeText(activity, "岗位发布成功", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AddRecruitmentActivity.this, Main2Activity.class);
                                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK );
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(activity, "岗位发布失败，请重试！", Toast.LENGTH_SHORT).show();
                                    }
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
        Log.d(TAG, "onCreate: ");
    }

}

class SubData{
    //职位名称
    private String jobName;
    //岗位描述
    private String description;
    //联系人及联系方式
    private String contact;
    //工作地点
    private String location;
    //投递要求
    private String deliveryRequest;
    //薪资
    private String salary;
    //学历要求
    private String degree;
    //工作时间
    private String workTime;
    //行业标签
    private String industryLabel;
    //岗位标签
    private String stationLabel;
    //招聘、实习或者兼职
    private String jobType;


    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeliveryRequest() {
        return deliveryRequest;
    }

    public void setDeliveryRequest(String deliveryRequest) {
        this.deliveryRequest = deliveryRequest;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getIndustryLabel() {
        return industryLabel;
    }

    public void setIndustryLabel(String industryLabel) {
        this.industryLabel = industryLabel;
    }

    public String getStationLabel() {
        return stationLabel;
    }

    public void setStationLabel(String stationLabel) {
        this.stationLabel = stationLabel;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
}