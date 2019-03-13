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


import ModifyHTMLElement.ATTRIBUTE_KEY;
import ModifyHTMLElement.CSS_SELECTOR;
import ModifyHTMLElement.ELEMENT_ATTRIBUTE;
import ModifyHTMLElement.ELEMENT_HTML;
import ModifyHTMLElement.ELEMENT_TEXT;
import ModifyHTMLElement.MODIFIED_VALUE;
import ModifyHTMLElement.OUTPUT_TYPE;
import ModifyHTMLElement.REL_INVALID_HTML;
import ModifyHTMLElement.REL_NOT_FOUND;
import ModifyHTMLElement.REL_ORIGINAL;
import ModifyHTMLElement.REL_SUCCESS;
import java.io.File;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;


public class TestModifyHTMLElement extends AbstractHTMLTest {
    private TestRunner testRunner;

    @Test
    public void testModifyText() throws Exception {
        final String MOD_VALUE = "Newly modified value to replace " + (ATL_WEATHER_TEXT);
        testRunner.setProperty(CSS_SELECTOR, ("#" + (ATL_ID)));
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_TEXT);
        testRunner.setProperty(MODIFIED_VALUE, MOD_VALUE);
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertTrue(((ffs.size()) == 1));
        String data = new String(testRunner.getContentAsByteArray(ffs.get(0)));
        // Contents will be the entire HTML doc. So lets use Jsoup again just the grab the element we want.
        Document doc = Jsoup.parse(data);
        Elements eles = doc.select(("#" + (ATL_ID)));
        Element ele = eles.get(0);
        Assert.assertTrue(StringUtils.equals(MOD_VALUE, ele.text()));
    }

    @Test
    public void testModifyHTMLWithExpressionLanguage() throws Exception {
        final String MOD_VALUE = "Newly modified value to replace " + (ATL_WEATHER_TEXT);
        testRunner.setProperty(CSS_SELECTOR, ("#" + (ATL_ID)));
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_TEXT);
        testRunner.setProperty(MODIFIED_VALUE, (("${\" " + MOD_VALUE) + " \":trim()}"));
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertTrue(((ffs.size()) == 1));
        String data = new String(testRunner.getContentAsByteArray(ffs.get(0)));
        // Contents will be the entire HTML doc. So lets use Jsoup again just the grab the element we want.
        Document doc = Jsoup.parse(data);
        Elements eles = doc.select(("#" + (ATL_ID)));
        Element ele = eles.get(0);
        Assert.assertNotNull(ele.text());
    }

    @Test
    public void testModifyHTML() throws Exception {
        final String MOD_VALUE = "Newly modified HTML to replace " + (GDR_WEATHER_TEXT);
        testRunner.setProperty(CSS_SELECTOR, ("#" + (GDR_ID)));
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_HTML);
        testRunner.setProperty(MODIFIED_VALUE, MOD_VALUE);
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertTrue(((ffs.size()) == 1));
        String data = new String(testRunner.getContentAsByteArray(ffs.get(0)));
        // Contents will be the entire HTML doc. So lets use Jsoup again just the grab the element we want.
        Document doc = Jsoup.parse(data);
        Elements eles = doc.select(("#" + (GDR_ID)));
        Element ele = eles.get(0);
        Assert.assertTrue(StringUtils.equals(MOD_VALUE, ele.html()));
    }

    @Test
    public void testModifyAttribute() throws Exception {
        final String MOD_VALUE = "http://localhost/newlink";
        testRunner.setProperty(CSS_SELECTOR, ("#" + (GDR_ID)));
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_ATTRIBUTE);
        testRunner.setProperty(ATTRIBUTE_KEY, "href");
        testRunner.setProperty(MODIFIED_VALUE, MOD_VALUE);
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertTrue(((ffs.size()) == 1));
        String data = new String(testRunner.getContentAsByteArray(ffs.get(0)));
        // Contents will be the entire HTML doc. So lets use Jsoup again just the grab the element we want.
        Document doc = Jsoup.parse(data);
        Elements eles = doc.select(("#" + (GDR_ID)));
        Element ele = eles.get(0);
        Assert.assertTrue(StringUtils.equals(MOD_VALUE, ele.attr("href")));
    }

    @Test
    public void testModifyElementNotFound() throws Exception {
        final String MOD_VALUE = "http://localhost/newlink";
        testRunner.setProperty(CSS_SELECTOR, "b");
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_HTML);
        testRunner.setProperty(MODIFIED_VALUE, MOD_VALUE);
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 0);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 0);
        testRunner.assertTransferCount(REL_NOT_FOUND, 1);
    }

    @Test
    public void testModifyValueContainsHTMLCharacters() throws Exception {
        final String MOD_VALUE = "Text that contains > and < characters";
        testRunner.setProperty(CSS_SELECTOR, ("#" + (GDR_ID)));
        testRunner.setProperty(OUTPUT_TYPE, ELEMENT_HTML);
        testRunner.setProperty(MODIFIED_VALUE, MOD_VALUE);
        testRunner.enqueue(new File("src/test/resources/Weather.html").toPath());
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_INVALID_HTML, 0);
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.assertTransferCount(REL_NOT_FOUND, 0);
        List<MockFlowFile> ffs = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertTrue(((ffs.size()) == 1));
        String data = new String(testRunner.getContentAsByteArray(ffs.get(0)));
        // Contents will be the entire HTML doc. So lets use Jsoup again just the grab the element we want.
        Document doc = Jsoup.parse(data);
        Elements eles = doc.select(("#" + (GDR_ID)));
        Element ele = eles.get(0);
        Assert.assertTrue(StringUtils.equals(MOD_VALUE, ele.text()));
        Assert.assertTrue(StringUtils.equals(MOD_VALUE.replace(">", "&gt;").replace("<", "&lt;"), ele.html()));
    }
}

