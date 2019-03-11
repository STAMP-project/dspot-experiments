/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps;


import java.util.ArrayList;
import java.util.List;
import org.easymock.IArgumentMatcher;
import org.geoserver.config.ConfigurationListener;
import org.geoserver.config.GeoServer;
import org.junit.Assert;
import org.junit.Test;


public class WPSInitializerTest {
    WPSInitializer initer;

    @Test
    public void testNoSave() throws Exception {
        GeoServer gs = createMock(GeoServer.class);
        List<ConfigurationListener> listeners = new ArrayList();
        gs.addListener(capture(listeners));
        expectLastCall().atLeastOnce();
        // load all process groups so there is no call to save
        List<ProcessGroupInfo> procGroups = WPSInitializer.lookupProcessGroups();
        WPSInfo wps = createNiceMock(WPSInfo.class);
        expect(wps.getProcessGroups()).andReturn(procGroups).anyTimes();
        replay(wps);
        expect(gs.getService(WPSInfo.class)).andReturn(wps).anyTimes();
        replay(gs);
        initer.initialize(gs);
        Assert.assertEquals(1, listeners.size());
        ConfigurationListener l = listeners.get(0);
        l.handleGlobalChange(null, null, null, null);
        l.handlePostGlobalChange(null);
        verify(gs);
    }

    @Test
    public void testSingleSave() throws Exception {
        GeoServer gs = createMock(GeoServer.class);
        List<ConfigurationListener> listeners = new ArrayList();
        gs.addListener(capture(listeners));
        expectLastCall().atLeastOnce();
        // empty list should cause save
        List<ProcessGroupInfo> procGroups = new ArrayList();
        WPSInfo wps = createNiceMock(WPSInfo.class);
        expect(wps.getProcessGroups()).andReturn(procGroups).anyTimes();
        replay(wps);
        expect(gs.getService(WPSInfo.class)).andReturn(wps).anyTimes();
        gs.save(wps);
        expectLastCall().once();
        replay(gs);
        initer.initialize(gs);
        Assert.assertEquals(1, listeners.size());
        ConfigurationListener l = listeners.get(0);
        l.handleGlobalChange(null, null, null, null);
        l.handlePostGlobalChange(null);
        verify(gs);
    }

    static class ListenerCapture implements IArgumentMatcher {
        List<ConfigurationListener> listeners;

        public ListenerCapture(List<ConfigurationListener> listeners) {
            this.listeners = listeners;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof ConfigurationListener) {
                listeners.add(((ConfigurationListener) (argument)));
                return true;
            }
            return false;
        }

        @Override
        public void appendTo(StringBuffer buffer) {
            buffer.append("ListenerCapture");
        }
    }
}

