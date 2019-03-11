/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.impl.internal.spi.serialization;


import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.ehcache.core.spi.service.FileBasedPersistenceContext;
import org.ehcache.impl.config.serializer.DefaultSerializationProviderConfiguration;
import org.ehcache.impl.config.serializer.DefaultSerializerConfiguration;
import org.ehcache.impl.internal.spi.TestServiceProvider;
import org.ehcache.impl.serialization.ByteArraySerializer;
import org.ehcache.impl.serialization.CharSerializer;
import org.ehcache.impl.serialization.CompactJavaSerializer;
import org.ehcache.impl.serialization.DoubleSerializer;
import org.ehcache.impl.serialization.FloatSerializer;
import org.ehcache.impl.serialization.IntegerSerializer;
import org.ehcache.impl.serialization.LongSerializer;
import org.ehcache.impl.serialization.StringSerializer;
import org.ehcache.spi.persistence.StateRepository;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;
import org.ehcache.spi.serialization.StatefulSerializer;
import org.ehcache.spi.serialization.UnsupportedTypeException;
import org.ehcache.spi.service.Service;
import org.ehcache.spi.service.ServiceProvider;
import org.ehcache.test.MockitoUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import static org.ehcache.impl.config.serializer.DefaultSerializerConfiguration.Type.KEY;
import static org.ehcache.impl.config.serializer.DefaultSerializerConfiguration.Type.VALUE;


/**
 *
 *
 * @author Ludovic Orban
 */
