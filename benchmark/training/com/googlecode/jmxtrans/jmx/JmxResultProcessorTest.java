/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.googlecode.jmxtrans.jmx;


import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.googlecode.jmxtrans.model.Result;
import java.lang.management.ManagementFactory;
import java.util.List;
import javax.annotation.Nullable;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ReflectionException;
import javax.management.openmbean.OpenDataException;
import org.junit.Assume;
import org.junit.Test;


/**
 * <b>Warning:</b> This test class relies on MBeans exposed by the JVM. As far
 * as I understand the documentation, it only relies on beans and attributes
 * that should be present on all implementations. Still there is some chance
 * that this test will fail on some JVMs or that it depends too much on
 * specific platform properties.
 */
public class JmxResultProcessorTest {
    private static final String TEST_DOMAIN_NAME = "ObjectDomainName";

    @Test
    public void canCreateBasicResultData() throws InstanceNotFoundException, MalformedObjectNameException {
        Attribute integerAttribute = new Attribute("StartTime", 51L);
        ObjectInstance runtime = getRuntime();
        List<Result> results = getResults();
        assertThat(results).hasSize(1);
        Result integerResult = results.get(0);
        assertThat(integerResult.getAttributeName()).isEqualTo("StartTime");
        assertThat(integerResult.getClassName()).isEqualTo("sun.management.RuntimeImpl");
        assertThat(integerResult.getKeyAlias()).isEqualTo("resultAlias");
        assertThat(integerResult.getTypeName()).isEqualTo("type=Runtime");
        assertThat(integerResult.getValue()).isEqualTo(51L);
    }

    @Test
    public void doesNotReorderTypeNames() throws MalformedObjectNameException {
        String className = "java.lang.SomeClass";
        String propertiesOutOfOrder = "z-key=z-value,a-key=a-value,k-key=k-value";
        List<Result> results = getResults();
        assertThat(results).hasSize(1);
        Result integerResult = results.get(0);
        assertThat(integerResult.getTypeName()).isEqualTo(propertiesOutOfOrder);
    }

    @Test
    public void testNullValueInCompositeData() throws InstanceNotFoundException, MalformedObjectNameException, OpenDataException {
        ObjectInstance runtime = getRuntime();
        List<Result> results = getResults();
        assertThat(results).hasSize(0);
    }

    @Test
    public void canReadSingleIntegerValue() throws InstanceNotFoundException, MalformedObjectNameException {
        Attribute integerAttribute = new Attribute("CollectionCount", 51L);
        ObjectInstance runtime = getRuntime();
        List<Result> results = getResults();
        assertThat(results).hasSize(1);
        Result integerResult = results.get(0);
        assertThat(integerResult.getAttributeName()).isEqualTo("CollectionCount");
        assertThat(integerResult.getValue()).isInstanceOf(Long.class);
        assertThat(integerResult.getValue()).isEqualTo(51L);
    }

    @Test
    public void canReadSingleBooleanValue() throws InstanceNotFoundException, MalformedObjectNameException {
        Attribute booleanAttribute = new Attribute("BootClassPathSupported", true);
        ObjectInstance runtime = getRuntime();
        List<Result> results = getResults();
        assertThat(results).hasSize(1);
        Result result = results.get(0);
        assertThat(result.getAttributeName()).isEqualTo("BootClassPathSupported");
        assertThat(result.getValue()).isInstanceOf(Boolean.class);
        assertThat(result.getValue()).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void canReadTabularData() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, MalformedObjectNameException, ReflectionException {
        ObjectInstance runtime = getRuntime();
        AttributeList attr = ManagementFactory.getPlatformMBeanServer().getAttributes(runtime.getObjectName(), new String[]{ "SystemProperties" });
        List<Result> results = getResults();
        assertThat(results.size()).isGreaterThan(2);
        Optional<Result> result = firstMatch(results, "SystemProperties", "java.version", "value");
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getAttributeName()).isEqualTo("SystemProperties");
        assertThat(result.get().getValuePath()).isEqualTo(ImmutableList.of("java.version", "value"));
        assertThat(result.get().getValue()).isEqualTo(System.getProperty("java.version"));
    }

