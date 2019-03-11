/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.flow.config;


import Intervals.d;
import Intervals.m;
import Intervals.s;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.geoserver.flow.ControllerPriorityComparator;
import org.geoserver.flow.FlowController;
import org.geoserver.flow.controller.BasicOWSController;
import org.geoserver.flow.controller.GlobalFlowController;
import org.geoserver.flow.controller.IpFlowController;
import org.geoserver.flow.controller.IpRequestMatcher;
import org.geoserver.flow.controller.RateFlowController;
import org.geoserver.flow.controller.SingleIpFlowController;
import org.geoserver.flow.controller.UserConcurrentFlowController;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Files;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.geoserver.security.PropertyFileWatcher;
import org.junit.Assert;
import org.junit.Test;


public class DefaultControlFlowConfigurationTest {
    @Test
    public void testParsing() throws Exception {
        Properties p = new PropertyFileWatcher.LinkedProperties();
        p.put("timeout", "10");
        p.put("ows.global", "100");
        p.put("ows.wms.getmap", "8");
        p.put("user", "6");
        p.put("ip", "12");
        p.put("ip.192.168.1.8", "14");
        p.put("ip.192.168.1.10", "15");
        p.put("ip.blacklist", "192.168.1.1,192.168.1.2");
        p.put("ip.whitelist", "192.168.1.3,192.168.1.4");
        p.put("user.ows", "20/s");
        p.put("user.ows.wms", "300/m;3s");
        p.put("ip.ows.wms.getmap", "100/m;3s");
        p.put("ip.ows.wps.execute", "50/d;60s");
        DefaultControlFlowConfigurator configurator = new DefaultControlFlowConfigurator(new DefaultControlFlowConfigurationTest.FixedWatcher(p));
        Assert.assertTrue(configurator.isStale());
        List<FlowController> controllers = configurator.buildFlowControllers();
        Collections.sort(controllers, new ControllerPriorityComparator());
        Assert.assertFalse(configurator.isStale());
        Assert.assertEquals((10 * 1000), configurator.getTimeout());
        Assert.assertEquals(10, controllers.size());
        System.out.println(controllers);
        Assert.assertTrue(((controllers.get(0)) instanceof RateFlowController));
        RateFlowController rfc = ((RateFlowController) (controllers.get(0)));
        Assert.assertEquals("wms.getmap", rfc.getMatcher().toString());
        Assert.assertEquals(100, rfc.getMaxRequests());
        Assert.assertEquals(m.getDuration(), rfc.getTimeInterval());
        Assert.assertEquals(3000, rfc.getDelay());
        Assert.assertTrue(((controllers.get(1)) instanceof RateFlowController));
        rfc = ((RateFlowController) (controllers.get(1)));
        Assert.assertEquals("wms", rfc.getMatcher().toString());
        Assert.assertEquals(300, rfc.getMaxRequests());
        Assert.assertEquals(m.getDuration(), rfc.getTimeInterval());
        Assert.assertEquals(3000, rfc.getDelay());
        Assert.assertTrue(((controllers.get(2)) instanceof RateFlowController));
        rfc = ((RateFlowController) (controllers.get(2)));
        Assert.assertEquals("Any OGC request", rfc.getMatcher().toString());
        Assert.assertEquals(20, rfc.getMaxRequests());
        Assert.assertEquals(s.getDuration(), rfc.getTimeInterval());
        Assert.assertEquals(0, rfc.getDelay());
        Assert.assertTrue(((controllers.get(3)) instanceof UserConcurrentFlowController));
        UserConcurrentFlowController uc = ((UserConcurrentFlowController) (controllers.get(3)));
        Assert.assertEquals(6, uc.getPriority());
        Assert.assertTrue(((controllers.get(4)) instanceof BasicOWSController));
        BasicOWSController oc = ((BasicOWSController) (controllers.get(4)));
        Assert.assertEquals(8, oc.getPriority());
        Assert.assertEquals("wms.getmap", oc.getMatcher().toString());
        Assert.assertTrue(((controllers.get(5)) instanceof IpFlowController));
        IpFlowController ipFc = ((IpFlowController) (controllers.get(5)));
        Assert.assertEquals(12, ipFc.getPriority());
        Assert.assertTrue(((controllers.get(6)) instanceof SingleIpFlowController));
        SingleIpFlowController ipSc = ((SingleIpFlowController) (controllers.get(6)));
        Assert.assertEquals(14, ipSc.getPriority());
        IpRequestMatcher ipMatcher = ((IpRequestMatcher) (ipSc.getMatcher()));
        Assert.assertEquals("192.168.1.8", ipMatcher.getIp());
        Assert.assertTrue(((controllers.get(7)) instanceof SingleIpFlowController));
        ipMatcher = ((IpRequestMatcher) (getMatcher()));
        Assert.assertEquals("192.168.1.10", ipMatcher.getIp());
        Assert.assertTrue(((controllers.get(8)) instanceof GlobalFlowController));
        GlobalFlowController gc = ((GlobalFlowController) (controllers.get(8)));
        Assert.assertEquals(100, gc.getPriority());
        Assert.assertTrue(((controllers.get(9)) instanceof RateFlowController));
        rfc = ((RateFlowController) (controllers.get(9)));
        Assert.assertEquals("wps.execute", rfc.getMatcher().toString());
        Assert.assertEquals(50, rfc.getMaxRequests());
        Assert.assertEquals(d.getDuration(), rfc.getTimeInterval());
        Assert.assertEquals(60000, rfc.getDelay());
        // store the properties into a temp folder and relaod
        Assert.assertTrue(configurator.getFileLocations().isEmpty());
        File tmpDir = DefaultControlFlowConfigurationTest.createTempDir();
        GeoServerResourceLoader resourceLoader = new GeoServerResourceLoader(tmpDir);
        configurator.saveConfiguration(resourceLoader);
        Resource controlFlowProps = Files.asResource(resourceLoader.find("controlflow.properties"));
        Assert.assertTrue(Resources.exists(controlFlowProps));
        PropertyFileWatcher savedProps = new PropertyFileWatcher(controlFlowProps);
        Assert.assertEquals(savedProps.getProperties(), p);
    }

    @Test
    public void testParsingPriority() throws Exception {
        Properties p = new Properties();
        p.put("timeout", "10");
        p.put("ows.global", "100");
        p.put("ows.priority.http", "gs-priority,3");
        p.put("ows.wms", "6");
        p.put("ows.wfs.getFeature", "12");
        checkPriorityParsing(p);
    }

    @Test
    public void testParsingPriorityWithSpaces() throws Exception {
        Properties p = new Properties();
        p.put("timeout", "10");
        p.put("ows.global", "100");
        p.put("ows.priority.http", " gs-priority , 3 ");
        p.put("ows.wms", "6");
        p.put("ows.wfs.getFeature", "12");
        checkPriorityParsing(p);
    }

    static class FixedWatcher extends PropertyFileWatcher {
        boolean stale = true;

        Properties properties;

        public FixedWatcher(Properties properties) {
            super(((Resource) (null)));
            this.properties = properties;
        }

        @Override
        public boolean isStale() {
            if (stale) {
                stale = false;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Properties getProperties() throws IOException {
            return properties;
        }
    }
}

