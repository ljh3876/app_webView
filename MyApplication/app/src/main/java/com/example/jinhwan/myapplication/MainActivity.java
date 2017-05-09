package com.example.jinhwan.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<URL> list = new ArrayList<URL>();
    ArrayList<String> sitename = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ListView listView ;
    ProgressDialog dialog;
    EditText edit;
    WebView web;
    Animation animTop;
    LinearLayout search;
    LinearLayout front;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Web View");
        init();
    }
    public void init(){
        listView = (ListView)findViewById(R.id.list);
        search = (LinearLayout)findViewById(R.id.linear) ;
        front = (LinearLayout)findViewById(R.id.front);
        edit = (EditText)findViewById(R.id.editText);
        web =(WebView)findViewById(R.id.webView);
        dialog = new ProgressDialog(this);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,sitename);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                web.loadUrl(list.get(position).getUrl());
                listView.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
                front.setVisibility(View.VISIBLE);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("삭제")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("삭제 하시겠습니까?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                sitename.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            }
        });


        web.addJavascriptInterface(new JavaScriptMethods(),"script");
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        web.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.setMessage("Loading..");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        web.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress >=100) dialog.dismiss();
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return super.onJsAlert(view, url, message, result);
            }
        });


        animTop = AnimationUtils.loadAnimation(this,R.anim.translate_top);
        animTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                search.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "즐겨찾기추가");
        menu.add(0, 2, 1,"즐겨찾기목록");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 1){
            web.loadUrl("file:///android_asset/urladd.html");
            listView.setVisibility(View.INVISIBLE);
            web.setVisibility(View.VISIBLE);
            search.setAnimation(animTop);
        }
        else if(item.getItemId() == 2){
            search.setVisibility(View.INVISIBLE);
            web.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);

        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v){
        if(v.getId()==R.id.btn2){
            web.loadUrl(edit.getText().toString());
        }
    }

    Handler myhandler = new Handler();
    class JavaScriptMethods{
        @JavascriptInterface
        public void addUrl(final String name, final String url){
            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(list.size()==0){
                        list.add(new URL(name, url));
                        sitename.add(name);
                        adapter.notifyDataSetChanged();
                        web.loadUrl("javascript:setMsg('등록되었습니다.')");
                    }
                    else {
                        boolean flag = false;
                        for (int i = 0; i<list.size();i++) {
                            if((url).equals(list.get(i).getUrl())){
                                flag = true;
                            }
                        }
                        if (flag) {
                            web.loadUrl("javascript:displayMsg()");
                        }
                        else {
                            list.add(new URL(name, url));
                            sitename.add(name);
                            adapter.notifyDataSetChanged();
                            web.loadUrl("javascript:setMsg('등록되었습니다.')");
                        }
                    }
                }
            });
        }

        @JavascriptInterface
        public void showLayout(){
            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    search.setVisibility(View.VISIBLE);
                }
            });
        }

    }


}
