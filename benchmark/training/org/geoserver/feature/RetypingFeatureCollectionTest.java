/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.feature;


import Filter.INCLUDE;
import java.io.IOException;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.SchemaException;
import org.geotools.feature.visitor.CountVisitor;
import org.geotools.feature.visitor.MaxVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeatureType;


public class RetypingFeatureCollectionTest {
    FeatureVisitor lastVisitor = null;

    private ListFeatureCollection collection;

    private SimpleFeatureType renamedSchema;

    @Test
    public void testMaxVisitorDelegation() throws IOException, SchemaException {
        MaxVisitor visitor = new MaxVisitor(CommonFactoryFinder.getFilterFactory2().property("value"));
        assertOptimalVisit(visitor);
    }

    @Test
    public void testCountVisitorDelegation() throws IOException, SchemaException {
        FeatureVisitor visitor = new CountVisitor();
        assertOptimalVisit(visitor);
    }

    /**
     * TEST for GEOS-8176 [https://osgeo-org.atlassian.net/browse/GEOS-8176].
     *
     * <p>Make sure that the subCollection returned is retyped.
     *
     * @author Ian Turton
     */
    @Test
    public void testSubCollectionRetyping() {
        RetypingFeatureCollection retypedCollection = new RetypingFeatureCollection(collection, renamedSchema);
        SimpleFeatureCollection subCollection = retypedCollection.subCollection(INCLUDE);
        Assert.assertSame(renamedSchema, subCollection.getSchema());
    }
}

