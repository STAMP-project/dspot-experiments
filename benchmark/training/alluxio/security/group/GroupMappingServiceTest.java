/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.security.group;


import GroupMappingService.Factory;
import alluxio.ConfigurationRule;
import alluxio.conf.InstancedConfiguration;
import alluxio.conf.PropertyKey;
import alluxio.security.group.provider.IdentityUserGroupsMapping;
import alluxio.util.ConfigurationUtils;
import java.io.Closeable;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link alluxio.security.group.GroupMappingService}.
 */
public final class GroupMappingServiceTest {
    /**
     * Tests the {@link GroupMappingService#getGroups(String)} method.
     */
    @Test
    public void group() throws Throwable {
        String userName = "alluxio-user1";
        InstancedConfiguration conf = new InstancedConfiguration(ConfigurationUtils.defaults());
        try (Closeable mConfigurationRule = new ConfigurationRule(PropertyKey.SECURITY_GROUP_MAPPING_CLASS, IdentityUserGroupsMapping.class.getName(), conf).toResource()) {
            GroupMappingService groups = Factory.get(conf);
            Assert.assertNotNull(groups);
            Assert.assertNotNull(groups.getGroups(userName));
            Assert.assertEquals(groups.getGroups(userName).size(), 1);
            Assert.assertEquals(groups.getGroups(userName).get(0), userName);
        }
    }
}

