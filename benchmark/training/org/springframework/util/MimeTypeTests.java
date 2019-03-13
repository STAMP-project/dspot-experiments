/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.util;


import MimeTypeUtils.ALL;
import MimeTypeUtils.APPLICATION_JSON;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import static MimeTypeUtils.TEXT_PLAIN;


/**
 * Unit tests for {@link MimeType}.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Dimitrios Liapis
 */
public class MimeTypeTests {
    @Test(expected = IllegalArgumentException.class)
    public void slashInSubtype() {
        new MimeType("text", "/");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void valueOfNoSubtype() {
        MimeType.valueOf("audio");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void valueOfNoSubtypeSlash() {
        MimeType.valueOf("audio/");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void valueOfIllegalType() {
        MimeType.valueOf("audio(/basic");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void valueOfIllegalSubtype() {
        MimeType.valueOf("audio/basic)");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void valueOfIllegalCharset() {
        MimeType.valueOf("text/html; charset=foo-bar");
    }

    @Test
    public void parseCharset() {
        String s = "text/html; charset=iso-8859-1";
        MimeType mimeType = MimeType.valueOf(s);
        Assert.assertEquals("Invalid type", "text", mimeType.getType());
        Assert.assertEquals("Invalid subtype", "html", mimeType.getSubtype());
        Assert.assertEquals("Invalid charset", StandardCharsets.ISO_8859_1, mimeType.getCharset());
    }

    @Test
    public void parseQuotedCharset() {
        String s = "application/xml;charset=\"utf-8\"";
        MimeType mimeType = MimeType.valueOf(s);
        Assert.assertEquals("Invalid type", "application", mimeType.getType());
        Assert.assertEquals("Invalid subtype", "xml", mimeType.getSubtype());
        Assert.assertEquals("Invalid charset", StandardCharsets.UTF_8, mimeType.getCharset());
    }

    @Test
    public void parseQuotedSeparator() {
        String s = "application/xop+xml;charset=utf-8;type=\"application/soap+xml;action=\\\"http://x.y.z\\\"\"";
        MimeType mimeType = MimeType.valueOf(s);
        Assert.assertEquals("Invalid type", "application", mimeType.getType());
        Assert.assertEquals("Invalid subtype", "xop+xml", mimeType.getSubtype());
        Assert.assertEquals("Invalid charset", StandardCharsets.UTF_8, mimeType.getCharset());
        Assert.assertEquals("\"application/soap+xml;action=\\\"http://x.y.z\\\"\"", mimeType.getParameter("type"));
    }

    @Test
    public void withConversionService() {
        ConversionService conversionService = new DefaultConversionService();
        Assert.assertTrue(conversionService.canConvert(String.class, MimeType.class));
        MimeType mimeType = MimeType.valueOf("application/xml");
        Assert.assertEquals(mimeType, conversionService.convert("application/xml", MimeType.class));
    }

    @Test
    public void includes() {
        MimeType textPlain = TEXT_PLAIN;
        Assert.assertTrue("Equal types is not inclusive", textPlain.includes(textPlain));
        MimeType allText = new MimeType("text");
        Assert.assertTrue("All subtypes is not inclusive", allText.includes(textPlain));
        Assert.assertFalse("All subtypes is inclusive", textPlain.includes(allText));
        Assert.assertTrue("All types is not inclusive", ALL.includes(textPlain));
        Assert.assertFalse("All types is inclusive", textPlain.includes(ALL));
        Assert.assertTrue("All types is not inclusive", ALL.includes(textPlain));
        Assert.assertFalse("All types is inclusive", textPlain.includes(ALL));
        MimeType applicationSoapXml = new MimeType("application", "soap+xml");
        MimeType applicationWildcardXml = new MimeType("application", "*+xml");
        MimeType suffixXml = new MimeType("application", "x.y+z+xml");// SPR-15795

        Assert.assertTrue(applicationSoapXml.includes(applicationSoapXml));
        Assert.assertTrue(applicationWildcardXml.includes(applicationWildcardXml));
        Assert.assertTrue(applicationWildcardXml.includes(suffixXml));
        Assert.assertTrue(applicationWildcardXml.includes(applicationSoapXml));
        Assert.assertFalse(applicationSoapXml.includes(applicationWildcardXml));
        Assert.assertFalse(suffixXml.includes(applicationWildcardXml));
        Assert.assertFalse(applicationWildcardXml.includes(APPLICATION_JSON));
    }

    @Test
    public void isCompatible() {
        MimeType textPlain = TEXT_PLAIN;
        Assert.assertTrue("Equal types is not compatible", textPlain.isCompatibleWith(textPlain));
        MimeType allText = new MimeType("text");
        Assert.assertTrue("All subtypes is not compatible", allText.isCompatibleWith(textPlain));
        Assert.assertTrue("All subtypes is not compatible", textPlain.isCompatibleWith(allText));
        Assert.assertTrue("All types is not compatible", ALL.isCompatibleWith(textPlain));
        Assert.assertTrue("All types is not compatible", textPlain.isCompatibleWith(ALL));
        Assert.assertTrue("All types is not compatible", ALL.isCompatibleWith(textPlain));
        Assert.assertTrue("All types is compatible", textPlain.isCompatibleWith(ALL));
        MimeType applicationSoapXml = new MimeType("application", "soap+xml");
        MimeType applicationWildcardXml = new MimeType("application", "*+xml");
        MimeType suffixXml = new MimeType("application", "x.y+z+xml");// SPR-15795

        Assert.assertTrue(applicationSoapXml.isCompatibleWith(applicationSoapXml));
        Assert.assertTrue(applicationWildcardXml.isCompatibleWith(applicationWildcardXml));
        Assert.assertTrue(applicationWildcardXml.isCompatibleWith(suffixXml));
        Assert.assertTrue(applicationWildcardXml.isCompatibleWith(applicationSoapXml));
        Assert.assertTrue(applicationSoapXml.isCompatibleWith(applicationWildcardXml));
        Assert.assertTrue(suffixXml.isCompatibleWith(applicationWildcardXml));
        Assert.assertFalse(applicationWildcardXml.isCompatibleWith(APPLICATION_JSON));
    }

    @Test
    public void testToString() {
        MimeType mimeType = new MimeType("text", "plain");
        String result = mimeType.toString();
        Assert.assertEquals("Invalid toString() returned", "text/plain", result);
    }

    @Test
    public void parseMimeType() {
        String s = "audio/*";
        MimeType mimeType = MimeTypeUtils.parseMimeType(s);
        Assert.assertEquals("Invalid type", "audio", mimeType.getType());
        Assert.assertEquals("Invalid subtype", "*", mimeType.getSubtype());
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void parseMimeTypeNoSubtype() {
        MimeTypeUtils.parseMimeType("audio");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void parseMimeTypeNoSubtypeSlash() {
        MimeTypeUtils.parseMimeType("audio/");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void parseMimeTypeTypeRange() {
        MimeTypeUtils.parseMimeType("*/json");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void parseMimeTypeIllegalType() {
        MimeTypeUtils.parseMimeType("audio(/basic");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void parseMimeTypeIllegalSubtype() {
        MimeTypeUtils.parseMimeType("audio/basic)");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void parseMimeTypeMissingTypeAndSubtype() {
        MimeTypeUtils.parseMimeType("     ;a=b");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void parseMimeTypeEmptyParameterAttribute() {
        MimeTypeUtils.parseMimeType("audio/*;=value");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void parseMimeTypeEmptyParameterValue() {
        MimeTypeUtils.parseMimeType("audio/*;attr=");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void parseMimeTypeIllegalParameterAttribute() {
        MimeTypeUtils.parseMimeType("audio/*;attr<=value");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void parseMimeTypeIllegalParameterValue() {
        MimeTypeUtils.parseMimeType("audio/*;attr=v>alue");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void parseMimeTypeIllegalCharset() {
        MimeTypeUtils.parseMimeType("text/html; charset=foo-bar");
    }

    // SPR-8917
    @Test
    public void parseMimeTypeQuotedParameterValue() {
        MimeType mimeType = MimeTypeUtils.parseMimeType("audio/*;attr=\"v>alue\"");
        Assert.assertEquals("\"v>alue\"", mimeType.getParameter("attr"));
    }

    // SPR-8917
    @Test
    public void parseMimeTypeSingleQuotedParameterValue() {
        MimeType mimeType = MimeTypeUtils.parseMimeType("audio/*;attr='v>alue'");
        Assert.assertEquals("'v>alue'", mimeType.getParameter("attr"));
    }

    // SPR-16630
    @Test
    public void parseMimeTypeWithSpacesAroundEquals() {
        MimeType mimeType = MimeTypeUtils.parseMimeType("multipart/x-mixed-replace;boundary = --myboundary");
        Assert.assertEquals("--myboundary", mimeType.getParameter("boundary"));
    }

    // SPR-16630
    @Test
    public void parseMimeTypeWithSpacesAroundEqualsAndQuotedValue() {
        MimeType mimeType = MimeTypeUtils.parseMimeType("text/plain; foo = \" bar \" ");
        Assert.assertEquals("\" bar \"", mimeType.getParameter("foo"));
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void parseMimeTypeIllegalQuotedParameterValue() {
        MimeTypeUtils.parseMimeType("audio/*;attr=\"");
    }

    @Test
    public void parseMimeTypes() {
        String s = "text/plain, text/html, text/x-dvi, text/x-c";
        List<MimeType> mimeTypes = MimeTypeUtils.parseMimeTypes(s);
        Assert.assertNotNull("No mime types returned", mimeTypes);
        Assert.assertEquals("Invalid amount of mime types", 4, mimeTypes.size());
        mimeTypes = MimeTypeUtils.parseMimeTypes(null);
        Assert.assertNotNull("No mime types returned", mimeTypes);
        Assert.assertEquals("Invalid amount of mime types", 0, mimeTypes.size());
    }

    // SPR-17459
    @Test
    public void parseMimeTypesWithQuotedParameters() {
        testWithQuotedParameters("foo/bar;param=\",\"");
        testWithQuotedParameters("foo/bar;param=\"s,a,\"");
        testWithQuotedParameters("foo/bar;param=\"s,\"", "text/x-c");
        testWithQuotedParameters("foo/bar;param=\"a\\\"b,c\"");
        testWithQuotedParameters("foo/bar;param=\"\\\\\"");
        testWithQuotedParameters("foo/bar;param=\"\\,\\\"");
    }

    @Test
    public void compareTo() {
        MimeType audioBasic = new MimeType("audio", "basic");
        MimeType audio = new MimeType("audio");
        MimeType audioWave = new MimeType("audio", "wave");
        MimeType audioBasicLevel = new MimeType("audio", "basic", Collections.singletonMap("level", "1"));
        // equal
        Assert.assertEquals("Invalid comparison result", 0, audioBasic.compareTo(audioBasic));
        Assert.assertEquals("Invalid comparison result", 0, audio.compareTo(audio));
        Assert.assertEquals("Invalid comparison result", 0, audioBasicLevel.compareTo(audioBasicLevel));
        Assert.assertTrue("Invalid comparison result", ((audioBasicLevel.compareTo(audio)) > 0));
        List<MimeType> expected = new ArrayList<>();
        expected.add(audio);
        expected.add(audioBasic);
        expected.add(audioBasicLevel);
        expected.add(audioWave);
        List<MimeType> result = new ArrayList(expected);
        Random rnd = new Random();
        // shuffle & sort 10 times
        for (int i = 0; i < 10; i++) {
            Collections.shuffle(result, rnd);
            Collections.sort(result);
            for (int j = 0; j < (result.size()); j++) {
                Assert.assertSame(((("Invalid media type at " + j) + ", run ") + i), expected.get(j), result.get(j));
            }
        }
    }

    @Test
    public void compareToCaseSensitivity() {
        MimeType m1 = new MimeType("audio", "basic");
        MimeType m2 = new MimeType("Audio", "Basic");
        Assert.assertEquals("Invalid comparison result", 0, m1.compareTo(m2));
        Assert.assertEquals("Invalid comparison result", 0, m2.compareTo(m1));
        m1 = new MimeType("audio", "basic", Collections.singletonMap("foo", "bar"));
        m2 = new MimeType("audio", "basic", Collections.singletonMap("Foo", "bar"));
        Assert.assertEquals("Invalid comparison result", 0, m1.compareTo(m2));
        Assert.assertEquals("Invalid comparison result", 0, m2.compareTo(m1));
        m1 = new MimeType("audio", "basic", Collections.singletonMap("foo", "bar"));
        m2 = new MimeType("audio", "basic", Collections.singletonMap("foo", "Bar"));
        Assert.assertTrue("Invalid comparison result", ((m1.compareTo(m2)) != 0));
        Assert.assertTrue("Invalid comparison result", ((m2.compareTo(m1)) != 0));
    }

    /**
     * SPR-13157
     *
     * @since 4.2
     */
    @Test
    public void equalsIsCaseInsensitiveForCharsets() {
        MimeType m1 = new MimeType("text", "plain", Collections.singletonMap("charset", "UTF-8"));
        MimeType m2 = new MimeType("text", "plain", Collections.singletonMap("charset", "utf-8"));
        Assert.assertEquals(m1, m2);
        Assert.assertEquals(m2, m1);
        Assert.assertEquals(0, m1.compareTo(m2));
        Assert.assertEquals(0, m2.compareTo(m1));
    }
}

