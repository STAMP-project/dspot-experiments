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
package org.springframework.cache.config;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * Abstract cache annotation tests (containing several reusable methods).
 *
 * @author Costin Leau
 * @author Chris Beams
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public abstract class AbstractCacheAnnotationTests {
    protected ConfigurableApplicationContext ctx;

    protected CacheableService<?> cs;

    protected CacheableService<?> ccs;

    protected CacheManager cm;

    @Test
    public void testCacheable() throws Exception {
        testCacheable(this.cs);
    }

    @Test
    public void testCacheableNull() throws Exception {
        testCacheableNull(this.cs);
    }

    @Test
    public void testCacheableSync() throws Exception {
        testCacheableSync(this.cs);
    }

    @Test
    public void testCacheableSyncNull() throws Exception {
        testCacheableSyncNull(this.cs);
    }

    @Test
    public void testInvalidate() throws Exception {
        testEvict(this.cs);
    }

    @Test
    public void testEarlyInvalidate() throws Exception {
        testEvictEarly(this.cs);
    }

    @Test
    public void testEvictWithException() throws Exception {
        testEvictException(this.cs);
    }

    @Test
    public void testEvictAll() throws Exception {
        testEvictAll(this.cs);
    }

    @Test
    public void testInvalidateWithKey() throws Exception {
        testEvictWKey(this.cs);
    }

    @Test
    public void testEarlyInvalidateWithKey() throws Exception {
        testEvictWKeyEarly(this.cs);
    }

    @Test
    public void testConditionalExpression() throws Exception {
        testConditionalExpression(this.cs);
    }

    @Test
    public void testConditionalExpressionSync() throws Exception {
        testConditionalExpressionSync(this.cs);
    }

    @Test
    public void testUnlessExpression() throws Exception {
        testUnlessExpression(this.cs);
    }

    @Test
    public void testClassCacheUnlessExpression() throws Exception {
        testUnlessExpression(this.cs);
    }

    @Test
    public void testKeyExpression() throws Exception {
        testKeyExpression(this.cs);
    }

    @Test
    public void testVarArgsKey() throws Exception {
        testVarArgsKey(this.cs);
    }

    @Test
    public void testClassCacheCacheable() throws Exception {
        testCacheable(this.ccs);
    }

    @Test
    public void testClassCacheInvalidate() throws Exception {
        testEvict(this.ccs);
    }

    @Test
    public void testClassEarlyInvalidate() throws Exception {
        testEvictEarly(this.ccs);
    }

    @Test
    public void testClassEvictAll() throws Exception {
        testEvictAll(this.ccs);
    }

    @Test
    public void testClassEvictWithException() throws Exception {
        testEvictException(this.ccs);
    }

    @Test
    public void testClassCacheInvalidateWKey() throws Exception {
        testEvictWKey(this.ccs);
    }

    @Test
    public void testClassEarlyInvalidateWithKey() throws Exception {
        testEvictWKeyEarly(this.ccs);
    }

    @Test
    public void testNullValue() throws Exception {
        testNullValue(this.cs);
    }

    @Test
    public void testClassNullValue() throws Exception {
        Object key = new Object();
        Assert.assertNull(this.ccs.nullValue(key));
        int nr = this.ccs.nullInvocations().intValue();
        Assert.assertNull(this.ccs.nullValue(key));
        Assert.assertEquals(nr, this.ccs.nullInvocations().intValue());
        Assert.assertNull(this.ccs.nullValue(new Object()));
        // the check method is also cached
        Assert.assertEquals(nr, this.ccs.nullInvocations().intValue());
        Assert.assertEquals((nr + 1), AnnotatedClassCacheableService.nullInvocations.intValue());
    }

    @Test
    public void testMethodName() throws Exception {
        testMethodName(this.cs, "name");
    }

    @Test
    public void testClassMethodName() throws Exception {
        testMethodName(this.ccs, "nametestCache");
    }

    @Test
    public void testRootVars() throws Exception {
        testRootVars(this.cs);
    }

    @Test
    public void testClassRootVars() throws Exception {
        testRootVars(this.ccs);
    }

    @Test
    public void testCustomKeyGenerator() {
        Object param = new Object();
        Object r1 = this.cs.customKeyGenerator(param);
        Assert.assertSame(r1, this.cs.customKeyGenerator(param));
        Cache cache = this.cm.getCache("testCache");
        // Checks that the custom keyGenerator was used
        Object expectedKey = SomeCustomKeyGenerator.generateKey("customKeyGenerator", param);
        Assert.assertNotNull(cache.get(expectedKey));
    }

    @Test
    public void testUnknownCustomKeyGenerator() {
        try {
            Object param = new Object();
            this.cs.unknownCustomKeyGenerator(param);
            Assert.fail("should have failed with NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
    }

    @Test
    public void testCustomCacheManager() {
        CacheManager customCm = this.ctx.getBean("customCacheManager", CacheManager.class);
        Object key = new Object();
        Object r1 = this.cs.customCacheManager(key);
        Assert.assertSame(r1, this.cs.customCacheManager(key));
        Cache cache = customCm.getCache("testCache");
        Assert.assertNotNull(cache.get(key));
    }

    @Test
    public void testUnknownCustomCacheManager() {
        try {
            Object param = new Object();
            this.cs.unknownCustomCacheManager(param);
            Assert.fail("should have failed with NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
    }

    @Test
    public void testNullArg() throws Exception {
        testNullArg(this.cs);
    }

    @Test
    public void testClassNullArg() throws Exception {
        testNullArg(this.ccs);
    }

    @Test
    public void testCheckedException() throws Exception {
        testCheckedThrowable(this.cs);
    }

    @Test
    public void testClassCheckedException() throws Exception {
        testCheckedThrowable(this.ccs);
    }

    @Test
    public void testCheckedExceptionSync() throws Exception {
        testCheckedThrowableSync(this.cs);
    }

    @Test
    public void testClassCheckedExceptionSync() throws Exception {
        testCheckedThrowableSync(this.ccs);
    }

    @Test
    public void testUncheckedException() throws Exception {
        testUncheckedThrowable(this.cs);
    }

    @Test
    public void testClassUncheckedException() throws Exception {
        testUncheckedThrowable(this.ccs);
    }

    @Test
    public void testUncheckedExceptionSync() throws Exception {
        testUncheckedThrowableSync(this.cs);
    }

    @Test
    public void testClassUncheckedExceptionSync() throws Exception {
        testUncheckedThrowableSync(this.ccs);
    }

    @Test
    public void testUpdate() {
        testCacheUpdate(this.cs);
    }

    @Test
    public void testClassUpdate() {
        testCacheUpdate(this.ccs);
    }

    @Test
    public void testConditionalUpdate() {
        testConditionalCacheUpdate(this.cs);
    }

    @Test
    public void testClassConditionalUpdate() {
        testConditionalCacheUpdate(this.ccs);
    }

    @Test
    public void testMultiCache() {
        testMultiCache(this.cs);
    }

    @Test
    public void testClassMultiCache() {
        testMultiCache(this.ccs);
    }

    @Test
    public void testMultiEvict() {
        testMultiEvict(this.cs);
    }

    @Test
    public void testClassMultiEvict() {
        testMultiEvict(this.ccs);
    }

    @Test
    public void testMultiPut() {
        testMultiPut(this.cs);
    }

    @Test
    public void testClassMultiPut() {
        testMultiPut(this.ccs);
    }

    @Test
    public void testPutRefersToResult() throws Exception {
        testPutRefersToResult(this.cs);
    }

    @Test
    public void testClassPutRefersToResult() throws Exception {
        testPutRefersToResult(this.ccs);
    }

    @Test
    public void testMultiCacheAndEvict() {
        testMultiCacheAndEvict(this.cs);
    }

    @Test
    public void testClassMultiCacheAndEvict() {
        testMultiCacheAndEvict(this.ccs);
    }

    @Test
    public void testMultiConditionalCacheAndEvict() {
        testMultiConditionalCacheAndEvict(this.cs);
    }

    @Test
    public void testClassMultiConditionalCacheAndEvict() {
        testMultiConditionalCacheAndEvict(this.ccs);
    }
}

