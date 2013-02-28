package com.research.gltexture;

import java.util.Calendar;

public class Mood {
	private boolean[] F_D_T = new boolean[3];
	private int filter;
	private String date;
	private String text;
	
	public Mood(int mood, boolean F, boolean D, boolean T){
		//init boolean
		F_D_T[0]=F;
		F_D_T[1]=D;
		F_D_T[2]=T;
		//init filter
		setFilter(mood);
		//init date
		setDate();
		//init text
		setText(mood);
	}
	
	public int getFilter(){
		return filter;
	}
	public String getDate(){
		return date;
	}
	public String getText(){
		return text;
	}
	
	private void setFilter(int mood){
		filter = mood;
	}
	private void setDate(){
		Calendar cal = Calendar.getInstance();
		int d = cal.get(Calendar.DATE);
		int m = cal.get(Calendar.MONTH);
		int y = cal.get(Calendar.YEAR);
		date = String.valueOf(d) + "/" + String.valueOf(m)+"/"+String.valueOf(y);
	}
	private void setText(int mood){
		text = text_string[mood];
	}
	
	final static public int HAPPY = 0;
	final static public int SURPRISE = 1;
	final static public int SAD = 2;
	final static public int EYE = 3;
	final static public int ORZ = 4;
	final static public int FAINT = 5;
	final static public int CRAZY = 6;
	final static public int MAD = 7;
	final static public int LAUGH = 8;
	final static public int KISS = 9;
	
	final private static String[] text_string={
		"so happy~~~ ^_^",
		"scared me!  o_O",
		"i am blue.. T_T",
		"you sure?..",
		"ok,you win! orz",
		"totally lost @_@",
		"ah,ah,crazy!!>.<",
		"mad,. ",
		"lol",
		"kiss me! >3<"	
	};

}
