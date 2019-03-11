package com.zaxxer.hikari.pool;


import com.zaxxer.hikari.HikariDataSource;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link HikariDataSource#isRunning()}.
 */
public class TestIsRunning {
    @Test
    public void testRunningNormally() {
        try (HikariDataSource ds = new HikariDataSource(basicConfig())) {
            Assert.assertTrue(ds.isRunning());
        }
    }

    @Test
    public void testNoPool() {
        try (HikariDataSource ds = TestElf.newHikariDataSource()) {
            Assert.assertNull("Pool should not be initialized.", TestElf.getPool(ds));
            Assert.assertFalse(ds.isRunning());
        }
    }

    @Test
    public void testSuspendAndResume() {
        try (HikariDataSource ds = new HikariDataSource(basicConfig())) {
            ds.getHikariPoolMXBean().suspendPool();
            Assert.assertFalse(ds.isRunning());
            ds.getHikariPoolMXBean().resumePool();
            Assert.assertTrue(ds.isRunning());
        }
    }

    @Test
    public void testShutdown() {
        try (HikariDataSource ds = new HikariDataSource(basicConfig())) {
            ds.close();
            Assert.assertFalse(ds.isRunning());
        }
    }
}

