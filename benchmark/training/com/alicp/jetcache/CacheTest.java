/**
 * Created on 2018/1/8.
 */
package com.alicp.jetcache;


import CacheResult.EXISTS_WITHOUT_MSG;
import CacheResult.FAIL_WITHOUT_MSG;
import CacheResult.PART_SUCCESS_WITHOUT_MSG;
import CacheResultCode.NOT_EXISTS;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static CacheResultCode.FAIL;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
@SuppressWarnings("unchecked")
public class CacheTest {
    private Cache cache;

    private Cache concreteCache;

    private int tryLockUnlockCount = 2;

    private int tryLockInquiryCount = 2;

    private int tryLockLockCount = 2;

    private Answer delegateAnswer = ( invocation) -> invocation.getMethod().invoke(concreteCache, invocation.getArguments());

    @Test
    public void testTryLock_Null() {
        Assertions.assertNull(cache.tryLock(null, 1, TimeUnit.HOURS));
    }

    @Test
    public void testTryLock_Success() {
        AutoReleaseLock lock = cache.tryLock("key", 1, TimeUnit.HOURS);
        Assertions.assertNotNull(lock);
        Assertions.assertNotNull(concreteCache.get("key"));
    }

    @Test
    public void testTryLock_LockRetry1() {
        Mockito.when(cache.PUT_IF_ABSENT(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(FAIL_WITHOUT_MSG).then(delegateAnswer);
        AutoReleaseLock lock = cache.tryLock("key", 1, TimeUnit.HOURS);
        Assertions.assertNotNull(lock);
    }

    @Test
    public void testTryLock_LockRetry2() {
        Mockito.when(cache.PUT_IF_ABSENT(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(FAIL_WITHOUT_MSG).thenReturn(PART_SUCCESS_WITHOUT_MSG);
        AutoReleaseLock lock = cache.tryLock("key", 1, TimeUnit.HOURS);
        Assertions.assertNull(lock);
    }

    @Test
    public void testTryLock_LockAndGetRetry1() {
        Mockito.when(cache.PUT_IF_ABSENT(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(FAIL_WITHOUT_MSG);
        AutoReleaseLock lock = cache.tryLock("key", 1, TimeUnit.HOURS);
        Assertions.assertNull(lock);
    }

    @Test
    public void testTryLock_LockAndGetRetry2() {
        Mockito.when(cache.PUT_IF_ABSENT(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(FAIL_WITHOUT_MSG);
        concreteCache.put("key", "other value");
        AutoReleaseLock lock = cache.tryLock("key", 1, TimeUnit.HOURS);
        Assertions.assertNull(lock);
    }

    @Test
    public void testTryLock_LockAndGetRetry3() {
        Mockito.when(cache.PUT_IF_ABSENT(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).then(( i) -> {
            i.getMethod().invoke(concreteCache, i.getArguments());
            return CacheResult.FAIL_WITHOUT_MSG;
        });
        AutoReleaseLock lock = cache.tryLock("key", 1, TimeUnit.HOURS);
        Assertions.assertNotNull(lock);
    }

    @Test
    public void testTryLock_LockAndGetRetry4() {
        Mockito.when(cache.PUT_IF_ABSENT(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).then(( i) -> {
            i.getMethod().invoke(concreteCache, i.getArguments());
            return CacheResult.FAIL_WITHOUT_MSG;
        });
        Mockito.when(cache.GET(ArgumentMatchers.any())).thenReturn(new CacheGetResult(FAIL, null, null)).then(delegateAnswer);
        AutoReleaseLock lock = cache.tryLock("key", 1, TimeUnit.HOURS);
        Assertions.assertNotNull(lock);
    }

    @Test
    public void testTryLock_LockAndGetRetry5() {
        Mockito.when(cache.PUT_IF_ABSENT(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).then(( i) -> {
            i.getMethod().invoke(concreteCache, i.getArguments());
            return CacheResult.FAIL_WITHOUT_MSG;
        });
        Mockito.when(cache.GET(ArgumentMatchers.any())).thenReturn(new CacheGetResult(FAIL, null, null));
        AutoReleaseLock lock = cache.tryLock("key", 1, TimeUnit.HOURS);
        Assertions.assertNull(lock);
    }

    @Test
    public void testTryLock_Unlock1() {
        AutoReleaseLock lock = cache.tryLock("key", 1, TimeUnit.HOURS);
        lock.close();
        Assertions.assertEquals(NOT_EXISTS, concreteCache.GET("key").getResultCode());
    }

    @Test
    public void testTryLock_Unlock2() {
        Mockito.when(cache.REMOVE(ArgumentMatchers.any())).thenReturn(FAIL_WITHOUT_MSG).then(delegateAnswer);
        AutoReleaseLock lock = cache.tryLock("key", 1, TimeUnit.HOURS);
        lock.close();
        Assertions.assertEquals(NOT_EXISTS, concreteCache.GET("key").getResultCode());
    }

    @Test
    public void testTryLock_Unlock3() {
        Mockito.when(cache.REMOVE(ArgumentMatchers.any())).thenReturn(FAIL_WITHOUT_MSG).thenReturn(PART_SUCCESS_WITHOUT_MSG);
        AutoReleaseLock lock = cache.tryLock("key", 1, TimeUnit.HOURS);
        lock.close();
        Assertions.assertNotNull(concreteCache.get("key"));
    }

    @Test
    public void testTryLock_Unlock4() {
        Mockito.when(cache.REMOVE(ArgumentMatchers.any())).thenReturn(EXISTS_WITHOUT_MSG);
        AutoReleaseLock lock = cache.tryLock("key", 1, TimeUnit.HOURS);
        lock.close();
        Assertions.assertNotNull(concreteCache.get("key"));
    }

    @Test
    public void testTryLock_Unlock5() throws Exception {
        Mockito.when(cache.PUT_IF_ABSENT(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).then(( i) -> concreteCache.PUT_IF_ABSENT("key", i.getArgument(1).toString(), 100, TimeUnit.HOURS));
        AutoReleaseLock lock = cache.tryLock("key", 1, TimeUnit.MILLISECONDS);
        Thread.sleep(2);
        lock.close();
        Assertions.assertNotNull(concreteCache.GET("key"));
    }
}

