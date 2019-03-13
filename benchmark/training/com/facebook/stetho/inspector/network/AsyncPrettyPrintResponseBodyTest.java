/**
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.stetho.inspector.network;


import com.facebook.stetho.common.Util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;

import static PrettyPrinterDisplayType.TEXT;


public class AsyncPrettyPrintResponseBodyTest {
    private static final String TEST_REQUEST_ID = "1234";

    private static final String TEST_HEADER_NAME = "header name";

    private static final String TEST_HEADER_VALUE = "header value";

    private static final String PRETTY_PRINT_PREFIX = "pretty printed result: ";

    private static final String[] UNREGISTERED_HEADER_NAMES = new String[]{ "unregistered header name 1", "unregistered header name 2", "unregistered header name 3" };

    private static final String[] UNREGISTERED_HEADER_VALUES = new String[]{ "unregistered header value 1", "unregistered header value 2", "unregistered header value 3" };

    private static final byte[] TEST_RESPONSE_BODY;

    private static final ByteArrayInputStream mInputStream;

    static {
        int responseBodyLength = (4096 * 2) + 2048;// span multiple buffers when tee-ing

        TEST_RESPONSE_BODY = new byte[responseBodyLength];
        for (int i = 0; i < responseBodyLength; i++) {
            AsyncPrettyPrintResponseBodyTest.TEST_RESPONSE_BODY[i] = AsyncPrettyPrintResponseBodyTest.positionToByte(i);
        }
        mInputStream = new ByteArrayInputStream(AsyncPrettyPrintResponseBodyTest.TEST_RESPONSE_BODY);
    }

    private AsyncPrettyPrinterRegistry mAsyncPrettyPrinterRegistry;

    private AsyncPrettyPrintResponseBodyTest.PrettyPrinterTestFactory mPrettyPrinterTestFactory;

    private ResponseBodyFileManager mResponseBodyFileManager;

    @Test
    public void testAsyncPrettyPrinterResult() throws IOException {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        AsyncPrettyPrinter mAsyncPrettyPrinter = getInstance(AsyncPrettyPrintResponseBodyTest.TEST_HEADER_NAME, AsyncPrettyPrintResponseBodyTest.TEST_HEADER_VALUE);
        mAsyncPrettyPrinter.printTo(writer, AsyncPrettyPrintResponseBodyTest.mInputStream);
        Assert.assertEquals(((AsyncPrettyPrintResponseBodyTest.PRETTY_PRINT_PREFIX) + (Arrays.toString(AsyncPrettyPrintResponseBodyTest.TEST_RESPONSE_BODY))), out.toString());
    }

    @Test
    public void testInitAsyncPrettyPrinterForResponseWithRegisteredHeader() {
        ArrayList<String> headerNames = new ArrayList<String>();
        ArrayList<String> headerValues = new ArrayList<String>();
        headerNames.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_NAMES[0]);
        headerNames.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_NAMES[1]);
        headerNames.add(AsyncPrettyPrintResponseBodyTest.TEST_HEADER_NAME);
        headerValues.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_VALUES[0]);
        headerValues.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_VALUES[1]);
        headerValues.add(AsyncPrettyPrintResponseBodyTest.TEST_HEADER_VALUE);
        AsyncPrettyPrintResponseBodyTest.TestInspectorResponse testResponse = new AsyncPrettyPrintResponseBodyTest.TestInspectorResponse(headerNames, headerValues, AsyncPrettyPrintResponseBodyTest.TEST_REQUEST_ID);
        AsyncPrettyPrinter prettyPrinter = NetworkEventReporterImpl.createPrettyPrinterForResponse(testResponse, mAsyncPrettyPrinterRegistry);
        Assert.assertNotNull(prettyPrinter);
    }

    @Test
    public void testInitAsyncPrettyPrinterForResponseWithUnregisteredHeader() {
        ArrayList<String> headerNames = new ArrayList<String>();
        ArrayList<String> headerValues = new ArrayList<String>();
        headerNames.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_NAMES[0]);
        headerNames.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_NAMES[1]);
        headerNames.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_NAMES[2]);
        headerValues.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_VALUES[0]);
        headerValues.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_VALUES[1]);
        headerValues.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_VALUES[2]);
        AsyncPrettyPrintResponseBodyTest.TestInspectorResponse testResponse = new AsyncPrettyPrintResponseBodyTest.TestInspectorResponse(headerNames, headerValues, AsyncPrettyPrintResponseBodyTest.TEST_REQUEST_ID);
        AsyncPrettyPrinter prettyPrinter = NetworkEventReporterImpl.createPrettyPrinterForResponse(testResponse, mAsyncPrettyPrinterRegistry);
        Assert.assertEquals(null, prettyPrinter);
    }

    @Test
    public void testGetInstanceWithUnmatchedHeader() {
        ArrayList<String> headerNames = new ArrayList<String>();
        ArrayList<String> headerValues = new ArrayList<String>();
        headerNames.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_NAMES[0]);
        headerNames.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_NAMES[1]);
        headerNames.add(AsyncPrettyPrintResponseBodyTest.TEST_HEADER_NAME);
        headerValues.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_VALUES[0]);
        headerValues.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_VALUES[1]);
        headerValues.add(AsyncPrettyPrintResponseBodyTest.UNREGISTERED_HEADER_VALUES[2]);
        AsyncPrettyPrintResponseBodyTest.TestInspectorResponse testResponse = new AsyncPrettyPrintResponseBodyTest.TestInspectorResponse(headerNames, headerValues, AsyncPrettyPrintResponseBodyTest.TEST_REQUEST_ID);
        AsyncPrettyPrinter prettyPrinter = NetworkEventReporterImpl.createPrettyPrinterForResponse(testResponse, mAsyncPrettyPrinterRegistry);
        Assert.assertEquals(null, prettyPrinter);
    }

    private class PrettyPrinterTestFactory extends DownloadingAsyncPrettyPrinterFactory {
        @Override
        protected void doPrint(PrintWriter output, InputStream payload, String schema) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Util.copy(payload, out, new byte[1024]);
            String prettifiedContent = (AsyncPrettyPrintResponseBodyTest.PRETTY_PRINT_PREFIX) + (Arrays.toString(out.toByteArray()));
            output.write(prettifiedContent);
            output.close();
        }

        @Override
        @Nullable
        protected MatchResult matchAndParseHeader(String headerName, String headerValue) {
            if ((headerName.equals(AsyncPrettyPrintResponseBodyTest.TEST_HEADER_NAME)) && (headerValue.equals(AsyncPrettyPrintResponseBodyTest.TEST_HEADER_VALUE))) {
                return new MatchResult("https://www.facebook.com", TEXT);
            } else {
                return null;
            }
        }
    }

    private class TestInspectorResponse implements NetworkEventReporter.InspectorResponse {
        private ArrayList<String> mHeaderNames;

        private ArrayList<String> mHeaderValues;

        private String mRequestId;

        public TestInspectorResponse(ArrayList<String> headerNames, ArrayList<String> headerValues, String requestId) {
            mHeaderNames = headerNames;
            mHeaderValues = headerValues;
            mRequestId = requestId;
        }

        public int headerCount() {
            return mHeaderNames.size();
        }

        public String headerName(int index) {
            return mHeaderNames.get(index);
        }

        public String headerValue(int index) {
            return mHeaderValues.get(index);
        }

        @Nullable
        public String firstHeaderValue(String name) {
            return mHeaderValues.get(0);
        }

        public String requestId() {
            return mRequestId;
        }

        public String url() {
            return "test url";
        }

        public int statusCode() {
            return 200;
        }

        public String reasonPhrase() {
            return "test reason phrase";
        }

        public boolean connectionReused() {
            return false;
        }

        public int connectionId() {
            return 111;
        }

        public boolean fromDiskCache() {
            return false;
        }
    }
}

