/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog;


import MatchAction.ALL;
import MatchAction.ANY;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.geoserver.catalog.impl.AuthorityURL;
import org.geoserver.catalog.impl.CoverageInfoImpl;
import org.geoserver.catalog.impl.CoverageStoreInfoImpl;
import org.geoserver.catalog.impl.DataStoreInfoImpl;
import org.geoserver.catalog.impl.FeatureTypeInfoImpl;
import org.geoserver.catalog.impl.LayerInfoImpl;
import org.geoserver.catalog.impl.NamespaceInfoImpl;
import org.geoserver.catalog.impl.StyleInfoImpl;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.junit.Assert;
import org.junit.Test;


public class PredicatesTest {
    private WorkspaceInfoImpl ws;

    private NamespaceInfoImpl ns;

    private DataStoreInfoImpl dataStore;

    private FeatureTypeInfoImpl featureType;

    private CoverageStoreInfoImpl coverageStore;

    private CoverageInfoImpl coverage;

    private LayerInfoImpl vectorLayer;

    private LayerInfoImpl rasterLayer;

    private StyleInfoImpl defaultStyle;

    private StyleInfoImpl style1;

    private StyleInfoImpl style2;

    @Test
    public void testPropertyEqualsSimple() {
        Assert.assertTrue(Predicates.equal("prefix", ns.getPrefix()).evaluate(ns));
        Assert.assertTrue(Predicates.equal("id", ws.getId()).evaluate(ws));
        Assert.assertFalse(Predicates.equal("id", "somethingElse").evaluate(ws));
        Set<StyleInfo> styles = new HashSet<StyleInfo>();
        styles.add(style1);
        Assert.assertFalse(Predicates.equal("styles", styles, ALL).evaluate(vectorLayer));
        Assert.assertTrue(Predicates.equal("styles", styles, ANY).evaluate(vectorLayer));
        styles.add(style2);
        Assert.assertTrue(Predicates.equal("styles", styles).evaluate(vectorLayer));
    }

    @Test
    public void testPropertyNotEqualsSimple() {
        Assert.assertTrue(Predicates.notEqual("id", "somethingElse").evaluate(ws));
    }

    @Test
    public void testPropertyEqualsCompound() {
        Assert.assertTrue(Predicates.equal("resource.id", featureType.getId()).evaluate(vectorLayer));
        Assert.assertTrue(Predicates.equal("resource.maxFeatures", featureType.getMaxFeatures()).evaluate(vectorLayer));
        Assert.assertTrue(Predicates.equal("resource.store.type", dataStore.getType()).evaluate(vectorLayer));
        Assert.assertTrue(Predicates.equal("resource.store.connectionParameters.boolParam", true).evaluate(vectorLayer));
        Assert.assertFalse(Predicates.equal("resource.store.connectionParameters.boolParam", false).evaluate(vectorLayer));
        ws.getMetadata().put("checkMe", new Date(1000));
        Assert.assertTrue(Predicates.equal("metadata.checkMe", new Date(1000)).evaluate(ws));
        Assert.assertFalse(Predicates.equal("resource.store.someNonExistentProperty", "someValue").evaluate(vectorLayer));
    }

    @Test
    public void testPropertyEqualsConverters() {
        Object expected;
        expected = featureType.getMaxFeatures();
        Assert.assertTrue(Predicates.equal("resource.maxFeatures", expected).evaluate(vectorLayer));
        expected = String.valueOf(featureType.getMaxFeatures());
        Assert.assertTrue(Predicates.equal("resource.maxFeatures", expected).evaluate(vectorLayer));
        expected = new Double(featureType.getMaxFeatures());
        Assert.assertTrue(Predicates.equal("resource.maxFeatures", expected).evaluate(vectorLayer));
        expected = "true";
        Assert.assertTrue(Predicates.equal("resource.store.connectionParameters.boolParam", expected).evaluate(vectorLayer));
        expected = "false";
        Assert.assertFalse(Predicates.equal("resource.store.connectionParameters.boolParam", false).evaluate(vectorLayer));
        ws.getMetadata().put("checkMe", new Date(1000));
        expected = new Timestamp(1000);
        Assert.assertTrue(Predicates.equal("resource.store.workspace.metadata.checkMe", expected).evaluate(vectorLayer));
        Assert.assertFalse(Predicates.equal("resource.store.someNonExistentProperty", "someValue").evaluate(vectorLayer));
    }

