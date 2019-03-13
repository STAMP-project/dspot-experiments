package org.testcontainers.jdbc;


import com.googlecode.junittoolbox.ParallelParameterized;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(ParallelParameterized.class)
public class JDBCDriverWithPoolTest {
    public static final String URL = "jdbc:tc:mysql:5://hostname/databasename?TC_INITFUNCTION=org.testcontainers.jdbc.JDBCDriverWithPoolTest::sampleInitFunction";

    private final DataSource dataSource;

    public JDBCDriverWithPoolTest(Supplier<DataSource> dataSourceSupplier) {
        this.dataSource = dataSourceSupplier.get();
    }

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Test
    public void testMySQLWithConnectionPoolUsingSameContainer() throws InterruptedException, SQLException {
        // Populate the database with some data in multiple threads, so that multiple connections from the pool will be used
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    new QueryRunner(dataSource).insert("INSERT INTO my_counter (n) VALUES (5)", ((ResultSetHandler<Object>) (( rs) -> true)));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
        // Complete population of the database
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);
        // compare to expected results
        int count = new QueryRunner(dataSource).query("SELECT COUNT(1) FROM my_counter", ( rs) -> {
            rs.next();
            return rs.getInt(1);
        });
        assertEquals("Reuse of a datasource points to the same DB container", 100, count);
        int sum = new QueryRunner(dataSource).query("SELECT SUM(n) FROM my_counter", ( rs) -> {
            rs.next();
            return rs.getInt(1);
        });
        // 100 records * 5 = 500 expected
        assertEquals("Reuse of a datasource points to the same DB container", 500, sum);
    }
}

