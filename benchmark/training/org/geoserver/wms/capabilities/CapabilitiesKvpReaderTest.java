/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.capabilities;


import java.util.HashMap;
import org.geoserver.wms.GetCapabilitiesRequest;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("rawtypes")
public class CapabilitiesKvpReaderTest {
    private CapabilitiesKvpReader reader;

    private HashMap kvp;

    private HashMap rawKvp;

    @SuppressWarnings("unchecked")
    @Test
    public void testDefault() throws Exception {
        rawKvp.put("request", "getcapabilities");
        kvp.put("request", "getcapabilities");
        GetCapabilitiesRequest read = reader.read(reader.createRequest(), kvp, rawKvp);
        Assert.assertNotNull(read);
        Assert.assertEquals("getcapabilities", read.getRequest().toLowerCase());
        Assert.assertNull(read.getBaseUrl());
        Assert.assertNull(read.getNamespace());
    }

    /**
     * 1.0 "WMTVER" parameter supplied instead of "VERSION"? Version negotiation should agree on
     * 1.1.1
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testWMTVER() throws Exception {
        rawKvp.put("WMTVER", "1.0");
        GetCapabilitiesRequest read = reader.read(reader.createRequest(), kvp, rawKvp);
        Assert.assertNotNull(read);
        Assert.assertEquals("1.1.1", read.getVersion());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testVersion() throws Exception {
        kvp.put("Version", "1.1.1");
        GetCapabilitiesRequest read = reader.read(reader.createRequest(), kvp, rawKvp);
        Assert.assertNotNull(read);
        Assert.assertEquals("1.1.1", read.getVersion());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNamespace() throws Exception {
        kvp.put("namespace", "og");
        GetCapabilitiesRequest read = reader.read(reader.createRequest(), kvp, rawKvp);
        Assert.assertNotNull(read);
        Assert.assertEquals("og", read.getNamespace());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateSequence() throws Exception {
        kvp.put("updateSequence", "1000");
        GetCapabilitiesRequest read = reader.read(reader.createRequest(), kvp, rawKvp);
        Assert.assertNotNull(read);
        Assert.assertEquals("1000", read.getUpdateSequence());
    }
}

