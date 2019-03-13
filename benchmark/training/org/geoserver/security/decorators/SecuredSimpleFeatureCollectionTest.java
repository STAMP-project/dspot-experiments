/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.decorators;


import java.io.IOException;
import java.util.Arrays;
import org.geoserver.security.CatalogMode;
import org.geoserver.security.WrapperPolicy;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.SchemaException;
import org.geotools.feature.visitor.CountVisitor;
import org.geotools.feature.visitor.MaxVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.FeatureVisitor;
import org.opengis.filter.expression.PropertyName;


public class SecuredSimpleFeatureCollectionTest {
    FeatureVisitor lastVisitor = null;

    private ListFeatureCollection collection;

    @Test
    public void testMaxVisitorDelegation() throws IOException, SchemaException {
        MaxVisitor visitor = new MaxVisitor(CommonFactoryFinder.getFilterFactory2().property("value"));
        WrapperPolicy policy = WrapperPolicy.hide(new org.geoserver.security.VectorAccessLimits(CatalogMode.HIDE, null, null, null, null));
        assertOptimalVisit(visitor, policy);
    }

    @Test
    public void testMaxOnHiddenField() throws IOException, SchemaException {
        MaxVisitor visitor = new MaxVisitor(CommonFactoryFinder.getFilterFactory2().property("value"));
        PropertyName geom = CommonFactoryFinder.getFilterFactory2().property("the_geom");
        WrapperPolicy policy = WrapperPolicy.hide(new org.geoserver.security.VectorAccessLimits(CatalogMode.HIDE, Arrays.asList(geom), null, null, null));
        SecuredSimpleFeatureCollection secured = new SecuredSimpleFeatureCollection(collection, policy);
        secured.accepts(visitor, null);
        Assert.assertNull(lastVisitor);
    }

    @Test
    public void testCountVisitorDelegation() throws IOException, SchemaException {
        FeatureVisitor visitor = new CountVisitor();
        WrapperPolicy policy = WrapperPolicy.hide(new org.geoserver.security.VectorAccessLimits(CatalogMode.HIDE, null, null, null, null));
        assertOptimalVisit(visitor, policy);
    }
}

