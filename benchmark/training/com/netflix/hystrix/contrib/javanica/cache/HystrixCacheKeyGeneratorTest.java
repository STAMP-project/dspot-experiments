/**
 * Copyright 2015 Netflix, Inc.
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
package com.netflix.hystrix.contrib.javanica.cache;


import DefaultHystrixGeneratedCacheKey.EMPTY;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.MetaHolder;
import org.junit.Assert;
import org.junit.Test;


public class HystrixCacheKeyGeneratorTest {
    @Test
    public void testGenerateCacheKey_givenUser_shouldReturnCorrectCacheKey() throws NoSuchMethodException {
        // given
        HystrixCacheKeyGeneratorTest.TestCacheClass testCacheClass = new HystrixCacheKeyGeneratorTest.TestCacheClass();
        String id = "1";
        HystrixCacheKeyGeneratorTest.User user = new HystrixCacheKeyGeneratorTest.User();
        user.setId(id);
        HystrixCacheKeyGeneratorTest.Profile profile = new HystrixCacheKeyGeneratorTest.Profile("user name");
        user.setProfile(profile);
        String expectedKey = id + (user.getProfile().getName());
        MetaHolder metaHolder = MetaHolder.builder().method(HystrixCacheKeyGeneratorTest.TestCacheClass.class.getMethod("cacheResultMethod", String.class, HystrixCacheKeyGeneratorTest.User.class)).args(new Object[]{ id, user }).obj(testCacheClass).build();
        CacheInvocationContext<CacheResult> context = CacheInvocationContextFactory.createCacheResultInvocationContext(metaHolder);
        HystrixCacheKeyGenerator keyGenerator = HystrixCacheKeyGenerator.getInstance();
        // when
        String actual = keyGenerator.generateCacheKey(context).getCacheKey();
        // then
        Assert.assertEquals(expectedKey, actual);
    }

    @Test
    public void testGenerateCacheKey_givenUserWithNullProfile_shouldReturnCorrectCacheKey() throws NoSuchMethodException {
        // given
        HystrixCacheKeyGeneratorTest.TestCacheClass testCacheClass = new HystrixCacheKeyGeneratorTest.TestCacheClass();
        String id = "1";
        HystrixCacheKeyGeneratorTest.User user = new HystrixCacheKeyGeneratorTest.User();
        user.setId(id);
        user.setProfile(null);
        String expectedKey = id;
        MetaHolder metaHolder = MetaHolder.builder().method(HystrixCacheKeyGeneratorTest.TestCacheClass.class.getMethod("cacheResultMethod", String.class, HystrixCacheKeyGeneratorTest.User.class)).args(new Object[]{ id, user }).obj(testCacheClass).build();
        CacheInvocationContext<CacheResult> context = CacheInvocationContextFactory.createCacheResultInvocationContext(metaHolder);
        HystrixCacheKeyGenerator keyGenerator = HystrixCacheKeyGenerator.getInstance();
        // when
        String actual = keyGenerator.generateCacheKey(context).getCacheKey();
        // then
        Assert.assertEquals(expectedKey, actual);
    }

    @Test
    public void testGenerateCacheKey_givenCacheKeyMethodWithNoArguments_shouldReturnEmptyCacheKey() throws NoSuchMethodException {
        // given
        HystrixCacheKeyGeneratorTest.TestCacheClass testCacheClass = new HystrixCacheKeyGeneratorTest.TestCacheClass();
        MetaHolder metaHolder = MetaHolder.builder().method(HystrixCacheKeyGeneratorTest.TestCacheClass.class.getMethod("cacheResultMethod")).args(new Object[]{  }).obj(testCacheClass).build();
        CacheInvocationContext<CacheResult> context = CacheInvocationContextFactory.createCacheResultInvocationContext(metaHolder);
        HystrixCacheKeyGenerator keyGenerator = HystrixCacheKeyGenerator.getInstance();
        // when
        HystrixGeneratedCacheKey actual = keyGenerator.generateCacheKey(context);
        // then
        Assert.assertEquals(EMPTY, actual);
    }

    public static class TestCacheClass {
        @CacheResult
        public Object cacheResultMethod(@CacheKey
        String id, @CacheKey("profile.name")
        HystrixCacheKeyGeneratorTest.User user) {
            return "test";
        }

        @CacheResult
        public Object cacheResultMethod() {
            return "test";
        }
    }

    public static class User {
        private String id;

        private HystrixCacheKeyGeneratorTest.Profile profile;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public HystrixCacheKeyGeneratorTest.Profile getProfile() {
            return profile;
        }

        public void setProfile(HystrixCacheKeyGeneratorTest.Profile profile) {
            this.profile = profile;
        }
    }

    public static class Profile {
        private String name;

        public Profile() {
        }

        public Profile(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

