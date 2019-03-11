/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.jts;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;
import org.geoserver.wps.WPSTestSupport;
import org.geoserver.wps.process.GeoServerProcessors;
import org.geotools.data.Parameter;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.process.Process;
import org.geotools.process.ProcessException;
import org.geotools.process.ProcessFactory;
import org.geotools.process.factory.AnnotatedBeanProcessFactory;
import org.geotools.process.vector.BoundsProcess;
import org.geotools.process.vector.NearestProcess;
import org.geotools.process.vector.SnapProcess;
import org.geotools.util.SimpleInternationalString;
import org.junit.Test;
import org.opengis.feature.type.Name;
import org.opengis.util.InternationalString;


/**
 * Tests some processes that do not require integration with the application context
 *
 * @author Andrea Aime - OpenGeo
 */
public class BeanProcessFactoryTest extends WPSTestSupport {
    public class BeanProcessFactory extends AnnotatedBeanProcessFactory {
        public BeanProcessFactory() {
            super(new SimpleInternationalString("Some bean based processes custom processes"), "bean", BoundsProcess.class, NearestProcess.class, SnapProcess.class);
        }
    }

    BeanProcessFactoryTest.BeanProcessFactory factory;

    @Test
    public void testNames() {
        Set<Name> names = getNames();
        Assert.assertTrue(((names.size()) > 0));
        // System.out.println(names);
        Assert.assertTrue(names.contains(new NameImpl("bean", "Bounds")));
    }

    @Test
    public void testDescribeBounds() {
        NameImpl boundsName = new NameImpl("bean", "Bounds");
        InternationalString desc = factory.getDescription(boundsName);
        Assert.assertNotNull(desc);
        Map<String, Parameter<?>> params = factory.getParameterInfo(boundsName);
        Assert.assertEquals(1, params.size());
        Parameter<?> features = params.get("features");
        Assert.assertEquals(FeatureCollection.class, features.type);
        Assert.assertTrue(features.required);
        Map<String, Parameter<?>> result = factory.getResultInfo(boundsName, null);
        Assert.assertEquals(1, result.size());
        Parameter<?> bounds = result.get("bounds");
        Assert.assertEquals(ReferencedEnvelope.class, bounds.type);
    }

    @Test
    public void testExecuteBounds() throws ProcessException {
        // prepare a mock feature collection
        SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        tb.setName("test");
        final ReferencedEnvelope re = new ReferencedEnvelope((-10), 10, (-10), 10, null);
        FeatureCollection fc = new ListFeatureCollection(tb.buildFeatureType()) {
            @Override
            public synchronized ReferencedEnvelope getBounds() {
                return re;
            }
        };
        Process p = factory.create(new NameImpl("bean", "Bounds"));
        Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put("features", fc);
        Map<String, Object> result = p.execute(inputs, null);
        Assert.assertEquals(1, result.size());
        ReferencedEnvelope computed = ((ReferencedEnvelope) (result.get("bounds")));
        Assert.assertEquals(re, computed);
    }

    @Test
    public void testSPI() throws Exception {
        NameImpl boundsName = new NameImpl("bean", "Bounds");
        ProcessFactory factory = GeoServerProcessors.createProcessFactory(boundsName, false);
        Assert.assertNotNull(factory);
        Process buffer = GeoServerProcessors.createProcess(boundsName);
        Assert.assertNotNull(buffer);
    }
}

