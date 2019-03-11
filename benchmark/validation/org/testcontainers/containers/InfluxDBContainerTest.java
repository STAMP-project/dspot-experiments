package org.testcontainers.containers;


import org.hamcrest.CoreMatchers;
import org.influxdb.InfluxDB;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


public class InfluxDBContainerTest {
    @ClassRule
    public static InfluxDBContainer influxDBContainer = new InfluxDBContainer();

    @Test
    public void getUrl() {
        String actual = InfluxDBContainerTest.influxDBContainer.getUrl();
        Assert.assertThat(actual, CoreMatchers.notNullValue());
    }

    @Test
    public void getNewInfluxDB() {
        InfluxDB actual = InfluxDBContainerTest.influxDBContainer.getNewInfluxDB();
        Assert.assertThat(actual, CoreMatchers.notNullValue());
        Assert.assertThat(actual.ping(), CoreMatchers.notNullValue());
    }

    @Test
    public void getLivenessCheckPort() {
        Integer actual = InfluxDBContainerTest.influxDBContainer.getLivenessCheckPort();
        Assert.assertThat(actual, CoreMatchers.notNullValue());
    }

    @Test
    public void isRunning() {
        boolean actual = InfluxDBContainerTest.influxDBContainer.isRunning();
        Assert.assertThat(actual, CoreMatchers.is(true));
    }
}

