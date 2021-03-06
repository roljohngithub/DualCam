package com.cam.dualcam.twitter;

import com.cam.dualcam.R;
import com.cam.dualcam.R.id;
import com.cam.dualcam.R.layout;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TwitterWebview extends Activity {

	private Intent mIntent;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_webview_layout);
		mIntent = getIntent();
		String url = (String)mIntent.getExtras().get("URL");
		
		System.out.println("URL -->" + url);
		WebView webView = (WebView) findViewById(R.id.webview);
		webView.setWebViewClient( new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				if( url.contains(TwitterConstant.TWITTER_CALLBACK_URL))
				{
					Uri uri = Uri.parse( url );
					String oauthVerifier = uri.getQueryParameter( "oauth_verifier" );
					mIntent.putExtra( "oauth_verifier", oauthVerifier );
					setResult( RESULT_OK, mIntent ); 
					finish();
					System.out.println("oauthVerifier -->" + oauthVerifier);
					return true;
				}
				return false;
			}
		});
		webView.loadUrl(url);
	}
}