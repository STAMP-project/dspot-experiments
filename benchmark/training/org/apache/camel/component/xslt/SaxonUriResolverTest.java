/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.xslt;


import java.io.StringWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import net.sf.saxon.TransformerFactoryImpl;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class SaxonUriResolverTest extends CamelTestSupport {
    private static final String XSL_PATH = "org/apache/camel/component/xslt/transform_includes_data.xsl";

    private static final String XML_DATA = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root>1</root>";

    private static final String XML_RESP = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><MyDate>February</MyDate>";

    @Test
    public void test() throws Exception {
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        Source xsl = fromClasspath(SaxonUriResolverTest.XSL_PATH);
        xsl.setSystemId(("classpath:/" + (SaxonUriResolverTest.XSL_PATH)));
        Source xml = fromString(SaxonUriResolverTest.XML_DATA);
        TransformerFactory factory = new TransformerFactoryImpl();
        Transformer transformer = factory.newTransformer(xsl);
        transformer.setURIResolver(new XsltUriResolver(context(), SaxonUriResolverTest.XSL_PATH));
        transformer.transform(xml, result);
        Assert.assertEquals(SaxonUriResolverTest.XML_RESP, writer.toString());
    }
}

