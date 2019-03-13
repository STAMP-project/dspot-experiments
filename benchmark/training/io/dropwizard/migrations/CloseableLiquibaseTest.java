package io.dropwizard.migrations;


import io.dropwizard.db.ManagedPooledDataSource;
import net.jcip.annotations.NotThreadSafe;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.junit.jupiter.api.Test;


@NotThreadSafe
public class CloseableLiquibaseTest {
    CloseableLiquibase liquibase;

    ManagedPooledDataSource dataSource;

    @Test
    public void testWhenClosingAllConnectionsInPoolIsReleased() throws Exception {
        ConnectionPool pool = dataSource.getPool();
        assertThat(pool.getActive()).isEqualTo(1);
        liquibase.close();
        assertThat(pool.getActive()).isZero();
        assertThat(pool.getIdle()).isZero();
        assertThat(pool.isClosed()).isTrue();
    }
}

