/**
 * Copyright 2002-2014 the original author or authors.
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


import java.io.IOException;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CachePut;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cache.jcache.AbstractJCacheTests;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class CachePutOperationTests extends AbstractCacheOperationTests<CachePutOperation> {
    @Test
    public void simplePut() {
        CachePutOperation operation = createSimpleOperation();
        CacheInvocationParameter[] allParameters = operation.getAllParameters(2L, sampleInstance);
        Assert.assertEquals(2, allParameters.length);
        assertCacheInvocationParameter(allParameters[0], Long.class, 2L, 0);
        assertCacheInvocationParameter(allParameters[1], SampleObject.class, sampleInstance, 1);
        CacheInvocationParameter valueParameter = operation.getValueParameter(2L, sampleInstance);
        Assert.assertNotNull(valueParameter);
        assertCacheInvocationParameter(valueParameter, SampleObject.class, sampleInstance, 1);
    }

    @Test
    public void noCacheValue() {
        CacheMethodDetails<CachePut> methodDetails = create(CachePut.class, SampleObject.class, "noCacheValue", Long.class);
        thrown.expect(IllegalArgumentException.class);
        createDefaultOperation(methodDetails);
    }

    @Test
    public void multiCacheValues() {
        CacheMethodDetails<CachePut> methodDetails = create(CachePut.class, SampleObject.class, "multiCacheValues", Long.class, SampleObject.class, SampleObject.class);
        thrown.expect(IllegalArgumentException.class);
        createDefaultOperation(methodDetails);
    }

    @Test
    public void invokeWithWrongParameters() {
        CachePutOperation operation = createSimpleOperation();
        thrown.expect(IllegalStateException.class);
        operation.getValueParameter(2L);
    }

    @Test
    public void fullPutConfig() {
        CacheMethodDetails<CachePut> methodDetails = create(CachePut.class, SampleObject.class, "fullPutConfig", Long.class, SampleObject.class);
        CachePutOperation operation = createDefaultOperation(methodDetails);
        Assert.assertTrue(operation.isEarlyPut());
        Assert.assertNotNull(operation.getExceptionTypeFilter());
        Assert.assertTrue(operation.getExceptionTypeFilter().match(IOException.class));
        Assert.assertFalse(operation.getExceptionTypeFilter().match(NullPointerException.class));
    }
}

