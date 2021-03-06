package com.cam.dualcam;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.cam.dualcam.socialpackage.MyTwitter;
import com.cam.dualcam.twitter.TwitterConstant;
import com.cam.dualcam.twitter.TwitterUtil;
import com.cam.dualcam.utility.Field;
import com.cam.dualcam.widget.LoadingDialog;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.hintdesk.core.util.StringUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MotherCrystal extends FragmentActivity{
	
	private static final String TAG = "MotherCrystal";
	
	public static int STATE = Field.MOTHER_STATE;
	public static final int SPLASH = Field.MOTHER_SPLASH;
	public static final int SOCIALMEDIA = Field.MOTHER_SOCIALMEDIA;
	public static final int CAM = Field.MOTHER_CAM; 
	public static final int CAMERA = 3;
	public static int fragmentShown = -1;
	
	private boolean isResumed = false;
	public boolean resumeMe = false;
	public boolean resumeForFB = false;

	private UiLifecycleHelper uiHelper;
	private MenuItem settings;
	
	private static final int CHILD_FRAGMENT = CAM +1;
	
	private Fragment[] pieces = new Fragment[CHILD_FRAGMENT];
		
	public Bundle bundyDundy;
	
	public LoadingDialog loading;
	
	/*	Great Blue Bird's	*/
	private int TWITTER_AUTH = Field.TWITTER_AUTH;
	
	/*	Azure Scribe's	*/
	
	/*	Overseer's	*/
	private boolean isBlueBirdIN = false;
	private boolean isAzureScribeIN = false;
	public AlertDialog camFragPopUpDialog;
	
	/*	Random, not yet done	*/
	public MyTwitter myTwit;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    //detectIfUserLogInTwitter();
	    
	    if(savedInstanceState != null)
		    setBundyDundy(savedInstanceState);
	    else if(getBundyDundy() == null)
	    	setBundyDundy(new Bundle());
	    
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    Log.i(TAG, "from onCreate");
	    setContentView(R.layout.mother_crystal);
	    
	    loading = new LoadingDialog(MotherCrystal.this);

	    FragmentManager fm = getSupportFragmentManager();
	    pieces[SPLASH] = fm.findFragmentById(R.id.splash_fragment);
	    pieces[SOCIALMEDIA] = fm.findFragmentById(R.id.socialmedia_fragment);
	    pieces[CAM] = fm.findFragmentById(R.id.cam_fragment);
	    //pieces[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
	    FragmentTransaction transaction = fm.beginTransaction();
	    
	    for(int i = 0; i < pieces.length; i++) {
	        transaction.hide(pieces[i]);
	    }
	    
	    transaction.commit();
	    
	    myTwit = new MyTwitter(this);
	    
	    resumeForFB = false;
	} 
	
	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	    Log.i(TAG, "from onResumeFragments.");
	    
	    //Load persistent value for views
	    ancestralRecall();
	    checkbundyDundy("onResumeFragments");
	    Log.i(TAG, "from onResumeFragments. ancestralRecall : "+fragmentShown);
	    switch(fragmentShown){
	    case Field.MOTHER_SPLASH:
	    	linkStart();
	    	break;
	    	
	    case Field.MOTHER_SOCIALMEDIA:
	    	showFragment(SOCIALMEDIA, false);
	    	break;
	    	
	    case Field.MOTHER_CAM:
	    	
	    	showFragment(CAM, false);
	    	break;	
	    	
	    default:
	    	Log.i(TAG, "fragmentShown has failed : "+fragmentShown+". linkStart()ing.");
	    	linkStart();
//	    	Log.i(TAG, "fragmentShown has failed : "+fragmentShown+". Restarting.");
//	    	linkRESTART();
	    	break;
	    }

	}
	
	
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    isResumed = true;
	    Log.i(TAG, "from onResume.");

	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	    Log.i(TAG, "from onPause.");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	    Log.i(TAG, "from onActivityResult.");
	    
	    	
	    if (requestCode == TWITTER_AUTH)
	  		{
	      		
	  			if (resultCode == Activity.RESULT_OK)
	  			{
	  				
	  					
	  				Log.i(TAG, "from onActivityResult : RESULT_OK");
	  				String oauthVerifier = (String) data.getExtras().get("oauth_verifier");	  				
	  				try {
	  					//new TwitterGetAccessTokenTask().execute(oauthVerifier);
	  					myTwit.prepAccessToken(oauthVerifier);
//	  					if(myTwit.isTwitterOn()){
//		  					showFragment(CAM, false);
//		  				}
//	  					else
//	  						Log.i(TAG, "cant show fragment cam");
//	  					
	  				} catch (Exception e) {
	  					//Error contingency plan
	  					worstCaseScenario();
	  				}
	  			}
	  			 
	  		
	  		}
	    
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	    Log.i(TAG, "from onDestroy.");
	    
	    if(!resumeMe){
	    	fragmentShown = 0;	
	    }
	    resumeMe = false;
	    ancestralVision();
	    
	}
	
	@Override
	public void onStop(){
		super.onStop();
		if(uiHelper != null)
			uiHelper.onStop();
	    Log.i(TAG, "from onStop.");

	    if(!resumeForFB)
	    if(((CamFrag)pieces[CAM]).mCamera != null){
    		Log.i(TAG, "from onStop : Releasing mCamera");
    		((CamFrag)pieces[CAM]).releaseCamera();
	    }
	    
	    if(resumeMe){
	 	    
		    if(((CamFrag)pieces[CAM]).mMediaPlayer != null){
		    	Log.i(TAG, "from onStop : Pausing mMediaPlayer");
		    	((CamFrag)pieces[CAM]).bgMusicUtility("pause");
		    }
	    }
	    else{
	    	if(((CamFrag)pieces[CAM]).mMediaPlayer != null){
	    		Log.i(TAG, "from onStop : Releasing mMediaPlayer");
	    		((CamFrag)pieces[CAM]).bgMusicUtility("release");
	    	}
	    	
	    }
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		bundyDundy.putString("Ai", "Love");
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	    Log.i(TAG, "from onSaveInstanceState.");
	    checkbundyDundy("onSaveInstanceState");
	    
	    resumeMe = true;
	    ancestralVision();
	    
	    
	}
	
	@Override
	 public void onConfigurationChanged(Configuration newConfig) {
		 super.onConfigurationChanged(newConfig);
		 Log.i(TAG, "from onConfigurationChanged.");
		 //linkRESTART();
	 }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	Log.i(TAG, "from onCreateOptionsMenu.");
	 //getMenuInflater().inflate(R.menu.dual_cam, menu);
	 //Toast.makeText(getApplicationContext(), "OptionsMenu is clicked", Field.SHOWTIME).show();
