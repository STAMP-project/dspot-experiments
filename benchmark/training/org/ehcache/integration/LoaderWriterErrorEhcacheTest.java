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


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.spi.loaderwriter.BulkCacheLoadingException;
import org.ehcache.spi.loaderwriter.BulkCacheWritingException;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.spi.loaderwriter.CacheLoadingException;
import org.ehcache.spi.loaderwriter.CacheWritingException;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Ludovic Orban
 */
public class LoaderWriterErrorEhcacheTest {
    private CacheManager cacheManager;

    private Cache<Number, CharSequence> testCache;

    private CacheLoaderWriter<Number, CharSequence> cacheLoaderWriter;

    @Test
    public void testGetWithLoaderException() throws Exception {
        Mockito.when(cacheLoaderWriter.load(ArgumentMatchers.eq(1))).thenThrow(new Exception("TestException: cannot load data"));
        try {
            testCache.get(1);
            Assert.fail("expected CacheLoadingException");
        } catch (CacheLoadingException ex) {
            // expected
        }
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).load(ArgumentMatchers.eq(1));
    }

    @Test
    public void testGetAllWithLoaderException() throws Exception {
        Mockito.when(cacheLoaderWriter.loadAll(ArgumentMatchers.<Iterable<Number>>any())).thenAnswer(( invocation) -> {
            @SuppressWarnings("unchecked")
            Iterable<Integer> iterable = ((Iterable<Integer>) (invocation.getArguments()[0]));
            Map<Number, CharSequence> result = new HashMap<>();
            for (int i : iterable) {
                switch (i) {
                    case 1 :
                        result.put(1, "one");
                        break;
                    case 2 :
                        throw new Exception("Mock Exception: cannot load 2");
                    case 3 :
                        result.put(3, "three");
                        break;
                    case 4 :
                        result.put(4, null);
                        break;
                    default :
                        throw new AssertionError(("should not try to load key " + i));
                }
            }
            return result;
        });
        try {
            testCache.getAll(new HashSet<Number>(Arrays.asList(1, 2, 3, 4)));
            Assert.fail("expected BulkCacheLoadingException");
        } catch (BulkCacheLoadingException ex) {
            Assert.assertThat(ex.getFailures().size(), Is.is(1));
            Assert.assertThat(ex.getFailures().get(2), Is.is(IsNull.notNullValue()));
            Assert.assertThat(ex.getSuccesses().size(), Is.is(Matchers.lessThan(4)));
            Assert.assertThat(ex.getSuccesses().containsKey(2), Is.is(false));
        }
    }

    @Test
    public void testPutWithWriterException() throws Exception {
        Mockito.doThrow(new Exception("Mock Exception: cannot write 1")).when(cacheLoaderWriter).write(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"));
        try {
            testCache.put(1, "one");
            Assert.fail("expected CacheWritingException");
        } catch (CacheWritingException ex) {
            // expected
        }
    }

    @Test
    public void testRemoveWithWriterException() throws Exception {
        Mockito.doThrow(new Exception("Mock Exception: cannot write 1")).when(cacheLoaderWriter).delete(ArgumentMatchers.eq(1));
        try {
            testCache.remove(1);
            Assert.fail("expected CacheWritingException");
        } catch (CacheWritingException ex) {
            // expected
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testRemoveAllWithWriterException() throws Exception {
        Mockito.doAnswer(( invocation) -> {
            Iterable<Integer> iterable = ((Iterable) (invocation.getArguments()[0]));
            Set<Integer> result = new HashSet<>();
            for (Integer i : iterable) {
                switch (i) {
                    case 2 :
                        throw new Exception("Mock Exception: cannot write 2");
                    case 1 :
                    case 3 :
                    case 4 :
                        result.add(i);
                        break;
                    default :
                        throw new AssertionError(("should not try to delete key " + i));
                }
            }
            return result;
        }).when(cacheLoaderWriter).deleteAll(ArgumentMatchers.<Iterable>any());
        try {
            testCache.removeAll(new HashSet<Number>(Arrays.asList(1, 2, 3, 4)));
            Assert.fail("expected CacheWritingException");
        } catch (BulkCacheWritingException ex) {
            Assert.assertThat(ex.getFailures().size(), Is.is(1));
            Assert.assertThat(ex.getFailures().get(2), Is.is(IsNull.notNullValue()));
            Assert.assertThat(ex.getSuccesses().size(), Is.is(3));
            Assert.assertThat(ex.getSuccesses().containsAll(Arrays.asList(1, 3, 4)), Is.is(true));
        }
    }

    @Test
    public void testRemove2ArgsWithNoCacheEntry_should_not_call_writer() throws Exception {
        Mockito.doThrow(new Exception("Mock Exception: cannot write 1")).when(cacheLoaderWriter).delete(ArgumentMatchers.eq(1));
        testCache.remove(1, "one");
    }

    @Test
    public void testRemove2ArgsWithNotMatchingCacheEntry_should_not_call_writer() throws Exception {
        Mockito.doThrow(new Exception("Mock Exception: cannot write 1")).when(cacheLoaderWriter).delete(ArgumentMatchers.eq(1));
        testCache.put(1, "un");
        testCache.remove(1, "one");
    }

    @Test
    public void testRemove2ArgsWithWriterException_should_call_writer() throws Exception {
        Mockito.doThrow(new Exception("Mock Exception: cannot write 1")).when(cacheLoaderWriter).delete(ArgumentMatchers.eq(1));
        testCache.put(1, "one");
        try {
            testCache.remove(1, "one");
            Assert.fail("expected CacheWritingException");
        } catch (CacheWritingException ex) {
            // expected
        }
    }

    @Test
    public void testReplace2ArgsWithWriterException_should_call_writer() throws Exception {
        Mockito.doThrow(new Exception("Mock Exception: cannot write 1")).when(cacheLoaderWriter).write(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one#2"));
        testCache.put(1, "one");
        try {
            testCache.replace(1, "one#2");
            Assert.fail("expected CacheWritingException");
        } catch (CacheWritingException ex) {
            // expected
        }
    }

    @Test
    public void testReplace2ArgsWithNoCacheEntry_should_not_call_writer() throws Exception {
        Mockito.doThrow(new Exception("Mock Exception: cannot write 1")).when(cacheLoaderWriter).write(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one#2"));
        testCache.replace(1, "one#2");
    }

    @Test
    public void testReplace3ArgsWithWriterException_should_call_writer() throws Exception {
        Mockito.doThrow(new Exception("Mock Exception: cannot write 1")).when(cacheLoaderWriter).write(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one#2"));
        testCache.put(1, "one");
        try {
            testCache.replace(1, "one", "one#2");
            Assert.fail("expected CacheWritingException");
        } catch (CacheWritingException ex) {
            // expected
        }
    }

    @Test
    public void testReplace3ArgsWithNotMatchingCacheEntry_should_not_call_writer() throws Exception {
        Mockito.doThrow(new Exception("Mock Exception: cannot write 1")).when(cacheLoaderWriter).write(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one#2"));
        testCache.put(1, "un");
        testCache.replace(1, "one", "one#2");
    }

    @Test
    public void testReplace3ArgsWithNoCacheEntry_should_not_call_writer() throws Exception {
        Mockito.doThrow(new Exception("Mock Exception: cannot write 1")).when(cacheLoaderWriter).write(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one#2"));
        testCache.replace(1, "one", "one#2");
    }

    @Test
    public void testPutIfAbsentWithWriterException_should_call_writer() throws Exception {
        Mockito.doThrow(new Exception("Mock Exception: cannot write 1")).when(cacheLoaderWriter).write(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"));
        try {
            testCache.putIfAbsent(1, "one");
            Assert.fail("expected CacheWritingException");
        } catch (CacheWritingException ex) {
            // expected
        }
        testCache.put(2, "two");
        testCache.putIfAbsent(2, "two#2");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testPutAllWithWriterException() throws Exception {
        Mockito.doThrow(new Exception("Mock Exception: cannot write 1")).when(cacheLoaderWriter).writeAll(ArgumentMatchers.<Iterable>any());
        Map<Integer, String> values = new HashMap<>();
        values.put(1, "one");
        values.put(2, "two");
        try {
            testCache.putAll(values);
            Assert.fail("expected CacheWritingException");
        } catch (CacheWritingException ex) {
            // expected
        }
    }
}

