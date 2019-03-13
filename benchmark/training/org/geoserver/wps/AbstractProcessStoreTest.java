/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps;


import Filter.INCLUDE;
import Query.ALL;
import java.util.List;
import org.geoserver.wps.executor.ExecutionStatus;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.NameImpl;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;


/**
 * Base class for status store tests
 *
 * @author Andrea Aime - GeoSolutions
 */
public abstract class AbstractProcessStoreTest {
    static final FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2();

    protected ProcessStatusStore store;

    protected ExecutionStatus s1;

    protected ExecutionStatus s2;

    protected ExecutionStatus s3;

    protected ExecutionStatus s4;

    @Test
    public void testFilter() throws CQLException {
        // simple filters
        checkFiltered(store, query("processName = 'test1'"), s1);
        checkFiltered(store, query("phase = 'RUNNING'"), s3, s4);
        checkFiltered(store, query("progress > 30"), s3, s4);
        // force a post filter
        checkFiltered(store, query("strToLowerCase(phase) = 'running'"), s3, s4);
        checkFiltered(store, query("strToLowerCase(phase) = 'running' AND progress > 30"), s3, s4);
    }

    @Test
    public void testPaging() throws CQLException {
        // simple filters with some paging, sometimes odd
        checkFiltered(store, query("processName = 'test1'", 0, 1), s1);
        checkFiltered(store, query("processName = 'test1'", 1, 1));
        checkFiltered(store, query("phase = 'RUNNING'", 0, 1, asc("progress")), s3);
        checkFiltered(store, query("phase = 'RUNNING'", 1, 1, asc("progress")), s4);
        checkFiltered(store, query("phase = 'RUNNING'", 0, 1, desc("progress")), s4);
        checkFiltered(store, query("phase = 'RUNNING'", 1, 1, desc("progress")), s3);
        // force a post filter
        String lowercaseRunning = "strToLowerCase(phase) = 'running'";
        checkFiltered(store, query(lowercaseRunning), s3, s4);
        checkFiltered(store, query(lowercaseRunning, 0, 1, asc("progress")), s3);
        checkFiltered(store, query(lowercaseRunning, 1, 1, asc("progress")), s4);
        checkFiltered(store, query(lowercaseRunning, 0, 1, desc("progress")), s4);
        checkFiltered(store, query(lowercaseRunning, 1, 1, desc("progress")), s3);
        // force a mix of pre and post filter
        String lowercaseRunningProgress = "strToLowerCase(phase) = 'running' AND progress > 30";
        checkFiltered(store, query(lowercaseRunningProgress), s3, s4);
        checkFiltered(store, query(lowercaseRunningProgress), s3, s4);
        checkFiltered(store, query(lowercaseRunningProgress, 0, 1, asc("progress")), s3);
        checkFiltered(store, query(lowercaseRunningProgress, 1, 1, asc("progress")), s4);
        checkFiltered(store, query(lowercaseRunningProgress, 0, 1, desc("progress")), s4);
        checkFiltered(store, query(lowercaseRunningProgress, 1, 1, desc("progress")), s3);
    }

    @Test
    public void testDelete() throws CQLException {
        Assert.assertEquals(1, store.remove(CQL.toFilter("processName = 'test1'")));
        checkContains(store.list(ALL), s2, s3, s4);
        Assert.assertEquals(2, store.remove(CQL.toFilter("progress > 30")));
        checkContains(store.list(ALL), s2);
        Assert.assertEquals(1, store.remove(CQL.toFilter("phase = 'FAILED'")));
        checkContains(store.list(ALL));
    }

    @Test
    public void testIsolated() {
        store.remove(INCLUDE);
        ExecutionStatus status = new ExecutionStatus(new NameImpl("test"), "abcde", false);
        store.save(status);
        List<ExecutionStatus> statuses = store.list(ALL);
        Assert.assertEquals(1, statuses.size());
        Assert.assertEquals("incorrect status", status, statuses.get(0));
        Assert.assertNotSame(status, statuses.get(0));
    }
}

