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
package org.ehcache.integration;


import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.impl.config.copy.DefaultCopierConfiguration;
import org.ehcache.impl.config.serializer.DefaultSerializerConfiguration;
import org.ehcache.impl.copy.ReadWriteCopier;
import org.ehcache.impl.copy.SerializingCopier;
import org.ehcache.spi.copy.Copier;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.ehcache.impl.config.copy.DefaultCopierConfiguration.Type.VALUE;


/**
 * Created by alsu on 01/09/15.
 */
public class CacheCopierTest {
    CacheManager cacheManager;

    CacheConfigurationBuilder<Long, CacheCopierTest.Person> baseConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, CacheCopierTest.Person.class, ResourcePoolsBuilder.heap(5));

    @Test
    public void testCopyValueOnRead() throws Exception {
        CacheConfiguration<Long, CacheCopierTest.Person> cacheConfiguration = baseConfig.add(new DefaultCopierConfiguration<>(CacheCopierTest.PersonOnReadCopier.class, VALUE)).build();
        Cache<Long, CacheCopierTest.Person> cache = cacheManager.createCache("cache", cacheConfiguration);
        CacheCopierTest.Person original = new CacheCopierTest.Person("Bar", 24);
        cache.put(1L, original);
        CacheCopierTest.Person retrieved = cache.get(1L);
        Assert.assertNotSame(original, retrieved);
        Assert.assertThat(retrieved.name, Matchers.equalTo("Bar"));
        Assert.assertThat(retrieved.age, Matchers.equalTo(24));
        original.age = 56;
        retrieved = cache.get(1L);
        Assert.assertThat(retrieved.age, Matchers.equalTo(56));
        Assert.assertNotSame(cache.get(1L), cache.get(1L));
    }

    @Test
    public void testCopyValueOnWrite() throws Exception {
        CacheConfiguration<Long, CacheCopierTest.Person> cacheConfiguration = baseConfig.add(new DefaultCopierConfiguration<>(CacheCopierTest.PersonOnWriteCopier.class, VALUE)).build();
        Cache<Long, CacheCopierTest.Person> cache = cacheManager.createCache("cache", cacheConfiguration);
        CacheCopierTest.Person original = new CacheCopierTest.Person("Bar", 24);
        cache.put(1L, original);
        CacheCopierTest.Person retrieved = cache.get(1L);
        Assert.assertNotSame(original, retrieved);
        Assert.assertThat(retrieved.name, Matchers.equalTo("Bar"));
        Assert.assertThat(retrieved.age, Matchers.equalTo(24));
        original.age = 56;
        retrieved = cache.get(1L);
        Assert.assertThat(retrieved.age, Matchers.equalTo(24));
        Assert.assertSame(cache.get(1L), cache.get(1L));
    }

    @Test
    public void testIdentityCopier() throws Exception {
        CacheConfiguration<Long, CacheCopierTest.Person> cacheConfiguration = baseConfig.build();
        Cache<Long, CacheCopierTest.Person> cache = cacheManager.createCache("cache", cacheConfiguration);
        CacheCopierTest.Person original = new CacheCopierTest.Person("Bar", 24);
        cache.put(1L, original);
        CacheCopierTest.Person retrieved = cache.get(1L);
        Assert.assertSame(original, retrieved);
        original.age = 25;
        retrieved = cache.get(1L);
        Assert.assertSame(original, retrieved);
        Assert.assertSame(cache.get(1L), cache.get(1L));
    }

    @Test
    public void testSerializingCopier() throws Exception {
        CacheConfiguration<Long, CacheCopierTest.Person> cacheConfiguration = baseConfig.add(new DefaultCopierConfiguration<>(SerializingCopier.<CacheCopierTest.Person>asCopierClass(), VALUE)).add(new DefaultSerializerConfiguration<>(CacheCopierTest.PersonSerializer.class, DefaultSerializerConfiguration.Type.VALUE)).build();
        Cache<Long, CacheCopierTest.Person> cache = cacheManager.createCache("cache", cacheConfiguration);
        CacheCopierTest.Person original = new CacheCopierTest.Person("Bar", 24);
        cache.put(1L, original);
        CacheCopierTest.Person retrieved = cache.get(1L);
        Assert.assertNotSame(original, retrieved);
        Assert.assertThat(retrieved.name, Matchers.equalTo("Bar"));
        Assert.assertThat(retrieved.age, Matchers.equalTo(24));
        original.age = 56;
        retrieved = cache.get(1L);
        Assert.assertThat(retrieved.age, Matchers.equalTo(24));
        Assert.assertNotSame(cache.get(1L), cache.get(1L));
    }

    @Test
    public void testReadWriteCopier() throws Exception {
        CacheConfiguration<Long, CacheCopierTest.Person> cacheConfiguration = baseConfig.add(new DefaultCopierConfiguration<>(CacheCopierTest.PersonCopier.class, VALUE)).build();
        Cache<Long, CacheCopierTest.Person> cache = cacheManager.createCache("cache", cacheConfiguration);
        CacheCopierTest.Person original = new CacheCopierTest.Person("Bar", 24);
        cache.put(1L, original);
        CacheCopierTest.Person retrieved = cache.get(1L);
        Assert.assertNotSame(original, retrieved);
        Assert.assertThat(retrieved.name, Matchers.equalTo("Bar"));
        Assert.assertThat(retrieved.age, Matchers.equalTo(24));
        original.age = 56;
        retrieved = cache.get(1L);
        Assert.assertThat(retrieved.age, Matchers.equalTo(24));
        Assert.assertNotSame(cache.get(1L), cache.get(1L));
    }

    private static class Description {
        int id;

        String alias;

        Description(CacheCopierTest.Description other) {
            this.id = other.id;
            this.alias = other.alias;
        }

        Description(int id, String alias) {
            this.id = id;
            this.alias = alias;
        }

        @Override
        public boolean equals(final Object other) {
            if ((this) == other)
                return true;

            if ((other == null) || ((this.getClass()) != (other.getClass())))
                return false;

            CacheCopierTest.Description that = ((CacheCopierTest.Description) (other));
            if ((id) != (that.id))
                return false;

            if ((alias) == null ? (alias) != null : !(alias.equals(that.alias)))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = (31 * result) + (id);
            result = (31 * result) + ((alias) == null ? 0 : alias.hashCode());
            return result;
        }
    }

    private static class Person implements Serializable {
        private static final long serialVersionUID = 1L;

        String name;

        int age;

        Person(CacheCopierTest.Person other) {
            this.name = other.name;
            this.age = other.age;
        }

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(final Object other) {
            if ((this) == other)
                return true;

            if ((other == null) || ((this.getClass()) != (other.getClass())))
                return false;

            CacheCopierTest.Person that = ((CacheCopierTest.Person) (other));
            if ((age) != (that.age))
                return false;

            if ((name) == null ? (that.name) != null : !(name.equals(that.name)))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = (31 * result) + (age);
            result = (31 * result) + ((name) == null ? 0 : name.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return ((("Person[name: " + ((name) != null ? name : "")) + ", age: ") + (age)) + "]";
        }
    }

    public static class DescriptionCopier extends ReadWriteCopier<CacheCopierTest.Description> {
        @Override
        public CacheCopierTest.Description copy(final CacheCopierTest.Description obj) {
            return new CacheCopierTest.Description(obj);
        }
    }

    public static class PersonOnReadCopier implements Copier<CacheCopierTest.Person> {
        @Override
        public CacheCopierTest.Person copyForRead(final CacheCopierTest.Person obj) {
            return new CacheCopierTest.Person(obj);
        }

        @Override
        public CacheCopierTest.Person copyForWrite(final CacheCopierTest.Person obj) {
            return obj;
        }
    }

    public static class PersonOnWriteCopier implements Copier<CacheCopierTest.Person> {
        @Override
        public CacheCopierTest.Person copyForRead(final CacheCopierTest.Person obj) {
            return obj;
        }

        @Override
        public CacheCopierTest.Person copyForWrite(final CacheCopierTest.Person obj) {
            return new CacheCopierTest.Person(obj);
        }
    }

    public static class PersonCopier extends ReadWriteCopier<CacheCopierTest.Person> {
        @Override
        public CacheCopierTest.Person copy(final CacheCopierTest.Person obj) {
            return new CacheCopierTest.Person(obj);
        }
    }

    public static class PersonSerializer implements Serializer<CacheCopierTest.Person> {
        private static final Charset CHARSET = Charset.forName("US-ASCII");

        public PersonSerializer(ClassLoader loader) {
        }

        @Override
        public ByteBuffer serialize(final CacheCopierTest.Person object) throws SerializerException {
            ByteBuffer buffer = ByteBuffer.allocate(((object.name.length()) + 4));
            buffer.putInt(object.age);
            buffer.put(object.name.getBytes(CacheCopierTest.PersonSerializer.CHARSET)).flip();
            return buffer;
        }

        @Override
        public CacheCopierTest.Person read(final ByteBuffer binary) throws ClassNotFoundException, SerializerException {
            int age = binary.getInt();
            byte[] bytes = new byte[binary.remaining()];
            binary.get(bytes);
            String name = new String(bytes, CacheCopierTest.PersonSerializer.CHARSET);
            return new CacheCopierTest.Person(name, age);
        }

        @Override
        public boolean equals(final CacheCopierTest.Person object, final ByteBuffer binary) throws ClassNotFoundException, SerializerException {
            return object.equals(read(binary));
        }
    }
}

