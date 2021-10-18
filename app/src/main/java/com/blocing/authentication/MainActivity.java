package com.blocing.authentication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.blocing.authentication.api.ApiClient;
import com.blocing.authentication.api.ApiInterface;
import com.blocing.authentication.dto.Student;
import com.blocing.authentication.utils.Status;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private IntentIntegrator qrScan;
    Status STATUS;
    String did, name, university;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "camera permission granted", Toast.LENGTH_SHORT).show();
        }
        else {
            requestCameraPermission(this);
        }
        qrCodeScanStart();
    }

    private void qrCodeScanStart() {
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
        qrScan.setPrompt("QRCODE로 확인해주세요");
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT)
                        .show();
                qrCodeScanStart();
            }
            else {
                // string to json parsing
                try {
                    JSONObject jsonObject = new JSONObject(result.getContents());
                    if(jsonObject.has("did")) {
                        validator(jsonObject);
                    }
                    else {
                        // 의미 없는 qr스캔
                        Toast.makeText(this, "InCorrect dataType", Toast.LENGTH_SHORT)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
            qrCodeScanStart();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                qrCodeScanStart();
            }
        }, 2500);
    }

    private void validator(JSONObject jsonObject) throws JSONException {
        did = jsonObject.getString("did");
        name = jsonObject.getString("name");
        university = jsonObject.getString("university");
        getStudentWithDid(did);
    }

    private void getStudentWithDid(String did) {
        ApiInterface apiService = ApiClient.getClient()
                .create(ApiInterface.class);

        Call<Student> call = apiService.getStudent(did);
        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if(response.isSuccessful()) {
                    Student student = response.body();

                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date date = new Date();
                        String today = simpleDateFormat.format(date);
                        Date today_date = simpleDateFormat.parse(today);
                        Date expire_date = simpleDateFormat.parse(student.getUpdate_date());
                        Log.d("TTT", "MainActivity - onResponse() called " + today);
                        Log.d("TTT", "MainActivity - onResponse() called " + student.getUpdate_date());
                        if(did.equals(student.getCard_did())) {
                            if (expire_date.before(today_date)) {
                                STATUS = Status.EXPIRED;
                            } else {
                                STATUS = Status.VALID;
                            }

                            TextView textView = findViewById(R.id.text);
                            if (STATUS.equals(Status.EXPIRED)) {
                                textView.setText(name + "님 유효기간이 " + student.getUpdate_date() + "부로 만료되었습니다.");
                            } else if (STATUS.equals(Status.VALID)) {
                                textView.setText(name + "님 " + university + " 학생이 인증되었습니다.");
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                TextView textView = findViewById(R.id.text);
                textView.setText("신원 인증에 실패했습니다.");
                Log.d("test", "MainActivity - onFailure() called " + t.getMessage());
            }
        });
    }

    // 카메라 권한
    private void requestCameraPermission(Activity activity) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 100);
        }
        else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 100);
        }
    }
}