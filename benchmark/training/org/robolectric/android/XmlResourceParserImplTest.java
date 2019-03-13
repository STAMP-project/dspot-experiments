package org.robolectric.android;


import XmlResourceParser.END_DOCUMENT;
import XmlResourceParser.END_TAG;
import XmlResourceParser.START_DOCUMENT;
import XmlResourceParser.START_TAG;
import android.app.Application;
import android.content.res.XmlResourceParser;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.RuntimeEnvironment;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import static XmlResourceParserImpl.AVAILABLE_FEATURES;
import static XmlResourceParserImpl.UNAVAILABLE_FEATURES;
import static org.robolectric.R.attr.parentStyleReference;
import static org.robolectric.R.id.tacos;
import static org.robolectric.R.layout.main;
import static org.robolectric.R.xml.has_attribute_resource_value;
import static org.robolectric.R.xml.has_id;
import static org.robolectric.R.xml.has_parent_style_reference;
import static org.robolectric.R.xml.has_style_attribute_reference;


@RunWith(AndroidJUnit4.class)
public class XmlResourceParserImplTest {
    private static final String RES_AUTO_NS = "http://schemas.android.com/apk/res-auto";

    private XmlResourceParser parser;

    private Application context;

    @Test
    public void testGetXmlInt() throws Exception {
        assertThat(parser).isNotNull();
        int evt = parser.next();
        assertThat(evt).isEqualTo(START_DOCUMENT);
    }

    @Test
    public void testGetXmlString() {
        assertThat(parser).isNotNull();
    }

    @Test
    public void testSetFeature() throws Exception {
        for (String feature : AVAILABLE_FEATURES) {
            parser.setFeature(feature, true);
            try {
                parser.setFeature(feature, false);
                Assert.fail((feature + " should be true."));
            } catch (XmlPullParserException ex) {
                // pass
            }
        }
        for (String feature : UNAVAILABLE_FEATURES) {
            try {
                parser.setFeature(feature, false);
                Assert.fail((feature + " should not be true."));
            } catch (XmlPullParserException ex) {
                // pass
            }
            try {
                parser.setFeature(feature, true);
                Assert.fail((feature + " should not be true."));
            } catch (XmlPullParserException ex) {
                // pass
            }
        }
    }

    @Test
    public void testGetFeature() {
        for (String feature : AVAILABLE_FEATURES) {
            assertThat(parser.getFeature(feature)).isTrue();
        }
        for (String feature : UNAVAILABLE_FEATURES) {
            assertThat(parser.getFeature(feature)).isFalse();
        }
        assertThat(parser.getFeature(null)).isFalse();
    }

    @Test
    public void testSetProperty() {
        try {
            parser.setProperty("foo", "bar");
            Assert.fail("Properties should not be supported");
        } catch (XmlPullParserException ex) {
            // pass
        }
    }

    @Test
    public void testGetProperty() {
        // Properties are not supported
        assertThat(parser.getProperty("foo")).isNull();
    }

    @Test
    public void testSetInput_Reader() {
        try {
            parser.setInput(new StringReader(""));
            Assert.fail("This method should not be supported");
        } catch (XmlPullParserException ex) {
            // pass
        }
    }

