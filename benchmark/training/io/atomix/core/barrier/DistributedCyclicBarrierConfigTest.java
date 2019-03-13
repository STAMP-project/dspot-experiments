/**
 * Copyright 2018-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.core.barrier;


import MultiPrimaryProtocol.TYPE;
import io.atomix.core.Atomix;
import io.atomix.protocols.raft.MultiRaftProtocolConfig;
import io.atomix.utils.serializer.NamespaceConfig;
import org.junit.Assert;
import org.junit.Test;


/**
 * Cyclic barrier configuration test.
 */
public class DistributedCyclicBarrierConfigTest {
    @Test
    public void testConfig() throws Exception {
        DistributedCyclicBarrierConfig config = new DistributedCyclicBarrierConfig();
        Assert.assertNull(config.getName());
        Assert.assertEquals(DistributedCyclicBarrierType.instance(), config.getType());
        Assert.assertNull(config.getNamespaceConfig());
        Assert.assertNull(config.getProtocolConfig());
        Assert.assertFalse(config.isReadOnly());
        config.setName("foo");
        config.setNamespaceConfig(new NamespaceConfig().setName("test").setCompatible(true).setRegistrationRequired(false));
        config.setProtocolConfig(new MultiRaftProtocolConfig().setGroup("test-group"));
        config.setReadOnly(true);
        Assert.assertEquals("foo", config.getName());
        Assert.assertEquals("test", config.getNamespaceConfig().getName());
        Assert.assertEquals("test-group", getGroup());
        Assert.assertTrue(config.isReadOnly());
    }

    @Test
    public void testLoadConfig() throws Exception {
        DistributedCyclicBarrierConfig config = Atomix.config(getClass().getClassLoader().getResource("primitives.conf").getPath()).getPrimitive("cyclic-barrier");
        Assert.assertEquals("cyclic-barrier", config.getName());
        Assert.assertEquals(TYPE, config.getProtocolConfig().getType());
        Assert.assertFalse(config.isReadOnly());
    }
}

