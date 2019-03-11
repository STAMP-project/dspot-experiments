/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.common;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;

import static ExecutionConfig.PARALLELISM_DEFAULT;


public class ExecutionConfigTest extends TestLogger {
    @Test
    public void testDoubleTypeRegistration() {
        ExecutionConfig config = new ExecutionConfig();
        List<Class<?>> types = Arrays.<Class<?>>asList(Double.class, Integer.class, Double.class);
        List<Class<?>> expectedTypes = Arrays.<Class<?>>asList(Double.class, Integer.class);
        for (Class<?> tpe : types) {
            config.registerKryoType(tpe);
        }
        int counter = 0;
        for (Class<?> tpe : config.getRegisteredKryoTypes()) {
            Assert.assertEquals(tpe, expectedTypes.get((counter++)));
        }
        Assert.assertEquals(expectedTypes.size(), counter);
    }

    @Test
    public void testConfigurationOfParallelism() {
        ExecutionConfig config = new ExecutionConfig();
        // verify explicit change in parallelism
        int parallelism = 36;
        config.setParallelism(parallelism);
        Assert.assertEquals(parallelism, config.getParallelism());
        // verify that parallelism is reset to default flag value
        parallelism = PARALLELISM_DEFAULT;
        config.setParallelism(parallelism);
        Assert.assertEquals(parallelism, config.getParallelism());
    }

    @Test
    public void testDisableGenericTypes() {
        ExecutionConfig conf = new ExecutionConfig();
        TypeInformation<Object> typeInfo = new org.apache.flink.api.java.typeutils.GenericTypeInfo<Object>(Object.class);
        // by default, generic types are supported
        TypeSerializer<Object> serializer = typeInfo.createSerializer(conf);
        Assert.assertTrue((serializer instanceof KryoSerializer));
        // expect an exception when generic types are disabled
        conf.disableGenericTypes();
        try {
            typeInfo.createSerializer(conf);
            Assert.fail("should have failed with an exception");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void testExecutionConfigSerialization() throws IOException, ClassNotFoundException {
        final Random r = new Random();
        final int parallelism = 1 + (r.nextInt(10));
        final boolean closureCleanerEnabled = r.nextBoolean();
        final boolean forceAvroEnabled = r.nextBoolean();
        final boolean forceKryoEnabled = r.nextBoolean();
        final boolean disableGenericTypes = r.nextBoolean();
        final boolean objectReuseEnabled = r.nextBoolean();
        final boolean sysoutLoggingEnabled = r.nextBoolean();
        final ExecutionConfig config = new ExecutionConfig();
        if (closureCleanerEnabled) {
            config.enableClosureCleaner();
        } else {
            config.disableClosureCleaner();
        }
        if (forceAvroEnabled) {
            config.enableForceAvro();
        } else {
            config.disableForceAvro();
        }
        if (forceKryoEnabled) {
            config.enableForceKryo();
        } else {
            config.disableForceKryo();
        }
        if (disableGenericTypes) {
            config.disableGenericTypes();
        } else {
            config.enableGenericTypes();
        }
        if (objectReuseEnabled) {
            config.enableObjectReuse();
        } else {
            config.disableObjectReuse();
        }
        if (sysoutLoggingEnabled) {
            config.enableSysoutLogging();
        } else {
            config.disableSysoutLogging();
        }
        config.setParallelism(parallelism);
        final ExecutionConfig copy1 = CommonTestUtils.createCopySerializable(config);
        final ExecutionConfig copy2 = new org.apache.flink.util.SerializedValue(config).deserializeValue(getClass().getClassLoader());
        Assert.assertNotNull(copy1);
        Assert.assertNotNull(copy2);
        Assert.assertEquals(config, copy1);
        Assert.assertEquals(config, copy2);
        Assert.assertEquals(closureCleanerEnabled, copy1.isClosureCleanerEnabled());
        Assert.assertEquals(forceAvroEnabled, copy1.isForceAvroEnabled());
        Assert.assertEquals(forceKryoEnabled, copy1.isForceKryoEnabled());
        Assert.assertEquals(disableGenericTypes, copy1.hasGenericTypesDisabled());
        Assert.assertEquals(objectReuseEnabled, copy1.isObjectReuseEnabled());
        Assert.assertEquals(sysoutLoggingEnabled, copy1.isSysoutLoggingEnabled());
        Assert.assertEquals(parallelism, copy1.getParallelism());
    }
}

