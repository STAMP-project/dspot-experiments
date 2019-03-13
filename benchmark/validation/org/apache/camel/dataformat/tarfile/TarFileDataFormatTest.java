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
package org.apache.camel.dataformat.tarfile;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.ObjectHelper;
import org.junit.Test;


/**
 * Unit tests for {@link TarFileDataFormat}.
 */
public class TarFileDataFormatTest extends CamelTestSupport {
    private static final File TEST_DIR = new File("target/tar");

    private TarFileDataFormat tar;

    @Test
    public void testTarWithoutFileName() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:tar");
        mock.expectedMessageCount(1);
        template.sendBody("direct:tar", TarUtils.TEXT);
        assertMockEndpointsSatisfied();
        Exchange exchange = mock.getReceivedExchanges().get(0);
        assertEquals(((exchange.getIn().getMessageId()) + ".tar"), exchange.getIn().getHeader(Exchange.FILE_NAME));
        assertTrue(ObjectHelper.equalByteArray(TarUtils.getTaredText(exchange.getIn().getMessageId()), ((byte[]) (exchange.getIn().getBody()))));
    }

    @Test
    public void testTarWithFileName() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:tar");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(Exchange.FILE_NAME, "poem.txt.tar");
        template.sendBodyAndHeader("direct:tar", TarUtils.TEXT, Exchange.FILE_NAME, "poem.txt");
        assertMockEndpointsSatisfied();
        Exchange exchange = mock.getReceivedExchanges().get(0);
        assertTrue(ObjectHelper.equalByteArray(TarUtils.getTaredText("poem.txt"), ((byte[]) (exchange.getIn().getBody()))));
    }

    @Test
    public void testTarWithPathElements() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:tar");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(Exchange.FILE_NAME, "poem.txt.tar");
        template.sendBodyAndHeader("direct:tar", TarUtils.TEXT, Exchange.FILE_NAME, "poems/poem.txt");
        assertMockEndpointsSatisfied();
        Exchange exchange = mock.getReceivedExchanges().get(0);
        assertTrue(ObjectHelper.equalByteArray(TarUtils.getTaredText("poem.txt"), ((byte[]) (exchange.getIn().getBody()))));
    }

    @Test
    public void testTarWithPreservedPathElements() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:tar");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(Exchange.FILE_NAME, "poem.txt.tar");
        tar.setPreservePathElements(true);
        template.sendBodyAndHeader("direct:tar", TarUtils.TEXT, Exchange.FILE_NAME, "poems/poem.txt");
        assertMockEndpointsSatisfied();
        Exchange exchange = mock.getReceivedExchanges().get(0);
        assertTrue(ObjectHelper.equalByteArray(TarUtils.getTaredTextInFolder("poems/", "poems/poem.txt"), ((byte[]) (exchange.getIn().getBody()))));
    }

    @Test
    public void testUntar() throws Exception {
        getMockEndpoint("mock:untar").expectedBodiesReceived(TarUtils.TEXT);
        getMockEndpoint("mock:untar").expectedHeaderReceived(Exchange.FILE_NAME, "file");
        template.sendBody("direct:untar", TarUtils.getTaredText("file"));
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTarAndUntar() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:tarAndUntar");
        mock.expectedMessageCount(1);
        template.sendBody("direct:tarAndUntar", TarUtils.TEXT);
        assertMockEndpointsSatisfied();
        Exchange exchange = mock.getReceivedExchanges().get(0);
        assertEquals(exchange.getIn().getMessageId(), exchange.getIn().getHeader(Exchange.FILE_NAME));
        assertEquals(TarUtils.TEXT, new String(((byte[]) (exchange.getIn().getBody())), "UTF-8"));
    }

    @Test
    public void testTarToFileWithoutFileName() throws Exception {
        NotifyBuilder notify = whenDone(1).create();
        String[] files = TarFileDataFormatTest.TEST_DIR.list();
        assertTrue(((files == null) || ((files.length) == 0)));
        MockEndpoint mock = getMockEndpoint("mock:intercepted");
        mock.expectedMessageCount(1);
        template.sendBody("direct:tarToFile", TarUtils.TEXT);
        assertMockEndpointsSatisfied();
        // use builder to ensure the exchange is fully done before we check for file exists
        assertTrue(notify.matches(5, TimeUnit.SECONDS));
        Exchange exchange = mock.getReceivedExchanges().get(0);
        File file = new File(TarFileDataFormatTest.TEST_DIR, ((exchange.getIn().getMessageId()) + ".tar"));
        assertTrue(file.exists());
        assertTrue(ObjectHelper.equalByteArray(TarUtils.getTaredText(exchange.getIn().getMessageId()), TarUtils.getBytes(file)));
    }

    @Test
    public void testTarToFileWithFileName() throws Exception {
        NotifyBuilder notify = whenDone(1).create();
        MockEndpoint mock = getMockEndpoint("mock:tarToFile");
        mock.expectedMessageCount(1);
        File file = new File(TarFileDataFormatTest.TEST_DIR, "poem.txt.tar");
        assertFalse(file.exists());
        template.sendBodyAndHeader("direct:tarToFile", TarUtils.TEXT, Exchange.FILE_NAME, "poem.txt");
        // just make sure the file is created
        mock.assertIsSatisfied();
        // use builder to ensure the exchange is fully done before we check for file exists
        assertTrue(notify.matches(5, TimeUnit.SECONDS));
        assertTrue(file.exists());
        assertTrue(ObjectHelper.equalByteArray(TarUtils.getTaredText("poem.txt"), TarUtils.getBytes(file)));
    }

    @Test
    public void testDslTar() throws Exception {
        getMockEndpoint("mock:dslTar").expectedBodiesReceived(TarUtils.getTaredText("poem.txt"));
        getMockEndpoint("mock:dslTar").expectedHeaderReceived(Exchange.FILE_NAME, "poem.txt.tar");
        template.sendBodyAndHeader("direct:dslTar", TarUtils.TEXT, Exchange.FILE_NAME, "poem.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testDslUntar() throws Exception {
        getMockEndpoint("mock:dslUntar").expectedBodiesReceived(TarUtils.TEXT);
        getMockEndpoint("mock:dslUntar").expectedHeaderReceived(Exchange.FILE_NAME, "test.txt");
        template.sendBody("direct:dslUntar", TarUtils.getTaredText("test.txt"));
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testUntarWithEmptyDirectorySupported() throws Exception {
        deleteDirectory(new File("hello_out"));
        tar.setUsingIterator(true);
        tar.setAllowEmptyDirectory(true);
        template.sendBody("direct:untarWithEmptyDirectory", new File("src/test/resources/data/hello.tar"));
        assertTrue(Files.exists(Paths.get("hello_out/Configurations2")));
        deleteDirectory(new File("hello_out"));
    }

    @Test
    public void testUntarWithEmptyDirectoryUnsupported() throws Exception {
        deleteDirectory(new File("hello_out"));
        tar.setUsingIterator(true);
        tar.setAllowEmptyDirectory(false);
        template.sendBody("direct:untarWithEmptyDirectory", new File("src/test/resources/data/hello.tar"));
        assertTrue((!(Files.exists(Paths.get("hello_out/Configurations2")))));
        deleteDirectory(new File("hello_out"));
    }
}

