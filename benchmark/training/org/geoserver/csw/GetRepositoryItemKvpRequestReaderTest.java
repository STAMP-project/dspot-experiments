/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw;


import ServiceException.MISSING_PARAMETER_VALUE;
import java.util.Map;
import junit.framework.TestCase;
import org.geoserver.config.impl.GeoServerImpl;
import org.geoserver.platform.Service;
import org.geoserver.platform.ServiceException;


/**
 * Unit test suite for {@link GetRepositoryItemKvpRequestReader}
 *
 * @version $Id$
 */
public class GetRepositoryItemKvpRequestReaderTest extends TestCase {
    private GeoServerImpl geoServerImpl;

    private Service csw;

    private Map<String, String> params;

    public void testGetRequestNoIdRequested() throws Exception {
        params.put("VERSION", "2.0.2");
        try {
            getRequest(params);
            TestCase.fail("expected ServiceException if no ID is requested");
        } catch (ServiceException e) {
            TestCase.assertEquals(MISSING_PARAMETER_VALUE, e.getCode());
            TestCase.assertEquals("id", e.getLocator());
        }
    }

    public void testParseValidRequest() throws Exception {
        params.put("service", "csw");
        params.put("VERSION", "2.0.2");
        params.put("id", "foo");
        GetRepositoryItemType request = getRequest(params);
        TestCase.assertEquals("2.0.2", request.getVersion());
        TestCase.assertEquals("csw", request.getService());
        TestCase.assertEquals("foo", request.getId());
    }
}