//		 popUpMenu.show(this.getFragmentManager(), "popUpMenu");
	 //customPopUpMenu().show();
//	 if(popUpDialog != null)
//		 popUpDialog.show();
	if(((CamFrag)pieces[CAM]).popUpDialog != null ){
		Log.i(TAG, "popUpDialog is NOT Null.");
		((CamFrag)pieces[CAM]).popUpDialog.show();
	}
	else
		Log.i(TAG, "popUpDialog is Null.");
	
	return false;
	}

	
/**		ACTS	**/	
	
/*	Act of the Magician	*/	
	
	public void doMagic(String magic){
		Toast.makeText(getApplicationContext(), magic, Field.SHOWTIME).show();
	}
	
	public Bundle getBundyDundy(){
		return bundyDundy;
	}
	
	public void setBundyDundy(Bundle bundydundy){
		bundyDundy = bundydundy;
	}
	
	public void worstCaseScenario(){

		Log.i(TAG, "from worstCaseScenario. Fragment shown : "+fragmentShown);
		showFragment(SOCIALMEDIA, false);
	}
	
	public void linkRESTART() {
		
		Intent toSave = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		toSave.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		toSave.putExtra("bundyDundy", bundyDundy);
		finish();
		startActivity(toSave);
	}
	

	private void linkStart(){
		showFragment(SPLASH, false);
		
		//((SocialMediaFragment)pieces[SOCIALMEDIA]).initControlTwitter();
		CountDownTimer splashTime = new CountDownTimer(2000,1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {

			}
			
			@Override
			public void onFinish() {
				
				showFragment(SOCIALMEDIA, false);
			}
		};
		
		splashTime.start();
		
	}

	public void showFragment(int fragmentIndex, boolean addToBackStack) {
		Log.i(TAG, "from showFragment("+fragmentIndex+","+addToBackStack+")");
		fragmentShown = fragmentIndex;
		
		switch(fragmentIndex){
		case SPLASH:
			bundyDundy.putBoolean(Field.splash+Field.isShown, true);
			
			bundyDundy.putBoolean(Field.social+Field.isShown, false);
			bundyDundy.putBoolean(Field.cam+Field.isShown, false);
			break;
			
		case SOCIALMEDIA:
			bundyDundy.putInt("fragmentShown", fragmentShown);
			bundyDundy.putBoolean(Field.social+Field.isShown, true);

			bundyDundy.putBoolean(Field.cam+Field.isShown, false);
			break;
			
		case CAM:

			bundyDundy.putInt("fragmentShown", fragmentShown);
			bundyDundy.putBoolean(Field.cam+Field.isShown, true);
			
			bundyDundy.putBoolean(Field.social
					+Field.isShown, false);
			bundyDundy.putBoolean(Field.splash+Field.isShown, false);
			
//			if(resumeMe){
//				((CamFrag)pieces[CAM]).setResumeInteractions();
//			}
//			else
				((CamFrag)pieces[CAM]).setInteractions();
			
			if(((CamFrag)pieces[CAM]).mMediaPlayer == null)
				((CamFrag)pieces[CAM]).bgMusicUtility("initialize");
			else
				((CamFrag)pieces[CAM]).bgMusicUtility("start");
			
			((CamFrag)pieces[CAM]).popUpDialog = ((CamFrag)pieces[CAM]).customPopUpMenu();
			
//			((CamFrag)pieces[CAM]).bgMusicUtility("initNotPlay");
			break;
			
		case CAMERA:
			bundyDundy.putBoolean(Field.camera+Field.isShown, true);
			
			bundyDundy.putBoolean(Field.social+Field.isShown, false);
			bundyDundy.putBoolean(Field.splash+Field.isShown, false);
			break;
			
		}
		
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction transaction = fm.beginTransaction();
	    for (int i = 0; i < pieces.length; i++) {
	        if (i == fragmentIndex) {
	        	if(loading != null)
	        		loading.dismiss();
	        	
	            transaction.show(pieces[i]);
	            
	            
	        } else {
	            transaction.hide(pieces[i]);
	        }
	    }
	    if (addToBackStack) {
	        transaction.addToBackStack(null);
	    }
	    
	    transaction.commit();
	    
	}
	
	public void ancestralVision(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("fragmentShown", fragmentShown);
        editor.putInt("STATE", STATE);
        editor.putBoolean("resumeMe", resumeMe);
        editor.commit();
	}
	
	public void ancestralRecall(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        fragmentShown = sp.getInt("fragmentShown", Field.defaultState);
        STATE = sp.getInt("STATE", Field.defaultState);
        resumeMe = sp.getBoolean("resumeMe", resumeMe);
	}
	
	public void emptyTheCatacombs(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
	}
	
	
