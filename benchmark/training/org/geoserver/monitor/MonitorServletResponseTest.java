/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.monitor;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import org.geoserver.monitor.MonitorServletResponse.MonitorOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.DelegatingServletOutputStream;


public class MonitorServletResponseTest {
    @Test
    public void testOutputStream() throws IOException {
        byte[] data = MonitorServletRequestTest.data();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ServletOutputStream mock = new DelegatingServletOutputStream(bos);
        MonitorOutputStream out = new MonitorOutputStream(mock);
        out.write(data);
        Assert.assertEquals(data.length, bos.size());
        Assert.assertEquals(data.length, out.getBytesWritten());
    }
}

