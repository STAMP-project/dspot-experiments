/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.http;


import MediaType.ALL;
import MediaType.APPLICATION_XHTML_XML;
import MediaType.TEXT_PLAIN;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import static MediaType.ALL;
import static MediaType.QUALITY_VALUE_COMPARATOR;
import static MediaType.SPECIFICITY_COMPARATOR;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 */
public class MediaTypeTests {
    @Test
    public void testToString() throws Exception {
        MediaType mediaType = new MediaType("text", "plain", 0.7);
        String result = mediaType.toString();
        Assert.assertEquals("Invalid toString() returned", "text/plain;q=0.7", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void slashInType() {
        new MediaType("text/plain");
    }

    @Test(expected = IllegalArgumentException.class)
    public void slashInSubtype() {
        new MediaType("text", "/");
    }

    @Test
    public void getDefaultQualityValue() {
        MediaType mediaType = new MediaType("text", "plain");
        Assert.assertEquals("Invalid quality value", 1, mediaType.getQualityValue(), 0.0);
    }

    @Test
    public void parseMediaType() throws Exception {
        String s = "audio/*; q=0.2";
        MediaType mediaType = MediaType.parseMediaType(s);
        Assert.assertEquals("Invalid type", "audio", mediaType.getType());
        Assert.assertEquals("Invalid subtype", "*", mediaType.getSubtype());
        Assert.assertEquals("Invalid quality factor", 0.2, mediaType.getQualityValue(), 0.0);
    }

    @Test(expected = InvalidMediaTypeException.class)
    public void parseMediaTypeNoSubtype() {
        MediaType.parseMediaType("audio");
    }

    @Test(expected = InvalidMediaTypeException.class)
    public void parseMediaTypeNoSubtypeSlash() {
        MediaType.parseMediaType("audio/");
    }

    @Test(expected = InvalidMediaTypeException.class)
    public void parseMediaTypeTypeRange() {
        MediaType.parseMediaType("*/json");
    }

    @Test(expected = InvalidMediaTypeException.class)
    public void parseMediaTypeIllegalType() {
        MediaType.parseMediaType("audio(/basic");
    }

    @Test(expected = InvalidMediaTypeException.class)
    public void parseMediaTypeIllegalSubtype() {
        MediaType.parseMediaType("audio/basic)");
    }

    @Test(expected = InvalidMediaTypeException.class)
    public void parseMediaTypeEmptyParameterAttribute() {
        MediaType.parseMediaType("audio/*;=value");
    }

    @Test(expected = InvalidMediaTypeException.class)
    public void parseMediaTypeEmptyParameterValue() {
        MediaType.parseMediaType("audio/*;attr=");
    }

    @Test(expected = InvalidMediaTypeException.class)
    public void parseMediaTypeIllegalParameterAttribute() {
        MediaType.parseMediaType("audio/*;attr<=value");
    }

    @Test(expected = InvalidMediaTypeException.class)
    public void parseMediaTypeIllegalParameterValue() {
        MediaType.parseMediaType("audio/*;attr=v>alue");
    }

    @Test(expected = InvalidMediaTypeException.class)
    public void parseMediaTypeIllegalQualityFactor() {
        MediaType.parseMediaType("audio/basic;q=1.1");
    }

    @Test(expected = InvalidMediaTypeException.class)
    public void parseMediaTypeIllegalCharset() {
        MediaType.parseMediaType("text/html; charset=foo-bar");
    }

    @Test
    public void parseURLConnectionMediaType() throws Exception {
        String s = "*; q=.2";
        MediaType mediaType = MediaType.parseMediaType(s);
        Assert.assertEquals("Invalid type", "*", mediaType.getType());
        Assert.assertEquals("Invalid subtype", "*", mediaType.getSubtype());
        Assert.assertEquals("Invalid quality factor", 0.2, mediaType.getQualityValue(), 0.0);
    }

    @Test
    public void parseMediaTypes() throws Exception {
        String s = "text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c";
        List<MediaType> mediaTypes = MediaType.parseMediaTypes(s);
        Assert.assertNotNull("No media types returned", mediaTypes);
        Assert.assertEquals("Invalid amount of media types", 4, mediaTypes.size());
        mediaTypes = MediaType.parseMediaTypes("");
        Assert.assertNotNull("No media types returned", mediaTypes);
        Assert.assertEquals("Invalid amount of media types", 0, mediaTypes.size());
    }

    @Test
    public void compareTo() {
        MediaType audioBasic = new MediaType("audio", "basic");
        MediaType audio = new MediaType("audio");
        MediaType audioWave = new MediaType("audio", "wave");
        MediaType audioBasicLevel = new MediaType("audio", "basic", Collections.singletonMap("level", "1"));
        MediaType audioBasic07 = new MediaType("audio", "basic", 0.7);
        // equal
        Assert.assertEquals("Invalid comparison result", 0, audioBasic.compareTo(audioBasic));
        Assert.assertEquals("Invalid comparison result", 0, audio.compareTo(audio));
        Assert.assertEquals("Invalid comparison result", 0, audioBasicLevel.compareTo(audioBasicLevel));
        Assert.assertTrue("Invalid comparison result", ((audioBasicLevel.compareTo(audio)) > 0));
        List<MediaType> expected = new ArrayList<>();
        expected.add(audio);
        expected.add(audioBasic);
        expected.add(audioBasicLevel);
        expected.add(audioBasic07);
        expected.add(audioWave);
        List<MediaType> result = new ArrayList(expected);
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
    public void compareToConsistentWithEquals() {
        MediaType m1 = MediaType.parseMediaType("text/html; q=0.7; charset=iso-8859-1");
        MediaType m2 = MediaType.parseMediaType("text/html; charset=iso-8859-1; q=0.7");
        Assert.assertEquals("Media types not equal", m1, m2);
        Assert.assertEquals("compareTo() not consistent with equals", 0, m1.compareTo(m2));
        Assert.assertEquals("compareTo() not consistent with equals", 0, m2.compareTo(m1));
        m1 = MediaType.parseMediaType("text/html; q=0.7; charset=iso-8859-1");
        m2 = MediaType.parseMediaType("text/html; Q=0.7; charset=iso-8859-1");
        Assert.assertEquals("Media types not equal", m1, m2);
        Assert.assertEquals("compareTo() not consistent with equals", 0, m1.compareTo(m2));
        Assert.assertEquals("compareTo() not consistent with equals", 0, m2.compareTo(m1));
    }

    @Test
    public void compareToCaseSensitivity() {
        MediaType m1 = new MediaType("audio", "basic");
        MediaType m2 = new MediaType("Audio", "Basic");
        Assert.assertEquals("Invalid comparison result", 0, m1.compareTo(m2));
        Assert.assertEquals("Invalid comparison result", 0, m2.compareTo(m1));
        m1 = new MediaType("audio", "basic", Collections.singletonMap("foo", "bar"));
        m2 = new MediaType("audio", "basic", Collections.singletonMap("Foo", "bar"));
        Assert.assertEquals("Invalid comparison result", 0, m1.compareTo(m2));
        Assert.assertEquals("Invalid comparison result", 0, m2.compareTo(m1));
        m1 = new MediaType("audio", "basic", Collections.singletonMap("foo", "bar"));
        m2 = new MediaType("audio", "basic", Collections.singletonMap("foo", "Bar"));
        Assert.assertTrue("Invalid comparison result", ((m1.compareTo(m2)) != 0));
        Assert.assertTrue("Invalid comparison result", ((m2.compareTo(m1)) != 0));
    }

    @Test
    public void specificityComparator() throws Exception {
        MediaType audioBasic = new MediaType("audio", "basic");
        MediaType audioWave = new MediaType("audio", "wave");
        MediaType audio = new MediaType("audio");
        MediaType audio03 = new MediaType("audio", "*", 0.3);
        MediaType audio07 = new MediaType("audio", "*", 0.7);
        MediaType audioBasicLevel = new MediaType("audio", "basic", Collections.singletonMap("level", "1"));
        MediaType textHtml = new MediaType("text", "html");
        MediaType allXml = new MediaType("application", "*+xml");
        MediaType all = ALL;
        Comparator<MediaType> comp = SPECIFICITY_COMPARATOR;
        // equal
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audioBasic, audioBasic));
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audio, audio));
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audio07, audio07));
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audio03, audio03));
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audioBasicLevel, audioBasicLevel));
        // specific to unspecific
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audioBasic, audio)) < 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audioBasic, all)) < 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio, all)) < 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(APPLICATION_XHTML_XML, allXml)) < 0));
        // unspecific to specific
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio, audioBasic)) > 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(allXml, APPLICATION_XHTML_XML)) > 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(all, audioBasic)) > 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(all, audio)) > 0));
        // qualifiers
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio, audio07)) < 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio07, audio)) > 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio07, audio03)) < 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio03, audio07)) > 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio03, all)) < 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(all, audio03)) > 0));
        // other parameters
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audioBasic, audioBasicLevel)) > 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audioBasicLevel, audioBasic)) < 0));
        // different types
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audioBasic, textHtml));
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(textHtml, audioBasic));
        // different subtypes
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audioBasic, audioWave));
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audioWave, audioBasic));
    }

    @Test
    public void sortBySpecificityRelated() {
        MediaType audioBasic = new MediaType("audio", "basic");
        MediaType audio = new MediaType("audio");
        MediaType audio03 = new MediaType("audio", "*", 0.3);
        MediaType audio07 = new MediaType("audio", "*", 0.7);
        MediaType audioBasicLevel = new MediaType("audio", "basic", Collections.singletonMap("level", "1"));
        MediaType all = ALL;
        List<MediaType> expected = new ArrayList<>();
        expected.add(audioBasicLevel);
        expected.add(audioBasic);
        expected.add(audio);
        expected.add(audio07);
        expected.add(audio03);
        expected.add(all);
        List<MediaType> result = new ArrayList(expected);
        Random rnd = new Random();
        // shuffle & sort 10 times
        for (int i = 0; i < 10; i++) {
            Collections.shuffle(result, rnd);
            MediaType.sortBySpecificity(result);
            for (int j = 0; j < (result.size()); j++) {
                Assert.assertSame(("Invalid media type at " + j), expected.get(j), result.get(j));
            }
        }
    }

    @Test
    public void sortBySpecificityUnrelated() {
        MediaType audioBasic = new MediaType("audio", "basic");
        MediaType audioWave = new MediaType("audio", "wave");
        MediaType textHtml = new MediaType("text", "html");
        List<MediaType> expected = new ArrayList<>();
        expected.add(textHtml);
        expected.add(audioBasic);
        expected.add(audioWave);
        List<MediaType> result = new ArrayList(expected);
        MediaType.sortBySpecificity(result);
        for (int i = 0; i < (result.size()); i++) {
            Assert.assertSame(("Invalid media type at " + i), expected.get(i), result.get(i));
        }
    }

    @Test
    public void qualityComparator() throws Exception {
        MediaType audioBasic = new MediaType("audio", "basic");
        MediaType audioWave = new MediaType("audio", "wave");
        MediaType audio = new MediaType("audio");
        MediaType audio03 = new MediaType("audio", "*", 0.3);
        MediaType audio07 = new MediaType("audio", "*", 0.7);
        MediaType audioBasicLevel = new MediaType("audio", "basic", Collections.singletonMap("level", "1"));
        MediaType textHtml = new MediaType("text", "html");
        MediaType allXml = new MediaType("application", "*+xml");
        MediaType all = ALL;
        Comparator<MediaType> comp = QUALITY_VALUE_COMPARATOR;
        // equal
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audioBasic, audioBasic));
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audio, audio));
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audio07, audio07));
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audio03, audio03));
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audioBasicLevel, audioBasicLevel));
        // specific to unspecific
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audioBasic, audio)) < 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audioBasic, all)) < 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio, all)) < 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(APPLICATION_XHTML_XML, allXml)) < 0));
        // unspecific to specific
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio, audioBasic)) > 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(all, audioBasic)) > 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(all, audio)) > 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(allXml, APPLICATION_XHTML_XML)) > 0));
        // qualifiers
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio, audio07)) < 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio07, audio)) > 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio07, audio03)) < 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio03, audio07)) > 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audio03, all)) > 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(all, audio03)) < 0));
        // other parameters
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audioBasic, audioBasicLevel)) > 0));
        Assert.assertTrue("Invalid comparison result", ((comp.compare(audioBasicLevel, audioBasic)) < 0));
        // different types
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audioBasic, textHtml));
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(textHtml, audioBasic));
        // different subtypes
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audioBasic, audioWave));
        Assert.assertEquals("Invalid comparison result", 0, comp.compare(audioWave, audioBasic));
    }

    @Test
    public void sortByQualityRelated() {
        MediaType audioBasic = new MediaType("audio", "basic");
        MediaType audio = new MediaType("audio");
        MediaType audio03 = new MediaType("audio", "*", 0.3);
        MediaType audio07 = new MediaType("audio", "*", 0.7);
        MediaType audioBasicLevel = new MediaType("audio", "basic", Collections.singletonMap("level", "1"));
        MediaType all = ALL;
        List<MediaType> expected = new ArrayList<>();
        expected.add(audioBasicLevel);
        expected.add(audioBasic);
        expected.add(audio);
        expected.add(all);
        expected.add(audio07);
        expected.add(audio03);
        List<MediaType> result = new ArrayList(expected);
        Random rnd = new Random();
        // shuffle & sort 10 times
        for (int i = 0; i < 10; i++) {
            Collections.shuffle(result, rnd);
            MediaType.sortByQualityValue(result);
            for (int j = 0; j < (result.size()); j++) {
                Assert.assertSame(("Invalid media type at " + j), expected.get(j), result.get(j));
            }
        }
    }

    @Test
    public void sortByQualityUnrelated() {
        MediaType audioBasic = new MediaType("audio", "basic");
        MediaType audioWave = new MediaType("audio", "wave");
        MediaType textHtml = new MediaType("text", "html");
        List<MediaType> expected = new ArrayList<>();
        expected.add(textHtml);
        expected.add(audioBasic);
        expected.add(audioWave);
        List<MediaType> result = new ArrayList(expected);
        MediaType.sortBySpecificity(result);
        for (int i = 0; i < (result.size()); i++) {
            Assert.assertSame(("Invalid media type at " + i), expected.get(i), result.get(i));
        }
    }

    @Test
    public void testWithConversionService() {
        ConversionService conversionService = new DefaultConversionService();
        Assert.assertTrue(conversionService.canConvert(String.class, MediaType.class));
        MediaType mediaType = MediaType.parseMediaType("application/xml");
        Assert.assertEquals(mediaType, conversionService.convert("application/xml", MediaType.class));
    }

    @Test
    public void isConcrete() {
        Assert.assertTrue("text/plain not concrete", TEXT_PLAIN.isConcrete());
        Assert.assertFalse("*/* concrete", ALL.isConcrete());
        Assert.assertFalse("text/* concrete", new MediaType("text", "*").isConcrete());
    }
}

