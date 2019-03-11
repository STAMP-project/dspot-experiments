package liquibase.executor.jvm;


import java.sql.SQLException;
import liquibase.database.Database;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.core.OracleDatabase;
import liquibase.executor.ExecutorService;
import org.junit.Assert;
import org.junit.Test;


public class JdbcExecutorTest {
    @Test
    public void getInstance() {
        final Database oracle1 = new OracleDatabase() {
            @Override
            public boolean equals(Object o) {
                return o == (this);
            }
        };
        final Database oracle2 = new OracleDatabase() {
            @Override
            public boolean equals(Object o) {
                return o == (this);
            }
        };
        final Database mysql = new MySQLDatabase() {
            @Override
            public boolean equals(Object o) {
                return o == (this);
            }
        };
        Assert.assertNotNull(ExecutorService.getInstance().getExecutor(oracle1));
        Assert.assertNotNull(ExecutorService.getInstance().getExecutor(oracle2));
        Assert.assertNotNull(ExecutorService.getInstance().getExecutor(mysql));
        Assert.assertTrue(((ExecutorService.getInstance().getExecutor(oracle1)) == (ExecutorService.getInstance().getExecutor(oracle1))));
        Assert.assertTrue(((ExecutorService.getInstance().getExecutor(oracle2)) == (ExecutorService.getInstance().getExecutor(oracle2))));
        Assert.assertTrue(((ExecutorService.getInstance().getExecutor(mysql)) == (ExecutorService.getInstance().getExecutor(mysql))));
        Assert.assertTrue(((ExecutorService.getInstance().getExecutor(oracle1)) != (ExecutorService.getInstance().getExecutor(oracle2))));
        Assert.assertTrue(((ExecutorService.getInstance().getExecutor(oracle1)) != (ExecutorService.getInstance().getExecutor(mysql))));
    }

    @Test
    public void testGetErrorCode() {
        Assert.assertEquals("", new JdbcExecutor().getErrorCode(new RuntimeException()));
        Assert.assertEquals("(123) ", new JdbcExecutor().getErrorCode(new SQLException("reason", "sqlState", 123)));
        Assert.assertEquals("(0) ", new JdbcExecutor().getErrorCode(new SQLException()));
    }
}

