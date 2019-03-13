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


import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.hamcrest.Matchers;
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
public class LoaderWriterSimpleEhcacheTest {
    private CacheManager cacheManager;

    private Cache<Number, CharSequence> testCache;

    private CacheLoaderWriter<Number, CharSequence> cacheLoaderWriter;

    @Test
    public void testSimplePutIfAbsentWithLoaderAndWriter_absent() throws Exception {
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.putIfAbsent(1, "one"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).load(ArgumentMatchers.eq(1));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).write(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"));
    }

    @Test
    public void testSimplePutIfAbsentWithLoaderAndWriter_existsInSor() throws Exception {
        Mockito.when(cacheLoaderWriter.load(ArgumentMatchers.eq(1))).thenAnswer(((Answer) (( invocation) -> "un")));
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.putIfAbsent(1, "one"), Matchers.<CharSequence>equalTo("un"));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("un"));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).load(ArgumentMatchers.eq(1));
        Mockito.verifyNoMoreInteractions(cacheLoaderWriter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimplePutIfAbsentWithLoaderAndWriter_existsInStore() throws Exception {
        testCache.put(1, "un");
        Mockito.reset(cacheLoaderWriter);
        Assert.assertThat(testCache.putIfAbsent(1, "one"), Matchers.<CharSequence>equalTo("un"));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("un"));
        Mockito.verifyZeroInteractions(cacheLoaderWriter);
    }

    @Test
    public void testSimpleReplace2ArgsWithLoaderAndWriter_absent() throws Exception {
        Mockito.when(cacheLoaderWriter.load(ArgumentMatchers.eq(1))).thenAnswer(((Answer) (( invocation) -> null)));
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.replace(1, "one"), Matchers.is(Matchers.nullValue()));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).load(ArgumentMatchers.eq(1));
        Mockito.verifyNoMoreInteractions(cacheLoaderWriter);
    }

    @Test
    public void testSimpleReplace2ArgsWithLoaderAndWriter_existsInSor() throws Exception {
        Mockito.when(cacheLoaderWriter.load(ArgumentMatchers.eq(1))).thenAnswer(((Answer) (( invocation) -> "un")));
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.replace(1, "one"), Matchers.<CharSequence>equalTo("un"));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).load(ArgumentMatchers.eq(1));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).write(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleReplace2ArgsWithLoaderAndWriter_existsInStore() throws Exception {
        testCache.put(1, "un");
        Mockito.reset(cacheLoaderWriter);
        Assert.assertThat(testCache.replace(1, "one"), Matchers.<CharSequence>equalTo("un"));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).write(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"));
        Mockito.verifyNoMoreInteractions(cacheLoaderWriter);
    }

    @Test
    public void testSimpleReplace3ArgsWithLoaderAndWriter_absent() throws Exception {
        Mockito.when(cacheLoaderWriter.load(ArgumentMatchers.eq(1))).thenAnswer(((Answer) (( invocation) -> null)));
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.replace(1, "un", "one"), Matchers.is(false));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).load(ArgumentMatchers.eq(1));
        Mockito.verifyNoMoreInteractions(cacheLoaderWriter);
    }

    @Test
    public void testSimpleReplace3ArgsWithLoaderAndWriter_existsInSor() throws Exception {
        Mockito.when(cacheLoaderWriter.load(ArgumentMatchers.eq(1))).thenAnswer(((Answer) (( invocation) -> "un")));
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.replace(1, "un", "one"), Matchers.is(true));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).load(ArgumentMatchers.eq(1));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).write(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"));
    }

    @Test
    public void testSimpleReplace3ArgsWithLoaderAndWriter_existsInSor_notEquals() throws Exception {
        Mockito.when(cacheLoaderWriter.load(ArgumentMatchers.eq(1))).thenAnswer(((Answer) (( invocation) -> "un")));
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.replace(1, "uno", "one"), Matchers.is(false));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("un"));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).load(ArgumentMatchers.eq(1));
        Mockito.verifyNoMoreInteractions(cacheLoaderWriter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleReplace3ArgsWithLoaderAndWriter_existsInStore() throws Exception {
        testCache.put(1, "un");
        Mockito.reset(cacheLoaderWriter);
        Assert.assertThat(testCache.replace(1, "un", "one"), Matchers.is(true));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).write(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"));
        Mockito.verifyNoMoreInteractions(cacheLoaderWriter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleReplace3ArgsWithLoaderAndWriter_existsInStore_notEquals() throws Exception {
        testCache.put(1, "un");
        Mockito.reset(cacheLoaderWriter);
        Assert.assertThat(testCache.replace(1, "uno", "one"), Matchers.is(false));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("un"));
        Mockito.verifyZeroInteractions(cacheLoaderWriter);
    }

    @Test
    public void testSimpleRemove2ArgsWithLoaderAndWriter_absent() throws Exception {
        Mockito.when(cacheLoaderWriter.load(ArgumentMatchers.eq(1))).thenAnswer(((Answer) (( invocation) -> null)));
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.remove(1, "one"), Matchers.is(false));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).load(ArgumentMatchers.eq(1));
        Mockito.verifyNoMoreInteractions(cacheLoaderWriter);
    }

    @Test
    public void testSimpleRemove2ArgsWithLoaderAndWriter_existsInSor() throws Exception {
        Mockito.when(cacheLoaderWriter.load(ArgumentMatchers.eq(1))).thenAnswer(((Answer) (( invocation) -> "un")));
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.remove(1, "un"), Matchers.is(true));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).load(ArgumentMatchers.eq(1));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).delete(ArgumentMatchers.eq(1));
    }

    @Test
    public void testSimpleRemove2ArgsWithLoaderAndWriter_existsInSor_notEquals() throws Exception {
        Mockito.when(cacheLoaderWriter.load(ArgumentMatchers.eq(1))).thenAnswer(((Answer) (( invocation) -> "un")));
        Assert.assertThat(testCache.containsKey(1), Matchers.is(false));
        Assert.assertThat(testCache.remove(1, "one"), Matchers.is(false));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).load(ArgumentMatchers.eq(1));
        Mockito.verifyNoMoreInteractions(cacheLoaderWriter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleRemove2ArgsWithLoaderAndWriter_existsInStore() throws Exception {
        testCache.put(1, "un");
        Mockito.reset(cacheLoaderWriter);
        Assert.assertThat(testCache.remove(1, "un"), Matchers.is(true));
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).delete(ArgumentMatchers.eq(1));
        Mockito.verifyNoMoreInteractions(cacheLoaderWriter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleRemove2ArgsWithLoaderAndWriter_existsInStore_notEquals() throws Exception {
        testCache.put(1, "un");
        Mockito.reset(cacheLoaderWriter);
        Assert.assertThat(testCache.remove(1, "one"), Matchers.is(false));
        Mockito.verifyZeroInteractions(cacheLoaderWriter);
    }
}

