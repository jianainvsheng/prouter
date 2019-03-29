package com.source.prouter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.source.sdk.annotaion.IActivity;
import com.source.sdk.prouter.PRouter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PRouter.getInstance().navigation(MainActivity.this,"/source/SecondActivity");
            }
        });
    }
}
