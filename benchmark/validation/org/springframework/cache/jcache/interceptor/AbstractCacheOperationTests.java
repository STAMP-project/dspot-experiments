/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cache.jcache.interceptor;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.cache.jcache.AbstractJCacheTests;


/**
 *
 *
 * @author Stephane Nicoll
 */
public abstract class AbstractCacheOperationTests<O extends JCacheOperation<?>> extends AbstractJCacheTests {
    protected final SampleObject sampleInstance = new SampleObject();

    @Test
    public void simple() {
        O operation = createSimpleOperation();
        Assert.assertEquals("Wrong cache name", "simpleCache", getCacheName());
        Assert.assertEquals(("Unexpected number of annotation on " + (getMethod())), 1, getAnnotations().size());
        Assert.assertEquals("Wrong method annotation", getCacheAnnotation(), getAnnotations().iterator().next());
        Assert.assertNotNull("cache resolver should be set", getCacheResolver());
    }
}

