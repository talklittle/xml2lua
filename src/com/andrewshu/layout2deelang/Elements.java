package com.andrewshu.layout2deelang;

import java.util.HashSet;

public class Elements {
	
	static String VIEW = "View";
	static String TEXT_VIEW = "TextView";
	static String IMAGE_VIEW = "ImageView";
	static String PROGRESS_BAR = "ProgressBar";
	
	static String FRAME_LAYOUT = "FrameLayout";
	static String LINEAR_LAYOUT = "LinearLayout";
	static String RELATIVE_LAYOUT = "RelativeLayout";
	
	static final HashSet<String> supported = new HashSet<String>();
	static {
		supported.add(VIEW);
		supported.add(TEXT_VIEW);
		supported.add(IMAGE_VIEW);
		supported.add(PROGRESS_BAR);
		supported.add(FRAME_LAYOUT);
		supported.add(LINEAR_LAYOUT);
		supported.add(RELATIVE_LAYOUT);
	}

}