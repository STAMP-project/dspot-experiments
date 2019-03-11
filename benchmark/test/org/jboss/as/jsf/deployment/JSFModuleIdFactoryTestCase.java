/**
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.jsf.deployment;


import JsfVersionMarker.JSF_2_0;
import JsfVersionMarker.WAR_BUNDLES_JSF_IMPL;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the JSFModuleIdFactory
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2012 Red Hat Inc.
 */
public class JSFModuleIdFactoryTestCase {
    private static final String API_MODULE = "javax.faces.api";

    private static final String IMPL_MODULE = "com.sun.jsf-impl";

    private static final String INJECTION_MODULE = "org.jboss.as.jsf-injection";

    private static final JSFModuleIdFactory factory = JSFModuleIdFactory.getInstance();

    /* This test is for the unusual situation where there is no module path.
    If you want to run this test then uncomment the test and remove the module.path
    system property in pom.xml.  Note that if you do this, other tests will fail.
    @Test
    public void noModulePathTest() {
    JSFModuleIdFactory factory = JSFModuleIdFactory.getInstance();
    Assert.assertEquals(1, factory.getActiveJSFVersions().size());

    Assert.assertEquals(API_MODULE, factory.getApiModId("main").getName());
    Assert.assertEquals("main", factory.getApiModId("main").getSlot());
    Assert.assertEquals(IMPL_MODULE, factory.getImplModId("main").getName());
    Assert.assertEquals("main", factory.getImplModId("main").getSlot());
    Assert.assertEquals(INJECTION_MODULE, factory.getInjectionModId("main").getName());
    Assert.assertEquals("main", factory.getInjectionModId("main").getSlot());
    }
     */
    @Test
    public void getActiveJSFVersionsTest() {
        List<String> versions = JSFModuleIdFactoryTestCase.factory.getActiveJSFVersions();
        Assert.assertEquals(3, versions.size());
        Assert.assertTrue(versions.contains("main"));
        Assert.assertFalse(versions.contains("1.2"));
        Assert.assertTrue(versions.contains("myfaces"));
        Assert.assertTrue(versions.contains("myfaces2"));
    }

    @Test
    public void computeSlotTest() {
        Assert.assertEquals("main", JSFModuleIdFactoryTestCase.factory.computeSlot("main"));
        Assert.assertEquals("main", JSFModuleIdFactoryTestCase.factory.computeSlot(null));
        Assert.assertEquals("main", JSFModuleIdFactoryTestCase.factory.computeSlot(JSF_2_0));
        Assert.assertEquals("myfaces2", JSFModuleIdFactoryTestCase.factory.computeSlot("myfaces2"));
    }

    @Test
    public void validSlotTest() {
        Assert.assertTrue(JSFModuleIdFactoryTestCase.factory.isValidJSFSlot("main"));
        Assert.assertFalse(JSFModuleIdFactoryTestCase.factory.isValidJSFSlot("1.2"));
        Assert.assertTrue(JSFModuleIdFactoryTestCase.factory.isValidJSFSlot("myfaces"));
        Assert.assertTrue(JSFModuleIdFactoryTestCase.factory.isValidJSFSlot("myfaces2"));
        Assert.assertTrue(JSFModuleIdFactoryTestCase.factory.isValidJSFSlot(JSF_2_0));
        Assert.assertFalse(JSFModuleIdFactoryTestCase.factory.isValidJSFSlot(WAR_BUNDLES_JSF_IMPL));
        Assert.assertFalse(JSFModuleIdFactoryTestCase.factory.isValidJSFSlot("bogus"));
        Assert.assertFalse(JSFModuleIdFactoryTestCase.factory.isValidJSFSlot("bogus2"));
    }

    @Test
    public void modIdsTest() {
        Assert.assertEquals(JSFModuleIdFactoryTestCase.API_MODULE, JSFModuleIdFactoryTestCase.factory.getApiModId("main").getName());
        Assert.assertEquals("main", JSFModuleIdFactoryTestCase.factory.getApiModId("main").getSlot());
        Assert.assertEquals(JSFModuleIdFactoryTestCase.IMPL_MODULE, JSFModuleIdFactoryTestCase.factory.getImplModId("main").getName());
        Assert.assertEquals("main", JSFModuleIdFactoryTestCase.factory.getImplModId("main").getSlot());
        Assert.assertEquals(JSFModuleIdFactoryTestCase.INJECTION_MODULE, JSFModuleIdFactoryTestCase.factory.getInjectionModId("main").getName());
        Assert.assertEquals("main", JSFModuleIdFactoryTestCase.factory.getInjectionModId("main").getSlot());
        Assert.assertEquals(JSFModuleIdFactoryTestCase.API_MODULE, JSFModuleIdFactoryTestCase.factory.getApiModId("myfaces").getName());
        Assert.assertEquals("myfaces", JSFModuleIdFactoryTestCase.factory.getApiModId("myfaces").getSlot());
        Assert.assertEquals(JSFModuleIdFactoryTestCase.IMPL_MODULE, JSFModuleIdFactoryTestCase.factory.getImplModId("myfaces").getName());
        Assert.assertEquals("myfaces", JSFModuleIdFactoryTestCase.factory.getImplModId("myfaces").getSlot());
        Assert.assertEquals(JSFModuleIdFactoryTestCase.INJECTION_MODULE, JSFModuleIdFactoryTestCase.factory.getInjectionModId("myfaces").getName());
        Assert.assertEquals("myfaces", JSFModuleIdFactoryTestCase.factory.getInjectionModId("myfaces").getSlot());
        Assert.assertEquals(JSFModuleIdFactoryTestCase.API_MODULE, JSFModuleIdFactoryTestCase.factory.getApiModId("myfaces2").getName());
        Assert.assertEquals("myfaces2", JSFModuleIdFactoryTestCase.factory.getApiModId("myfaces2").getSlot());
        Assert.assertEquals(JSFModuleIdFactoryTestCase.IMPL_MODULE, JSFModuleIdFactoryTestCase.factory.getImplModId("myfaces2").getName());
        Assert.assertEquals("myfaces2", JSFModuleIdFactoryTestCase.factory.getImplModId("myfaces2").getSlot());
        Assert.assertEquals(JSFModuleIdFactoryTestCase.INJECTION_MODULE, JSFModuleIdFactoryTestCase.factory.getInjectionModId("myfaces2").getName());
        Assert.assertEquals("myfaces2", JSFModuleIdFactoryTestCase.factory.getInjectionModId("myfaces2").getSlot());
    }
}