public class DefaultSerializationProviderTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCreateSerializerNoConfig() throws Exception {
        DefaultSerializationProviderConfiguration dspfConfig = new DefaultSerializationProviderConfiguration();
        DefaultSerializationProvider dsp = new DefaultSerializationProvider(dspfConfig);
        dsp.start(TestServiceProvider.providerContaining());
        MatcherAssert.assertThat(dsp.createValueSerializer(HashMap.class, ClassLoader.getSystemClassLoader()), Matchers.instanceOf(CompactJavaSerializer.class));
        try {
            dsp.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader());
            Assert.fail("expected UnsupportedTypeException");
        } catch (UnsupportedTypeException ute) {
            // expected
        }
    }

    @Test
    public void testCreateSerializerWithConfig() throws Exception {
        DefaultSerializationProviderConfiguration dspfConfig = new DefaultSerializationProviderConfiguration();
        DefaultSerializationProvider dsp = new DefaultSerializationProvider(dspfConfig);
        dsp.start(TestServiceProvider.providerContaining());
        DefaultSerializerConfiguration<?> dspConfig = new DefaultSerializerConfiguration<>(getSerializerClass(), VALUE);
        MatcherAssert.assertThat(dsp.createValueSerializer(String.class, ClassLoader.getSystemClassLoader(), dspConfig), Matchers.instanceOf(DefaultSerializationProviderTest.TestSerializer.class));
        MatcherAssert.assertThat(dsp.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), dspConfig), Matchers.instanceOf(DefaultSerializationProviderTest.TestSerializer.class));
    }

    @Test
    public void testCreateSerializerWithFactoryConfig() throws Exception {
        DefaultSerializationProviderConfiguration dspfConfig = new DefaultSerializationProviderConfiguration();
        Class<Serializer<Long>> serializerClass = getSerializerClass();
        dspfConfig.addSerializerFor(Long.class, serializerClass);
        DefaultSerializationProvider dsp = new DefaultSerializationProvider(dspfConfig);
        dsp.start(TestServiceProvider.providerContaining());
        MatcherAssert.assertThat(dsp.createValueSerializer(Long.class, ClassLoader.getSystemClassLoader()), Matchers.instanceOf(DefaultSerializationProviderTest.TestSerializer.class));
        MatcherAssert.assertThat(dsp.createValueSerializer(HashMap.class, ClassLoader.getSystemClassLoader()), Matchers.instanceOf(CompactJavaSerializer.class));
    }

    @Test
    public void testCreateTransientSerializers() throws Exception {
        DefaultSerializationProviderConfiguration dspfConfig = new DefaultSerializationProviderConfiguration();
        Class<Serializer<String>> serializerClass = getSerializerClass();
        dspfConfig.addSerializerFor(String.class, serializerClass);
        DefaultSerializationProvider dsp = new DefaultSerializationProvider(dspfConfig);
        dsp.start(TestServiceProvider.providerContaining());
        MatcherAssert.assertThat(dsp.createKeySerializer(String.class, ClassLoader.getSystemClassLoader()), Matchers.instanceOf(DefaultSerializationProviderTest.TestSerializer.class));
        MatcherAssert.assertThat(dsp.createKeySerializer(Serializable.class, ClassLoader.getSystemClassLoader()), Matchers.instanceOf(CompactJavaSerializer.class));
        MatcherAssert.assertThat(dsp.createKeySerializer(Integer.class, ClassLoader.getSystemClassLoader()), Matchers.instanceOf(IntegerSerializer.class));
    }

    @Test
    public void tesCreateTransientSerializersWithOverriddenSerializableType() throws Exception {
        DefaultSerializationProviderConfiguration dspfConfig = new DefaultSerializationProviderConfiguration();
        Class<Serializer<Serializable>> serializerClass = getSerializerClass();
        dspfConfig.addSerializerFor(Serializable.class, serializerClass);
        DefaultSerializationProvider dsp = new DefaultSerializationProvider(dspfConfig);
        dsp.start(TestServiceProvider.providerContaining());
        MatcherAssert.assertThat(dsp.createKeySerializer(HashMap.class, ClassLoader.getSystemClassLoader()), Matchers.instanceOf(DefaultSerializationProviderTest.TestSerializer.class));
        MatcherAssert.assertThat(dsp.createKeySerializer(Serializable.class, ClassLoader.getSystemClassLoader()), Matchers.instanceOf(DefaultSerializationProviderTest.TestSerializer.class));
        MatcherAssert.assertThat(dsp.createKeySerializer(Integer.class, ClassLoader.getSystemClassLoader()), Matchers.instanceOf(IntegerSerializer.class));
    }

    @Test
    public void testRemembersCreationConfigurationAfterStopStart() throws UnsupportedTypeException {
        DefaultSerializationProviderConfiguration configuration = new DefaultSerializationProviderConfiguration();
        Class<Serializer<String>> serializerClass = getSerializerClass();
        configuration.addSerializerFor(String.class, serializerClass);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(configuration);
        @SuppressWarnings("unchecked")
        ServiceProvider<Service> serviceProvider = MockitoUtil.mock(ServiceProvider.class);
        serializationProvider.start(serviceProvider);
        MatcherAssert.assertThat(serializationProvider.createKeySerializer(String.class, ClassLoader.getSystemClassLoader()), Matchers.instanceOf(DefaultSerializationProviderTest.TestSerializer.class));
        serializationProvider.stop();
        serializationProvider.start(serviceProvider);
        MatcherAssert.assertThat(serializationProvider.createKeySerializer(String.class, ClassLoader.getSystemClassLoader()), Matchers.instanceOf(DefaultSerializationProviderTest.TestSerializer.class));
    }

    @Test
    public void testReleaseSerializerWithProvidedCloseableSerializerDoesNotClose() throws Exception {
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        DefaultSerializationProviderTest.CloseableSerializer<?> closeableSerializer = new DefaultSerializationProviderTest.CloseableSerializer<>();
        provider.providedVsCount.put(closeableSerializer, new AtomicInteger(1));
        provider.releaseSerializer(closeableSerializer);
        MatcherAssert.assertThat(closeableSerializer.closed, Is.is(false));
    }

    @Test
    public void testReleaseSerializerWithInstantiatedCloseableSerializerDoesClose() throws Exception {
        @SuppressWarnings("unchecked")
        Class<? extends Serializer<String>> serializerClass = ((Class) (DefaultSerializationProviderTest.CloseableSerializer.class));
        DefaultSerializerConfiguration<String> config = new DefaultSerializerConfiguration<>(serializerClass, KEY);
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        Serializer<?> serializer = provider.createKeySerializer(String.class, ClassLoader.getSystemClassLoader(), config);
        provider.releaseSerializer(serializer);
        Assert.assertTrue(((DefaultSerializationProviderTest.CloseableSerializer) (serializer)).closed);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReleaseSerializerByAnotherProvider() throws Exception {
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        Serializer<?> serializer = MockitoUtil.mock(Serializer.class);
        provider.releaseSerializer(serializer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReleaseSameInstanceMultipleTimesThrows() throws Exception {
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        Serializer<?> serializer = MockitoUtil.mock(Serializer.class);
        provider.providedVsCount.put(serializer, new AtomicInteger(1));
        provider.releaseSerializer(serializer);
        MatcherAssert.assertThat(provider.providedVsCount.get("foo"), Matchers.nullValue());
        provider.releaseSerializer(serializer);
    }

    @Test
    public void testCreateKeySerializerWithActualInstanceInServiceConfig() throws Exception {
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        @SuppressWarnings("unchecked")
        DefaultSerializationProviderTest.TestSerializer<String> serializer = MockitoUtil.mock(DefaultSerializationProviderTest.TestSerializer.class);
        DefaultSerializerConfiguration<String> config = new DefaultSerializerConfiguration<>(serializer, KEY);
        Serializer<?> created = provider.createKeySerializer(DefaultSerializationProviderTest.TestSerializer.class, ClassLoader.getSystemClassLoader(), config);
        Assert.assertSame(serializer, created);
    }

    @Test
    public void testSameInstanceRetrievedMultipleTimesUpdatesTheProvidedCount() throws Exception {
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        @SuppressWarnings("unchecked")
        DefaultSerializationProviderTest.TestSerializer<String> serializer = MockitoUtil.mock(DefaultSerializationProviderTest.TestSerializer.class);
        DefaultSerializerConfiguration<String> config = new DefaultSerializerConfiguration<>(serializer, KEY);
        Serializer<?> created = provider.createKeySerializer(DefaultSerializationProviderTest.TestSerializer.class, ClassLoader.getSystemClassLoader(), config);
        Assert.assertSame(serializer, created);
        MatcherAssert.assertThat(provider.providedVsCount.get(created).get(), Is.is(1));
        created = provider.createKeySerializer(DefaultSerializationProviderTest.TestSerializer.class, ClassLoader.getSystemClassLoader(), config);
        Assert.assertSame(serializer, created);
        MatcherAssert.assertThat(provider.providedVsCount.get(created).get(), Is.is(2));
    }

    @Test
    public void testDefaultSerializableSerializer() throws Exception {
        DefaultSerializationProvider provider = getStartedProvider();
        Serializer<Serializable> keySerializer = provider.createKeySerializer(Serializable.class, ClassLoader.getSystemClassLoader());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(CompactJavaSerializer.class));
        keySerializer = provider.createKeySerializer(Serializable.class, ClassLoader.getSystemClassLoader(), getPersistenceSpaceIdentifierMock());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(CompactJavaSerializer.class));
    }

    @Test
    public void testDefaultStringSerializer() throws Exception {
        DefaultSerializationProvider provider = getStartedProvider();
        Serializer<String> keySerializer = provider.createKeySerializer(String.class, ClassLoader.getSystemClassLoader());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(StringSerializer.class));
        keySerializer = provider.createKeySerializer(String.class, ClassLoader.getSystemClassLoader(), getPersistenceSpaceIdentifierMock());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(StringSerializer.class));
    }

    @Test
    public void testDefaultIntegerSerializer() throws Exception {
        DefaultSerializationProvider provider = getStartedProvider();
        Serializer<Integer> keySerializer = provider.createKeySerializer(Integer.class, ClassLoader.getSystemClassLoader());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(IntegerSerializer.class));
        keySerializer = provider.createKeySerializer(Integer.class, ClassLoader.getSystemClassLoader(), getPersistenceSpaceIdentifierMock());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(IntegerSerializer.class));
    }

    @Test
    public void testDefaultLongSerializer() throws Exception {
        DefaultSerializationProvider provider = getStartedProvider();
        Serializer<Long> keySerializer = provider.createKeySerializer(Long.class, ClassLoader.getSystemClassLoader());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(LongSerializer.class));
        keySerializer = provider.createKeySerializer(Long.class, ClassLoader.getSystemClassLoader(), getPersistenceSpaceIdentifierMock());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(LongSerializer.class));
    }

    @Test
    public void testDefaultCharSerializer() throws Exception {
        DefaultSerializationProvider provider = getStartedProvider();
        Serializer<Character> keySerializer = provider.createKeySerializer(Character.class, ClassLoader.getSystemClassLoader());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(CharSerializer.class));
        keySerializer = provider.createKeySerializer(Character.class, ClassLoader.getSystemClassLoader(), getPersistenceSpaceIdentifierMock());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(CharSerializer.class));
    }

    @Test
    public void testDefaultDoubleSerializer() throws Exception {
        DefaultSerializationProvider provider = getStartedProvider();
        Serializer<Double> keySerializer = provider.createKeySerializer(Double.class, ClassLoader.getSystemClassLoader());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(DoubleSerializer.class));
        keySerializer = provider.createKeySerializer(Double.class, ClassLoader.getSystemClassLoader(), getPersistenceSpaceIdentifierMock());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(DoubleSerializer.class));
    }

    @Test
    public void testDefaultFloatSerializer() throws Exception {
        DefaultSerializationProvider provider = getStartedProvider();
        Serializer<Float> keySerializer = provider.createKeySerializer(Float.class, ClassLoader.getSystemClassLoader());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(FloatSerializer.class));
        keySerializer = provider.createKeySerializer(Float.class, ClassLoader.getSystemClassLoader(), getPersistenceSpaceIdentifierMock());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(FloatSerializer.class));
    }

    @Test
    public void testDefaultByteArraySerializer() throws Exception {
        DefaultSerializationProvider provider = getStartedProvider();
        Serializer<byte[]> keySerializer = provider.createKeySerializer(byte[].class, ClassLoader.getSystemClassLoader());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(ByteArraySerializer.class));
        keySerializer = provider.createKeySerializer(byte[].class, ClassLoader.getSystemClassLoader(), getPersistenceSpaceIdentifierMock());
        MatcherAssert.assertThat(keySerializer, Matchers.instanceOf(ByteArraySerializer.class));
    }

    @Test
    public void testCreateTransientSerializerWithoutConstructor() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("does not have a constructor that takes in a ClassLoader.");
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        provider.start(TestServiceProvider.providerContaining());
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.BaseSerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration);
    }

    @Test
    public void testCreatePersistentSerializerWithoutConstructor() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("does not have a constructor that takes in a ClassLoader.");
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        provider.start(TestServiceProvider.providerContaining());
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.BaseSerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration, getPersistenceSpaceIdentifierMock());
    }

    @Test
    public void testCreateTransientStatefulSerializerWithoutConstructor() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("does not have a constructor that takes in a ClassLoader.");
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        provider.start(TestServiceProvider.providerContaining());
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.StatefulBaseSerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration);
    }

    @Test
    public void testCreatePersistentStatefulSerializerWithoutConstructor() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("does not have a constructor that takes in a ClassLoader.");
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        provider.start(TestServiceProvider.providerContaining());
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.StatefulBaseSerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration, getPersistenceSpaceIdentifierMock());
    }

    @Test
    public void testCreateTransientMinimalSerializer() throws Exception {
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        provider.start(TestServiceProvider.providerContaining());
        DefaultSerializationProviderTest.MinimalSerializer.baseConstructorInvoked = false;
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.MinimalSerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        Serializer<Object> valueSerializer = provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration);
        MatcherAssert.assertThat(valueSerializer, Matchers.instanceOf(DefaultSerializationProviderTest.MinimalSerializer.class));
        MatcherAssert.assertThat(DefaultSerializationProviderTest.MinimalSerializer.baseConstructorInvoked, Is.is(true));
    }

    @Test
    public void testCreatePersistentMinimalSerializer() throws Exception {
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        provider.start(TestServiceProvider.providerContaining());
        DefaultSerializationProviderTest.MinimalSerializer.baseConstructorInvoked = false;
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.MinimalSerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        Serializer<Object> valueSerializer = provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration, getPersistenceSpaceIdentifierMock());
        MatcherAssert.assertThat(valueSerializer, Matchers.instanceOf(DefaultSerializationProviderTest.MinimalSerializer.class));
        MatcherAssert.assertThat(DefaultSerializationProviderTest.MinimalSerializer.baseConstructorInvoked, Is.is(true));
    }

    @Test
    public void testTransientMinimalStatefulSerializer() throws Exception {
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        provider.start(TestServiceProvider.providerContaining());
        DefaultSerializationProviderTest.MinimalStatefulSerializer.baseConstructorInvoked = false;
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.MinimalStatefulSerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        Serializer<Object> valueSerializer = provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration);
        MatcherAssert.assertThat(valueSerializer, Matchers.instanceOf(DefaultSerializationProviderTest.MinimalStatefulSerializer.class));
        MatcherAssert.assertThat(DefaultSerializationProviderTest.MinimalStatefulSerializer.baseConstructorInvoked, Is.is(true));
    }

    @Test
    public void testPersistentMinimalStatefulSerializer() throws Exception {
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        provider.start(TestServiceProvider.providerContaining());
        DefaultSerializationProviderTest.MinimalStatefulSerializer.baseConstructorInvoked = false;
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.MinimalStatefulSerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        Serializer<Object> valueSerializer = provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration, getPersistenceSpaceIdentifierMock());
        MatcherAssert.assertThat(valueSerializer, Matchers.instanceOf(DefaultSerializationProviderTest.MinimalStatefulSerializer.class));
        MatcherAssert.assertThat(DefaultSerializationProviderTest.MinimalStatefulSerializer.baseConstructorInvoked, Is.is(true));
    }

    @Test
    public void testTransientLegacySerializer() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("does not have a constructor that takes in a ClassLoader.");
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        provider.start(TestServiceProvider.providerContaining());
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.LegacySerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration);
    }

    @Test
    public void testPersistentLegacySerializer() throws Exception {
        DefaultSerializationProvider provider = getStartedProvider();
        DefaultSerializationProviderTest.LegacySerializer.legacyConstructorInvoked = false;
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.LegacySerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("does not have a constructor that takes in a ClassLoader.");
        Serializer<Object> valueSerializer = provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration, getPersistenceSpaceIdentifierMock());
        MatcherAssert.assertThat(valueSerializer, Matchers.instanceOf(DefaultSerializationProviderTest.LegacySerializer.class));
        MatcherAssert.assertThat(DefaultSerializationProviderTest.LegacySerializer.legacyConstructorInvoked, Is.is(true));
    }

    @Test
    public void testTransientLegacyComboSerializer() throws Exception {
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        provider.start(TestServiceProvider.providerContaining());
        DefaultSerializationProviderTest.LegacyComboSerializer.baseConstructorInvoked = false;
        DefaultSerializationProviderTest.LegacyComboSerializer.legacyConstructorInvoked = false;
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.LegacyComboSerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        Serializer<Object> valueSerializer = provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration);
        MatcherAssert.assertThat(valueSerializer, Matchers.instanceOf(DefaultSerializationProviderTest.LegacyComboSerializer.class));
        MatcherAssert.assertThat(DefaultSerializationProviderTest.LegacyComboSerializer.baseConstructorInvoked, Is.is(true));
        MatcherAssert.assertThat(DefaultSerializationProviderTest.LegacyComboSerializer.legacyConstructorInvoked, Is.is(false));
    }

    @Test
    public void testPersistentLegacyComboSerializer() throws Exception {
        DefaultSerializationProvider provider = getStartedProvider();
        DefaultSerializationProviderTest.LegacyComboSerializer.baseConstructorInvoked = false;
        DefaultSerializationProviderTest.LegacyComboSerializer.legacyConstructorInvoked = false;
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.LegacyComboSerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        Serializer<Object> valueSerializer = provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration, getPersistenceSpaceIdentifierMock());
        MatcherAssert.assertThat(valueSerializer, Matchers.instanceOf(DefaultSerializationProviderTest.LegacyComboSerializer.class));
        MatcherAssert.assertThat(DefaultSerializationProviderTest.LegacyComboSerializer.baseConstructorInvoked, Is.is(true));
        MatcherAssert.assertThat(DefaultSerializationProviderTest.LegacyComboSerializer.legacyConstructorInvoked, Is.is(false));
    }

    @Test
    public void testCreateTransientStatefulLegacySerializer() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("does not have a constructor that takes in a ClassLoader.");
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        provider.start(TestServiceProvider.providerContaining());
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.StatefulLegacySerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration);
    }

    @Test
    public void testCreatePersistentStatefulLegacySerializer() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("does not have a constructor that takes in a ClassLoader.");
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        provider.start(TestServiceProvider.providerContaining());
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.StatefulLegacySerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration, getPersistenceSpaceIdentifierMock());
    }

    @Test
    public void testTransientStatefulLegacyComboSerializer() throws Exception {
        DefaultSerializationProvider provider = new DefaultSerializationProvider(null);
        provider.start(TestServiceProvider.providerContaining());
        DefaultSerializationProviderTest.StatefulLegacyComboSerializer.baseConstructorInvoked = false;
        DefaultSerializationProviderTest.StatefulLegacyComboSerializer.legacyConstructorInvoked = false;
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.StatefulLegacyComboSerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        Serializer<Object> valueSerializer = provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration);
        MatcherAssert.assertThat(valueSerializer, Matchers.instanceOf(DefaultSerializationProviderTest.StatefulLegacyComboSerializer.class));
        MatcherAssert.assertThat(DefaultSerializationProviderTest.StatefulLegacyComboSerializer.baseConstructorInvoked, Is.is(true));
        MatcherAssert.assertThat(DefaultSerializationProviderTest.StatefulLegacyComboSerializer.legacyConstructorInvoked, Is.is(false));
    }

    @Test
    public void testPersistentStatefulLegacyComboSerializer() throws Exception {
        DefaultSerializationProvider provider = getStartedProvider();
        DefaultSerializationProviderTest.StatefulLegacyComboSerializer.baseConstructorInvoked = false;
        DefaultSerializationProviderTest.StatefulLegacyComboSerializer.legacyConstructorInvoked = false;
        @SuppressWarnings("unchecked")
        Class<Serializer<Object>> serializerClass = ((Class) (DefaultSerializationProviderTest.StatefulLegacyComboSerializer.class));
        DefaultSerializerConfiguration<Object> configuration = new DefaultSerializerConfiguration<>(serializerClass, VALUE);
        Serializer<Object> valueSerializer = provider.createValueSerializer(Object.class, ClassLoader.getSystemClassLoader(), configuration, getPersistenceSpaceIdentifierMock());
        MatcherAssert.assertThat(valueSerializer, Matchers.instanceOf(DefaultSerializationProviderTest.StatefulLegacyComboSerializer.class));
        MatcherAssert.assertThat(DefaultSerializationProviderTest.StatefulLegacyComboSerializer.baseConstructorInvoked, Is.is(true));
        MatcherAssert.assertThat(DefaultSerializationProviderTest.StatefulLegacyComboSerializer.legacyConstructorInvoked, Is.is(false));
    }

    public static class TestSerializer<T> implements Serializer<T> {
        public TestSerializer(ClassLoader classLoader) {
        }

        @Override
        public ByteBuffer serialize(T object) {
            return null;
        }

        @Override
        public T read(ByteBuffer binary) {
            return null;
        }

        @Override
        public boolean equals(T object, ByteBuffer binary) {
            return false;
        }
    }

    public static class CloseableSerializer<T> implements Closeable , Serializer<T> {
        boolean closed = false;

        public CloseableSerializer() {
        }

        public CloseableSerializer(ClassLoader classLoader) {
        }

        @Override
        public void close() throws IOException {
            closed = true;
        }

        @Override
        public ByteBuffer serialize(Object object) throws SerializerException {
            return null;
        }

        @Override
        public T read(ByteBuffer binary) throws ClassNotFoundException, SerializerException {
            return null;
        }

        @Override
        public boolean equals(Object object, ByteBuffer binary) throws ClassNotFoundException, SerializerException {
            return false;
        }
    }

    public static class BaseSerializer<T> implements Serializer<T> {
        @Override
        public ByteBuffer serialize(final T object) throws SerializerException {
            return null;
        }

        @Override
        public T read(final ByteBuffer binary) throws ClassNotFoundException, SerializerException {
            return null;
        }

        @Override
        public boolean equals(final T object, final ByteBuffer binary) throws ClassNotFoundException, SerializerException {
            return false;
        }
    }

    public static class MinimalSerializer<T> extends DefaultSerializationProviderTest.BaseSerializer<T> {
        private static boolean baseConstructorInvoked = false;

        public MinimalSerializer(ClassLoader loader) {
            DefaultSerializationProviderTest.MinimalSerializer.baseConstructorInvoked = true;
        }
    }

    // Stateful but no constructor
    public static class StatefulBaseSerializer<T> extends DefaultSerializationProviderTest.BaseSerializer<T> implements StatefulSerializer<T> {
        @Override
        public void init(final StateRepository stateRepository) {
        }
    }

    public static class MinimalStatefulSerializer<T> extends DefaultSerializationProviderTest.BaseSerializer<T> implements StatefulSerializer<T> {
        private static boolean baseConstructorInvoked = false;

        public MinimalStatefulSerializer(ClassLoader loader) {
            DefaultSerializationProviderTest.MinimalStatefulSerializer.baseConstructorInvoked = true;
        }

        @Override
        public void init(final StateRepository stateRepository) {
        }
    }

    public static class LegacySerializer<T> extends DefaultSerializationProviderTest.BaseSerializer<T> {
        private static boolean legacyConstructorInvoked = false;

        public LegacySerializer(ClassLoader loader, FileBasedPersistenceContext context) {
            DefaultSerializationProviderTest.LegacySerializer.legacyConstructorInvoked = true;
        }
    }

    public static class LegacyComboSerializer<T> extends DefaultSerializationProviderTest.BaseSerializer<T> {
        private static boolean baseConstructorInvoked = false;

        private static boolean legacyConstructorInvoked = false;

        public LegacyComboSerializer(ClassLoader loader) {
            DefaultSerializationProviderTest.LegacyComboSerializer.baseConstructorInvoked = true;
        }

        public LegacyComboSerializer(ClassLoader loader, FileBasedPersistenceContext context) {
            DefaultSerializationProviderTest.LegacyComboSerializer.legacyConstructorInvoked = true;
        }
    }

    public static class StatefulLegacySerializer<T> extends DefaultSerializationProviderTest.StatefulBaseSerializer<T> {
        private static boolean legacyConstructorInvoked = false;

        public StatefulLegacySerializer(ClassLoader loader, FileBasedPersistenceContext context) {
            DefaultSerializationProviderTest.StatefulLegacySerializer.legacyConstructorInvoked = true;
        }
    }

    public static class StatefulLegacyComboSerializer<T> extends DefaultSerializationProviderTest.BaseSerializer<T> implements StatefulSerializer<T> {
        private static boolean baseConstructorInvoked = false;

        private static boolean legacyConstructorInvoked = false;

        public StatefulLegacyComboSerializer(final ClassLoader loader) {
            DefaultSerializationProviderTest.StatefulLegacyComboSerializer.baseConstructorInvoked = true;
        }

        public StatefulLegacyComboSerializer(ClassLoader loader, FileBasedPersistenceContext context) {
            DefaultSerializationProviderTest.StatefulLegacyComboSerializer.legacyConstructorInvoked = true;
        }

        @Override
        public void init(final StateRepository stateRepository) {
        }
    }
}

