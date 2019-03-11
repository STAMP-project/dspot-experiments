/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.ppio;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.geoserver.wps.process.RawData;
import org.junit.Assert;
import org.junit.Test;


public class RawDataPPIOTest {
    @Test
    public void testInputStreamClosed() throws Exception {
        try (RawDataPPIOTest.TestInputStream is = new RawDataPPIOTest.TestInputStream();OutputStream os = new ByteArrayOutputStream()) {
            RawDataPPIO ppio = buildRawDataPPIOWithMockManager();
            RawData rawData = mockRawDataWithInputStream(is);
            ppio.encode(rawData, os);
            Assert.assertTrue(is.isClosed());
        }
    }

    private class TestInputStream extends ByteArrayInputStream {
        private boolean isClosed = false;

        public TestInputStream() {
            super("Test data".getBytes());
        }

        public boolean isClosed() {
            return isClosed;
        }

        @Override
        public void close() throws IOException {
            super.close();
            isClosed = true;
        }
    }
}

