package com.tagtraum.perf.gcviewer.util;


import com.tagtraum.perf.gcviewer.UnittestHelper;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

import static HttpUrlConnectionHelper.GZIP;
import static com.tagtraum.perf.gcviewer.UnittestHelper.FOLDER.OPENJDK;


public class TestHttpUrlConnectionHelper {
    private static final String SAMPLE_GCLOG_SUN1_6_0 = "SampleSun1_6_0PrintHeapAtGC.txt";

    private static final String SAMPLE_GCLOG_SUN1_6_0_GZ = (TestHttpUrlConnectionHelper.SAMPLE_GCLOG_SUN1_6_0) + ".gz";

    private static final String PARENT_PATH = ("src/test/resources/" + (OPENJDK.getFolderName())) + "/";

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    @Test
    public void openInputStream404GzipEncoding() throws Exception {
        String filename = (TestHttpUrlConnectionHelper.SAMPLE_GCLOG_SUN1_6_0) + "xx";
        File file = new File(TestHttpUrlConnectionHelper.PARENT_PATH, filename);
        String contentType = null;
        String contentEncoding = GZIP;
        testOpenInputStreamNotOk(file, contentEncoding, contentType, null, filename);
    }

    @Test
    public void openInputStream404NoEncoding() throws Exception {
        String filename = (TestHttpUrlConnectionHelper.SAMPLE_GCLOG_SUN1_6_0) + "xx";
        File file = new File(TestHttpUrlConnectionHelper.PARENT_PATH, filename);
        String contentEncoding = null;
        String contentType = null;
        testOpenInputStreamNotOk(file, contentEncoding, contentType, null, filename);
    }

    @Test
    public void openInputStreamGZipOk() throws Exception {
        String filename = TestHttpUrlConnectionHelper.SAMPLE_GCLOG_SUN1_6_0_GZ;
        File file = new File(TestHttpUrlConnectionHelper.PARENT_PATH, filename);
        String contentEncoding = GZIP;
        testOpenInputStreamOk(file, contentEncoding, contentEncoding, TestHttpUrlConnectionHelper.SAMPLE_GCLOG_SUN1_6_0);
    }

    @Test
    public void openInputStreamOk() throws Exception {
        String filename = TestHttpUrlConnectionHelper.SAMPLE_GCLOG_SUN1_6_0;
        File file = new File(TestHttpUrlConnectionHelper.PARENT_PATH, filename);
        String contentEncoding = null;
        testOpenInputStreamOk(file, contentEncoding, null, filename);
    }
}

