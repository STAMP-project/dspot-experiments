package org.kairosdb.core.health;


import HealthCheck.Result;
import Response.Status.INTERNAL_SERVER_ERROR;
import Response.Status.NO_CONTENT;
import Response.Status.OK;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.datastore.DatastoreQuery;
import org.kairosdb.core.datastore.KairosDatastore;
import org.kairosdb.core.exception.DatastoreException;
import org.mockito.Mockito;


public class HealthCheckResourceTest {
    private HealthCheckResource resourceService;

    private KairosDatastore datastore;

    private DatastoreQuery query;

    @Test(expected = NullPointerException.class)
    public void testConstructorNullHealthCheckServiceInvalid() {
        new HealthCheckResource(null);
    }

    @Test
    public void testCheckAllHealthy() {
        Response response = resourceService.check();
        Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(NO_CONTENT.getStatusCode()));
    }

    @Test
    public void testCheckUnHealthy() throws DatastoreException {
        Mockito.when(query.execute()).thenThrow(new DatastoreException("Error"));
        Response response = resourceService.check();
        Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(INTERNAL_SERVER_ERROR.getStatusCode()));
    }

    @Test
    public void testStatusAllHealthy() {
        Response response = resourceService.status();
        Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(OK.getStatusCode()));
    }

    @Test
    public void testStatusUnHealthy() throws DatastoreException {
        Mockito.when(datastore.getMetricNames(null)).thenThrow(new DatastoreException("Error"));
        Response response = resourceService.status();
        Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(OK.getStatusCode()));
    }

    private class TestHealthCheckService implements HealthCheckService {
        @Override
        public List<HealthStatus> getChecks() {
            List<HealthStatus> list = new ArrayList<HealthStatus>();
            list.add(new HealthCheckResourceTest.TestHealthStatus());
            list.add(new DatastoreQueryHealthCheck(datastore));
            return list;
        }
    }

    private class TestHealthStatus implements HealthStatus {
        @Override
        public String getName() {
            return getClass().getSimpleName();
        }

        @Override
        public Result execute() {
            return Result.healthy();
        }
    }
}

