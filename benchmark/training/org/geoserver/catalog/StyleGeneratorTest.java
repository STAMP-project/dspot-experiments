/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.easymock.classextension.EasyMock;
import org.geoserver.catalog.impl.CatalogFactoryImpl;
import org.geotools.data.DataUtilities;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.NamedLayer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.xml.styling.SLDParser;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.vfny.geoserver.util.SLDValidator;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;


public class StyleGeneratorTest {
    @Test
    public void testCreateInWorkspace() throws Exception {
        ResourcePool rp = EasyMock.createNiceMock(ResourcePool.class);
        Catalog cat = EasyMock.createNiceMock(Catalog.class);
        expect(cat.getFactory()).andReturn(new CatalogFactoryImpl(null)).anyTimes();
        expect(cat.getResourcePool()).andReturn(rp).anyTimes();
        WorkspaceInfo ws = EasyMock.createNiceMock(WorkspaceInfo.class);
        FeatureTypeInfo ft = EasyMock.createNiceMock(FeatureTypeInfo.class);
        expect(ft.getName()).andReturn("foo").anyTimes();
        EasyMock.replay(rp, ft, ws, cat);
        StyleGenerator gen = new StyleGenerator(cat);
        gen.setWorkspace(ws);
        SimpleFeatureType schema = DataUtilities.createType("foo", "geom:Point");
        StyleInfo style = gen.createStyle(new SLDHandler(), ft, schema);
        Assert.assertNotNull(style);
        Assert.assertNotNull(style.getWorkspace());
    }

    @Test
    public void testGenericStyle() throws Exception {
        ResourcePool rp = EasyMock.createNiceMock(ResourcePool.class);
        rp.writeStyle(((StyleInfo) (anyObject())), ((InputStream) (anyObject())));
        expectLastCall().andAnswer(new org.easymock.IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                Object[] args = getCurrentArguments();
                InputStream is = ((InputStream) (args[1]));
                byte[] input = IOUtils.toByteArray(is);
                SLDParser parser = new SLDParser(CommonFactoryFinder.getStyleFactory());
                parser.setInput(new ByteArrayInputStream(input));
                StyledLayerDescriptor sld = parser.parseSLD();
                NamedLayer nl = ((NamedLayer) (sld.getStyledLayers()[0]));
                Assert.assertEquals("foo", nl.getName());
                Style style = nl.getStyles()[0];
                Assert.assertEquals("A orange generic style", style.getDescription().getTitle().toString());
                Assert.assertEquals(1, style.featureTypeStyles().size());
                FeatureTypeStyle fts = style.featureTypeStyles().get(0);
                Assert.assertEquals("first", fts.getOptions().get("ruleEvaluation"));
                Assert.assertEquals(4, fts.rules().size());
                Assert.assertEquals("raster", fts.rules().get(0).getDescription().getTitle().toString());
                Assert.assertEquals("orange polygon", fts.rules().get(1).getDescription().getTitle().toString());
                Assert.assertEquals("orange line", fts.rules().get(2).getDescription().getTitle().toString());
                Assert.assertEquals("orange point", fts.rules().get(3).getDescription().getTitle().toString());
                // make sure it's valid
                SLDValidator validator = new SLDValidator();
                List errors = validator.validateSLD(new ByteArrayInputStream(input));
                Assert.assertEquals(0, errors.size());
                return null;
            }
        });
        Catalog cat = EasyMock.createNiceMock(Catalog.class);
        expect(cat.getFactory()).andReturn(new CatalogFactoryImpl(null)).anyTimes();
        expect(cat.getResourcePool()).andReturn(rp).anyTimes();
        WorkspaceInfo ws = EasyMock.createNiceMock(WorkspaceInfo.class);
        FeatureTypeInfo ft = EasyMock.createNiceMock(FeatureTypeInfo.class);
        expect(ft.getName()).andReturn("foo").anyTimes();
        EasyMock.replay(rp, ft, ws, cat);
        StyleGenerator gen = new StyleGenerator(cat) {
            protected void randomizeRamp() {
                // do not randomize for this test
            }
        };
        gen.setWorkspace(ws);
        SimpleFeatureType schema = DataUtilities.createType("foo", "geom:Geometry");
        StyleInfo style = gen.createStyle(new SLDHandler(), ft, schema);
        Assert.assertNotNull(style);
        Assert.assertNotNull(style.getWorkspace());
    }

    @Test
    public void testRasterStyle() throws Exception {
        ResourcePool rp = EasyMock.createNiceMock(ResourcePool.class);
        rp.writeStyle(((StyleInfo) (anyObject())), ((InputStream) (anyObject())));
        expectLastCall().andAnswer(new org.easymock.IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                Object[] args = getCurrentArguments();
                InputStream is = ((InputStream) (args[1]));
                byte[] input = IOUtils.toByteArray(is);
                SLDParser parser = new SLDParser(CommonFactoryFinder.getStyleFactory());
                parser.setInput(new ByteArrayInputStream(input));
                StyledLayerDescriptor sld = parser.parseSLD();
                NamedLayer nl = ((NamedLayer) (sld.getStyledLayers()[0]));
                Assert.assertEquals("foo", nl.getName());
                Style style = nl.getStyles()[0];
                Assert.assertEquals("A raster style", style.getDescription().getTitle().toString());
                Assert.assertEquals(1, style.featureTypeStyles().size());
                FeatureTypeStyle fts = style.featureTypeStyles().get(0);
                Assert.assertEquals(1, fts.rules().size());
                Assert.assertThat(fts.rules().get(0).symbolizers().get(0), Matchers.instanceOf(RasterSymbolizer.class));
                // make sure it's valid
                SLDValidator validator = new SLDValidator();
                List errors = validator.validateSLD(new ByteArrayInputStream(input));
                Assert.assertEquals(0, errors.size());
                return null;
            }
        });
        Catalog cat = EasyMock.createNiceMock(Catalog.class);
        expect(cat.getFactory()).andReturn(new CatalogFactoryImpl(null)).anyTimes();
        expect(cat.getResourcePool()).andReturn(rp).anyTimes();
        WorkspaceInfo ws = EasyMock.createNiceMock(WorkspaceInfo.class);
        CoverageInfo ci = EasyMock.createNiceMock(CoverageInfo.class);
        expect(ci.getName()).andReturn("foo").anyTimes();
        EasyMock.replay(rp, ci, ws, cat);
        StyleGenerator gen = new StyleGenerator(cat) {
            protected void randomizeRamp() {
                // do not randomize for this test
            }
        };
        gen.setWorkspace(ws);
        StyleInfo style = gen.createStyle(new SLDHandler(), ci);
        Assert.assertNotNull(style);
        Assert.assertNotNull(style.getWorkspace());
    }
}

