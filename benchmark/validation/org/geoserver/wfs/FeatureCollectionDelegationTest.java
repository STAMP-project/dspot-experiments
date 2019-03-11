/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import java.util.List;
import org.geoserver.security.CatalogMode;
import org.geoserver.security.WrapperPolicy;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.visitor.CountVisitor;
import org.geotools.feature.visitor.MaxVisitor;
import org.junit.Test;
import org.opengis.feature.FeatureVisitor;


public class FeatureCollectionDelegationTest extends GeoServerSystemTestSupport {
    private final WrapperPolicy policy = WrapperPolicy.readOnlyHide(new org.geoserver.security.AccessLimits(CatalogMode.HIDE));

    private static final String FEATURE_TYPE_NAME = "testType";

    private FeatureVisitor lastVisitor = null;

    // These collections will delegate the MaxVisitor
    // As an example, ReTypingFeatureCollections may not delegate.
    private List<SimpleFeatureCollection> maxVisitorCollections;

    // These collection will delegate the CountVisitor
    private List<SimpleFeatureCollection> countVisitorCollections;

    @Test
    public void testMaxVisitorDelegation() {
        MaxVisitor visitor = new MaxVisitor(CommonFactoryFinder.getFilterFactory2().property("value"));
        assertOptimalVisit(visitor, maxVisitorCollections);
    }

    @Test
    public void testCountVisitorDelegation() {
        FeatureVisitor visitor = new CountVisitor();
        assertOptimalVisit(visitor, countVisitorCollections);
    }
}

