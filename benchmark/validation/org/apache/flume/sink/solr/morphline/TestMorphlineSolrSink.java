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
package org.apache.flume.sink.solr.morphline;


import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.flume.Channel;
import org.apache.flume.ChannelException;
import org.apache.flume.Transaction;
import org.apache.flume.channel.BasicTransactionSemantics;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMorphlineSolrSink extends SolrTestCaseJ4 {
    private EmbeddedSource source;

    private SolrServer solrServer;

    private MorphlineSink sink;

    private Map<String, Integer> expectedRecords;

    private File tmpFile;

    private static final boolean TEST_WITH_EMBEDDED_SOLR_SERVER = true;

    private static final String EXTERNAL_SOLR_SERVER_URL = System.getProperty("externalSolrServer");

    // private static final String EXTERNAL_SOLR_SERVER_URL = "http://127.0.0.1:8983/solr";
    private static final String RESOURCES_DIR = "target/test-classes";

    // private static final String RESOURCES_DIR = "src/test/resources";
    private static final AtomicInteger SEQ_NUM = new AtomicInteger();

    private static final AtomicInteger SEQ_NUM2 = new AtomicInteger();

    private static final Logger LOGGER = LoggerFactory.getLogger(TestMorphlineSolrSink.class);

    @Test
    public void testDocumentTypes() throws Exception {
        String path = (TestMorphlineSolrSink.RESOURCES_DIR) + "/test-documents";
        String[] files = new String[]{ path + "/testBMPfp.txt", path + "/boilerplate.html", path + "/NullHeader.docx", path + "/testWORD_various.doc", path + "/testPDF.pdf", path + "/testJPEG_EXIF.jpg", path + "/testXML.xml", // path + "/cars.csv",
        // path + "/cars.tsv",
        // path + "/cars.ssv",
        // path + "/cars.csv.gz",
        // path + "/cars.tar.gz",
        path + "/sample-statuses-20120906-141433.avro", path + "/sample-statuses-20120906-141433", path + "/sample-statuses-20120906-141433.gz", path + "/sample-statuses-20120906-141433.bz2" };
        testDocumentTypesInternal(files);
    }

    @Test
    public void testDocumentTypes2() throws Exception {
        String path = (TestMorphlineSolrSink.RESOURCES_DIR) + "/test-documents";
        String[] files = // path + "/testWAR.war",
        // path + "/testWindows-x86-32.exe",
        // path + "/testWINMAIL.dat",
        // path + "/testWMF.wmf",
        new String[]{ path + "/testPPT_various.ppt", path + "/testPPT_various.pptx", path + "/testEXCEL.xlsx", path + "/testEXCEL.xls", path + "/testPages.pages", path + "/testNumbers.numbers", path + "/testKeynote.key", path + "/testRTFVarious.rtf", path + "/complex.mbox", path + "/test-outlook.msg", path + "/testEMLX.emlx", // path + "/testRFC822",
        path + "/rsstest.rss", // path + "/testDITA.dita",
        path + "/testMP3i18n.mp3", path + "/testAIFF.aif", path + "/testFLAC.flac", // path + "/testFLAC.oga",
        // path + "/testVORBIS.ogg",
        path + "/testMP4.m4a", path + "/testWAV.wav", // path + "/testWMA.wma",
        path + "/testFLV.flv", // path + "/testWMV.wmv",
        path + "/testBMP.bmp", path + "/testPNG.png", path + "/testPSD.psd", path + "/testSVG.svg", path + "/testTIFF.tif", // path + "/test-documents.7z",
        // path + "/test-documents.cpio",
        // path + "/test-documents.tar",
        // path + "/test-documents.tbz2",
        // path + "/test-documents.tgz",
        // path + "/test-documents.zip",
        // path + "/test-zip-of-zip.zip",
        // path + "/testJAR.jar",
        // path + "/testKML.kml",
        // path + "/testRDF.rdf",
        path + "/testTrueType.ttf", path + "/testVISIO.vsd" }// path + "/testWAR.war",
        // path + "/testWindows-x86-32.exe",
        // path + "/testWINMAIL.dat",
        // path + "/testWMF.wmf",
        ;
        testDocumentTypesInternal(files);
    }

    @Test
    public void testErrorCounters() throws Exception {
        Channel channel = Mockito.mock(Channel.class);
        Mockito.when(channel.take()).thenThrow(new ChannelException("dummy"));
        Transaction transaction = Mockito.mock(BasicTransactionSemantics.class);
        Mockito.when(channel.getTransaction()).thenReturn(transaction);
        sink.setChannel(channel);
        sink.process();
        SinkCounter sinkCounter = ((SinkCounter) (Whitebox.getInternalState(sink, "sinkCounter")));
        assertEquals(1, sinkCounter.getChannelReadFail());
    }

    @Test
    public void testAvroRoundTrip() throws Exception {
        String file = ((TestMorphlineSolrSink.RESOURCES_DIR) + "/test-documents") + "/sample-statuses-20120906-141433.avro";
        testDocumentTypesInternal(file);
        QueryResponse rsp = query("*:*");
        Iterator<SolrDocument> iter = rsp.getResults().iterator();
        ListMultimap<String, String> expectedFieldValues;
        expectedFieldValues = ImmutableListMultimap.of("id", "1234567890", "text", "sample tweet one", "user_screen_name", "fake_user1");
        assertEquals(expectedFieldValues, next(iter));
        expectedFieldValues = ImmutableListMultimap.of("id", "2345678901", "text", "sample tweet two", "user_screen_name", "fake_user2");
        assertEquals(expectedFieldValues, next(iter));
        assertFalse(iter.hasNext());
    }
}

