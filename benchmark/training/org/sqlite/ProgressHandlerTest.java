package org.sqlite;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class ProgressHandlerTest {
    private Connection conn;

    private Statement stat;

    @Test
    public void basicProgressHandler() throws Exception {
        final int[] calls = new int[]{ 0 };
        ProgressHandler.setHandler(conn, 1, new ProgressHandler() {
            @Override
            protected int progress() throws SQLException {
                (calls[0])++;
                return 0;
            }
        });
        workWork();
        Assert.assertTrue(((calls[0]) > 0));
    }

    @Test
    public void testUnregister() throws Exception {
        final int[] calls = new int[]{ 0 };
        ProgressHandler.setHandler(conn, 1, new ProgressHandler() {
            @Override
            protected int progress() throws SQLException {
                (calls[0])++;
                return 0;
            }
        });
        workWork();
        Assert.assertTrue(((calls[0]) > 0));
        int totalCalls = calls[0];
        ProgressHandler.clearHandler(conn);
        workWork();
        assertEquals(totalCalls, calls[0]);
    }

    @Test
    public void testInterrupt() throws Exception {
        try {
            ProgressHandler.setHandler(conn, 1, new ProgressHandler() {
                @Override
                protected int progress() throws SQLException {
                    return 1;
                }
            });
            workWork();
        } catch (SQLException ex) {
            // Expected error
            return;
        }
        // Progress function throws, not reached
        Assert.fail();
    }
}

