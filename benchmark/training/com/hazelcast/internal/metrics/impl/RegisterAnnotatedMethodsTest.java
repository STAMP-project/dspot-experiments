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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


// todo: testing of null return value
// todo: testing of exception return value
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class RegisterAnnotatedMethodsTest extends HazelcastTestSupport {
    private MetricsRegistryImpl metricsRegistry;

    @Test(expected = IllegalArgumentException.class)
    public void register_methodWithArguments() {
        RegisterAnnotatedMethodsTest.MethodWithArgument object = new RegisterAnnotatedMethodsTest.MethodWithArgument();
        metricsRegistry.scanAndRegister(object, "foo");
    }

    public class MethodWithArgument {
        @Probe
        private long method(int x) {
            return 10;
        }
    }

    @Test
    public void register_withCustomName() {
        RegisterAnnotatedMethodsTest.GaugeMethodWithName object = new RegisterAnnotatedMethodsTest.GaugeMethodWithName();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.mymethod");
        Assert.assertEquals(10, gauge.read());
    }

    public class GaugeMethodWithName {
        @Probe(name = "mymethod")
        private long method() {
            return 10;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void register_methodReturnsVoid() {
        RegisterAnnotatedMethodsTest.VoidMethod object = new RegisterAnnotatedMethodsTest.VoidMethod();
        metricsRegistry.scanAndRegister(object, "foo");
    }

    public class VoidMethod {
        @Probe
        private void method() {
        }
    }

    @Test
    public void register_primitiveByte() {
        RegisterAnnotatedMethodsTest.PrimitiveByteMethod object = new RegisterAnnotatedMethodsTest.PrimitiveByteMethod();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.method");
        Assert.assertEquals(object.method(), gauge.read());
    }

    public class PrimitiveByteMethod {
        @Probe
        private byte method() {
            return 10;
        }
    }

    @Test
    public void register_primitiveShort() {
        RegisterAnnotatedMethodsTest.PrimitiveShortMethod object = new RegisterAnnotatedMethodsTest.PrimitiveShortMethod();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.method");
        Assert.assertEquals(object.method(), gauge.read());
    }

    public class PrimitiveShortMethod {
        @Probe
        private short method() {
            return 10;
        }
    }

    @Test
    public void register_primitiveInt() {
        RegisterAnnotatedMethodsTest.PrimitiveIntMethod object = new RegisterAnnotatedMethodsTest.PrimitiveIntMethod();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.method");
        Assert.assertEquals(10, gauge.read());
    }

    public class PrimitiveIntMethod {
        @Probe
        private int method() {
            return 10;
        }
    }

    @Test
    public void register_primitiveLong() {
        RegisterAnnotatedMethodsTest.PrimitiveLongMethod object = new RegisterAnnotatedMethodsTest.PrimitiveLongMethod();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.method");
        Assert.assertEquals(10, gauge.read());
    }

    public class PrimitiveLongMethod {
        @Probe
        private long method() {
            return 10;
        }
    }

    @Test
    public void register_primitiveFloat() {
        RegisterAnnotatedMethodsTest.PrimitiveFloatMethod object = new RegisterAnnotatedMethodsTest.PrimitiveFloatMethod();
        metricsRegistry.scanAndRegister(object, "foo");
        DoubleGauge gauge = metricsRegistry.newDoubleGauge("foo.method");
        Assert.assertEquals(object.method(), gauge.read(), 0.1);
    }

    public class PrimitiveFloatMethod {
        @Probe
        private float method() {
            return 10.0F;
        }
    }

    @Test
    public void register_primitiveDouble() {
        RegisterAnnotatedMethodsTest.PrimitiveDoubleMethod object = new RegisterAnnotatedMethodsTest.PrimitiveDoubleMethod();
        metricsRegistry.scanAndRegister(object, "foo");
        DoubleGauge gauge = metricsRegistry.newDoubleGauge("foo.method");
        Assert.assertEquals(object.method(), gauge.read(), 0.1);
    }

    public class PrimitiveDoubleMethod {
        @Probe
        private double method() {
            return 10.0;
        }
    }

    @Test
    public void register_atomicLong() {
        RegisterAnnotatedMethodsTest.AtomicLongMethod object = new RegisterAnnotatedMethodsTest.AtomicLongMethod();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.method");
        Assert.assertEquals(object.method().get(), gauge.read());
    }

    public class AtomicLongMethod {
        @Probe
        private AtomicLong method() {
            return new AtomicLong(10);
        }
    }

    @Test
    public void register_atomicInteger() {
        RegisterAnnotatedMethodsTest.AtomicIntegerMethod object = new RegisterAnnotatedMethodsTest.AtomicIntegerMethod();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.method");
        Assert.assertEquals(object.method().get(), gauge.read());
    }

    public class AtomicIntegerMethod {
        @Probe
        private AtomicInteger method() {
            return new AtomicInteger(10);
        }
    }

    @Test
    public void register_counter() {
        RegisterAnnotatedMethodsTest.CounterMethod object = new RegisterAnnotatedMethodsTest.CounterMethod();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.method");
        Assert.assertEquals(object.method().get(), gauge.read());
    }

    public class CounterMethod {
        @Probe
        private Counter method() {
            Counter counter = SwCounter.newSwCounter();
            counter.inc(10);
            return counter;
        }
    }

    @Test
    public void register_collection() {
        RegisterAnnotatedMethodsTest.CollectionMethod object = new RegisterAnnotatedMethodsTest.CollectionMethod();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.method");
        Assert.assertEquals(object.method().size(), gauge.read());
    }

    public class CollectionMethod {
        @Probe
        private Collection method() {
            ArrayList list = new ArrayList();
            for (int k = 0; k < 10; k++) {
                list.add(k);
            }
            return list;
        }
    }

    @Test
    public void register_map() {
        RegisterAnnotatedMethodsTest.MapMethod object = new RegisterAnnotatedMethodsTest.MapMethod();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.method");
        Assert.assertEquals(object.method().size(), gauge.read());
    }

    public class MapMethod {
        @Probe
        private Map method() {
            HashMap map = new HashMap();
            for (int k = 0; k < 10; k++) {
                map.put(k, k);
            }
            return map;
        }
    }

    @Test
    public void register_subclass() {
        RegisterAnnotatedMethodsTest.SubclassMethod object = new RegisterAnnotatedMethodsTest.SubclassMethod();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.method");
        Assert.assertEquals(object.method().size(), gauge.read());
    }

    public class SubclassMethod {
        @Probe
        private IdentityHashMap method() {
            IdentityHashMap map = new IdentityHashMap();
            for (int k = 0; k < 10; k++) {
                map.put(k, k);
            }
            return map;
        }
    }

    @Test
    public void register_staticMethod() {
        RegisterAnnotatedMethodsTest.StaticMethod object = new RegisterAnnotatedMethodsTest.StaticMethod();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.method");
        Assert.assertEquals(RegisterAnnotatedMethodsTest.StaticMethod.method(), gauge.read());
    }

    public static class StaticMethod {
        @Probe
        private static long method() {
            return 10;
        }
    }

    @Test
    public void register_interfaceWithGauges() {
        RegisterAnnotatedMethodsTest.SomeInterfaceImplementation object = new RegisterAnnotatedMethodsTest.SomeInterfaceImplementation();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.method");
        Assert.assertEquals(10, gauge.read());
    }

    public interface SomeInterface {
        @Probe
        int method();
    }

    public static class SomeInterfaceImplementation implements RegisterAnnotatedMethodsTest.SomeInterface {
        @Override
        public int method() {
            return 10;
        }
    }

    @Test
    public void register_superclassWithGaugeMethods() {
        RegisterAnnotatedMethodsTest.SubclassWithGauges object = new RegisterAnnotatedMethodsTest.SubclassWithGauges();
        metricsRegistry.scanAndRegister(object, "foo");
        LongGauge methodGauge = metricsRegistry.newLongGauge("foo.method");
        Assert.assertEquals(object.method(), methodGauge.read());
        LongGauge fieldGauge = metricsRegistry.newLongGauge("foo.field");
        Assert.assertEquals(object.field, fieldGauge.read());
    }

    abstract static class ClassWithGauges {
        @Probe
        int method() {
            return 10;
        }

        @Probe
        int field = 10;
    }

    private static class SubclassWithGauges extends RegisterAnnotatedMethodsTest.ClassWithGauges {}
}