/*	Act of the Overseer	*/	
	public void checkbundyDundy(String from){
		Log.i(TAG, "from "+from+"  bundyDundy Social : "+bundyDundy.getBoolean(Field.social+Field.isShown));
		Log.i(TAG, "from "+from+"  bundyDundy Cam : "+bundyDundy.getBoolean(Field.cam+Field.isShown));
		Log.i(TAG, "from "+from+"  bundyDundy Splash : "+bundyDundy.getBoolean(Field.splash+Field.isShown));
		Log.i(TAG, "from "+from+"  bundyDundy fragmentShown : "+bundyDundy.getInt("fragmentShown"));
		Log.i(TAG, "from "+from+"  bundyDundy Ai : "+bundyDundy.getString("Ai"));
	}
	

	
/*	Act of the Azure Scribe		*/	
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		Log.i(TAG,"from onSessionStateChange.");
	    // Only make changes if the activity is visible
		Toast.makeText(getApplicationContext(),"SessionState = "+state, Field.SHOWTIME).show();
		
	    if (isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        // Get the number of entries in the back stack
	        int backStackSize = manager.getBackStackEntryCount();
	        // Clear the back stack
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        if (state.isOpened()) {
	            // If the session state is open:
	            // Show the authenticated fragment
	            //showFragment(SOCIALMEDIA, false);
	        	if(fragmentShown == SOCIALMEDIA){
	        		showFragment(CAM, false);
	        	}
	    	    
	        } else if (state.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	            //showFragment(SPLASH, false);
	        	//showFragment(CAM, false);
	        	if(fragmentShown != CAM)
	        		showFragment(SOCIALMEDIA, false);
	        }
	    }
	    
	    
	}
	
	public Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, 
				SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	

	
	
/*	Act of the Great Blue Bird	*/	
	
	private void detectIfUserLogInTwitter() {
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.getBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN,false)) {
        	
        	//is Blue Bird logged in
        	isBlueBirdIN = true;
        } else {
        	
        	//is Blue Bird logged out
        	isBlueBirdIN = false;
        }
		
	}
	
	
	class TwitterGetAccessTokenTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String userName) {
            //textViewUserName.setText(Html.fromHtml("<b> Welcome " + userName + "</b>"));
        	showFragment(CAM, false);
        }

        @Override
        protected String doInBackground(String... params) {
        	
            Twitter twitter = TwitterUtil.getInstance().getTwitter();
            RequestToken requestToken = TwitterUtil.getInstance().getRequestToken();
            if (!StringUtil.isNullOrWhitespace(params[0])) {
                try {
                	System.out.println("requestToken -->" + requestToken); 
                	System.out.println("params[0]  -->" + params[0]); 
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN, accessToken.getToken());
                    editor.putString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, accessToken.getTokenSecret());
                    editor.putBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN, true);
                    editor.commit();
                    
                    
                    return twitter.showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } else {
            	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String accessTokenString = sharedPreferences.getString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
                String accessTokenSecret = sharedPreferences.getString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");
                AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                try {
                    TwitterUtil.getInstance().setTwitterFactory(accessToken);
                    return TwitterUtil.getInstance().getTwitter().showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
	
	public void setOrientation(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		int orient = sp.getInt(Field.PREFS_SET_ORIENTATION, Field.MODE_Orientation_DEFAULT);
		switch(orient)
		{
		case Field.MODE_Portrait:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
			
		case Field.MODE_Landscape:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
			
		case Field.MODE_Auto:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			break;
			
		default:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			break;
			
		}
	}

	
}