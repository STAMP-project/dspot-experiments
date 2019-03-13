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
package org.apache.camel.component.exec;


import java.io.File;
import java.io.InputStream;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.w3c.dom.Document;


@ContextConfiguration(locations = { "exec-mock-executor-context.xml" })
public class ExecOutFileTest extends AbstractJUnit4SpringContextTests {
    private static final String FILE_CONTENT = ExecOutFileTest.buildFileContent();

    private static final File FILE = new File("target/outfiletest.xml");

    @Produce(uri = "direct:input")
    private ProducerTemplate producerTemplate;

    @Test
    @DirtiesContext
    public void testOutFile() throws Exception {
        Exchange e = sendWithMockedExecutor();
        ExecResult result = e.getIn().getBody(ExecResult.class);
        Assert.assertNotNull(result);
        File outFile = result.getCommand().getOutFile();
        Assert.assertNotNull(outFile);
        Assert.assertEquals(ExecOutFileTest.FILE_CONTENT, FileUtils.readFileToString(outFile));
    }

    @Test
    @DirtiesContext
    public void testOutFileConvertToInputStream() throws Exception {
        Exchange e = sendWithMockedExecutor();
        InputStream body = e.getIn().getBody(InputStream.class);
        Assert.assertNotNull(body);
        Assert.assertEquals(ExecOutFileTest.FILE_CONTENT, IOUtils.toString(body));
    }

    @Test
    @DirtiesContext
    public void testOutFileConvertToDocument() throws Exception {
        Exchange e = sendWithMockedExecutor();
        Document body = e.getIn().getBody(Document.class);
        Assert.assertNotNull(body);// do not parse it

    }

    @Test
    @DirtiesContext
    public void testOutFileConvertToString() throws Exception {
        Exchange e = sendWithMockedExecutor();
        Assert.assertEquals(ExecOutFileTest.FILE_CONTENT, e.getIn().getBody(String.class));
    }

    @Test
    @DirtiesContext
    public void testOutFileConvertToByteArray() throws Exception {
        Exchange e = sendWithMockedExecutor();
        byte[] body = e.getIn().getBody(byte[].class);
        Assert.assertEquals(ExecOutFileTest.FILE_CONTENT, new String(body));
    }
}

