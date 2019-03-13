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
package com.hazelcast.internal.metrics.impl;


import com.hazelcast.internal.metrics.DoubleGauge;
import com.hazelcast.internal.metrics.LongGauge;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.util.counters.Counter;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class RegisterAnnotatedFieldsTest extends HazelcastTestSupport {
    private MetricsRegistryImpl metricsRegistry;

    @Test
    public void register_customName() {
        RegisterAnnotatedFieldsTest.ObjectLongGaugeFieldWithName object = new RegisterAnnotatedFieldsTest.ObjectLongGaugeFieldWithName();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.myfield");
        object.field = 10;
        Assert.assertEquals(object.field, gauge.read());
    }

    public class ObjectLongGaugeFieldWithName {
        @Probe(name = "myfield")
        private long field;
    }

    @Test
    public void register_primitiveInteger() {
        RegisterAnnotatedFieldsTest.PrimitiveIntegerField object = new RegisterAnnotatedFieldsTest.PrimitiveIntegerField();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.field");
        object.field = 10;
        Assert.assertEquals(object.field, gauge.read());
    }

    public class PrimitiveIntegerField {
        @Probe
        private int field;
    }

    @Test
    public void register_primitiveLong() {
        RegisterAnnotatedFieldsTest.PrimitiveLongField object = new RegisterAnnotatedFieldsTest.PrimitiveLongField();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.field");
        object.field = 10;
        Assert.assertEquals(object.field, gauge.read());
    }

    public class PrimitiveLongField {
        @Probe
        private long field;
    }

    @Test
    public void register_primitiveDouble() {
        RegisterAnnotatedFieldsTest.PrimitiveDoubleField object = new RegisterAnnotatedFieldsTest.PrimitiveDoubleField();
        metricsRegistry.scanAndRegister(object, "foo");
        DoubleGauge gauge = metricsRegistry.newDoubleGauge("foo.field");
        object.field = 10;
        Assert.assertEquals(object.field, gauge.read(), 0.1);
    }

    public class PrimitiveDoubleField {
        @Probe
        private double field;
    }

    @Test
    public void register_concurrentHashMap() {
        RegisterAnnotatedFieldsTest.ConcurrentMapField object = new RegisterAnnotatedFieldsTest.ConcurrentMapField();
        object.field.put("foo", "foo");
        object.field.put("bar", "bar");
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.field");
        Assert.assertEquals(object.field.size(), gauge.read());
        object.field = null;
        Assert.assertEquals(0, gauge.read());
    }

    public class ConcurrentMapField {
        @Probe
        private ConcurrentHashMap field = new ConcurrentHashMap();
    }

    @Test
    public void register_counterFields() {
        RegisterAnnotatedFieldsTest.CounterField object = new RegisterAnnotatedFieldsTest.CounterField();
        object.field.inc(10);
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.field");
        Assert.assertEquals(10, gauge.read());
        object.field = null;
        Assert.assertEquals(0, gauge.read());
    }

    public class CounterField {
        @Probe
        private Counter field = SwCounter.newSwCounter();
    }

    @Test
    public void register_staticField() {
        RegisterAnnotatedFieldsTest.StaticField object = new RegisterAnnotatedFieldsTest.StaticField();
        RegisterAnnotatedFieldsTest.StaticField.field.set(10);
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.field");
        Assert.assertEquals(10, gauge.read());
        RegisterAnnotatedFieldsTest.StaticField.field = null;
        Assert.assertEquals(0, gauge.read());
    }

    public static class StaticField {
        @Probe
        static AtomicInteger field = new AtomicInteger();
    }

    @Test
    public void register_superclassRegistration() {
        RegisterAnnotatedFieldsTest.Subclass object = new RegisterAnnotatedFieldsTest.Subclass();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.field");
        Assert.assertEquals(0, gauge.read());
        object.field = 10;
        Assert.assertEquals(10, gauge.read());
    }

    public static class SuperClass {
        @Probe
        int field;
    }

    public static class Subclass extends RegisterAnnotatedFieldsTest.SuperClass {}
}

