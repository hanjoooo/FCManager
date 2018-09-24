package com.example.khanj.fcmanager.MyPage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.khanj.fcmanager.R;

public class FoodCalorieCrawlingActivity extends AppCompatActivity {
    private String htmlPageUrl = "http://ckimtalks.tistory.com/m/entry/%EC%8B%9D%ED%92%88-%EC%B9%BC%EB%A1%9C%EB%A6%AC-%EB%8B%A4%EC%9D%B4%EC%96%B4%ED%8A%B8-%EC%8B%9D%EB%8B%A8-%EC%B6%94%EC%B2%9C-%EA%B1%B4%EA%B0%95%ED%95%9C-%EB%8B%A4%EC%9D%B4%EC%96%B4%ED%8A%B8%EB%A5%BC-%EC%9C%84%ED%95%9C-%EC%9D%8C%EC%8B%9D%EB%B3%84-%EC%B9%BC%EB%A1%9C%EB%A6%AC%ED%91%9C?category=552119"; //파싱할 홈페이지의 URL주소
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_calorie_crawling);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(htmlPageUrl+"/html/test.html");
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClientClass());
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("check URL",url);
            view.loadUrl(url);
            return true;
        }
    }
}
