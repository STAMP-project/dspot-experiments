package com.querydsl.example.sql.guice;


import com.google.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(GuiceTestRunner.class)
public class ConnectionContextTest {
    @Inject
    private ConnectionContext context;

    @Test
    public void get_connection() {
        Assert.assertNotNull(context.getConnection(true));
        Assert.assertNotNull(context.getConnection());
        context.removeConnection();
        Assert.assertNull(context.getConnection());
    }
}

