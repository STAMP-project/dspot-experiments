package com.netflix.eureka.registry;


import InstanceStatus.OUT_OF_SERVICE;
import InstanceStatus.UP;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.AbstractTester;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.resources.ServerCodecs;
import org.junit.Test;


/**
 * Created by Nikos Michalakis on 7/14/16.
 */
public class AwsInstanceRegistryTest extends InstanceRegistryTest {
    @Test
    public void testOverridesWithAsgEnabledThenDisabled() {
        // Regular registration first
        InstanceInfo myInstance = AwsInstanceRegistryTest.createLocalUpInstanceWithAsg(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);
        registerInstanceLocally(myInstance);
        verifyLocalInstanceStatus(myInstance.getId(), UP);
        // Now we disable the ASG and we should expect OUT_OF_SERVICE status.
        getAwsAsgUtil().setStatus(myInstance.getASGName(), false);
        myInstance = AwsInstanceRegistryTest.createLocalUpInstanceWithAsg(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);
        registerInstanceLocally(myInstance);
        verifyLocalInstanceStatus(myInstance.getId(), OUT_OF_SERVICE);
        // Now we re-enable the ASG and we should expect UP status.
        getAwsAsgUtil().setStatus(myInstance.getASGName(), true);
        myInstance = AwsInstanceRegistryTest.createLocalUpInstanceWithAsg(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);
        registerInstanceLocally(myInstance);
        verifyLocalInstanceStatus(myInstance.getId(), UP);
    }

    private static class TestAwsInstanceRegistry extends AwsInstanceRegistry {
        public TestAwsInstanceRegistry(EurekaServerConfig serverConfig, EurekaClientConfig clientConfig, ServerCodecs serverCodecs, EurekaClient eurekaClient) {
            super(serverConfig, clientConfig, serverCodecs, eurekaClient);
        }

        @Override
        public boolean isLeaseExpirationEnabled() {
            return false;
        }

        @Override
        public InstanceInfo getNextServerFromEureka(String virtualHostname, boolean secure) {
            return null;
        }
    }
}

