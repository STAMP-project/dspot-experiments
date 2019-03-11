/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript;


import EcmascriptParserOptions.RECORDING_COMMENTS_DESCRIPTOR;
import EcmascriptParserOptions.RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR;
import EcmascriptParserOptions.RHINO_LANGUAGE_VERSION;
import EcmascriptParserOptions.Version.VERSION_DEFAULT;
import Version.VERSION_1_8;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ParserOptionsTest;
import net.sourceforge.pmd.lang.ecmascript.rule.AbstractEcmascriptRule;
import net.sourceforge.pmd.properties.BooleanProperty;
import org.junit.Assert;
import org.junit.Test;

import static EcmascriptParserOptions.RECORDING_COMMENTS_DESCRIPTOR;
import static EcmascriptParserOptions.RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR;


public class EcmascriptParserOptionsTest {
    @Test
    public void testDefaults() throws Exception {
        EcmascriptParserOptions parserOptions = new EcmascriptParserOptions();
        Assert.assertTrue(parserOptions.isRecordingComments());
        Assert.assertTrue(parserOptions.isRecordingLocalJsDocComments());
        Assert.assertEquals(VERSION_DEFAULT, parserOptions.getRhinoLanguageVersion());
        EcmascriptParserOptionsTest.MyRule rule = new EcmascriptParserOptionsTest.MyRule();
        parserOptions = ((EcmascriptParserOptions) (getParserOptions()));
        Assert.assertTrue(parserOptions.isRecordingComments());
        Assert.assertTrue(parserOptions.isRecordingLocalJsDocComments());
        Assert.assertEquals(VERSION_DEFAULT, parserOptions.getRhinoLanguageVersion());
    }

    @Test
    public void testConstructor() throws Exception {
        EcmascriptParserOptionsTest.MyRule rule = new EcmascriptParserOptionsTest.MyRule();
        rule.setProperty(RECORDING_COMMENTS_DESCRIPTOR, true);
        Assert.assertTrue(isRecordingComments());
        rule.setProperty(RECORDING_COMMENTS_DESCRIPTOR, false);
        Assert.assertFalse(isRecordingComments());
        rule.setProperty(RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR, true);
        Assert.assertTrue(isRecordingLocalJsDocComments());
        rule.setProperty(RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR, false);
        Assert.assertFalse(isRecordingLocalJsDocComments());
        rule.setProperty(RHINO_LANGUAGE_VERSION, Version.VERSION_DEFAULT);
        Assert.assertEquals(VERSION_DEFAULT, getRhinoLanguageVersion());
        rule.setProperty(RHINO_LANGUAGE_VERSION, VERSION_1_8);
        Assert.assertEquals(EcmascriptParserOptions.Version.VERSION_1_8, getRhinoLanguageVersion());
    }

    @Test
    public void testSetters() {
        EcmascriptParserOptions options = new EcmascriptParserOptions();
        options.setSuppressMarker("foo");
        Assert.assertEquals("foo", options.getSuppressMarker());
        options.setSuppressMarker(null);
        Assert.assertNull(options.getSuppressMarker());
    }

    @Test
    public void testEqualsHashcode() throws Exception {
        BooleanProperty[] properties = new BooleanProperty[]{ RECORDING_COMMENTS_DESCRIPTOR, RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR };
        for (int i = 0; i < (properties.length); i++) {
            BooleanProperty property = properties[i];
            EcmascriptParserOptionsTest.MyRule rule = new EcmascriptParserOptionsTest.MyRule();
            rule.setProperty(property, true);
            ParserOptions options1 = rule.getParserOptions();
            rule.setProperty(property, false);
            ParserOptions options2 = rule.getParserOptions();
            rule.setProperty(property, true);
            ParserOptions options3 = rule.getParserOptions();
            rule.setProperty(property, false);
            ParserOptions options4 = rule.getParserOptions();
            ParserOptionsTest.verifyOptionsEqualsHashcode(options1, options2, options3, options4);
        }
        EcmascriptParserOptions options1 = new EcmascriptParserOptions();
        options1.setSuppressMarker("foo");
        EcmascriptParserOptions options2 = new EcmascriptParserOptions();
        options2.setSuppressMarker("bar");
        EcmascriptParserOptions options3 = new EcmascriptParserOptions();
        options3.setSuppressMarker("foo");
        EcmascriptParserOptions options4 = new EcmascriptParserOptions();
        options4.setSuppressMarker("bar");
        ParserOptionsTest.verifyOptionsEqualsHashcode(options1, options2, options3, options4);
        options1 = new EcmascriptParserOptions();
        options1.setRhinoLanguageVersion(VERSION_DEFAULT);
        options2 = new EcmascriptParserOptions();
        options2.setRhinoLanguageVersion(EcmascriptParserOptions.Version.VERSION_1_8);
        options3 = new EcmascriptParserOptions();
        options3.setRhinoLanguageVersion(VERSION_DEFAULT);
        options4 = new EcmascriptParserOptions();
        options4.setRhinoLanguageVersion(EcmascriptParserOptions.Version.VERSION_1_8);
        ParserOptionsTest.verifyOptionsEqualsHashcode(options1, options2, options3, options4);
    }

    private static final class MyRule extends AbstractEcmascriptRule {}
}

