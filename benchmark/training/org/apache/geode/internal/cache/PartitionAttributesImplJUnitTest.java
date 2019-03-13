/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import PartitionAttributesFactory.GLOBAL_MAX_MEMORY_DEFAULT;
import PartitionAttributesFactory.GLOBAL_MAX_MEMORY_PROPERTY;
import PartitionAttributesFactory.LOCAL_MAX_MEMORY_DEFAULT;
import PartitionAttributesFactory.LOCAL_MAX_MEMORY_PROPERTY;
import java.util.Properties;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.PartitionAttributes;
import org.apache.geode.cache.PartitionResolver;
import org.apache.geode.cache.partition.PartitionListener;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit test suite for PartitionAttributesImpl.
 */
public class PartitionAttributesImplJUnitTest {
    private String colocatedRegionFullPath;

    private Properties globalProps;

    private String globalProps_key1;

    private String globalProps_value1;

    private Properties localProps;

    private String localProps_key1;

    private String localProps_value1;

    private int localMaxMemory;

    private boolean offHeap;

    private PartitionResolver<Object, Object> partitionResolver;

    private long recoveryDelay;

    private int redundancy;

    private long startupRecoveryDelay;

    private long totalMaxMemory;

    private int maxNumberOfBuckets;

    private int newTestAvailableOffHeapMemory;

    private int greaterLocalMaxMemory;

    private FixedPartitionAttributesImpl fixedPartitionAttributes;

    private PartitionListener partitionListener;

    private Cache cache;

    @Test
    public void testSetters() {
        PartitionAttributesImpl instance = createPartitionAttributesImpl();
        instance.setLocalMaxMemory(this.localMaxMemory);
        Assert.assertEquals(this.colocatedRegionFullPath, instance.getColocatedWith());
        Assert.assertEquals(this.globalProps, instance.getGlobalProperties());
        Assert.assertEquals(this.localMaxMemory, instance.getLocalMaxMemory());
        Assert.assertEquals(this.localProps, instance.getLocalProperties());
        Assert.assertEquals(this.offHeap, instance.getOffHeap());
        Assert.assertEquals(this.partitionResolver, instance.getPartitionResolver());
        Assert.assertEquals(this.recoveryDelay, instance.getRecoveryDelay());
        Assert.assertEquals(this.redundancy, instance.getRedundancy());
        Assert.assertEquals(this.startupRecoveryDelay, instance.getStartupRecoveryDelay());
        Assert.assertEquals(this.totalMaxMemory, instance.getTotalMaxMemory());
        Assert.assertEquals(this.maxNumberOfBuckets, instance.getTotalNumBuckets());
    }

    @Test
    public void testMergeWithoutOffHeap() {
        PartitionAttributesImpl instance = createPartitionAttributesImpl();
        instance.setLocalMaxMemory(this.localMaxMemory);
        PartitionAttributesImpl destination = new PartitionAttributesImpl();
        destination.merge(instance);
        Assert.assertEquals(this.colocatedRegionFullPath, destination.getColocatedWith());
        // assertIndexDetailsEquals(this.globalProps, destination.getGlobalProperties());
        // assertIndexDetailsEquals(this.localProps, destination.getLocalProperties());
        Assert.assertEquals(this.partitionResolver, destination.getPartitionResolver());
        Assert.assertEquals(this.recoveryDelay, destination.getRecoveryDelay());
        Assert.assertEquals(this.redundancy, destination.getRedundancy());
        Assert.assertEquals(this.startupRecoveryDelay, destination.getStartupRecoveryDelay());
        Assert.assertEquals(this.totalMaxMemory, destination.getTotalMaxMemory());
        Assert.assertEquals(this.maxNumberOfBuckets, destination.getTotalNumBuckets());
    }

    @Test
    public void testCloneWithoutOffHeap() {
        PartitionAttributesImpl instance = createPartitionAttributesImpl();
        instance.setLocalMaxMemory(this.localMaxMemory);
        PartitionAttributesImpl clone = ((PartitionAttributesImpl) (instance.clone()));
        assertGetValues(clone);
        Assert.assertEquals(this.localMaxMemory, instance.getLocalMaxMemory());
        Assert.assertEquals(this.offHeap, instance.getOffHeap());
    }

    @Test
    public void testCloneWithOffHeapAndDefaultLocalMaxMemory() {
        PartitionAttributesImpl.setTestAvailableOffHeapMemory(((this.newTestAvailableOffHeapMemory) + "m"));
        this.offHeap = true;
        PartitionAttributesImpl instance = createPartitionAttributesImpl();
        PartitionAttributesImpl clone = ((PartitionAttributesImpl) (instance.clone()));
        assertGetValues(clone);
        Assert.assertEquals(this.newTestAvailableOffHeapMemory, instance.getLocalMaxMemory());
        Assert.assertEquals(this.offHeap, instance.getOffHeap());
    }

