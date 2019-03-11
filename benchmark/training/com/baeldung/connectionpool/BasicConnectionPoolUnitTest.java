package com.baeldung.connectionpool;


import java.sql.Connection;
import org.junit.Assert;
import org.junit.Test;


public class BasicConnectionPoolUnitTest {
    private static ConnectionPool connectionPool;

    @Test
    public void givenBasicConnectionPoolInstance_whenCalledgetConnection_thenCorrect() throws Exception {
        Assert.assertTrue(BasicConnectionPoolUnitTest.connectionPool.getConnection().isValid(1));
    }

    @Test
    public void givenBasicConnectionPoolInstance_whenCalledreleaseConnection_thenCorrect() throws Exception {
        Connection connection = BasicConnectionPoolUnitTest.connectionPool.getConnection();
        assertThat(BasicConnectionPoolUnitTest.connectionPool.releaseConnection(connection)).isTrue();
    }

    @Test
    public void givenBasicConnectionPoolInstance_whenCalledgetUrl_thenCorrect() {
        assertThat(BasicConnectionPoolUnitTest.connectionPool.getUrl()).isEqualTo("jdbc:h2:mem:test");
    }

    @Test
    public void givenBasicConnectionPoolInstance_whenCalledgetUser_thenCorrect() {
        assertThat(BasicConnectionPoolUnitTest.connectionPool.getUser()).isEqualTo("user");
    }

    @Test
    public void givenBasicConnectionPoolInstance_whenCalledgetPassword_thenCorrect() {
        assertThat(BasicConnectionPoolUnitTest.connectionPool.getPassword()).isEqualTo("password");
    }

    @Test(expected = RuntimeException.class)
    public void givenBasicConnectionPoolInstance_whenAskedForMoreThanMax_thenError() throws Exception {
        // this test needs to be independent so it doesn't share the same connection pool as other tests
        ConnectionPool cp = BasicConnectionPool.create("jdbc:h2:mem:test", "user", "password");
        final int MAX_POOL_SIZE = 20;
        for (int i = 0; i < (MAX_POOL_SIZE + 1); i++) {
            cp.getConnection();
        }
        Assert.fail();
    }

    @Test
    public void givenBasicConnectionPoolInstance_whenSutdown_thenEmpty() throws Exception {
        ConnectionPool cp = BasicConnectionPool.create("jdbc:h2:mem:test", "user", "password");
        assertThat(getSize()).isEqualTo(10);
        shutdown();
        assertThat(getSize()).isEqualTo(0);
    }
}

