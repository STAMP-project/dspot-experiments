package org.apereo.cas.hz;


import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import lombok.val;
import org.apereo.cas.configuration.model.support.hazelcast.HazelcastClusterProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * This is {@link HazelcastAwsDiscoveryStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class HazelcastAwsDiscoveryStrategyTests {
    @Test
    public void verifyAction() {
        val strategy = new HazelcastAwsDiscoveryStrategy();
        val properties = new HazelcastClusterProperties();
        val aws = properties.getDiscovery().getAws();
        aws.setAccessKey("AccessKey");
        aws.setSecretKey("Secret");
        aws.setIamRole("Role");
        aws.setHostHeader("Header");
        aws.setPort(1000);
        aws.setRegion("us-east-1");
        aws.setSecurityGroupName("Group");
        aws.setTagKey("TagKey");
        aws.setTagValue("TagValue");
        Assertions.assertNotNull(strategy.get(properties, Mockito.mock(JoinConfig.class), Mockito.mock(Config.class), Mockito.mock(NetworkConfig.class)));
    }
}

