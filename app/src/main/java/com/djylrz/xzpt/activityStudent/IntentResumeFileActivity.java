package com.djylrz.xzpt.activityStudent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.djylrz.xzpt.R;

public class IntentResumeFileActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnFileBrowsing;
    private Toolbar toolbar;
    private EditText url; //测试用输入远程文档地址，正式版请删除
    //todo 填写简历模板的名称 ->小榕
    private String title;//简历模板名称
    private TextView resumeFileName;//文件名称
    //todo 填写文件名，会在文件浏览顶部显示 ->小榕
    private String fileName="TBS测试.doc";
    //    private String fileUrl="http://123.207.239.170/test.docx";//远程文档地址，如下载失败请验证此链接是否还可用（那个时候可能我养不住服务器了）
    //todo 填写文档的地址 ->小榕
    private String fileUrl="http://www.fuzhou.gov.cn/tzgg/201904/P020190430575112821124.docx";//远程文档地址

    /**
     * Fragment中初始化Toolbar
     * @param toolbar
     * @param title 标题
     * @param isDisplayHomeAsUp 是否显示返回箭头
     */
    public void initToolbar(Toolbar toolbar, String title, boolean isDisplayHomeAsUp) {
        AppCompatActivity appCompatActivity= this;
        appCompatActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(isDisplayHomeAsUp);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_resume_file);
        toolbar = (Toolbar) findViewById(R.id.resume_toolbar);
        url = (EditText) findViewById(R.id.text_url);
        initToolbar(toolbar,title,false);
        resumeFileName = (TextView) findViewById(R.id.resume_file_name);
        resumeFileName.setText(fileName);
        btnFileBrowsing=findViewById(R.id.resume_file_download);
        btnFileBrowsing.setOnClickListener(IntentResumeFileActivity.this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resume_file_download:
                //从输入框获取远程文件地址，正式版请去除
                fileUrl = url.getText().toString();
                //动态权限申请
                if (ContextCompat.checkSelfPermission(IntentResumeFileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(IntentResumeFileActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    ResumeDisplayActivity.actionStart(IntentResumeFileActivity.this,fileUrl,fileName);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ResumeDisplayActivity.actionStart(IntentResumeFileActivity.this,fileUrl,fileName);
                } else {
                    Toast.makeText(this, "你拒绝了权限申请，可能无法下载文件到本地哟！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}
