/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi;


import GetHTMLElement.APPEND_ELEMENT_VALUE;
import GetHTMLElement.ATTRIBUTE_KEY;
import GetHTMLElement.CSS_SELECTOR;
import GetHTMLElement.DESTINATION;
import GetHTMLElement.DESTINATION_ATTRIBUTE;
import GetHTMLElement.DESTINATION_CONTENT;
import GetHTMLElement.ELEMENT_ATTRIBUTE;
import GetHTMLElement.ELEMENT_HTML;
import GetHTMLElement.ELEMENT_TEXT;
import GetHTMLElement.HTML_ELEMENT_ATTRIBUTE_NAME;
import GetHTMLElement.OUTPUT_TYPE;
import GetHTMLElement.PREPEND_ELEMENT_VALUE;
import GetHTMLElement.REL_INVALID_HTML;
import GetHTMLElement.REL_NOT_FOUND;
import GetHTMLElement.REL_ORIGINAL;
import GetHTMLElement.REL_SUCCESS;
import GetHTMLElement.URL;
import Selector.SelectorParseException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;


public class TestGetHTMLElement extends AbstractHTMLTest {
    private TestRunner testRunner;

    @Test(expected = SelectorParseException.class)
    public void testCSSSelectorSyntaxValidator() throws IOException {
        Document doc = Jsoup.parse(new File("src/test/resources/Weather.html"), StandardCharsets.UTF_8.name());
        doc.select("---invalidCssSelector");
    }

    @Test
    public void testNoElementFound() throws Exception {
        testRunner.setProperty(CSS_SELECTOR, "b");// Bold element is not present in sample HTML

        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 0);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_NOT_FOUND, 1);
    }

    @Test
    public void testInvalidSelector() throws Exception {
        testRunner.setProperty(CSS_SELECTOR, "InvalidCSSSelectorSyntax");
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 0);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_NOT_FOUND, 1);
    }

    @Test
    public void testSingleElementFound() throws Exception {
        testRunner.setProperty(CSS_SELECTOR, "head");
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
    }

    @Test
    public void testMultipleElementFound() throws Exception {
        testRunner.setProperty(CSS_SELECTOR, "a");
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 3);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
    }

    @Test
    public void testElementFoundWriteToAttribute() throws Exception {
        testRunner.setProperty(CSS_SELECTOR, ("#" + (ATL_ID)));
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_ATTRIBUTE);
        testRunner.setProperty(ATTRIBUTE_KEY, "href");
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        ffs.get(0).assertAttributeEquals(HTML_ELEMENT_ATTRIBUTE_NAME, ATL_WEATHER_LINK);
    }

    @Test
    public void testElementFoundWriteToContent() throws Exception {
        testRunner.setProperty(CSS_SELECTOR, ("#" + (ATL_ID)));
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_ATTRIBUTE);
        testRunner.setProperty(ATTRIBUTE_KEY, "href");
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        ffs.get(0).assertContentEquals(ATL_WEATHER_LINK);
    }

    @Test
    public void testValidPrependValueToFoundElement() throws Exception {
        final String PREPEND_VALUE = "TestPrepend";
        testRunner.setProperty(PREPEND_ELEMENT_VALUE, PREPEND_VALUE);
        testRunner.setProperty(CSS_SELECTOR, ("#" + (ATL_ID)));
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_ATTRIBUTE);
        testRunner.setProperty(ATTRIBUTE_KEY, "href");
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        ffs.get(0).assertContentEquals((PREPEND_VALUE + (ATL_WEATHER_LINK)));
    }

    @Test
    public void testValidPrependValueToNotFoundElement() throws Exception {
        final String PREPEND_VALUE = "TestPrepend";
        testRunner.setProperty(PREPEND_ELEMENT_VALUE, PREPEND_VALUE);
        testRunner.setProperty(CSS_SELECTOR, "b");
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_TEXT);
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 0);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 0);
        testRunner.assertTransferCount(REL_NOT_FOUND, 1);
    }

    @Test
    public void testValidAppendValueToFoundElement() throws Exception {
        final String APPEND_VALUE = "TestAppend";
        testRunner.setProperty(APPEND_ELEMENT_VALUE, APPEND_VALUE);
        testRunner.setProperty(CSS_SELECTOR, ("#" + (ATL_ID)));
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_ATTRIBUTE);
        testRunner.setProperty(ATTRIBUTE_KEY, "href");
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        ffs.get(0).assertContentEquals(((ATL_WEATHER_LINK) + APPEND_VALUE));
    }

    @Test
    public void testValidAppendValueToNotFoundElement() throws Exception {
        final String APPEND_VALUE = "TestAppend";
        testRunner.setProperty(APPEND_ELEMENT_VALUE, APPEND_VALUE);
        testRunner.setProperty(CSS_SELECTOR, "b");
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_TEXT);
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 0);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 0);
        testRunner.assertTransferCount(REL_NOT_FOUND, 1);
    }

    @Test
    public void testExtractAttributeFromElement() throws Exception {
        testRunner.setProperty(CSS_SELECTOR, "meta[name=author]");
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_ATTRIBUTE);
        testRunner.setProperty(ATTRIBUTE_KEY, "Content");
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        ffs.get(0).assertContentEquals(AUTHOR_NAME);
    }

    @Test
    public void testExtractAttributeFromElementRelativeUrl() throws Exception {
        testRunner.setProperty(CSS_SELECTOR, "script");
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_ATTRIBUTE);
        testRunner.setProperty(ATTRIBUTE_KEY, "src");
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        ffs.get(0).assertContentEquals("js/scripts.js");
    }

    @Test
    public void testExtractAttributeFromElementAbsoluteUrl() throws Exception {
        testRunner.setProperty(CSS_SELECTOR, "script");
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_ATTRIBUTE);
        testRunner.setProperty(ATTRIBUTE_KEY, "abs:src");
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        ffs.get(0).assertContentEquals("http://localhost/js/scripts.js");
    }

    @Test
    public void testExtractAttributeFromElementAbsoluteUrlWithEL() throws Exception {
        testRunner.setProperty(CSS_SELECTOR, "script");
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_ATTRIBUTE);
        testRunner.setProperty(ATTRIBUTE_KEY, "abs:src");
        testRunner.setProperty(URL, "${contentUrl}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("contentUrl", "https://example.com/a/b/c/Weather.html");
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath(), attributes);
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        ffs.get(0).assertContentEquals("https://example.com/a/b/c/js/scripts.js");
    }

    @Test
    public void testExtractAttributeFromElementAbsoluteUrlWithEmptyElResult() throws Exception {
        testRunner.setProperty(CSS_SELECTOR, "script");
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_ATTRIBUTE);
        testRunner.setProperty(ATTRIBUTE_KEY, "abs:src");
        // Expression Language returns empty string because flow-file doesn't have contentUrl attribute.
        testRunner.setProperty(URL, "${contentUrl}");
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 0);
        testRunner.assertTransferCount(REL_INVALID_HTML, 1);
        testRunner.assertTransferCount(REL_ORIGINAL, 0);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
    }

    @Test
    public void testExtractTextFromElement() throws Exception {
        testRunner.setProperty(CSS_SELECTOR, ("#" + (ATL_ID)));
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_TEXT);
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        ffs.get(0).assertContentEquals(ATL_WEATHER_TEXT);
    }

    @Test
    public void testExtractHTMLFromElement() throws Exception {
        testRunner.setProperty(CSS_SELECTOR, ("#" + (GDR_ID)));
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_HTML);
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        ffs.get(0).assertContentEquals(GDR_WEATHER_TEXT);
    }
}