    @Test
    public void testSetInput_InputStreamString() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("src/test/resources/res/xml/preferences.xml")) {
            parser.setInput(inputStream, "UTF-8");
            Assert.fail("This method should not be supported");
        } catch (XmlPullParserException ex) {
            // pass
        }
    }

    @Test
    public void testDefineEntityReplacementText() {
        try {
            parser.defineEntityReplacementText("foo", "bar");
            Assert.fail("This method should not be supported");
        } catch (XmlPullParserException ex) {
            // pass
        }
    }

    @Test
    public void testGetNamespacePrefix() {
        try {
            parser.getNamespacePrefix(0);
            Assert.fail("This method should not be supported");
        } catch (XmlPullParserException ex) {
            // pass
        }
    }

    @Test
    public void testGetInputEncoding() {
        assertThat(parser.getInputEncoding()).isNull();
    }

    @Test
    public void testGetNamespace_String() {
        try {
            parser.getNamespace("bar");
            Assert.fail("This method should not be supported");
        } catch (RuntimeException ex) {
            // pass
        }
    }

    @Test
    public void testGetNamespaceCount() {
        try {
            parser.getNamespaceCount(0);
            Assert.fail("This method should not be supported");
        } catch (XmlPullParserException ex) {
            // pass
        }
    }

    @Test
    public void testGetNamespaceUri() {
        try {
            parser.getNamespaceUri(0);
            Assert.fail("This method should not be supported");
        } catch (XmlPullParserException ex) {
            // pass
        }
    }

    @Test
    public void testGetColumnNumber() {
        assertThat(parser.getColumnNumber()).isEqualTo((-1));
    }

    @Test
    public void testGetDepth() throws Exception {
        // Recorded depths from preference file elements
        List<Integer> expectedDepths = Arrays.asList(1, 2, 3, 2, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 3);
        List<Integer> actualDepths = new ArrayList<>();
        int evt;
        while ((evt = parser.next()) != (XmlResourceParser.END_DOCUMENT)) {
            switch (evt) {
                case XmlResourceParser.START_TAG :
                    {
                        actualDepths.add(parser.getDepth());
                        break;
                    }
            }
        } 
        assertThat(actualDepths).isEqualTo(expectedDepths);
    }

    @Test
    public void testGetText() throws Exception {
        forgeAndOpenDocument("<foo/>");
        assertThat(parser.getText()).isEqualTo("");
        forgeAndOpenDocument("<foo>bar</foo>");
        assertThat(parser.getText()).isEqualTo("bar");
    }

    @Test
    public void testGetEventType() throws Exception {
        int evt;
        while ((evt = parser.next()) != (XmlResourceParser.END_DOCUMENT)) {
            assertThat(parser.getEventType()).isEqualTo(evt);
        } 
    }

    @Test
    public void testIsWhitespace() throws Exception {
        Assume.assumeTrue(RuntimeEnvironment.useLegacyResources());
        XmlResourceParserImpl parserImpl = ((XmlResourceParserImpl) (parser));
        assertThat(parserImpl.isWhitespace("bar")).isFalse();
        assertThat(parserImpl.isWhitespace(" ")).isTrue();
    }

    @Test
    public void testGetPrefix() {
        try {
            parser.getPrefix();
            Assert.fail("This method should not be supported");
        } catch (RuntimeException ex) {
            // pass
        }
    }

    @Test
    public void testGetNamespace() throws Exception {
        forgeAndOpenDocument("<foo xmlns=\"http://www.w3.org/1999/xhtml\">bar</foo>");
        assertThat(parser.getNamespace()).isEqualTo("http://www.w3.org/1999/xhtml");
    }

    @Test
    public void testGetName_atStart() throws Exception {
        assertThat(parser.getName()).isEqualTo(null);
        parseUntilNext(START_DOCUMENT);
        assertThat(parser.getName()).isEqualTo(null);
        parseUntilNext(START_TAG);
        assertThat(parser.getName()).isEqualTo("PreferenceScreen");
    }

    @Test
    public void testGetName() throws Exception {
        forgeAndOpenDocument("<foo/>");
        assertThat(parser.getName()).isEqualTo("foo");
    }

    @Test
    public void testGetAttribute() throws Exception {
        forgeAndOpenDocument("<foo xmlns:app=\"http://schemas.android.com/apk/res-auto\" app:bar=\"bar\"/>");
        XmlResourceParserImpl parserImpl = ((XmlResourceParserImpl) (parser));
        assertThat(parserImpl.getAttribute(XmlResourceParserImplTest.RES_AUTO_NS, "bar")).isEqualTo("bar");
    }

    @Test
    public void testGetAttributeNamespace() throws Exception {
        forgeAndOpenDocument("<foo xmlns:app=\"http://schemas.android.com/apk/res-auto\" app:bar=\"bar\"/>");
        assertThat(parser.getAttributeNamespace(0)).isEqualTo(XmlResourceParserImplTest.RES_AUTO_NS);
    }

    @Test
    public void testGetAttributeName() throws Exception {
        try {
            parser.getAttributeName(0);
            Assert.fail("Expected exception");
        } catch (IndexOutOfBoundsException expected) {
            // Expected
        }
        forgeAndOpenDocument("<foo bar=\"bar\"/>");
        assertThat(parser.getAttributeName(0)).isEqualTo("bar");
        try {
            parser.getAttributeName(attributeIndexOutOfIndex());
            Assert.fail("Expected exception");
        } catch (IndexOutOfBoundsException expected) {
            // Expected
        }
    }

    @Test
    public void testGetAttributePrefix() throws Exception {
        parseUntilNext(START_TAG);
        try {
            parser.getAttributePrefix(0);
            Assert.fail("This method should not be supported");
        } catch (RuntimeException ex) {
            // pass
        }
    }

    @Test
    public void testIsEmptyElementTag() throws Exception {
        assertThat(parser.isEmptyElementTag()).named("Before START_DOCUMENT should return false.").isEqualTo(false);
        forgeAndOpenDocument("<foo><bar/></foo>");
        assertThat(parser.isEmptyElementTag()).named("Not empty tag should return false.").isEqualTo(false);
        forgeAndOpenDocument("<foo/>");
        assertThat(parser.isEmptyElementTag()).named("In the Android implementation this method always return false.").isEqualTo(false);
    }

    @Test
    public void testGetAttributeCount() throws Exception {
        assertThat(parser.getAttributeCount()).named("When no node is being explored the number of attributes should be -1.").isEqualTo((-1));
        forgeAndOpenDocument("<foo bar=\"bar\"/>");
        assertThat(parser.getAttributeCount()).isEqualTo(1);
    }

    @Test
    public void testGetAttributeValue_Int() throws Exception {
        forgeAndOpenDocument("<foo bar=\"bar\"/>");
        assertThat(parser.getAttributeValue(0)).isEqualTo("bar");
        try {
            parser.getAttributeValue(attributeIndexOutOfIndex());
            Assert.fail();
        } catch (IndexOutOfBoundsException ex) {
            // pass
        }
    }

    @Test
    public void testGetAttributeEscapedValue() throws Exception {
        forgeAndOpenDocument("<foo bar=\"\\\'\"/>");
        assertThat(parser.getAttributeValue(0)).isEqualTo("\'");
    }

    @Test
    public void testGetAttributeEntityValue() throws Exception {
        forgeAndOpenDocument("<foo bar=\"\\u201e&#92;&#34;\"/>");
        assertThat(parser.getAttributeValue(0)).isEqualTo("\u201e\"");
    }

    @Test
    public void testGetNodeTextEscapedValue() throws Exception {
        forgeAndOpenDocument("<foo>\'</foo>");
        assertThat(parser.getText()).isEqualTo("\'");
    }

    @Test
    public void testGetNodeTextEntityValue() throws Exception {
        forgeAndOpenDocument("<foo>\\u201e\\&#34;</foo>");
        assertThat(parser.getText()).isEqualTo("\u201e\"");
    }

    @Test
    public void testGetAttributeType() {
        // Hardcoded to always return CDATA
        assertThat(parser.getAttributeType(attributeIndexOutOfIndex())).isEqualTo("CDATA");
    }

    @Test
    public void testIsAttributeDefault() {
        assertThat(parser.isAttributeDefault(attributeIndexOutOfIndex())).isFalse();
    }

    @Test
    public void testGetAttributeValueStringString() throws Exception {
        forgeAndOpenDocument("<foo xmlns:app=\"http://schemas.android.com/apk/res-auto\" app:bar=\"bar\"/>");
        assertThat(parser.getAttributeValue(XmlResourceParserImplTest.RES_AUTO_NS, "bar")).isEqualTo("bar");
    }

    @Test
    public void testNext() throws Exception {
        // Recorded events while parsing preferences from Android
        List<String> expectedEvents = // PreferenceScreen
        // PreferenceCategory
        // Preference
        // PreferenceScreen
        // Preference
        // Preference
        // CheckBoxPreference
        // EditTextPreference
        // ListPreference
        // Preference
        // RingtonePreference
        // Preference
        Arrays.asList("<xml>", "<", "<", "<", ">", ">", "<", "<", ">", "<", ">", ">", "<", ">", "<", ">", "<", ">", "<", ">", "<", ">", "<", ">", "<", ">", "<", "<", ">", ">", ">", "</xml>");
        List<String> actualEvents = new ArrayList<>();
        int evt;
        do {
            evt = parser.next();
            switch (evt) {
                case XmlPullParser.START_DOCUMENT :
                    actualEvents.add("<xml>");
                    break;
                case XmlPullParser.END_DOCUMENT :
                    actualEvents.add("</xml>");
                    break;
                case XmlPullParser.START_TAG :
                    actualEvents.add("<");
                    break;
                case XmlPullParser.END_TAG :
                    actualEvents.add(">");
                    break;
            }
        } while (evt != (XmlResourceParser.END_DOCUMENT) );
        assertThat(actualEvents).isEqualTo(expectedEvents);
    }

    @Test
    public void testRequire() throws Exception {
        parseUntilNext(START_TAG);
        parser.require(START_TAG, parser.getNamespace(), parser.getName());
        try {
            parser.require(END_TAG, parser.getNamespace(), parser.getName());
            Assert.fail("Require with wrong event should have failed");
        } catch (XmlPullParserException ex) {
            // pass
        }
        try {
            parser.require(START_TAG, "foo", parser.getName());
            Assert.fail("Require with wrong namespace should have failed");
        } catch (XmlPullParserException ex) {
            // pass
        }
        try {
            parser.require(START_TAG, parser.getNamespace(), "foo");
            Assert.fail("Require with wrong tag name should have failed");
        } catch (XmlPullParserException ex) {
            // pass
        }
    }

    @Test
    public void testNextText_noText() throws Exception {
        forgeAndOpenDocument("<foo><bar/></foo>");
        try {
            assertThat(parser.nextText()).isEqualTo(parser.getText());
            Assert.fail("nextText on a document with no text should have failed");
        } catch (XmlPullParserException ex) {
            assertThat(parser.getEventType()).isAnyOf(START_TAG, END_DOCUMENT);
        }
    }

    /**
     * Test that next tag will only return tag events.
     */
    @Test
    public void testNextTag() throws Exception {
        Set<Integer> acceptableTags = new HashSet<>();
        acceptableTags.add(START_TAG);
        acceptableTags.add(END_TAG);
        forgeAndOpenDocument("<foo><bar/><text>message</text></foo>");
        int evt;
        do {
            evt = parser.next();
            Assert.assertTrue(acceptableTags.contains(evt));
        } while ((evt == (XmlResourceParser.END_TAG)) && ("foo".equals(parser.getName())) );
    }

    @Test
    public void testGetAttributeListValue_StringStringStringArrayInt() throws Exception {
        String[] options = new String[]{ "foo", "bar" };
        forgeAndOpenDocument("<foo xmlns:app=\"http://schemas.android.com/apk/res-auto\" app:bar=\"bar\"/>");
        assertThat(parser.getAttributeListValue(XmlResourceParserImplTest.RES_AUTO_NS, "bar", options, 0)).isEqualTo(1);
        forgeAndOpenDocument("<foo xmlns:app=\"http://schemas.android.com/apk/res-auto\" app:bar=\"unexpected\"/>");
        assertThat(parser.getAttributeListValue(XmlResourceParserImplTest.RES_AUTO_NS, "bar", options, 0)).isEqualTo(0);
    }

    @Test
    public void testGetAttributeBooleanValue_StringStringBoolean() throws Exception {
        forgeAndOpenDocument("<foo xmlns:app=\"http://schemas.android.com/apk/res-auto\" app:bar=\"true\"/>");
        assertThat(parser.getAttributeBooleanValue(XmlResourceParserImplTest.RES_AUTO_NS, "bar", false)).isTrue();
        assertThat(parser.getAttributeBooleanValue(XmlResourceParserImplTest.RES_AUTO_NS, "foo", false)).isFalse();
    }

    @Test
    public void testGetAttributeBooleanValue_IntBoolean() throws Exception {
        forgeAndOpenDocument("<foo bar=\"true\"/>");
        assertThat(parser.getAttributeBooleanValue(0, false)).isTrue();
        assertThat(parser.getAttributeBooleanValue(attributeIndexOutOfIndex(), false)).isFalse();
    }

    @Test
    public void testGetAttributeResourceValueIntInt() throws Exception {
        parser = context.getResources().getXml(has_attribute_resource_value);
        parseUntilNext(START_TAG);
        assertThat(parser.getAttributeResourceValue(0, 42)).isEqualTo(main);
    }

    @Test
    public void testGetAttributeResourceValueStringStringInt() throws Exception {
        parser = context.getResources().getXml(has_attribute_resource_value);
        parseUntilNext(START_TAG);
        assertThat(parser.getAttributeResourceValue(XmlResourceParserImplTest.RES_AUTO_NS, "bar", 42)).isEqualTo(main);
        assertThat(parser.getAttributeResourceValue(XmlResourceParserImplTest.RES_AUTO_NS, "foo", 42)).isEqualTo(42);
    }

    @Test
    public void testGetAttributeResourceValueWhenNotAResource() throws Exception {
        forgeAndOpenDocument("<foo xmlns:app=\"http://schemas.android.com/apk/res-auto\" app:bar=\"banana\"/>");
        assertThat(parser.getAttributeResourceValue(XmlResourceParserImplTest.RES_AUTO_NS, "bar", 42)).isEqualTo(42);
    }

    @Test
    public void testGetAttributeIntValue_StringStringInt() throws Exception {
        forgeAndOpenDocument("<foo xmlns:app=\"http://schemas.android.com/apk/res-auto\" app:app=\"http://schemas.android.com/apk/res-auto\" app:bar=\"-12\"/>");
        assertThat(parser.getAttributeIntValue(XmlResourceParserImplTest.RES_AUTO_NS, "bar", 0)).isEqualTo((-12));
        assertThat(parser.getAttributeIntValue(XmlResourceParserImplTest.RES_AUTO_NS, "foo", 0)).isEqualTo(0);
    }

    @Test
    public void testGetAttributeIntValue_IntInt() throws Exception {
        forgeAndOpenDocument("<foo bar=\"-12\"/>");
        assertThat(parser.getAttributeIntValue(0, 0)).isEqualTo((-12));
        assertThat(parser.getAttributeIntValue(attributeIndexOutOfIndex(), 0)).isEqualTo(0);
        forgeAndOpenDocument("<foo bar=\"unexpected\"/>");
        assertThat(parser.getAttributeIntValue(0, 0)).isEqualTo(0);
    }

    @Test
    public void testGetAttributeUnsignedIntValue_StringStringInt() throws Exception {
        forgeAndOpenDocument("<foo xmlns:app=\"http://schemas.android.com/apk/res-auto\" app:bar=\"12\"/>");
        assertThat(parser.getAttributeUnsignedIntValue(XmlResourceParserImplTest.RES_AUTO_NS, "bar", 0)).isEqualTo(12);
        assertThat(parser.getAttributeUnsignedIntValue(XmlResourceParserImplTest.RES_AUTO_NS, "foo", 0)).isEqualTo(0);
        // Negative unsigned int must be
        forgeAndOpenDocument("<foo xmlns:app=\"http://schemas.android.com/apk/res-auto\" app:bar=\"-12\"/>");
        assertThat(parser.getAttributeUnsignedIntValue(XmlResourceParserImplTest.RES_AUTO_NS, "bar", 0)).named("Getting a negative number as unsigned should return the default value.").isEqualTo(0);
    }

    @Test
    public void testGetAttributeUnsignedIntValue_IntInt() throws Exception {
        forgeAndOpenDocument("<foo bar=\"12\"/>");
        assertThat(parser.getAttributeUnsignedIntValue(0, 0)).isEqualTo(12);
        assertThat(parser.getAttributeUnsignedIntValue(attributeIndexOutOfIndex(), 0)).isEqualTo(0);
        // Negative unsigned int must be
        forgeAndOpenDocument("<foo bar=\"-12\"/>");
        assertThat(parser.getAttributeUnsignedIntValue(0, 0)).named("Getting a negative number as unsigned should return the default value.").isEqualTo(0);
    }

    @Test
    public void testGetAttributeFloatValue_StringStringFloat() throws Exception {
        forgeAndOpenDocument("<foo xmlns:app=\"http://schemas.android.com/apk/res-auto\" app:bar=\"12.01\"/>");
        assertThat(parser.getAttributeFloatValue(XmlResourceParserImplTest.RES_AUTO_NS, "bar", 0.0F)).isEqualTo(12.01F);
        assertThat(parser.getAttributeFloatValue(XmlResourceParserImplTest.RES_AUTO_NS, "foo", 0.0F)).isEqualTo(0.0F);
        forgeAndOpenDocument("<foo bar=\"unexpected\"/>");
        assertThat(parser.getAttributeFloatValue(XmlResourceParserImplTest.RES_AUTO_NS, "bar", 0.0F)).isEqualTo(0.0F);
    }

    @Test
    public void testGetAttributeFloatValue_IntFloat() throws Exception {
        forgeAndOpenDocument("<foo bar=\"12.01\"/>");
        assertThat(parser.getAttributeFloatValue(0, 0.0F)).isEqualTo(12.01F);
        assertThat(parser.getAttributeFloatValue(attributeIndexOutOfIndex(), 0.0F)).isEqualTo(0.0F);
        forgeAndOpenDocument("<foo bar=\"unexpected\"/>");
        assertThat(parser.getAttributeFloatValue(0, 0.0F)).isEqualTo(0.0F);
    }

    @Test
    public void testGetAttributeListValue_IntStringArrayInt() throws Exception {
        String[] options = new String[]{ "foo", "bar" };
        forgeAndOpenDocument("<foo xmlns:app=\"http://schemas.android.com/apk/res-auto\" app:bar=\"bar\"/>");
        assertThat(parser.getAttributeListValue(0, options, 0)).isEqualTo(1);
        forgeAndOpenDocument("<foo xmlns:app=\"http://schemas.android.com/apk/res-auto\" app:bar=\"unexpected\"/>");
        assertThat(parser.getAttributeListValue(0, options, 0)).isEqualTo(0);
        assertThat(parser.getAttributeListValue(attributeIndexOutOfIndex(), options, 0)).isEqualTo(0);
    }

    @Test
    public void testGetIdAttribute() throws Exception {
        forgeAndOpenDocument("<foo/>");
        assertThat(parser.getIdAttribute()).isEqualTo(null);
        forgeAndOpenDocument("<foo id=\"bar\"/>");
        assertThat(parser.getIdAttribute()).isEqualTo("bar");
    }

    @Test
    public void testGetClassAttribute() throws Exception {
        forgeAndOpenDocument("<foo/>");
        assertThat(parser.getClassAttribute()).isEqualTo(null);
        forgeAndOpenDocument("<foo class=\"bar\"/>");
        assertThat(parser.getClassAttribute()).isEqualTo("bar");
    }

    @Test
    public void testGetIdAttributeResourceValue_defaultValue() throws Exception {
        assertThat(parser.getIdAttributeResourceValue(12)).isEqualTo(12);
        parser = context.getResources().getXml(has_id);
        parseUntilNext(START_TAG);
        assertThat(parser.getIdAttributeResourceValue(12)).isEqualTo(tacos);
    }

    @Test
    public void testGetStyleAttribute() throws Exception {
        forgeAndOpenDocument("<foo/>");
        assertThat(parser.getStyleAttribute()).isEqualTo(0);
    }

    @Test
    public void getStyleAttribute_allowStyleAttrReference() throws Exception {
        parser = context.getResources().getXml(has_style_attribute_reference);
        parseUntilNext(START_TAG);
        assertThat(parser.getStyleAttribute()).isEqualTo(parentStyleReference);
    }

    @Test
    public void getStyleAttribute_allowStyleAttrReferenceLackingExplicitAttrType() throws Exception {
        parser = context.getResources().getXml(has_parent_style_reference);
        parseUntilNext(START_TAG);
        assertThat(parser.getStyleAttribute()).isEqualTo(parentStyleReference);
    }

    @Test
    public void getStyleAttribute_withMeaninglessString_returnsZero() throws Exception {
        forgeAndOpenDocument("<foo style=\"android:style/whatever\"/>");
        assertThat(parser.getStyleAttribute()).isEqualTo(0);
    }
}

