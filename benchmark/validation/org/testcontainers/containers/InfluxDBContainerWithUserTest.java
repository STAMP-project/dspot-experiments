package org.testcontainers.containers;


import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class InfluxDBContainerWithUserTest {
    private static final String TEST_VERSION = "1.4.3";

    private static final String DATABASE = "test";

    private static final String USER = "test-user";

    private static final String PASSWORD = "test-password";

    @Rule
    public InfluxDBContainer influxDBContainer = new InfluxDBContainer(InfluxDBContainerWithUserTest.TEST_VERSION).withDatabase(InfluxDBContainerWithUserTest.DATABASE).withUsername(InfluxDBContainerWithUserTest.USER).withPassword(InfluxDBContainerWithUserTest.PASSWORD);

    @Test
    public void describeDatabases() {
        InfluxDB actual = influxDBContainer.getNewInfluxDB();
        Assert.assertThat(actual, CoreMatchers.notNullValue());
        Assert.assertThat(actual.describeDatabases(), CoreMatchers.hasItem(InfluxDBContainerWithUserTest.DATABASE));
    }

    @Test
    public void checkVersion() {
        InfluxDB actual = influxDBContainer.getNewInfluxDB();
        Assert.assertThat(actual, CoreMatchers.notNullValue());
        Assert.assertThat(actual.ping(), CoreMatchers.notNullValue());
        Assert.assertThat(actual.ping().getVersion(), CoreMatchers.is(InfluxDBContainerWithUserTest.TEST_VERSION));
        Assert.assertThat(actual.version(), CoreMatchers.is(InfluxDBContainerWithUserTest.TEST_VERSION));
    }

    @Test
    public void queryForWriteAndRead() {
        InfluxDB influxDB = influxDBContainer.getNewInfluxDB();
        Point point = Point.measurement("cpu").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("idle", 90L).addField("user", 9L).addField("system", 1L).build();
        influxDB.write(point);
        Query query = new Query("SELECT idle FROM cpu", InfluxDBContainerWithUserTest.DATABASE);
        QueryResult actual = influxDB.query(query);
        Assert.assertThat(actual, CoreMatchers.notNullValue());
        Assert.assertThat(actual.getError(), CoreMatchers.nullValue());
        Assert.assertThat(actual.getResults(), CoreMatchers.notNullValue());
        Assert.assertThat(actual.getResults().size(), CoreMatchers.is(1));
    }
}

