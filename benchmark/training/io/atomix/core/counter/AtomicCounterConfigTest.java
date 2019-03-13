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
package io.atomix.core.counter;


import MultiPrimaryProtocol.TYPE;
import io.atomix.core.Atomix;
import org.junit.Assert;
import org.junit.Test;


/**
 * Distributed counter configuration test.
 */
public class AtomicCounterConfigTest {
    @Test
    public void testLoadConfig() throws Exception {
        AtomicCounterConfig config = Atomix.config(getClass().getClassLoader().getResource("primitives.conf").getPath()).getPrimitive("atomic-counter");
        Assert.assertEquals("atomic-counter", config.getName());
        Assert.assertEquals(TYPE, config.getProtocolConfig().getType());
        Assert.assertFalse(config.isReadOnly());
    }
}

