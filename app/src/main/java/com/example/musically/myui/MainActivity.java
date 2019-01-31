package com.example.musically.myui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.musically.myui.widget.ExpandedTextView;
import com.ms.square.android.expandabletextview.ExpandableTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExpandableTextView
        ExpandedTextView textView = findViewById(R.id.text);
        SwipeRefreshLayout
        textView.setText("adfasdfasdfasfasdfasdfasdhfiuasdhfasdhfalsdfasdfasdfasdfadfsfdsfsadfadsffsfasfasffasfasffasdfadsfasfsdfasdfasdfadsfadsfadsfasdfasdfasdfasdfasdfasdfasdfa");
    }
}
