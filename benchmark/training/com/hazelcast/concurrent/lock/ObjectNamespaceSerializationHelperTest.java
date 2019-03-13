/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.concurrent.lock;


import Versions.V3_8;
import Versions.V3_9;
import com.hazelcast.internal.serialization.impl.SerializationServiceV1;
import com.hazelcast.spi.DefaultObjectNamespace;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.version.Version;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ObjectNamespaceSerializationHelperTest extends HazelcastTestSupport {
    private SerializationServiceV1 ss;

    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(ObjectNamespaceSerializationHelper.class);
    }

    @Test
    public void writeNamespaceCompatibly() throws Exception {
        final DefaultObjectNamespace defaultNS = new DefaultObjectNamespace("service", "object");
        final DistributedObjectNamespace distributedNS = new DistributedObjectNamespace("service", "object");
        for (Version v : Arrays.asList(V3_8, V3_9)) {
            Assert.assertTrue(Arrays.equals(serialize(defaultNS, v), serialize(distributedNS, v)));
        }
    }

    @Test
    public void readNamespaceCompatibly() throws Exception {
        final DefaultObjectNamespace defaultNS = new DefaultObjectNamespace("service", "object");
        final DistributedObjectNamespace distributedNS = new DistributedObjectNamespace("service", "object");
        for (Version v : Arrays.asList(V3_8, V3_9)) {
            final ObjectNamespace o1 = serialiseDeserialise(defaultNS, v);
            final ObjectNamespace o2 = serialiseDeserialise(distributedNS, v);
            Assert.assertTrue(((o1 instanceof DistributedObjectNamespace) && (o2 instanceof DistributedObjectNamespace)));
            Assert.assertEquals(o1, o2);
        }
    }
}

