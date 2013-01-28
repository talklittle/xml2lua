package com.andrewshu.layout2deelang;

import java.util.HashSet;

public class AndroidAttributes {
	
	static final HashSet<String> supported = new HashSet<String>();
	static {
		supported.add("android:id");
		supported.add("android:layout_width");
		supported.add("android:layout_height");
		supported.add("android:layout_weight");
		supported.add("android:layout_gravity");
		supported.add("android:layout_margin");
		supported.add("android:layout_marginLeft");
		supported.add("android:layout_marginTop");
		supported.add("android:layout_marginRight");
		supported.add("android:layout_marginBottom");
		supported.add("android:orientation");
		supported.add("android:minWidth");
		supported.add("android:minHeight");
		supported.add("android:descendantFocusability");
		supported.add("android:background");
		supported.add("android:padding");
		supported.add("android:paddingLeft");
		supported.add("android:paddingTop");
		supported.add("android:paddingRight");
		supported.add("android:paddingBottom");
		supported.add("android:visibility");
		supported.add("android:onClick");
		
		// TextView
		
		supported.add("android:text");
		supported.add("android:textColor");
		supported.add("android:textSize");
		supported.add("android:textStyle");
		supported.add("android:gravity");
		supported.add("android:singleLine");
		supported.add("android:ellipsize");
		
		// ImageView
		
		supported.add("android:src");
		supported.add("android:contentDescription");
	}
	
	static final HashSet<String> supportedByValue = new HashSet<String>();
	static {
		supportedByValue.add("android:textAppearance=\"?android:attr/textAppearanceSmall\"");
		supportedByValue.add("android:textAppearance=\"?android:attr/textAppearanceMedium\"");
		supportedByValue.add("android:textAppearance=\"?android:attr/textAppearanceLarge\"");
		
		supportedByValue.add("style=\"?android:attr/buttonStyleSmall\"");
	}

}
