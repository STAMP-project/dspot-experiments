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
package org.ehcache.jsr107;


import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Ludovic Orban
 */
public class LoaderWriterTest {
    @Mock
    private CacheLoader<Number, CharSequence> cacheLoader;

    @Mock
    private CacheWriter<Number, CharSequence> cacheWriter;

    private Cache<Number, CharSequence> testCache;

    private CacheManager cacheManager;

    @Test
    public void testSimplePutIfAbsentWithLoaderAndWriter_absent() throws Exception {
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.putIfAbsent(1, "one"), Matchers.is(true));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verify(cacheWriter, Mockito.times(1)).write(ArgumentMatchers.eq(new Eh107CacheLoaderWriter.Entry<Number, CharSequence>(1, "one")));
    }

    @Test
    public void testSimplePutIfAbsentWithLoaderAndWriter_existsInSor() throws Exception {
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.putIfAbsent(1, "one"), Matchers.is(true));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verify(cacheWriter, Mockito.times(1)).write(ArgumentMatchers.eq(new Eh107CacheLoaderWriter.Entry<Number, CharSequence>(1, "one")));
    }

    @Test
    public void testSimplePutIfAbsentWithLoaderAndWriter_existsInStore() throws Exception {
        testCache.put(1, "un");
        reset(cacheWriter);
        Assert.assertThat(testCache.putIfAbsent(1, "one"), Matchers.is(false));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("un"));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verifyZeroInteractions(cacheWriter);
    }

    @Test
    public void testSimpleReplace2ArgsWithLoaderAndWriter_absent() throws Exception {
        Mockito.when(cacheLoader.load(ArgumentMatchers.eq(1))).thenAnswer(( invocation) -> null);
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.replace(1, "one"), Matchers.is(false));
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verifyZeroInteractions(cacheWriter);
    }

    @Test
    public void testSimpleReplace2ArgsWithLoaderAndWriter_existsInSor() throws Exception {
        Mockito.when(cacheLoader.load(ArgumentMatchers.eq(1))).thenAnswer(( invocation) -> "un");
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.replace(1, "one"), Matchers.is(false));
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verifyZeroInteractions(cacheWriter);
    }

    @Test
    public void testSimpleReplace2ArgsWithLoaderAndWriter_existsInStore() throws Exception {
        testCache.put(1, "un");
        reset(cacheWriter);
        Assert.assertThat(testCache.replace(1, "one"), Matchers.is(true));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verify(cacheWriter, Mockito.times(1)).write(new Eh107CacheLoaderWriter.Entry<Number, CharSequence>(1, "one"));
    }

    @Test
    public void testSimpleReplace3ArgsWithLoaderAndWriter_absent() throws Exception {
        Mockito.when(cacheLoader.load(ArgumentMatchers.eq(1))).thenAnswer(( invocation) -> null);
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.replace(1, "un", "one"), Matchers.is(false));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verifyZeroInteractions(cacheWriter);
    }

    @Test
    public void testSimpleReplace3ArgsWithLoaderAndWriter_existsInSor() throws Exception {
        Mockito.when(cacheLoader.load(ArgumentMatchers.eq(1))).thenAnswer(( invocation) -> "un");
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.replace(1, "un", "one"), Matchers.is(false));
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verifyZeroInteractions(cacheWriter);
    }

    @Test
    public void testSimpleReplace3ArgsWithLoaderAndWriter_existsInStore() throws Exception {
        testCache.put(1, "un");
        reset(cacheWriter);
        Assert.assertThat(testCache.replace(1, "un", "one"), Matchers.is(true));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verify(cacheWriter, Mockito.times(1)).write(new Eh107CacheLoaderWriter.Entry<Number, CharSequence>(1, "one"));
    }

    @Test
    public void testSimpleReplace3ArgsWithLoaderAndWriter_existsInStore_notEquals() throws Exception {
        testCache.put(1, "un");
        reset(cacheWriter);
        Assert.assertThat(testCache.replace(1, "uno", "one"), Matchers.is(false));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("un"));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verifyZeroInteractions(cacheWriter);
    }

    @Test
    public void testSimpleRemove2ArgsWithLoaderAndWriter_absent() throws Exception {
        Mockito.when(cacheLoader.load(ArgumentMatchers.eq(1))).thenAnswer(( invocation) -> null);
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.remove(1, "one"), Matchers.is(false));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verifyZeroInteractions(cacheWriter);
    }

    @Test
    public void testSimpleRemove2ArgsWithLoaderAndWriter_existsInSor() throws Exception {
        Mockito.when(cacheLoader.load(ArgumentMatchers.eq(1))).thenAnswer(( invocation) -> "un");
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.remove(1, "un"), Matchers.is(false));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verifyZeroInteractions(cacheWriter);
    }

    @Test
    public void testSimpleRemove2ArgsWithLoaderAndWriter_existsInStore() throws Exception {
        testCache.put(1, "un");
        reset(cacheWriter);
        Assert.assertThat(testCache.remove(1, "un"), Matchers.is(true));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verify(cacheWriter, Mockito.times(1)).delete(ArgumentMatchers.eq(1));
    }

    @Test
    public void testSimpleRemove2ArgsWithLoaderAndWriter_existsInStore_notEquals() throws Exception {
        testCache.put(1, "un");
        reset(cacheWriter);
        Assert.assertThat(testCache.remove(1, "one"), Matchers.is(false));
        Mockito.verifyZeroInteractions(cacheLoader);
        Mockito.verifyZeroInteractions(cacheWriter);
    }
}

