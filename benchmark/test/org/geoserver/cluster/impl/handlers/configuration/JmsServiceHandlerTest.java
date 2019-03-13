/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster.impl.handlers.configuration;


import org.geoserver.test.GeoServerSystemTestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class JmsServiceHandlerTest extends GeoServerSystemTestSupport {
    @Test
    public void testGlobalServiceSimpleCrud() throws Exception {
        // service events handler
        JMSServiceHandler handler = createHandler();
        // create a new global service
        handler.synchronize(createNewServiceEvent("jms-test-service-1", "jms-test-service", "global-jms-test-service", null));
        checkServiceExists("jms-test-service", "global-jms-test-service", null);
        // update global service
        handler.synchronize(createModifyServiceEvent("jms-test-service", "global-jms-test-service-updated", null));
        checkServiceExists("jms-test-service", "global-jms-test-service-updated", null);
        // delete global service
        handler.synchronize(createRemoveServiceEvent("jms-test-service", null));
        Assert.assertThat(findService("jms-test-service", null), CoreMatchers.nullValue());
    }

    @Test
    public void testVirtualServiceSimpleCrud() throws Exception {
        // service events handler
        JMSServiceHandler handler = createHandler();
        // create a new virtual service
        handler.synchronize(createNewServiceEvent("jms-test-service-2", "jms-test-service", "virtual-jms-test-service", "jms-test-workspace"));
        checkServiceExists("jms-test-service", "virtual-jms-test-service", "jms-test-workspace");
        // update virtual service
        handler.synchronize(createModifyServiceEvent("jms-test-service", "virtual-jms-test-service-updated", "jms-test-workspace"));
        checkServiceExists("jms-test-service", "virtual-jms-test-service-updated", "jms-test-workspace");
        // delete virtual service
        handler.synchronize(createRemoveServiceEvent("jms-test-service", "jms-test-workspace"));
        Assert.assertThat(findService("jms-test-service", "jms-test-workspace"), CoreMatchers.nullValue());
    }

    @Test
    public void testGlobalAndVirtualServiceSimpleCrud() throws Exception {
        // service events handler
        JMSServiceHandler handler = createHandler();
        // create a new global and virtual service
        handler.synchronize(createNewServiceEvent("jms-test-service-1", "jms-test-service", "global-jms-test-service", null));
        checkServiceExists("jms-test-service", "global-jms-test-service", null);
        handler.synchronize(createNewServiceEvent("jms-test-service-2", "jms-test-service", "virtual-jms-test-service", "jms-test-workspace"));
        checkServiceExists("jms-test-service", "virtual-jms-test-service", "jms-test-workspace");
        // update global service
        handler.synchronize(createModifyServiceEvent("jms-test-service", "global-jms-test-service-updated", null));
        checkServiceExists("jms-test-service", "global-jms-test-service-updated", null);
        checkServiceExists("jms-test-service", "virtual-jms-test-service", "jms-test-workspace");
        // update virtual service
        handler.synchronize(createModifyServiceEvent("jms-test-service", "virtual-jms-test-service-updated", "jms-test-workspace"));
        checkServiceExists("jms-test-service", "virtual-jms-test-service-updated", "jms-test-workspace");
        checkServiceExists("jms-test-service", "global-jms-test-service-updated", null);
        // delete virtual service
        handler.synchronize(createRemoveServiceEvent("jms-test-service", "jms-test-workspace"));
        Assert.assertThat(findService("jms-test-service", "jms-test-workspace"), CoreMatchers.nullValue());
        Assert.assertThat(findService("jms-test-service", null), CoreMatchers.notNullValue());
        // delete global service
        handler.synchronize(createRemoveServiceEvent("jms-test-service", null));
        Assert.assertThat(findService("jms-test-service", null), CoreMatchers.nullValue());
    }

    @Test
    public void testUpdatingNonExistingVirtualService() throws Exception {
        // service events handler
        JMSServiceHandler handler = createHandler();
        // create a new global and virtual service
        handler.synchronize(createNewServiceEvent("jms-test-service-3", "jms-test-service", "global-jms-test-service", null));
        checkServiceExists("jms-test-service", "global-jms-test-service", null);
        handler.synchronize(createNewServiceEvent("jms-test-service-4", "jms-test-service", "virtual-jms-test-service", "jms-test-workspace"));
        checkServiceExists("jms-test-service", "virtual-jms-test-service", "jms-test-workspace");
        // create update virtual service event
        handler.synchronize(createModifyServiceEvent("jms-test-service", "virtual-jms-test-service-updated", "jms-test-workspace"));
        // remove virtual service
        handler.synchronize(createRemoveServiceEvent("jms-test-service", "jms-test-workspace"));
        Assert.assertThat(findService("jms-test-service", "jms-test-workspace"), CoreMatchers.nullValue());
        // check the update result
        checkServiceExists("jms-test-service", "global-jms-test-service", null);
    }
}

