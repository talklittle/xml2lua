package com.andrewshu.layout2deelang;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import sax.DocumentTracer;

public class Layout2Deelang extends DocumentTracer {

	private static String inputFileBasename;
	
	private Writer output;
	private int elementCount;
	
	private boolean allowBlankLine;

	@Override
	public void startDocument() throws SAXException {
		allowBlankLine = false;
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void startElement(String uri, String localName, String qname, Attributes attributes) throws SAXException {
		if (!Elements.supported.contains(localName)) {
			System.err.println("startElement: Unsupported element: " + localName);
			return;
		}

		elementCount++;
		
		boolean needsIndent;
		
		String viewName = getViewName(attributes);
		if (Elements.VIEW.equals(localName)) {
			printDeelang(String.format(Locale.ENGLISH, "%s = addView(%d)", viewName, elementCount));
			needsIndent = false;
		}
		else if (Elements.TEXT_VIEW.equals(localName)) {
			printDeelang(String.format(Locale.ENGLISH, "%s = addTextView(%d)", viewName, elementCount));
			needsIndent = false;
		}
		else if (Elements.IMAGE_VIEW.equals(localName)) {
			printDeelang(String.format(Locale.ENGLISH, "%s = addImageView(%d)", viewName, elementCount));
			needsIndent = false;
		}
		else if (Elements.PROGRESS_BAR.equals(localName)) {
			printDeelang(String.format(Locale.ENGLISH, "%s = addProgressBar(%d)", viewName, elementCount));
			needsIndent = false;
		}
		else if (Elements.FRAME_LAYOUT.equals(localName)) {
			printDeelang("");
			printDeelang(String.format(Locale.ENGLISH, "%s = beginFrameLayout(%d)", viewName, elementCount));
			needsIndent = true;
		}
		else if (Elements.LINEAR_LAYOUT.equals(localName)) {
			printDeelang("");
			printDeelang(String.format(Locale.ENGLISH, "%s = beginLinearLayout(%d)", viewName, elementCount));
			needsIndent = true;
		}
		else if (Elements.RELATIVE_LAYOUT.equals(localName)) {
			printDeelang("");
			printDeelang(String.format(Locale.ENGLISH, "%s = beginRelativeLayout(%d)", viewName, elementCount));
			needsIndent = true;
		}
		else {
			throw new IllegalStateException("startElement: unhandled element name: " + localName);
		}
		
		printUnsupportedAttributesDeelang(attributes);
		printAttributesDeelang(viewName, attributes);
		
		if (needsIndent)
			fIndent++;
	}
	
	private String getViewName(Attributes attributes) {
		int length = attributes.getLength();
		for (int i = 0; i < length; i++) {
			String namedId = parseId(attributes.getValue("android:id"));
			if (namedId != null)
				return namedId;
		}
		return "view" + elementCount;
	}
	
	private String parseId(String id) {
		if (id != null) {
			return id.substring(id.indexOf('/') + 1);  // "@+id/SOME_NAME"
		}
		return null;
	}

	@Override
	public void endElement(String uri, String localName, String qname) throws SAXException {
		if (!Elements.supported.contains(localName)) {
			System.err.println("endElement: Unsupported element: " + localName);
			return;
		}
		
		if (Elements.FRAME_LAYOUT.equals(localName)) {
			fIndent--;
			printDeelang("end()");
			printDeelang("");
		}
		else if (Elements.LINEAR_LAYOUT.equals(localName)) {
			fIndent--;
			printDeelang("end()");
			printDeelang("");
		}
		else if (Elements.RELATIVE_LAYOUT.equals(localName)) {
			fIndent--;
			printDeelang("end()");
			printDeelang("");
		}
	}

	@Override
	public void startEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		super.startEntity(name);
	}

