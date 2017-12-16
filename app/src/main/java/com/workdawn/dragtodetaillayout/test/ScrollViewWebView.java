package com.workdawn.dragtodetaillayout.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.workdawn.dragtodetaillayout.R;


public class ScrollViewWebView extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        WebView webView= (WebView) findViewById(R.id.wv);
        webView.requestFocus();
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.removeJavascriptInterface("searchBoxJavaBredge_");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl("https://github.com/workdawn/DragToDetail");
    }
}