    @Test
    public void testPropertyEqualsIndexed() {
        AuthorityURLInfo aurl1 = new AuthorityURL();
        aurl1.setName("url1");
        AuthorityURLInfo aurl2 = new AuthorityURL();
        aurl2.setName("url2");
        AuthorityURLInfo aurl3 = new AuthorityURL();
        aurl3.setName("url3");
        vectorLayer.setAuthorityURLs(Arrays.asList(aurl1, aurl2, aurl3));
        Assert.assertTrue(Predicates.equal("authorityURLs[1]", aurl1).evaluate(vectorLayer));
        Assert.assertTrue(Predicates.equal("authorityURLs[1].name", aurl1.getName()).evaluate(vectorLayer));
        Assert.assertTrue(Predicates.equal("authorityURLs[2]", aurl2).evaluate(vectorLayer));
        Assert.assertTrue(Predicates.equal("authorityURLs[2].name", aurl2.getName()).evaluate(vectorLayer));
        Assert.assertTrue(Predicates.equal("authorityURLs[3]", aurl3).evaluate(vectorLayer));
        Assert.assertTrue(Predicates.equal("authorityURLs[3].name", aurl3.getName()).evaluate(vectorLayer));
    }

    @Test
    public void testPropertyEqualsAny() {
        Assert.assertTrue(Predicates.equal("styles.id", style1.getId()).evaluate(vectorLayer));
        Assert.assertTrue(Predicates.equal("styles.name", style2.getName()).evaluate(vectorLayer));
        Assert.assertFalse(Predicates.equal("styles.id", "nonExistent").evaluate(vectorLayer));
    }

    @Test
    public void testContains() {
        Assert.assertTrue(Predicates.contains("URI", "example").evaluate(ns));
        Assert.assertFalse(Predicates.contains("resource.ns.URI", "example").evaluate(vectorLayer));
        Assert.assertTrue(Predicates.contains("resource.namespace.URI", "example").evaluate(vectorLayer));
        Assert.assertTrue(Predicates.contains("id", "vectorLayerId").evaluate(vectorLayer));
        Assert.assertTrue(Predicates.contains("id", "vectorLayerID").evaluate(vectorLayer));
        Assert.assertTrue(Predicates.contains("id", "torLAY").evaluate(vectorLayer));
        Assert.assertTrue(Predicates.contains("styles.name", "style2").evaluate(vectorLayer));
        Assert.assertTrue(Predicates.contains("styles.name", "Style2").evaluate(vectorLayer));
        Assert.assertTrue(Predicates.contains("styles.name", "YL").evaluate(vectorLayer));
        Assert.assertFalse(Predicates.contains("styles.name", "style3").evaluate(vectorLayer));
        String name = featureType.getName();
        Assert.assertTrue(Predicates.contains("resource.name", name).evaluate(vectorLayer));
        Assert.assertFalse(Predicates.contains("resource.name", "?").evaluate(vectorLayer));
        featureType.setName("name?.*$[]&()");
        Assert.assertTrue(Predicates.contains("resource.name", "?").evaluate(vectorLayer));
        Assert.assertTrue(Predicates.contains("resource.name", ".").evaluate(vectorLayer));
        Assert.assertTrue(Predicates.contains("resource.name", "*").evaluate(vectorLayer));
        featureType.setName(null);
        Assert.assertFalse(Predicates.contains("resource.name", name).evaluate(vectorLayer));
    }
}

