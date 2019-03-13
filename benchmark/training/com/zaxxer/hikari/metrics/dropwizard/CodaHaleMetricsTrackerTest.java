package com.zaxxer.hikari.metrics.dropwizard;


import com.codahale.metrics.MetricRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CodaHaleMetricsTrackerTest {
    @Mock
    public MetricRegistry mockMetricRegistry;

    private CodaHaleMetricsTracker testee;

    @Test
    public void close() throws Exception {
        testee.close();
        Mockito.verify(mockMetricRegistry).remove("mypool.pool.Wait");
        Mockito.verify(mockMetricRegistry).remove("mypool.pool.Usage");
        Mockito.verify(mockMetricRegistry).remove("mypool.pool.ConnectionCreation");
        Mockito.verify(mockMetricRegistry).remove("mypool.pool.ConnectionTimeoutRate");
        Mockito.verify(mockMetricRegistry).remove("mypool.pool.TotalConnections");
        Mockito.verify(mockMetricRegistry).remove("mypool.pool.IdleConnections");
        Mockito.verify(mockMetricRegistry).remove("mypool.pool.ActiveConnections");
        Mockito.verify(mockMetricRegistry).remove("mypool.pool.PendingConnections");
        Mockito.verify(mockMetricRegistry).remove("mypool.pool.MaxConnections");
        Mockito.verify(mockMetricRegistry).remove("mypool.pool.MinConnections");
    }
}

