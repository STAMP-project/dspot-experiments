/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security;


import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.locationtech.jts.geom.MultiPolygon;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;

import static CatalogMode.CHALLENGE;
import static CatalogMode.HIDE;
import static CatalogMode.MIXED;


public class TestAccessLimitsSerialization extends TestCase {
    FilterFactory2 ff;

    Filter filter;

    MultiPolygon g;

    public void testAccessLimits() throws Exception {
        AccessLimits limits = new AccessLimits(MIXED);
        testObjectSerialization(limits);
    }

    public void testSerializeWorkspaceAccessLimits() throws Exception {
        WorkspaceAccessLimits limits = new WorkspaceAccessLimits(HIDE, true, true, true);
        testObjectSerialization(limits);
    }

    public void testSerializeDataAccessLimits() throws Exception {
        DataAccessLimits limits = new DataAccessLimits(CHALLENGE, filter);
        testObjectSerialization(limits);
    }

    public void testCoverageAccessLimits() throws Exception {
        CoverageAccessLimits limits = new CoverageAccessLimits(MIXED, filter, g, null);
        testObjectSerialization(limits);
    }

    public void testVectorAccessLimits() throws Exception {
        List<PropertyName> properties = new ArrayList<PropertyName>();
        properties.add(ff.property("test"));
        VectorAccessLimits limits = new VectorAccessLimits(MIXED, properties, filter, properties, filter);
        testObjectSerialization(limits);
    }

    public void testWMSAccessLimits() throws Exception {
        List<PropertyName> properties = new ArrayList<PropertyName>();
        properties.add(ff.property("test"));
        WMSAccessLimits limits = new WMSAccessLimits(MIXED, filter, g, true);
        testObjectSerialization(limits);
    }
}

