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


import PutHTMLElement.APPEND_ELEMENT;
import PutHTMLElement.CSS_SELECTOR;
import PutHTMLElement.PREPEND_ELEMENT;
import PutHTMLElement.PUT_LOCATION_TYPE;
import PutHTMLElement.PUT_VALUE;
import PutHTMLElement.REL_INVALID_HTML;
import PutHTMLElement.REL_NOT_FOUND;
import PutHTMLElement.REL_ORIGINAL;
import PutHTMLElement.REL_SUCCESS;
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


public class TestPutHTMLElement extends AbstractHTMLTest {
    private TestRunner testRunner;

    @Test
    public void testAddNewElementToRoot() throws Exception {
        final String MOD_VALUE = "<p>modified value</p>";
        testRunner.setProperty(CSS_SELECTOR, "body");
        testRunner.setProperty(PUT_LOCATION_TYPE, PREPEND_ELEMENT);
        testRunner.setProperty(PUT_VALUE, MOD_VALUE);
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
        Elements eles = doc.select("body > p");
        Element ele = eles.get(0);
        Assert.assertTrue(StringUtils.equals(MOD_VALUE.replace("<p>", "").replace("</p>", ""), ele.html()));
    }

    @Test
    public void testPrependPElementToDiv() throws Exception {
        final String MOD_VALUE = "<p>modified value</p>";
        testRunner.setProperty(CSS_SELECTOR, "#put");
        testRunner.setProperty(PUT_LOCATION_TYPE, PREPEND_ELEMENT);
        testRunner.setProperty(PUT_VALUE, MOD_VALUE);
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
        Elements eles = doc.select("#put");
        Element ele = eles.get(0);
        Assert.assertTrue(StringUtils.equals("<p>modified value</p> \n<a href=\"httpd://localhost\"></a>", ele.html()));
    }

    @Test
    public void testAppendPElementToDiv() throws Exception {
        final String MOD_VALUE = "<p>modified value</p>";
        testRunner.setProperty(CSS_SELECTOR, "#put");
        testRunner.setProperty(PUT_LOCATION_TYPE, APPEND_ELEMENT);
        testRunner.setProperty(PUT_VALUE, MOD_VALUE);
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
        Elements eles = doc.select("#put");
        Element ele = eles.get(0);
        Assert.assertTrue(StringUtils.equals(("<a href=\"httpd://localhost\"></a> \n" + "<p>modified value</p>"), ele.html()));
    }
}

