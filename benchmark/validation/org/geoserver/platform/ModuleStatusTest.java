/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
/**
 *
 *
 * @author Morgan Thompson - Boundless
 */
package org.geoserver.platform;


import java.util.List;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;


/**
 * Unit tests for {@link ModuleStatusImpl}
 *
 * @author Morgan Thompson - Boundless
 */
public class ModuleStatusTest {
    @Test
    public void testModuleStatus() {
        ModuleStatus status = createMock("testStatus", ModuleStatus.class);
        expect(status.getName()).andReturn("test").atLeastOnce();
        ModuleStatusImpl impl = new ModuleStatusImpl();
        impl.setName("default");
        ApplicationContext appContext = createMock(ApplicationContext.class);
        expect(appContext.getBean("testStatus")).andStubReturn(status);
        expect(appContext.getBean("defaultStatus")).andStubReturn(impl);
        GeoServerExtensions gse = new GeoServerExtensions();
        gse.setApplicationContext(appContext);
        expect(appContext.getBeanNamesForType(ExtensionFilter.class)).andReturn(new String[0]);
        expect(appContext.getBeanNamesForType(ModuleStatus.class)).andStubReturn(new String[]{ "testStatus", "defaultStatus" });
        expect(appContext.isSingleton(((String) (anyObject())))).andReturn(true).anyTimes();
        expect(appContext.getBeanNamesForType(ExtensionProvider.class)).andReturn(new String[0]);
        EasyMock.replay(status, appContext);
        List<ModuleStatus> list = gse.extensions(ModuleStatus.class);
        Assert.assertEquals("test interfact mock", "test", status.getName());
        Assert.assertEquals("found module status", 2, list.size());
        boolean foundDefault = false;
        boolean foundTest = false;
        for (ModuleStatus s : list) {
            if ("default".equals(s.getName())) {
                foundDefault = true;
            }
            if ("test".equals(s.getName())) {
                foundTest = true;
            }
        }
        Assert.assertTrue(foundDefault);
        Assert.assertTrue(foundTest);
        EasyMock.verify(status);
    }

    @Test
    public void testModuleStatusBooleans() {
        ModuleStatusImpl impl = new ModuleStatusImpl();
        impl.setAvailable(false);
        impl.setEnabled(false);
        Assert.assertEquals(impl.isAvailable(), false);
        Assert.assertEquals(impl.isEnabled(), false);
    }
}