    @Test
    public void testCloneWithOffHeapAndLesserLocalMaxMemory() {
        PartitionAttributesImpl.setTestAvailableOffHeapMemory(((this.newTestAvailableOffHeapMemory) + "m"));
        this.offHeap = true;
        PartitionAttributesImpl instance = createPartitionAttributesImpl();
        instance.setLocalMaxMemory(this.localMaxMemory);
        PartitionAttributesImpl clone = ((PartitionAttributesImpl) (instance.clone()));
        assertGetValues(clone);
        Assert.assertEquals(this.localMaxMemory, instance.getLocalMaxMemory());
        Assert.assertEquals(this.offHeap, instance.getOffHeap());
    }

    @Test
    public void testCloneWithOffHeapAndGreaterLocalMaxMemory() {
        PartitionAttributesImpl.setTestAvailableOffHeapMemory(((this.newTestAvailableOffHeapMemory) + "m"));
        this.offHeap = true;
        PartitionAttributesImpl instance = createPartitionAttributesImpl();
        instance.setLocalMaxMemory(this.greaterLocalMaxMemory);
        PartitionAttributesImpl clone = ((PartitionAttributesImpl) (instance.clone()));
        assertGetValues(clone);
        Assert.assertEquals(this.greaterLocalMaxMemory, instance.getLocalMaxMemory());
        Assert.assertEquals(this.offHeap, instance.getOffHeap());
    }

    @Test
    public void testLocalPropertiesWithLOCAL_MAX_MEMORY_PROPERTY() {
        int value = 111;
        Properties props = new Properties();
        props.setProperty(LOCAL_MAX_MEMORY_PROPERTY, String.valueOf(value));
        PartitionAttributesImpl instance = new PartitionAttributesImpl();
        instance.setLocalProperties(props);
        Assert.assertNotNull(instance.getLocalProperties());
        Assert.assertFalse(instance.getLocalProperties().isEmpty());
        Assert.assertEquals(props, instance.getLocalProperties());
        Assert.assertEquals(value, instance.getLocalMaxMemory());
    }

    @Test
    public void testLocalPropertiesWithoutLOCAL_MAX_MEMORY_PROPERTY() {
        int value = 111;
        Properties props = new Properties();
        PartitionAttributesImpl instance = new PartitionAttributesImpl();
        instance.setLocalProperties(props);
        Assert.assertNotNull(instance.getLocalProperties());
        Assert.assertTrue(instance.getLocalProperties().isEmpty());
        Assert.assertEquals(props, instance.getLocalProperties());
        Assert.assertEquals(instance.getLocalMaxMemoryDefault(), instance.getLocalMaxMemory());
    }

    @Test
    public void testGlobalPropertiesWithGLOBAL_MAX_MEMORY_PROPERTY() {
        int value = 777;
        Properties props = new Properties();
        props.setProperty(GLOBAL_MAX_MEMORY_PROPERTY, String.valueOf(value));
        PartitionAttributesImpl instance = new PartitionAttributesImpl();
        instance.setGlobalProperties(props);
        Assert.assertNotNull(instance.getGlobalProperties());
        Assert.assertFalse(instance.getGlobalProperties().isEmpty());
        Assert.assertEquals(props, instance.getGlobalProperties());
        Assert.assertEquals(value, instance.getTotalMaxMemory());
    }

    @Test
    public void testGlobalPropertiesWithoutGLOBAL_MAX_MEMORY_PROPERTY() {
        int value = 777;
        Properties props = new Properties();
        PartitionAttributesImpl instance = new PartitionAttributesImpl();
        instance.setGlobalProperties(props);
        Assert.assertNotNull(instance.getGlobalProperties());
        Assert.assertTrue(instance.getGlobalProperties().isEmpty());
        Assert.assertEquals(props, instance.getGlobalProperties());
        Assert.assertEquals(GLOBAL_MAX_MEMORY_DEFAULT, instance.getTotalMaxMemory());
    }

    @Test
    public void testGetLocalMaxMemoryDefault() {
        PartitionAttributesImpl instance = new PartitionAttributesImpl();
        Assert.assertEquals(LOCAL_MAX_MEMORY_DEFAULT, instance.getLocalMaxMemoryDefault());
    }