    @Test(timeout = 1000)
    public void canReadFieldsOfTabularData() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, MalformedObjectNameException, ReflectionException {
        // Need to induce a GC for the attribute below to be populated
        Runtime.getRuntime().gc();
        ObjectInstance runtime = null;
        try {
            runtime = getG1YoungGen();
        } catch (InstanceNotFoundException e) {
            // ignore test if G1 not enabled
            Assume.assumeNoException("G1 GC in Java 7/8 needs to be enabled with -XX:+UseG1GC", e);
        }
        AttributeList attr;
        // but takes a non-deterministic amount of time for LastGcInfo to get populated
        while (true) {
            // but bounded by Test timeout
            attr = ManagementFactory.getPlatformMBeanServer().getAttributes(runtime.getObjectName(), new String[]{ "LastGcInfo" });
            if ((((Attribute) (attr.get(0))).getValue()) != null) {
                break;
            }
        } 
        List<Result> results = getResults();
        assertThat(results.size()).isGreaterThan(2);
        // Should have primitive typed fields
        assertThat(firstMatch(results, "LastGcInfo", "duration").get()).isNotNull();
        // assert tabular fields are excluded
        assertThat(firstMatch(results, "LastGcInfo", "memoryUsageBeforeGc").get()).isNull();
        assertThat(firstMatch(results, "LastGcInfo", "memoryUsageAfterGc").get()).isNull();
    }

    @Test
    public void canReadCompositeData() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, MalformedObjectNameException, ReflectionException {
        ObjectInstance memory = getMemory();
        AttributeList attr = ManagementFactory.getPlatformMBeanServer().getAttributes(memory.getObjectName(), new String[]{ "HeapMemoryUsage" });
        List<Result> results = getResults();
        assertThat(results).hasSize(4);
        for (Result result : results) {
            assertThat(result.getAttributeName()).isEqualTo("HeapMemoryUsage");
            assertThat(result.getTypeName()).isEqualTo("type=Memory");
        }
        Optional<Result> optionalResult = firstMatch(results, "HeapMemoryUsage", "init");
        assertThat(optionalResult.isPresent()).isTrue();
        Object objectValue = optionalResult.get().getValue();
        assertThat(objectValue).isInstanceOf(Long.class);
    }

    @Test
    public void canReadCompositeDataWithFilteringKeys() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, MalformedObjectNameException, ReflectionException {
        ObjectInstance memory = getMemory();
        AttributeList attr = ManagementFactory.getPlatformMBeanServer().getAttributes(memory.getObjectName(), new String[]{ "HeapMemoryUsage" });
        List<Result> results = getResults();
        assertThat(results).hasSize(1);
        for (Result result : results) {
            assertThat(result.getAttributeName()).isEqualTo("HeapMemoryUsage");
            assertThat(result.getTypeName()).isEqualTo("type=Memory");
        }
        Optional<Result> optionalResult = firstMatch(results, "HeapMemoryUsage", "init");
        assertThat(optionalResult.isPresent()).isTrue();
        Object objectValue = optionalResult.get().getValue();
        assertThat(objectValue).isInstanceOf(Long.class);
    }

    @Test
    public void canReadMapData() throws MalformedObjectNameException {
        Attribute mapAttribute = new Attribute("map", ImmutableMap.of("key1", "value1", "key2", "value2"));
        List<Result> results = getResults();
        assertThat(results).isNotNull();
        assertThat(results).hasSize(2);
        for (Result result : results) {
            assertThat(result.getAttributeName()).isEqualTo("map");
            assertThat(result.getTypeName()).isEqualTo("type=Memory");
        }
        assertThat(firstMatch(results, "map", "key1").get().getValue()).isEqualTo("value1");
        assertThat(firstMatch(results, "map", "key2").get().getValue()).isEqualTo("value2");
    }

    @Test
    public void canReadMapDataWithNonStringKeys() throws MalformedObjectNameException {
        Attribute mapAttribute = new Attribute("map", ImmutableMap.of(1, "value1", 2, "value2"));
        List<Result> results = getResults();
        assertThat(results).isNotNull();
        for (Result result : results) {
            assertThat(result.getAttributeName()).isEqualTo("map");
            assertThat(result.getTypeName()).isEqualTo("type=Memory");
        }
        assertThat(firstMatch(results, "map", "1").get().getValue()).isEqualTo("value1");
        assertThat(firstMatch(results, "map", "2").get().getValue()).isEqualTo("value2");
    }

    private static class ByAttributeName implements Predicate<Result> {
        private final String attributeName;

        public ByAttributeName(String attributeName) {
            this.attributeName = attributeName;
        }

        @Override
        public boolean apply(@Nullable
        Result result) {
            if (result == null) {
                return false;
            }
            return attributeName.equals(result.getAttributeName());
        }
    }

    private static class ByValuePath implements Predicate<Result> {
        private final ImmutableList<String> valuePath;

        public ByValuePath(String... valuePath) {
            this.valuePath = ImmutableList.copyOf(valuePath);
        }

        @Override
        public boolean apply(@Nullable
        Result result) {
            if (result == null) {
                return false;
            }
            return valuePath.equals(result.getValuePath());
        }
    }
}

