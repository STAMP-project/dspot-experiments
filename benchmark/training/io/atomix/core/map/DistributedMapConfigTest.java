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
package io.atomix.core.map;


import MultiPrimaryProtocol.TYPE;
import io.atomix.core.Atomix;
import io.atomix.core.types.Type1;
import io.atomix.core.types.Type2;
import io.atomix.core.types.Type3;
import org.junit.Assert;
import org.junit.Test;


/**
 * Distributed map configuration test.
 */
public class DistributedMapConfigTest {
    @Test
    public void testLoadConfig() throws Exception {
        DistributedMapConfig config = Atomix.config(getClass().getClassLoader().getResource("primitives.conf").getPath()).getPrimitive("map");
        Assert.assertEquals("map", config.getName());
        Assert.assertEquals(TYPE, config.getProtocolConfig().getType());
        Assert.assertFalse(config.isReadOnly());
        Assert.assertTrue(config.getNamespaceConfig().isRegistrationRequired());
        Assert.assertSame(Type1.class, config.getKeyType());
        Assert.assertSame(Type2.class, config.getValueType());
        Assert.assertSame(Type3.class, config.getExtraTypes().get(0));
        Assert.assertSame(Type1.class, config.getNamespaceConfig().getTypes().get(0).getType());
        Assert.assertSame(Type2.class, config.getNamespaceConfig().getTypes().get(1).getType());
    }
}

