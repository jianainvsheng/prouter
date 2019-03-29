package com.source.prouter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.source.sdk.annotaion.IActivity;
import com.source.sdk.prouter.PRouter;

/**
 * Created by yangjian on 2019/3/29.
 */
@IActivity(value = "/source/SecondActivity")
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
