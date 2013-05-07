package com.andrewshu.xml2lua.drawable;

import java.util.Locale;

import org.xml.sax.Attributes;

public class StateListDrawable2LuaHelper {
	
	private Drawable2Lua drawable2lua;
	
	StateListDrawable2LuaHelper(Drawable2Lua drawable2lua) {
		this.drawable2lua = drawable2lua;
	}
	
	void startElement(String uri, String localName, String qname, Attributes attributes) {
		if ("item".equals(localName)) {
			String stateSetString = getStateSetString(attributes);
			String drawable = attributes.getValue("android:drawable");
			
			printUnsupportedAttributesLua(attributes);
			printFixmeIfNeededLua(drawable);
			drawable2lua.printLua("drawable:addState(\"" + stateSetString + "\", \"" + drawable + "\")");
		}
	}
	
	String getStateSetString(Attributes attributes) {
		StringBuilder builder = new StringBuilder();
		
		boolean first = true;
		int length = attributes.getLength();
		for (int i = 0; i < length; i++) {
			String attributeName = attributes.getLocalName(i);
			if (attributeName.startsWith("state_")) {
				if (!first)
					builder.append("|");
				first = false;
				
				String value = attributes.getValue(i);
				if ("false".equals(value))
					builder.append("-");
				builder.append(attributeName);
			}
		}
		
		return builder.toString();
	}
	
	private void printUnsupportedAttributesLua(Attributes attributes) {
		int length = attributes.getLength();
		for (int i = 0; i < length; i++) {
			String qname = attributes.getQName(i);
//			String value = attributes.getValue(qname);
			if (!"android:drawable".equals(qname) && !qname.startsWith("android:state_")) {
				drawable2lua.printLua(String.format(
						Locale.ENGLISH,
						"-- FIXME: UNSUPPORTED ATTRIBUTE: %s=\"%s\"", qname, attributes.getValue(qname)
				));
			}
		}
	}
	
	private void printFixmeIfNeededLua(String attributeValue) {
		if (attributeValue != null && (attributeValue.startsWith("@") || attributeValue.startsWith("?")))
			drawable2lua.printLua("-- FIXME: replace reference \"" + attributeValue + "\" with value");
	}

}
