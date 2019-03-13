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
package io.atomix.core.registry;


import ConsensusProfile.TYPE;
import Profile.Type;
import io.atomix.core.AtomixRegistry;
import io.atomix.core.counter.AtomicCounterType;
import io.atomix.primitive.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;


/**
 * Static registry test.
 */
public class SimpleRegistryTest {
    @Test
    public void testStaticRegistryBuilder() throws Exception {
        AtomixRegistry registry = SimpleRegistry.builder().addProfileType(TYPE).addDiscoveryProviderType(BootstrapDiscoveryProvider.TYPE).addPrimitiveType(AtomicCounterType.instance()).addProtocolType(MultiRaftProtocol.TYPE).addPartitionGroupType(RaftPartitionGroup.TYPE).build();
        Assert.assertEquals(TYPE, registry.getType(Type.class, "consensus"));
        Assert.assertEquals(BootstrapDiscoveryProvider.TYPE, registry.getType(NodeDiscoveryProvider.Type.class, "bootstrap"));
        Assert.assertEquals(AtomicCounterType.instance(), registry.getType(PrimitiveType.class, "atomic-counter"));
        Assert.assertEquals(MultiRaftProtocol.TYPE, registry.getType(PrimitiveProtocol.Type.class, "multi-raft"));
        Assert.assertEquals(RaftPartitionGroup.TYPE, registry.getType(PartitionGroup.Type.class, "raft"));
    }
}

