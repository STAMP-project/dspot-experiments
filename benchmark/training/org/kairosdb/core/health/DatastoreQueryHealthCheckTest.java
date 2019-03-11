package org.kairosdb.core.health;


import DatastoreQueryHealthCheck.NAME;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.datastore.DatastoreQuery;
import org.kairosdb.core.datastore.KairosDatastore;
import org.kairosdb.core.exception.DatastoreException;
import org.mockito.Mockito;


public class DatastoreQueryHealthCheckTest {
    private KairosDatastore datastore;

    private DatastoreQuery query;

    private DatastoreQueryHealthCheck healthCheck;

    @Test(expected = NullPointerException.class)
    public void testConstructorNullDatastoreInvalid() {
        new DatastoreQueryHealthCheck(null);
    }

    @Test
    public void testCheckHealthy() throws Exception {
        Result result = healthCheck.check();
        Assert.assertTrue(result.isHealthy());
    }

    @Test
    public void testCheckUnHealthy() throws Exception {
        Exception exception = new DatastoreException("Error message");
        Mockito.when(query.execute()).thenThrow(exception);
        Result result = healthCheck.check();
        Assert.assertFalse(result.isHealthy());
        Assert.assertThat(result.getError(), CoreMatchers.<Throwable>equalTo(exception));
        Assert.assertThat(result.getMessage(), CoreMatchers.equalTo(exception.getMessage()));
    }

    @Test
    public void testGetName() {
        Assert.assertThat(healthCheck.getName(), CoreMatchers.equalTo(NAME));
    }
}

