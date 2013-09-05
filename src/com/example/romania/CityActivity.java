package com.example.romania;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.webkit.WebView;

public class CityActivity extends Activity {
	
	WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);
		
		Intent locationIntent = getIntent();
		
		String location = locationIntent.getStringExtra("Location");
		
		System.out.println(location);
		
		webview	= (WebView) findViewById(R.id.webView1);
		webview.getSettings().setJavaScriptEnabled(true);
		
		webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
		
        webview.getSettings().setBuiltInZoomControls(true);
        
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.setScrollbarFadingEnabled(false);
        
		StringBuilder url = new StringBuilder();
		url.append("http://www.romaniatourism.com/");
		
		String modLocation = location.substring(0, 1).toLowerCase() + location.substring(1);
		url.append(modLocation);
		
		url.append(".html");
		
		System.out.println(url.toString());
		
		webview.loadUrl(url.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.city, menu);
		return true;
	}

}
