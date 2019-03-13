package com.netflix.eureka.resources;


import InstanceStatus.DOWN;
import InstanceStatus.OUT_OF_SERVICE;
import InstanceStatus.UNKNOWN;
import Status.NOT_FOUND;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.eureka.AbstractTester;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class InstanceResourceTest extends AbstractTester {
    private final InstanceInfo testInstanceInfo = AbstractTester.createLocalInstance(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);

    private ApplicationResource applicationResource;

    private InstanceResource instanceResource;

    @Test
    public void testStatusOverrideReturnsNotFoundErrorCodeIfInstanceNotRegistered() throws Exception {
        Response response = instanceResource.statusUpdate(OUT_OF_SERVICE.name(), "false", "0");
        Assert.assertThat(response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(NOT_FOUND.getStatusCode())));
    }

    @Test
    public void testStatusOverrideDeleteReturnsNotFoundErrorCodeIfInstanceNotRegistered() throws Exception {
        Response response = instanceResource.deleteStatusUpdate(OUT_OF_SERVICE.name(), "false", "0");
        Assert.assertThat(response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(NOT_FOUND.getStatusCode())));
    }

    @Test
    public void testStatusOverrideDeleteIsAppliedToRegistry() throws Exception {
        // Override instance status
        registry.register(testInstanceInfo, false);
        registry.statusUpdate(testInstanceInfo.getAppName(), testInstanceInfo.getId(), OUT_OF_SERVICE, "0", false);
        Assert.assertThat(testInstanceInfo.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(OUT_OF_SERVICE)));
        // Remove the override
        Response response = instanceResource.deleteStatusUpdate("false", null, "0");
        Assert.assertThat(response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        Assert.assertThat(testInstanceInfo.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(UNKNOWN)));
    }

    @Test
    public void testStatusOverrideDeleteIsAppliedToRegistryAndProvidedStatusIsSet() throws Exception {
        // Override instance status
        registry.register(testInstanceInfo, false);
        registry.statusUpdate(testInstanceInfo.getAppName(), testInstanceInfo.getId(), OUT_OF_SERVICE, "0", false);
        Assert.assertThat(testInstanceInfo.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(OUT_OF_SERVICE)));
        // Remove the override
        Response response = instanceResource.deleteStatusUpdate("false", "DOWN", "0");
        Assert.assertThat(response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        Assert.assertThat(testInstanceInfo.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(DOWN)));
    }
}

