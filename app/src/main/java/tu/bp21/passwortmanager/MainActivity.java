package tu.bp21.passwortmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import tu.bp21.passwortmanager.DB.PasswordDatabase;
import tu.bp21.passwortmanager.DB.PasswortDao;

public class MainActivity extends AppCompatActivity {
	private WebView webView;

	@Override
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try{
			this.getSupportActionBar().hide();
		} catch (NullPointerException e) {}

		//if your build is in debug mode, enable webviews inspection
		if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
			WebView.setWebContentsDebuggingEnabled(true);
		}

		PasswordDatabase database = Room.databaseBuilder(getApplicationContext(),
				PasswordDatabase.class, "database").build();

		webView = new WebView(getApplicationContext());
		webView.setWebViewClient(new WebViewClient());
		webView.addJavascriptInterface(new JavascriptHandler(database.getDao()),"Java");
		webView.loadUrl("file:///android_asset/index.html");

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAllowFileAccess(true);
		

		setContentView(webView);
	}

	@Override
	public void onBackPressed() {
		if(webView.canGoBack()){
			webView.goBack();
		} else {
			super.onBackPressed();
		}
	}
}