package com.squareup.picasso3;


import MemoryPolicy.NO_CACHE;
import MemoryPolicy.NO_STORE;
import org.junit.Test;


public class MemoryPolicyTest {
    @Test
    public void dontReadFromMemoryCache() {
        int memoryPolicy = 0;
        memoryPolicy |= NO_CACHE.index;
        assertThat(MemoryPolicy.shouldReadFromMemoryCache(memoryPolicy)).isFalse();
    }

    @Test
    public void readFromMemoryCache() {
        int memoryPolicy = 0;
        memoryPolicy |= NO_STORE.index;
        assertThat(MemoryPolicy.shouldReadFromMemoryCache(memoryPolicy)).isTrue();
    }

    @Test
    public void dontWriteToMemoryCache() {
        int memoryPolicy = 0;
        memoryPolicy |= NO_STORE.index;
        assertThat(MemoryPolicy.shouldWriteToMemoryCache(memoryPolicy)).isFalse();
    }

    @Test
    public void writeToMemoryCache() {
        int memoryPolicy = 0;
        memoryPolicy |= NO_CACHE.index;
        assertThat(MemoryPolicy.shouldWriteToMemoryCache(memoryPolicy)).isTrue();
    }
}

