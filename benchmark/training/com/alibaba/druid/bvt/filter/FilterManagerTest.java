package com.alibaba.druid.bvt.filter;


import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterManager;
import java.sql.SQLException;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.junit.Assert;


public class FilterManagerTest extends TestCase {
    static {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(null);
            Assert.assertNotNull(FilterManager.getFilter("stat"));
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }

    public void test_instance() throws Exception {
        new FilterManager();
    }

    public void test_loadFilter() throws Exception {
        Exception error = null;
        try {
            FilterManager.loadFilter(new ArrayList<com.alibaba.druid.filter.Filter>(), FilterManagerTest.ErrorFilter.class.getName());
        } catch (SQLException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_loadFilter_2() throws Exception {
        Exception error = null;
        try {
            FilterManager.loadFilter(new ArrayList<com.alibaba.druid.filter.Filter>(), FilterManagerTest.ErrorFilter.class.getName());
        } catch (SQLException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public static class ErrorFilter extends FilterAdapter {
        public ErrorFilter() {
            throw new RuntimeException();
        }
    }
}