    @Test
    public void testSetAllWithOffHeapAndDefaultLocalMaxMemory() {
        PartitionAttributesImpl source = new PartitionAttributesImpl();
        source.setColocatedWith(this.colocatedRegionFullPath);
        this.fixedPartitionAttributes = new FixedPartitionAttributesImpl();
        this.fixedPartitionAttributes.setPartitionName("setPartitionName");
        source.addFixedPartitionAttributes(this.fixedPartitionAttributes);
        // with Default LocalMaxMemory
        // source.setLocalMaxMemory(this.localMaxMemory);
        // with OffHeap
        this.offHeap = true;
        source.setOffHeap(this.offHeap);
        source.setPartitionResolver(this.partitionResolver);
        source.setRecoveryDelay(this.recoveryDelay);
        source.setRedundantCopies(this.redundancy);
        source.setStartupRecoveryDelay(this.startupRecoveryDelay);
        source.setTotalMaxMemory(this.totalMaxMemory);
        source.setTotalNumBuckets(this.maxNumberOfBuckets);
        PartitionAttributesImpl instance = new PartitionAttributesImpl();
        instance.setAll(source);
        Assert.assertEquals(source.getLocalMaxMemory(), instance.getLocalMaxMemory());
        Assert.assertEquals(source, instance);
        Assert.assertEquals(source.hashCode(), instance.hashCode());
    }

    @Test
    public void testSetAllWithoutOffHeapAndDefaultLocalMaxMemory() {
        PartitionAttributesImpl source = new PartitionAttributesImpl();
        source.setColocatedWith(this.colocatedRegionFullPath);
        this.fixedPartitionAttributes = new FixedPartitionAttributesImpl();
        this.fixedPartitionAttributes.setPartitionName("setPartitionName");
        source.addFixedPartitionAttributes(this.fixedPartitionAttributes);
        // with Default LocalMaxMemory
        // source.setLocalMaxMemory(this.localMaxMemory);
        // without OffHeap
        // this.offHeap = true;
        // source.setOffHeap(this.offHeap);
        source.setPartitionResolver(this.partitionResolver);
        source.setRecoveryDelay(this.recoveryDelay);
        source.setRedundantCopies(this.redundancy);
        source.setStartupRecoveryDelay(this.startupRecoveryDelay);
        source.setTotalMaxMemory(this.totalMaxMemory);
        source.setTotalNumBuckets(this.maxNumberOfBuckets);
        PartitionAttributesImpl instance = new PartitionAttributesImpl();
        instance.setAll(source);
        Assert.assertEquals(source, instance);
        Assert.assertEquals(source.hashCode(), instance.hashCode());
    }

    @Test
    public void testSetAllWithoutOffHeapAndNonDefaultLocalMaxMemory() {
        PartitionAttributesImpl source = new PartitionAttributesImpl();
        source.setColocatedWith(this.colocatedRegionFullPath);
        this.fixedPartitionAttributes = new FixedPartitionAttributesImpl();
        this.fixedPartitionAttributes.setPartitionName("setPartitionName");
        source.addFixedPartitionAttributes(this.fixedPartitionAttributes);
        // with NonDefault LocalMaxMemory
        source.setLocalMaxMemory(this.localMaxMemory);
        // without OffHeap
        // this.offHeap = true;
        // source.setOffHeap(this.offHeap);
        source.setPartitionResolver(this.partitionResolver);
        source.setRecoveryDelay(this.recoveryDelay);
        source.setRedundantCopies(this.redundancy);
        source.setStartupRecoveryDelay(this.startupRecoveryDelay);
        source.setTotalMaxMemory(this.totalMaxMemory);
        source.setTotalNumBuckets(this.maxNumberOfBuckets);
        PartitionAttributesImpl instance = new PartitionAttributesImpl();
        instance.setAll(source);
        Assert.assertEquals(source, instance);
        Assert.assertEquals(source.hashCode(), instance.hashCode());
    }

    @Test
    public void testSetAllWithOffHeapAndNonDefaultLocalMaxMemory() {
        PartitionAttributesImpl source = new PartitionAttributesImpl();
        source.setColocatedWith(this.colocatedRegionFullPath);
        this.fixedPartitionAttributes = new FixedPartitionAttributesImpl();
        this.fixedPartitionAttributes.setPartitionName("setPartitionName");
        source.addFixedPartitionAttributes(this.fixedPartitionAttributes);
        // with NonDefault LocalMaxMemory
        source.setLocalMaxMemory(this.localMaxMemory);
        // with OffHeap
        this.offHeap = true;
        source.setOffHeap(this.offHeap);
        source.setPartitionResolver(this.partitionResolver);
        source.setRecoveryDelay(this.recoveryDelay);
        source.setRedundantCopies(this.redundancy);
        source.setStartupRecoveryDelay(this.startupRecoveryDelay);
        source.setTotalMaxMemory(this.totalMaxMemory);
        source.setTotalNumBuckets(this.maxNumberOfBuckets);
        PartitionAttributesImpl instance = new PartitionAttributesImpl();
        instance.setAll(source);
        Assert.assertEquals(source, instance);
        Assert.assertEquals(source.hashCode(), instance.hashCode());
    }

