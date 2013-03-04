package com.research.gltexture;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class MainActivity extends Activity {
	private GLLayer glView;
	private CamLayer mPreview;
	private TextView tv;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        final Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        glView = new GLLayer(this);
        glView.setEGLContextClientVersion(2);
        glView.setRenderer(glView);
        mPreview = new CamLayer(this, glView);
        tv = new TextView(this);
        
        setContentView(mPreview);
        addContentView(glView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        addContentView(tv, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }
    
    public void onResume(){
    	super.onResume();
    	glView.onResume();
    }
    
    public void onPause(){
    	super.onPause();
    	glView.onPause();
    }
    
}
