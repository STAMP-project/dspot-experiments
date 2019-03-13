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
package org.apache.camel.dataformat.zipfile;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.converter.stream.InputStreamCache;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Unit tests for {@link ZipFileDataFormat}.
 */
public class ZipFileDataFormatTest extends CamelTestSupport {
    private static final String TEXT = "The Masque of Queen Bersabe (excerpt) \n" + (((((((("by: Algernon Charles Swinburne \n\n" + "My lips kissed dumb the word of Ah \n") + "Sighed on strange lips grown sick thereby. \n") + "God wrought to me my royal bed; \n") + "The inner work thereof was red, \n") + "The outer work was ivory. \n") + "My mouth\'s heat was the heat of flame \n") + "For lust towards the kings that came \n") + "With horsemen riding royally.");

    private static final File TEST_DIR = new File("target/zip");

    private ZipFileDataFormat zip;

    @Test
    public void testZipAndStreamCaching() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:zipStreamCache");
        mock.setExpectedMessageCount(1);
        template.sendBody("direct:zipStreamCache", ZipFileDataFormatTest.TEXT);
        assertMockEndpointsSatisfied();
        Exchange exchange = mock.getReceivedExchanges().get(0);
        assertEquals(((exchange.getIn().getMessageId()) + ".zip"), exchange.getIn().getHeader(Exchange.FILE_NAME));
        assertIsInstanceOf(InputStreamCache.class, exchange.getIn().getBody());
        assertArrayEquals(ZipFileDataFormatTest.getZippedText(exchange.getIn().getMessageId()), exchange.getIn().getMandatoryBody(byte[].class));
    }

    @Test
    public void testZipWithoutFileName() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:zip");
        mock.expectedMessageCount(1);
        template.sendBody("direct:zip", ZipFileDataFormatTest.TEXT);
        assertMockEndpointsSatisfied();
        Exchange exchange = mock.getReceivedExchanges().get(0);
        assertEquals(((exchange.getIn().getMessageId()) + ".zip"), exchange.getIn().getHeader(Exchange.FILE_NAME));
        assertArrayEquals(ZipFileDataFormatTest.getZippedText(exchange.getIn().getMessageId()), ((byte[]) (exchange.getIn().getBody())));
    }

    @Test
    public void testZipWithFileName() throws Exception {
        getMockEndpoint("mock:zip").expectedBodiesReceived(ZipFileDataFormatTest.getZippedText("poem.txt"));
        getMockEndpoint("mock:zip").expectedHeaderReceived(Exchange.FILE_NAME, "poem.txt.zip");
        template.sendBodyAndHeader("direct:zip", ZipFileDataFormatTest.TEXT, Exchange.FILE_NAME, "poem.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testZipWithPathElements() throws Exception {
        getMockEndpoint("mock:zip").expectedBodiesReceived(ZipFileDataFormatTest.getZippedText("poem.txt"));
        getMockEndpoint("mock:zip").expectedHeaderReceived(Exchange.FILE_NAME, "poem.txt.zip");
        template.sendBodyAndHeader("direct:zip", ZipFileDataFormatTest.TEXT, Exchange.FILE_NAME, "poems/poem.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testZipWithPreservedPathElements() throws Exception {
        zip.setPreservePathElements(true);
        getMockEndpoint("mock:zip").expectedBodiesReceived(ZipFileDataFormatTest.getZippedTextInFolder("poems/", "poems/poem.txt"));
        getMockEndpoint("mock:zip").expectedHeaderReceived(Exchange.FILE_NAME, "poem.txt.zip");
        template.sendBodyAndHeader("direct:zip", ZipFileDataFormatTest.TEXT, Exchange.FILE_NAME, "poems/poem.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testUnzip() throws Exception {
        getMockEndpoint("mock:unzip").expectedBodiesReceived(ZipFileDataFormatTest.TEXT);
        getMockEndpoint("mock:unzip").expectedHeaderReceived(Exchange.FILE_NAME, "file");
        template.sendBody("direct:unzip", ZipFileDataFormatTest.getZippedText("file"));
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testUnzipWithEmptyDirectorySupported() throws Exception {
        deleteDirectory(new File("hello_out"));
        zip.setUsingIterator(true);
        zip.setAllowEmptyDirectory(true);
        template.sendBody("direct:unzipWithEmptyDirectory", new File("src/test/resources/hello.odt"));
        assertTrue(Files.exists(Paths.get("hello_out/Configurations2")));
        deleteDirectory(new File("hello_out"));
    }

    @Test
    public void testUnzipWithEmptyDirectoryUnsupported() throws Exception {
        deleteDirectory(new File("hello_out"));
        zip.setUsingIterator(true);
        zip.setAllowEmptyDirectory(false);
        template.sendBody("direct:unzipWithEmptyDirectory", new File("src/test/resources/hello.odt"));
        assertTrue((!(Files.exists(Paths.get("hello_out/Configurations2")))));
        deleteDirectory(new File("hello_out"));
    }

    @Test
    public void testZipAndUnzip() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:zipAndUnzip");
        mock.expectedMessageCount(1);
        template.sendBody("direct:zipAndUnzip", ZipFileDataFormatTest.TEXT);
        assertMockEndpointsSatisfied();
        Exchange exchange = mock.getReceivedExchanges().get(0);
        assertEquals(exchange.getIn().getMessageId(), exchange.getIn().getHeader(Exchange.FILE_NAME));
        assertEquals(ZipFileDataFormatTest.TEXT, new String(((byte[]) (exchange.getIn().getBody())), "UTF-8"));
    }

    @Test
    public void testZipToFileWithoutFileName() throws Exception {
        NotifyBuilder notify = whenDone(1).create();
        String[] files = ZipFileDataFormatTest.TEST_DIR.list();
        assertTrue(((files == null) || ((files.length) == 0)));
        MockEndpoint mock = getMockEndpoint("mock:intercepted");
        mock.expectedMessageCount(1);
        template.sendBody("direct:zipToFile", ZipFileDataFormatTest.TEXT);
        assertMockEndpointsSatisfied();
        // use builder to ensure the exchange is fully done before we check for file exists
        assertTrue("The exchange is not done in time.", notify.matches(5, TimeUnit.SECONDS));
        Exchange exchange = mock.getReceivedExchanges().get(0);
        File file = new File(ZipFileDataFormatTest.TEST_DIR, ((exchange.getIn().getMessageId()) + ".zip"));
        assertTrue("The file should exist.", file.exists());
        assertArrayEquals("Get a wrong message content.", ZipFileDataFormatTest.getZippedText(exchange.getIn().getMessageId()), ZipFileDataFormatTest.getBytes(file));
    }

    @Test
    public void testZipToFileWithFileName() throws Exception {
        NotifyBuilder notify = whenDone(1).create();
        MockEndpoint mock = getMockEndpoint("mock:zipToFile");
        mock.expectedMessageCount(1);
        File file = new File(ZipFileDataFormatTest.TEST_DIR, "poem.txt.zip");
        assertFalse("The zip should not exit.", file.exists());
        template.sendBodyAndHeader("direct:zipToFile", ZipFileDataFormatTest.TEXT, Exchange.FILE_NAME, "poem.txt");
        // just make sure the file is created
        mock.assertIsSatisfied();
        // use builder to ensure the exchange is fully done before we check for file exists
        assertTrue("The exchange is not done in time.", notify.matches(5, TimeUnit.SECONDS));
        assertTrue("The file should exist.", file.exists());
        assertArrayEquals("Get a wrong message content.", ZipFileDataFormatTest.getZippedText("poem.txt"), ZipFileDataFormatTest.getBytes(file));
    }

    @Test
    public void testDslZip() throws Exception {
        getMockEndpoint("mock:dslZip").expectedBodiesReceived(ZipFileDataFormatTest.getZippedText("poem.txt"));
        getMockEndpoint("mock:dslZip").expectedHeaderReceived(Exchange.FILE_NAME, "poem.txt.zip");
        template.sendBodyAndHeader("direct:dslZip", ZipFileDataFormatTest.TEXT, Exchange.FILE_NAME, "poem.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testDslUnzip() throws Exception {
        getMockEndpoint("mock:dslUnzip").expectedBodiesReceived(ZipFileDataFormatTest.TEXT);
        getMockEndpoint("mock:dslUnzip").expectedHeaderReceived(Exchange.FILE_NAME, "test.txt");
        template.sendBody("direct:dslUnzip", ZipFileDataFormatTest.getZippedText("test.txt"));
        assertMockEndpointsSatisfied();
    }
}