	@Override
	public void endEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		super.endEntity(name);
	}

	@Override
	public void warning(SAXParseException ex) throws SAXException {
		// TODO Auto-generated method stub
		super.warning(ex);
	}

	@Override
	public void error(SAXParseException ex) throws SAXException {
		// TODO Auto-generated method stub
		super.error(ex);
	}

	@Override
	public void fatalError(SAXParseException ex) throws SAXException {
		// TODO Auto-generated method stub
		super.fatalError(ex);
	}
	
	/**
	 * Print to the Deelang output.
	 */
	protected void printDeelang(String line) {
		try {
			if (output == null) {
				output = new FileWriter(new File(inputFileBasename + "." + System.currentTimeMillis() + ".dl"));
			}
			
			if ("".equals(line)) {
				// disallow printing multiple blank lines in a row. for style reasons only.
				if (!allowBlankLine)
					return;
				
				allowBlankLine = false;
			}
			else {
				allowBlankLine = true;
				
				// Deelang compiler chokes on whitespace-only lines
				for (int i = 0; i < fIndent; i++)
					output.write("    ");
			}
			
			output.write(line + "\n");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Print attributes currently unsupported by layout2deelang.
	 */
	protected void printUnsupportedAttributesDeelang(Attributes attributes) {
		int length = attributes.getLength();
		for (int i = 0; i < length; i++) {
			String qname = attributes.getQName(i);
			String value = attributes.getValue(qname);
			if (!AndroidAttributes.supported.contains(qname) && !AndroidAttributes.supportedByValue.contains(String.format(Locale.ENGLISH, "%s=\"%s\"", qname, value))) {
				printDeelang(String.format(Locale.ENGLISH, "// NOT CURRENTLY SUPPORTED: %s=\"%s\"", qname, attributes.getValue(qname)));
			}
		}
	}
	
	/**
	 * Print attributes associated with the View element.
	 */
	protected void printAttributesDeelang(String viewName, Attributes attributes) {
		//
		// View
		//
		
		// layout_width and layout_height are required; assume present
		String layout_width = attributes.getValue("android:layout_width");
		String layout_height = attributes.getValue("android:layout_height");
		printDeelang(String.format(Locale.ENGLISH, "%s.setLayoutSize(\"%s\", \"%s\")", viewName, layout_width, layout_height));
		
		// layout_weight
		String layout_weight = attributes.getValue("android:layout_weight");
		if (layout_weight != null)
			printDeelang(String.format(Locale.ENGLISH, "%s.setLayoutWeight(%f)", viewName, Float.parseFloat(layout_weight)));
		
		// layout_gravity
		String layout_gravity = attributes.getValue("android:layout_gravity");
		if (layout_gravity != null)
			printDeelang(String.format(Locale.ENGLISH, "%s.setLayoutGravity(\"%s\")", viewName, layout_gravity));
		
		// layout_margin*
		String layout_margin = attributes.getValue("android:layout_margin");
		if (layout_margin != null) {
			printDeelang(String.format(Locale.ENGLISH, "%s.setLayoutMargin(\"%s\")", viewName, layout_margin));
		}
		else {
			String layout_marginLeft   = attributes.getValue("android:layout_marginLeft");
			String layout_marginTop    = attributes.getValue("android:layout_marginTop");
			String layout_marginRight  = attributes.getValue("android:layout_marginRight");
			String layout_marginBottom = attributes.getValue("android:layout_marginBottom");
			if (layout_marginLeft != null && layout_marginTop != null && layout_marginRight != null && layout_marginBottom != null) {
				printDeelang(String.format(
						Locale.ENGLISH, "%s.setLayoutMargins(\"%s\", \"%s\", \"%s\", \"%s\")", viewName, layout_marginLeft, layout_marginTop, layout_marginRight, layout_marginBottom
				));
			}
			else {
				if (layout_marginLeft != null)
					printDeelang(String.format(Locale.ENGLISH, "%s.setLayoutMarginLeft(\"%s\")", viewName, layout_marginLeft));
				if (layout_marginTop != null)
					printDeelang(String.format(Locale.ENGLISH, "%s.setLayoutMarginTop(\"%s\")", viewName, layout_marginTop));
				if (layout_marginRight != null)
					printDeelang(String.format(Locale.ENGLISH, "%s.setLayoutMarginRight(\"%s\")", viewName, layout_marginRight));
				if (layout_marginBottom != null)
					printDeelang(String.format(Locale.ENGLISH, "%s.setLayoutMarginBottom(\"%s\")", viewName, layout_marginBottom));
			}
		}
		
		// orientation
		String orientation = attributes.getValue("android:orientation");
		if (orientation != null)
			printDeelang(String.format(Locale.ENGLISH, "%s.setOrientation(\"%s\")", viewName, orientation));
		
		// minWidth and minHeight
		String minWidth  = attributes.getValue("android:minWidth");
		String minHeight = attributes.getValue("android:minHeight");
		if (minWidth != null)
			printDeelang(String.format(Locale.ENGLISH, "%s.setMinWidth(\"%s\")", viewName, minWidth));
		if (minHeight != null)
			printDeelang(String.format(Locale.ENGLISH, "%s.setMinHeight(\"%s\")", viewName, minHeight));
		
		// descendantFocusability
		String descendantFocusability = attributes.getValue("android:descendantFocusability");
		if (descendantFocusability != null)
			printDeelang(String.format(Locale.ENGLISH, "%s.setDescendantFocusability(\"%s\")", viewName, descendantFocusability));

		// background
//		String background
		
		// padding -- NOTE: must come after background!
		String padding = attributes.getValue("android:padding");
		if (padding != null) {
			printDeelang(String.format(Locale.ENGLISH, "%s.setPadding(\"%s\")", viewName, padding));
		}
		else {
			String paddingLeft   = attributes.getValue("android:paddingLeft");
			String paddingTop    = attributes.getValue("android:paddingTop");
			String paddingRight  = attributes.getValue("android:paddingRight");
			String paddingBottom = attributes.getValue("android:paddingBottom");
			if (paddingLeft != null && paddingTop != null && paddingRight != null && paddingBottom != null) {
				printDeelang(String.format(
						Locale.ENGLISH, "%s.setPadding(\"%s\", \"%s\", \"%s\", \"%s\")", viewName, paddingLeft, paddingTop, paddingRight, paddingBottom
				));
			}
			else {
				if (paddingLeft != null)
					printDeelang(String.format(Locale.ENGLISH, "%s.setPaddingLeft(\"%s\")", viewName, paddingLeft));
				if (paddingTop != null)
					printDeelang(String.format(Locale.ENGLISH, "%s.setPaddingTop(\"%s\")", viewName, paddingTop));
				if (paddingRight != null)
					printDeelang(String.format(Locale.ENGLISH, "%s.setPaddingRight(\"%s\")", viewName, paddingRight));
				if (paddingBottom != null)
					printDeelang(String.format(Locale.ENGLISH, "%s.setPaddingBottom(\"%s\")", viewName, paddingBottom));
			}
		}
		
		// visibility
		String visibility = attributes.getValue("android:visibility");
		if (visibility != null)
			printDeelang(String.format(Locale.ENGLISH, "%s.setVisibility(\"%s\")", viewName, visibility));
		
		//
		// TextView
		//
		
		// TextView: gravity
		String gravity = attributes.getValue("android:gravity");
		if (gravity != null)
			printDeelang(String.format(Locale.ENGLISH, "%s.setGravity(\"%s\")", viewName, gravity));
		
		// TextView: text
		String text = attributes.getValue("android:text");
		if (text != null)
			printDeelang(String.format(Locale.ENGLISH, "%s.setText(\"%s\")", viewName, gravity));
		
		// TextView: textAppearance
		String textAppearance = attributes.getValue("android:textAppearance");
		if ("?android:attr/textAppearanceSmall".equals(textAppearance)) {
			printDeelang(viewName + ".setTextSize(TEXT_SIZE_SMALL)");
			printDeelang(viewName + ".setTextColor(TEXT_COLOR_SECONDARY)");
		}
		else if ("?android:attr/textAppearanceMedium".equals(textAppearance)) {
			printDeelang(viewName + ".setTextSize(TEXT_SIZE_MEDIUM)");
		}
		else if ("?android:attr/textAppearanceLarge".equals(textAppearance)) {
			printDeelang(viewName + ".setTextSize(TEXT_SIZE_LARGE)");
		}
		
		// TextView: textColor
		String textColor = attributes.getValue("android:textColor");
		if (textColor != null)
			printDeelang(String.format(Locale.ENGLISH, "%s.setTextColor(\"%s\")", viewName, textColor));
		
		// TextView: textStyle
		String textStyle = attributes.getValue("android:textStyle");
		if (textStyle != null)
			printDeelang(String.format(Locale.ENGLISH, "%s.setTextStyle(\"%s\")", viewName, textStyle));
		
		// TextView: textSize
		String textSize = attributes.getValue("android:textSize");
		if (textSize != null)
			printDeelang(String.format(Locale.ENGLISH, "%s.setTextSize(\"%s\")", viewName, textSize));
		
		// TextView: singleLine
		String singleLine = attributes.getValue("android:singleLine");
		if ("true".equals(singleLine))
			printDeelang(viewName + ".setSingleLine()");
			
		// printDeelang(String.format(Locale.ENGLISH, "%s.set???(\"%s\")", viewName, ???));
	}
	
	/**
	 * main. see DocumentTracer sample
	 */
	public static void main(String[] argv) throws Exception {
        // is there anything to do?
        if (argv.length == 0) {
            printUsage();
            System.exit(1);
        }

        // variables
        DocumentTracer tracer = new Layout2Deelang();
        XMLReader parser = null;
        boolean namespaces = DEFAULT_NAMESPACES;
        boolean namespacePrefixes = DEFAULT_NAMESPACE_PREFIXES;
        boolean validation = DEFAULT_VALIDATION;
        boolean externalDTD = DEFAULT_LOAD_EXTERNAL_DTD;
        boolean schemaValidation = DEFAULT_SCHEMA_VALIDATION;
        boolean schemaFullChecking = DEFAULT_SCHEMA_FULL_CHECKING;
        boolean honourAllSchemaLocations = DEFAULT_HONOUR_ALL_SCHEMA_LOCATIONS;
        boolean validateAnnotations = DEFAULT_VALIDATE_ANNOTATIONS;
        boolean dynamicValidation = DEFAULT_DYNAMIC_VALIDATION;
        boolean xincludeProcessing = DEFAULT_XINCLUDE;
        boolean xincludeFixupBaseURIs = DEFAULT_XINCLUDE_FIXUP_BASE_URIS;
        boolean xincludeFixupLanguage = DEFAULT_XINCLUDE_FIXUP_LANGUAGE;

        // process arguments
        for (int i = 0; i < argv.length; i++) {
            String arg = argv[i];
            if (arg.startsWith("-")) {
                String option = arg.substring(1);
                if (option.equals("p")) {
                    // get parser name
                    if (++i == argv.length) {
                        System.err.println("error: Missing argument to -p option.");
                    }
                    String parserName = argv[i];

                    // create parser
                    try {
                        parser = XMLReaderFactory.createXMLReader(parserName);
                    }
                    catch (Exception e) {
                        parser = null;
                        System.err.println("error: Unable to instantiate parser ("+parserName+")");
                    }
                    continue;
                }
                if (option.equalsIgnoreCase("n")) {
                    namespaces = option.equals("n");
                    continue;
                }
                if (option.equalsIgnoreCase("np")) {
                    namespacePrefixes = option.equals("np");
                    continue;
                }
                if (option.equalsIgnoreCase("v")) {
                    validation = option.equals("v");
                    continue;
                }
                if (option.equalsIgnoreCase("xd")) {
                    externalDTD = option.equals("xd");
                    continue;
                }
                if (option.equalsIgnoreCase("s")) {
                    schemaValidation = option.equals("s");
                    continue;
                }
                if (option.equalsIgnoreCase("f")) {
                    schemaFullChecking = option.equals("f");
                    continue;
                }
                if (option.equalsIgnoreCase("hs")) {
                    honourAllSchemaLocations = option.equals("hs");
                    continue;
                }
                if (option.equalsIgnoreCase("va")) {
                    validateAnnotations = option.equals("va");
                    continue;
                }
                if (option.equalsIgnoreCase("dv")) {
                    dynamicValidation = option.equals("dv");
                    continue;
                }
                if (option.equalsIgnoreCase("xi")) {
                    xincludeProcessing = option.equals("xi");
                    continue;
                }
                if (option.equalsIgnoreCase("xb")) {
                    xincludeFixupBaseURIs = option.equals("xb");
                    continue;
                }
                if (option.equalsIgnoreCase("xl")) {
                    xincludeFixupLanguage = option.equals("xl");
                    continue;
                }
                if (option.equals("h")) {
                    printUsage();
                    continue;
                }
            }

            // use default parser?
            if (parser == null) {

                // create parser
                try {
                    parser = XMLReaderFactory.createXMLReader(DEFAULT_PARSER_NAME);
                }
                catch (Exception e) {
                    System.err.println("error: Unable to instantiate parser ("+DEFAULT_PARSER_NAME+")");
                    continue;
                }
            }

            // set parser features
            try {
                parser.setFeature(NAMESPACES_FEATURE_ID, namespaces);
            }
            catch (SAXException e) {
                System.err.println("warning: Parser does not support feature ("+NAMESPACES_FEATURE_ID+")");
            }
            try {
                parser.setFeature(NAMESPACE_PREFIXES_FEATURE_ID, namespacePrefixes);
            }
            catch (SAXException e) {
                System.err.println("warning: Parser does not support feature ("+NAMESPACE_PREFIXES_FEATURE_ID+")");
            }
            try {
                parser.setFeature(VALIDATION_FEATURE_ID, validation);
            }
            catch (SAXException e) {
                System.err.println("warning: Parser does not support feature ("+VALIDATION_FEATURE_ID+")");
            }
            try {
                parser.setFeature(LOAD_EXTERNAL_DTD_FEATURE_ID, externalDTD);
            }
            catch (SAXNotRecognizedException e) {
                System.err.println("warning: Parser does not recognize feature ("+LOAD_EXTERNAL_DTD_FEATURE_ID+")");
            }
            catch (SAXNotSupportedException e) {
                System.err.println("warning: Parser does not support feature ("+LOAD_EXTERNAL_DTD_FEATURE_ID+")");
            }
            try {
                parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, schemaValidation);
            }
            catch (SAXNotRecognizedException e) {
                System.err.println("warning: Parser does not recognize feature ("+SCHEMA_VALIDATION_FEATURE_ID+")");
            }
            catch (SAXNotSupportedException e) {
                System.err.println("warning: Parser does not support feature ("+SCHEMA_VALIDATION_FEATURE_ID+")");
            }
            try {
                parser.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, schemaFullChecking);
            }
            catch (SAXNotRecognizedException e) {
                System.err.println("warning: Parser does not recognize feature ("+SCHEMA_FULL_CHECKING_FEATURE_ID+")");
            }
            catch (SAXNotSupportedException e) {
                System.err.println("warning: Parser does not support feature ("+SCHEMA_FULL_CHECKING_FEATURE_ID+")");
            }
            try {
                parser.setFeature(HONOUR_ALL_SCHEMA_LOCATIONS_ID, honourAllSchemaLocations);
            }
            catch (SAXNotRecognizedException e) {
                System.err.println("warning: Parser does not recognize feature ("+HONOUR_ALL_SCHEMA_LOCATIONS_ID+")");
            }
            catch (SAXNotSupportedException e) {
                System.err.println("warning: Parser does not support feature ("+HONOUR_ALL_SCHEMA_LOCATIONS_ID+")");
            }
            try {
                parser.setFeature(VALIDATE_ANNOTATIONS_ID, validateAnnotations);
            }
            catch (SAXNotRecognizedException e) {
                System.err.println("warning: Parser does not recognize feature ("+VALIDATE_ANNOTATIONS_ID+")");
            }
            catch (SAXNotSupportedException e) {
                System.err.println("warning: Parser does not support feature ("+VALIDATE_ANNOTATIONS_ID+")");
            }
            try {
                parser.setFeature(DYNAMIC_VALIDATION_FEATURE_ID, dynamicValidation);
            }
            catch (SAXNotRecognizedException e) {
                System.err.println("warning: Parser does not recognize feature ("+DYNAMIC_VALIDATION_FEATURE_ID+")");
            }
            catch (SAXNotSupportedException e) {
                System.err.println("warning: Parser does not support feature ("+DYNAMIC_VALIDATION_FEATURE_ID+")");
            }
            try {
                parser.setFeature(XINCLUDE_FEATURE_ID, xincludeProcessing);
            }
            catch (SAXNotRecognizedException e) {
                System.err.println("warning: Parser does not recognize feature ("+XINCLUDE_FEATURE_ID+")");
            }
            catch (SAXNotSupportedException e) {
                System.err.println("warning: Parser does not support feature ("+XINCLUDE_FEATURE_ID+")");
            }
            try {
                parser.setFeature(XINCLUDE_FIXUP_BASE_URIS_FEATURE_ID, xincludeFixupBaseURIs);
            }
            catch (SAXNotRecognizedException e) {
                System.err.println("warning: Parser does not recognize feature ("+XINCLUDE_FIXUP_BASE_URIS_FEATURE_ID+")");
            }
            catch (SAXNotSupportedException e) {
                System.err.println("warning: Parser does not support feature ("+XINCLUDE_FIXUP_BASE_URIS_FEATURE_ID+")");
            }
            try {
                parser.setFeature(XINCLUDE_FIXUP_LANGUAGE_FEATURE_ID, xincludeFixupLanguage);
            }
            catch (SAXNotRecognizedException e) {
                System.err.println("warning: Parser does not recognize feature ("+XINCLUDE_FIXUP_LANGUAGE_FEATURE_ID+")");
            }
            catch (SAXNotSupportedException e) {
                System.err.println("warning: Parser does not support feature ("+XINCLUDE_FIXUP_LANGUAGE_FEATURE_ID+")");
            }

            // set handlers
            parser.setDTDHandler(tracer);
            parser.setErrorHandler(tracer);
            if (parser instanceof XMLReader) {
                parser.setContentHandler(tracer);
                try {
                    parser.setProperty("http://xml.org/sax/properties/declaration-handler", tracer);
                }
                catch (SAXException e) {
                    e.printStackTrace(System.err);
                }
                try {
                    parser.setProperty("http://xml.org/sax/properties/lexical-handler", tracer);
                }
                catch (SAXException e) {
                    e.printStackTrace(System.err);
                }
            }

            // get file basename
            if (arg.contains("\\"))
            	inputFileBasename = arg.substring(arg.lastIndexOf('\\') + 1);
            else if (arg.contains("/"))
            	inputFileBasename = arg.substring(arg.lastIndexOf('/') + 1);
            else
            	inputFileBasename = arg;
            
            // parse file
            try {
                parser.parse(arg);
            }
            catch (SAXParseException e) {
                // ignore
            }
            catch (Exception e) {
                System.err.println("error: Parse error occurred - "+e.getMessage());
                if (e instanceof SAXException) {
                    Exception nested = ((SAXException)e).getException();
                    if (nested != null) {
                	   e = nested;
                    }
                }
                e.printStackTrace(System.err);
            }
        }

    } // main(String[])

    //
    // Private static methods
    //

    /** Prints the usage. */
    private static void printUsage() {

        System.err.println("usage: java sax.DocumentTracer (options) uri ...");
        System.err.println();

        System.err.println("options:");
        System.err.println("  -p name     Select parser by name.");
        System.err.println("  -n  | -N    Turn on/off namespace processing.");
        System.err.println("  -np | -NP   Turn on/off namespace prefixes.");
        System.err.println("              NOTE: Requires use of -n.");
        System.err.println("  -v  | -V    Turn on/off validation.");
        System.err.println("  -xd | -XD   Turn on/off loading of external DTDs.");
        System.err.println("              NOTE: Always on when -v in use and not supported by all parsers.");
        System.err.println("  -s  | -S    Turn on/off Schema validation support.");
        System.err.println("              NOTE: Not supported by all parsers.");
        System.err.println("  -f  | -F    Turn on/off Schema full checking.");
        System.err.println("              NOTE: Requires use of -s and not supported by all parsers.");
        System.err.println("  -hs | -HS   Turn on/off honouring of all schema locations.");
        System.err.println("              NOTE: Requires use of -s and not supported by all parsers.");
        System.err.println("  -va | -VA   Turn on/off validation of schema annotations.");
        System.err.println("              NOTE: Requires use of -s and not supported by all parsers.");
        System.err.println("  -dv | -DV   Turn on/off dynamic validation.");
        System.err.println("              NOTE: Not supported by all parsers.");
        System.err.println("  -xi | -XI   Turn on/off XInclude processing.");
        System.err.println("              NOTE: Not supported by all parsers.");
        System.err.println("  -xb | -XB   Turn on/off base URI fixup during XInclude processing.");
        System.err.println("              NOTE: Requires use of -xi and not supported by all parsers.");
        System.err.println("  -xl | -XL   Turn on/off language fixup during XInclude processing.");
        System.err.println("              NOTE: Requires use of -xi and not supported by all parsers.");
        System.err.println("  -h          This help screen.");
        System.err.println();

        System.err.println("defaults:");
        System.err.println("  Parser:     "+DEFAULT_PARSER_NAME);
        System.err.print("  Namespaces: ");
        System.err.println(DEFAULT_NAMESPACES ? "on" : "off");
        System.err.print("  Prefixes:   ");
        System.err.println(DEFAULT_NAMESPACE_PREFIXES ? "on" : "off");
        System.err.print("  Validation: ");
        System.err.println(DEFAULT_VALIDATION ? "on" : "off");
        System.err.print("  Load External DTD: ");
        System.err.println(DEFAULT_LOAD_EXTERNAL_DTD ? "on" : "off");
        System.err.print("  Schema:     ");
        System.err.println(DEFAULT_SCHEMA_VALIDATION ? "on" : "off");
        System.err.print("  Schema full checking:            ");
        System.err.println(DEFAULT_SCHEMA_FULL_CHECKING ? "on" : "off");
        System.err.print("  Honour all schema locations:     ");
        System.err.println(DEFAULT_HONOUR_ALL_SCHEMA_LOCATIONS ? "on" : "off");
        System.err.print("  Validate annotations:            ");
        System.err.println(DEFAULT_VALIDATE_ANNOTATIONS ? "on" : "off");
        System.err.print("  Dynamic:    ");
        System.err.println(DEFAULT_DYNAMIC_VALIDATION ? "on" : "off");
        System.err.print("  XInclude:   ");
        System.err.println(DEFAULT_XINCLUDE ? "on" : "off");
        System.err.print("  XInclude base URI fixup:  ");
        System.err.println(DEFAULT_XINCLUDE_FIXUP_BASE_URIS ? "on" : "off");
        System.err.print("  XInclude language fixup:  ");
        System.err.println(DEFAULT_XINCLUDE_FIXUP_LANGUAGE ? "on" : "off");
	}

}
