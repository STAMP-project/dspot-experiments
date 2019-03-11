package io.requery.test;


import Mode.SingleShotTime;
import io.requery.sql.EntityDataStore;
import io.requery.sql.Platform;
import io.requery.sql.platform.H2;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.junit.Test;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.NoBenchmarksException;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;


/**
 * Performs a query using raw JDBC and a query using the library and compares the execution times.
 * TODO extend to support inserts and more queries
 */
@State(Scope.Thread)
public class BenchmarkTest {
    private Platform platform;

    private DataSource dataSource;

    private EntityDataStore<Object> data;

    public BenchmarkTest() {
        this.platform = new H2();
    }

    @Test
    public void testCompareQuery() throws SQLException, RunnerException {
        Options options = new OptionsBuilder().include(((getClass().getName()) + ".*")).mode(SingleShotTime).timeUnit(TimeUnit.MILLISECONDS).warmupTime(TimeValue.seconds(5)).warmupIterations(2).measurementTime(TimeValue.seconds(10)).measurementIterations(5).threads(1).forks(2).build();
        try {
            run();
        } catch (NoBenchmarksException ignored) {
            // expected? only happens from gradle
        }
    }
}

