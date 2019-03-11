/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.util;


import java.io.File;
import javax.xml.xpath.XPathExpressionException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.InputSource;


public class XpathUtilsTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File testFile;

    private static final String XML = "<root>\n" + (((("<son>\n" + "<grandson name=\"someone\"/>\n") + "<grandson name=\"anyone\" address=\"\"></grandson>\n") + "</son>\n") + "</root>");

    @Test
    public void shouldEvaluateXpath() throws Exception {
        String xpath = "/root/son/grandson/@name";
        String value = XpathUtils.evaluate(getTestFile(), xpath);
        Assert.assertThat(value, Matchers.is("someone"));
    }

    @Test
    public void shouldEvaluateAnotherXpath() throws Exception {
        String xpath = "//son/grandson[2]/@name";
        String value = XpathUtils.evaluate(getTestFile(), xpath);
        Assert.assertThat(value, Matchers.is("anyone"));
    }

    @Test
    public void shouldEvaluateTextValueXpath() throws Exception {
        String xpath = "//son/grandson[2]/text()";
        String value = XpathUtils.evaluate(getTestFile(), xpath);
        Assert.assertThat(value, Matchers.is(""));
    }

    @Test(expected = XPathExpressionException.class)
    public void shouldThrowExceptionForIllegalXpath() throws Exception {
        XpathUtils.evaluate(getTestFile(), "//");
    }

    @Test
    public void shouldCheckIfNodeExists() throws Exception {
        String attribute = "//son/grandson[@name=\"anyone\"]/@address";
        Assert.assertThat(XpathUtils.evaluate(getTestFile(), attribute), Matchers.is(""));
        Assert.assertThat(XpathUtils.nodeExists(getTestFile(), attribute), Matchers.is(true));
        String textNode = "//son/grandson[2]/text()";
        Assert.assertThat(XpathUtils.nodeExists(getTestFile(), textNode), Matchers.is(false));
    }

    @Test
    public void shouldthrowExceptionForBadXML() {
        String attribute = "//badxpath";
        try {
            XpathUtils.evaluate(getTestFile("NOT XML"), attribute);
            Assert.fail("Should throw exception if xml is valid");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void shouldReturnEmptyStringWhenMatchedNodeIsNotTextNode() throws Exception {
        String xpath = "/root/son";
        String value = XpathUtils.evaluate(getTestFile(), xpath);
        Assert.assertThat(value, Matchers.is(""));
    }

    @Test
    public void shouldParseUTFFilesWithBOM() throws Exception {
        String xpath = "//son/grandson[@name=\"anyone\"]/@address";
        boolean exists = XpathUtils.nodeExists(getTestFileUsingUTFWithBOM(), xpath);
        Assert.assertThat(exists, Matchers.is(true));
    }

    @Test
    public void shouldEvaluateXpathOfCustomer() throws Exception {
        String xpath = "//coverageReport2/project/@coverage";
        File file = new File("../../common/src/test/resources/data/customer/CoverageSummary.xml");
        InputSource inputSource = new InputSource(file.getPath());
        Assert.assertThat(XpathUtils.nodeExists(inputSource, xpath), Matchers.is(true));
        String value = XpathUtils.evaluate(file, xpath);
        Assert.assertThat(value, Matchers.is("27.7730732"));
    }
}

