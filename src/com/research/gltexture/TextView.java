package com.research.gltexture;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

public class TextView extends View{
	private Paint   mPaint;
    private float   mOriginX = 450;
    private float   mOriginY = 690;
    
	public TextView(Context context){
		super(context);
		setFocusable(true);
		
		 mPaint = new Paint();
         mPaint.setAntiAlias(true);
         mPaint.setStrokeWidth(5);
         mPaint.setStrokeCap(Paint.Cap.ROUND);
         mPaint.setTextSize(24);
         mPaint.setTypeface(Typeface.create(Typeface.MONOSPACE,
                                            Typeface.NORMAL));
	}
	
	@Override 
	protected void onDraw(Canvas canvas){
		mPaint.setColor(Color.GRAY);
		canvas.translate(mOriginX, mOriginY);
		canvas.drawText(getText(), 0, 0, mPaint);
		canvas.translate(70, 30);
        canvas.drawText(getCurrentDate(), 0, 0, mPaint);
	}
	
	private String getCurrentDate(){
		Calendar cal = Calendar.getInstance();
		int date = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		String s_date = String.valueOf(date) + "/" + String.valueOf(month)+"/"+String.valueOf(year);
		
		return s_date;
	}
	
	private String getText(){
		return "i am blue..T.T";
	}
}
