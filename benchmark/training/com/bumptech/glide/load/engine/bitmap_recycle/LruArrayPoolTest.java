package com.bumptech.glide.load.engine.bitmap_recycle;


import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static LruArrayPool.MAX_OVER_SIZE_MULTIPLE;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class LruArrayPoolTest {
    private static final int MAX_SIZE = 10;

    private static final int MAX_PUT_SIZE = (LruArrayPoolTest.MAX_SIZE) / 2;

    private static final Class<byte[]> ARRAY_CLASS = byte[].class;

    private static final ArrayAdapterInterface<byte[]> ADAPTER = new ByteArrayAdapter();

    private LruArrayPool pool;

    @Test
    public void testNewPoolIsEmpty() {
        Assert.assertEquals(pool.getCurrentSize(), 0);
    }

    @Test
    public void testICanAddAndGetValidArray() {
        int size = 758;
        int value = 564;
        fillPool(pool, (size - 1), value);
        pool.put(LruArrayPoolTest.createArray(LruArrayPoolTest.ARRAY_CLASS, size, value));
        Object array = pool.get(size, LruArrayPoolTest.ARRAY_CLASS);
        Assert.assertNotNull(array);
        Assert.assertTrue(((array.getClass()) == (LruArrayPoolTest.ARRAY_CLASS)));
        Assert.assertTrue(((LruArrayPoolTest.ADAPTER.getArrayLength(((byte[]) (array)))) >= size));
        Assert.assertTrue(((((byte[]) (array))[0]) == ((byte) (0))));
    }

    @Test
    public void testItIsSizeLimited() {
        fillPool(pool, (((LruArrayPoolTest.MAX_SIZE) / (LruArrayPoolTest.ADAPTER.getElementSizeInBytes())) + 1), 1);
        Assert.assertTrue(((pool.getCurrentSize()) <= (LruArrayPoolTest.MAX_SIZE)));
    }

    @Test
    public void testArrayLargerThanPoolIsNotAdded() {
        pool = new LruArrayPool(LruArrayPoolTest.MAX_SIZE);
        pool.put(LruArrayPoolTest.createArray(LruArrayPoolTest.ARRAY_CLASS, (((LruArrayPoolTest.MAX_SIZE) / (LruArrayPoolTest.ADAPTER.getElementSizeInBytes())) + 1), 0));
        Assert.assertEquals(0, pool.getCurrentSize());
    }

    @Test
    public void testClearMemoryRemovesAllArrays() {
        fillPool(pool, (((LruArrayPoolTest.MAX_SIZE) / (LruArrayPoolTest.ADAPTER.getElementSizeInBytes())) + 1), 0);
        pool.clearMemory();
        Assert.assertEquals(0, pool.getCurrentSize());
    }

    @Test
    public void testTrimMemoryUiHiddenOrLessRemovesHalfOfArrays() {
        testTrimMemory(LruArrayPoolTest.MAX_SIZE, TRIM_MEMORY_UI_HIDDEN, ((LruArrayPoolTest.MAX_SIZE) / 2));
    }

    @Test
    public void testTrimMemoryRunningCriticalRemovesHalfOfBitmaps() {
        testTrimMemory(LruArrayPoolTest.MAX_SIZE, TRIM_MEMORY_RUNNING_CRITICAL, ((LruArrayPoolTest.MAX_SIZE) / 2));
    }

    @Test
    public void testTrimMemoryUiHiddenOrLessRemovesNoArraysIfPoolLessThanHalfFull() {
        testTrimMemory(((LruArrayPoolTest.MAX_SIZE) / 2), TRIM_MEMORY_UI_HIDDEN, ((LruArrayPoolTest.MAX_SIZE) / 2));
    }

    @Test
    public void testTrimMemoryBackgroundOrGreaterRemovesAllArrays() {
        for (int trimLevel : new int[]{ TRIM_MEMORY_BACKGROUND, TRIM_MEMORY_COMPLETE }) {
            testTrimMemory(LruArrayPoolTest.MAX_SIZE, trimLevel, 0);
        }
    }

    @Test
    public void get_withEmptyPool_returnsExactArray() {
        assertThat(pool.get(LruArrayPoolTest.MAX_PUT_SIZE, byte[].class)).hasLength(LruArrayPoolTest.MAX_PUT_SIZE);
    }

    @Test
    public void get_withPoolContainingLargerArray_returnsLargerArray() {
        byte[] expected = new byte[LruArrayPoolTest.MAX_PUT_SIZE];
        pool.put(expected);
        assertThat(pool.get(((LruArrayPoolTest.MAX_PUT_SIZE) - 1), byte[].class)).isSameAs(expected);
    }

    @Test
    public void get_withPoolContainingSmallerArray_returnsExactArray() {
        pool.put(new byte[(LruArrayPoolTest.MAX_PUT_SIZE) - 1]);
        assertThat(pool.get(LruArrayPoolTest.MAX_PUT_SIZE, byte[].class)).hasLength(LruArrayPoolTest.MAX_PUT_SIZE);
    }

    @Test
    public void get_withPoolLessThanHalfFull_returnsFromPools() {
        int size = (LruArrayPoolTest.MAX_SIZE) / 2;
        byte[] expected = new byte[size];
        pool.put(expected);
        assertThat(pool.get(1, byte[].class)).isSameAs(expected);
    }

    @Test
    public void get_withPoolMoreThanHalfFull_sizeMoreThanHalfArrayInPool_returnsArray() {
        Set<byte[]> expected = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            byte[] toPut = new byte[(LruArrayPoolTest.MAX_SIZE) / 3];
            expected.add(toPut);
            pool.put(toPut);
        }
        byte[] received = pool.get(2, byte[].class);
        assertThat(expected).contains(received);
    }

    @Test
    public void get_withPoolMoreThanHalfFull_sizeLessThanHalfArrayInPool_returnsNewArray() {
        pool = new LruArrayPool(100);
        for (int i = 0; i < 3; i++) {
            byte[] toPut = new byte[100 / 3];
            pool.put(toPut);
        }
        int requestedSize = (100 / 3) / (MAX_OVER_SIZE_MULTIPLE);
        byte[] received = pool.get(requestedSize, byte[].class);
        assertThat(received).hasLength(requestedSize);
    }

    @Test
    public void getExact_withEmptyPool_returnsExactArray() {
        byte[] result = pool.getExact(LruArrayPoolTest.MAX_PUT_SIZE, byte[].class);
        assertThat(result).hasLength(LruArrayPoolTest.MAX_PUT_SIZE);
    }

    @Test
    public void getExact_withPoolContainingLargerArray_returnsExactArray() {
        pool.put(new byte[LruArrayPoolTest.MAX_PUT_SIZE]);
        int expectedSize = (LruArrayPoolTest.MAX_PUT_SIZE) - 1;
        assertThat(pool.getExact(expectedSize, byte[].class)).hasLength(expectedSize);
    }

    @Test
    public void getExact_withPoolContainingSmallerArray_returnsExactArray() {
        pool.put(new byte[(LruArrayPoolTest.MAX_PUT_SIZE) - 1]);
        assertThat(pool.getExact(LruArrayPoolTest.MAX_PUT_SIZE, byte[].class)).hasLength(LruArrayPoolTest.MAX_PUT_SIZE);
    }

    @Test
    public void getExact_withPoolContainingExactArray_returnsArray() {
        byte[] expected = new byte[LruArrayPoolTest.MAX_PUT_SIZE];
        pool.put(expected);
        assertThat(pool.getExact(LruArrayPoolTest.MAX_PUT_SIZE, byte[].class)).isSameAs(expected);
    }

    @Test
    public void put_withArrayMoreThanHalfPoolSize_doesNotRetainArray() {
        int targetSize = ((LruArrayPoolTest.MAX_SIZE) / 2) + 1;
        byte[] toPut = new byte[targetSize];
        pool.put(toPut);
        assertThat(pool.getCurrentSize()).isEqualTo(0);
        assertThat(pool.get(targetSize, byte[].class)).isNotSameAs(toPut);
    }
}

