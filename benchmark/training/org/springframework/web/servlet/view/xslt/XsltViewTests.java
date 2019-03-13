/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.web.servlet.view.xslt;


import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.w3c.dom.Document;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
public class XsltViewTests {
    private static final String HTML_OUTPUT = "/org/springframework/web/servlet/view/xslt/products.xsl";

    private final MockHttpServletRequest request = new MockHttpServletRequest();

    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @Test(expected = IllegalArgumentException.class)
    public void withNoSource() throws Exception {
        final XsltView view = getXsltView(XsltViewTests.HTML_OUTPUT);
        view.render(Collections.emptyMap(), request, response);
    }

    @Test(expected = IllegalArgumentException.class)
    public void withoutUrl() throws Exception {
        final XsltView view = new XsltView();
        view.afterPropertiesSet();
    }

    @Test
    public void simpleTransformWithSource() throws Exception {
        Source source = new StreamSource(getProductDataResource().getInputStream());
        doTestWithModel(Collections.singletonMap("someKey", source));
    }

    @Test
    public void testSimpleTransformWithDocument() throws Exception {
        Document document = getDomDocument();
        doTestWithModel(Collections.singletonMap("someKey", document));
    }

    @Test
    public void testSimpleTransformWithNode() throws Exception {
        Document document = getDomDocument();
        doTestWithModel(Collections.singletonMap("someKey", document.getDocumentElement()));
    }

    @Test
    public void testSimpleTransformWithInputStream() throws Exception {
        doTestWithModel(Collections.singletonMap("someKey", getProductDataResource().getInputStream()));
    }

    @Test
    public void testSimpleTransformWithReader() throws Exception {
        doTestWithModel(Collections.singletonMap("someKey", new InputStreamReader(getProductDataResource().getInputStream())));
    }

    @Test
    public void testSimpleTransformWithResource() throws Exception {
        doTestWithModel(Collections.singletonMap("someKey", getProductDataResource()));
    }

    @Test
    public void testWithSourceKey() throws Exception {
        XsltView view = getXsltView(XsltViewTests.HTML_OUTPUT);
        view.setSourceKey("actualData");
        Map<String, Object> model = new HashMap<>();
        model.put("actualData", getProductDataResource());
        model.put("otherData", new ClassPathResource("dummyData.xsl", getClass()));
        view.render(model, this.request, this.response);
        assertHtmlOutput(this.response.getContentAsString());
    }

    @Test
    public void testContentTypeCarriedFromTemplate() throws Exception {
        XsltView view = getXsltView(XsltViewTests.HTML_OUTPUT);
        Source source = new StreamSource(getProductDataResource().getInputStream());
        view.render(Collections.singletonMap("someKey", source), this.request, this.response);
        Assert.assertTrue(this.response.getContentType().startsWith("text/html"));
        Assert.assertEquals("UTF-8", this.response.getCharacterEncoding());
    }

    @Test
    public void testModelParametersCarriedAcross() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("someKey", getProductDataResource());
        model.put("title", "Product List");
        doTestWithModel(model);
        Assert.assertTrue(this.response.getContentAsString().contains("Product List"));
    }

    @Test
    public void testStaticAttributesCarriedAcross() throws Exception {
        XsltView view = getXsltView(XsltViewTests.HTML_OUTPUT);
        view.setSourceKey("actualData");
        view.addStaticAttribute("title", "Product List");
        Map<String, Object> model = new HashMap<>();
        model.put("actualData", getProductDataResource());
        model.put("otherData", new ClassPathResource("dummyData.xsl", getClass()));
        view.render(model, this.request, this.response);
        assertHtmlOutput(this.response.getContentAsString());
        Assert.assertTrue(this.response.getContentAsString().contains("Product List"));
    }
}