    @Test
    public void testSetAllWithLocalAndGlobalProperties() {
        PartitionAttributesImpl source = new PartitionAttributesImpl();
        fillInForSetAllWithPropertiesTest(source);
        PartitionAttributesImpl instance = new PartitionAttributesImpl();
        instance.setAll(source);
        Assert.assertEquals(source, instance);
        Assert.assertEquals(source.hashCode(), instance.hashCode());
    }

    @Test
    public void testFillInForSetAllWithPropertiesTestAndHashCode() {
        PartitionAttributesImpl one = new PartitionAttributesImpl();
        fillInForSetAllWithPropertiesTest(one);
        PartitionAttributesImpl two = new PartitionAttributesImpl();
        fillInForSetAllWithPropertiesTest(two);
        Assert.assertEquals(one.hashCode(), two.hashCode());
        Assert.assertEquals(one, two);
    }

    @Test
    public void testEqualsAndHashCodeForEqualInstances() {
        this.fixedPartitionAttributes = new FixedPartitionAttributesImpl();
        this.fixedPartitionAttributes.setPartitionName("setPartitionName");
        PartitionAttributesImpl instance = new PartitionAttributesImpl();
        fillInForEqualityTest(instance);
        instance.addFixedPartitionAttributes(this.fixedPartitionAttributes);
        PartitionAttributesImpl other = new PartitionAttributesImpl();
        fillInForEqualityTest(other);
        other.addFixedPartitionAttributes(this.fixedPartitionAttributes);
        Assert.assertEquals(instance.hashCode(), other.hashCode());
        Assert.assertEquals(instance, other);
    }

    @Test
    public void testEqualsForNonEqualInstances() {
        FixedPartitionAttributesImpl fixedPartitionAttributes1 = new FixedPartitionAttributesImpl();
        fixedPartitionAttributes1.setPartitionName("setPartitionName1");
        FixedPartitionAttributesImpl fixedPartitionAttributes2 = new FixedPartitionAttributesImpl();
        fixedPartitionAttributes2.setPartitionName("setPartitionName2");
        PartitionAttributesImpl instance = new PartitionAttributesImpl();
        fillInForEqualityTest(instance);
        instance.addFixedPartitionAttributes(fixedPartitionAttributes1);
        PartitionAttributesImpl other = new PartitionAttributesImpl();
        fillInForEqualityTest(other);
        other.addFixedPartitionAttributes(fixedPartitionAttributes2);
        Assert.assertNotEquals(instance, other);
    }

    @Test
    public void validateColocationWithNonExistingRegion() {
        PartitionAttributesImpl instance = createPartitionAttributesImpl();
        instance.setColocatedWith("nonExistingRegion");
        assertThatThrownBy(() -> instance.validateColocation(cache)).isInstanceOf(IllegalStateException.class).hasMessageContaining("It should be created before setting");
    }

    @Test
    public void validateColocationWithNonPartitionedRegion() {
        org.apache.geode.cache.Region region = Mockito.mock(org.apache.geode.cache.Region.class);
        Mockito.when(cache.getRegion("nonPrRegion")).thenReturn(region);
        PartitionAttributesImpl instance = createPartitionAttributesImpl();
        instance.setColocatedWith("nonPrRegion");
        assertThatThrownBy(() -> instance.validateColocation(cache)).isInstanceOf(IllegalStateException.class).hasMessageContaining("supported only for PartitionedRegions");
    }

    @Test
    public void validateColocationWithSimilarPartitionedRegion() {
        PartitionedRegion region = Mockito.mock(PartitionedRegion.class);
        PartitionAttributes prAttributes = Mockito.mock(PartitionAttributes.class);
        Mockito.when(cache.getRegion("PrRegion")).thenReturn(region);
        Mockito.when(region.getPartitionAttributes()).thenReturn(prAttributes);
        PartitionAttributesImpl instance = createPartitionAttributesImpl();
        Mockito.when(prAttributes.getTotalNumBuckets()).thenReturn(instance.getTotalNumBuckets());
        Mockito.when(prAttributes.getRedundantCopies()).thenReturn(instance.getRedundantCopies());
        instance.setColocatedWith("PrRegion");
        instance.validateColocation(cache);
        Mockito.verify(cache, Mockito.times(1)).getRegion("PrRegion");
    }
}

