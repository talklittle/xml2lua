package com.andrewshu.xml2lua;

import java.util.HashSet;

public class Elements {
	
	public static String VIEW = "View";
	public static String TEXT_VIEW = "TextView";
	public static String IMAGE_VIEW = "ImageView";
	public static String PROGRESS_BAR = "ProgressBar";
	public static String BUTTON = "Button";
	public static String IMAGE_BUTTON = "ImageButton";
	
	public static String FRAME_LAYOUT = "FrameLayout";
	public static String DONT_PRESS_WITH_PARENT_FRAME_LAYOUT = "com.andrewshu.android.reddit.layout.DontPressWithParentFrameLayout";
	public static String LINEAR_LAYOUT = "LinearLayout";
	public static String RELATIVE_LAYOUT = "RelativeLayout";
	
	public static final HashSet<String> supported = new HashSet<String>();
	static {
		supported.add(VIEW);
		supported.add(TEXT_VIEW);
		supported.add(IMAGE_VIEW);
		supported.add(PROGRESS_BAR);
		supported.add(BUTTON);
		supported.add(IMAGE_BUTTON);
		
		supported.add(FRAME_LAYOUT);
		supported.add(DONT_PRESS_WITH_PARENT_FRAME_LAYOUT);
		supported.add(LINEAR_LAYOUT);
		supported.add(RELATIVE_LAYOUT);
	}

}
