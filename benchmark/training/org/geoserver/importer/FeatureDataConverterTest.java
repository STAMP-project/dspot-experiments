/**
 * (c) 2013 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer;


import FeatureDataConverter.DEFAULT;
import FeatureDataConverter.TO_POSTGIS;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.impl.FeatureTypeInfoImpl;
import org.geoserver.catalog.impl.LayerInfoImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;


/**
 *
 *
 * @author Ian Schneider <ischneider@opengeo.org>
 */
public class FeatureDataConverterTest {
    @Test
    public void testXMLUnsafeAttributeRenaming() {
        SimpleFeatureType badatts = buildFeatureTypeWithXMLUnsafeAtts();
        badatts = DEFAULT.convertType(badatts, null, null, null);
        Assert.assertEquals("_123_number_first", badatts.getAttributeDescriptors().get(0).getLocalName());
        Assert.assertEquals("i_has_spaces", badatts.getAttributeDescriptors().get(1).getLocalName());
    }

    @Test
    public void testPostgisConversion() {
        SimpleFeatureType t = TO_POSTGIS.convertType(buildFeatureTypeWithXMLUnsafeAtts(), null, null, null);
        Assert.assertEquals("_123_number_first", t.getAttributeDescriptors().get(0).getLocalName());
        Assert.assertEquals("i_has_spaces", t.getAttributeDescriptors().get(1).getLocalName());
    }

    @Test
    public void testLayerNameFromTask() {
        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
        typeBuilder.setName("badname");
        SimpleFeatureType badname = typeBuilder.buildFeatureType();
        ImportTask task = new ImportTask();
        LayerInfo layer = new LayerInfoImpl();
        ResourceInfo resource = new FeatureTypeInfoImpl(((Catalog) (null)));
        layer.setResource(resource);
        layer.setName("goodname");
        task.setLayer(layer);
        badname = DEFAULT.convertType(badname, null, null, task);
        Assert.assertEquals("goodname", badname.getName().getLocalPart());
    }
}

