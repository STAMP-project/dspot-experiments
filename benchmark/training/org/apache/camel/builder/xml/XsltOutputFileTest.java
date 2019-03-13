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
package org.apache.camel.builder.xml;


import Exchange.XSLT_FILE_NAME;
import java.io.File;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.NoSuchHeaderException;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class XsltOutputFileTest extends ContextTestSupport {
    @Test
    public void testXsltOutputFile() throws Exception {
        TestSupport.createDirectory("target/data/xslt");
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("<?xml version=\"1.0\" encoding=\"UTF-8\"?><goodbye>world!</goodbye>");
        mock.expectedFileExists("target/data/xslt/xsltme.xml");
        mock.message(0).body().isInstanceOf(File.class);
        template.sendBodyAndHeader("direct:start", "<hello>world!</hello>", XSLT_FILE_NAME, "target/data/xslt/xsltme.xml");
        mock.assertIsSatisfied();
    }

    @Test
    public void testXsltOutputFileMissingHeader() throws Exception {
        try {
            template.sendBody("direct:start", "<hello>world!</hello>");
            Assert.fail("Should thrown exception");
        } catch (CamelExecutionException e) {
            NoSuchHeaderException nshe = TestSupport.assertIsInstanceOf(NoSuchHeaderException.class, e.getCause());
            Assert.assertEquals(XSLT_FILE_NAME, nshe.getHeaderName());
        }
    }
}

