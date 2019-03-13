package org.sqlite;


import SQLiteErrorCode.SQLITE_BUSY.code;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;


public class BusyHandlerTest {
    private Connection conn;

    private Statement stat;

    public class BusyWork extends Thread {
        private final Connection conn;

        private final Statement stat;

        private final CountDownLatch lockedLatch = new CountDownLatch(1);

        private final CountDownLatch completeLatch = new CountDownLatch(1);

        public BusyWork() throws Exception {
            conn = DriverManager.getConnection("jdbc:sqlite:target/test.db");
            Function.create(conn, "wait_for_latch", new Function() {
                @Override
                protected void xFunc() throws SQLException {
                    lockedLatch.countDown();
                    try {
                        completeLatch.await();
                    } catch (InterruptedException e) {
                        throw new SQLException("Interrupted");
                    }
                    result(100);
                }
            });
            stat = conn.createStatement();
            stat.setQueryTimeout(1);
        }

        @Override
        public void run() {
            try {
                // Generate some work for the sqlite vm
                stat.executeUpdate("drop table if exists foo;");
                stat.executeUpdate("create table foo (id integer);");
                stat.execute("insert into foo (id) values (wait_for_latch());");
            } catch (SQLException ex) {
                System.out.println(("HERE" + (ex.toString())));
            }
        }
    }

    @Test
    public void basicBusyHandler() throws Exception {
        final int[] calls = new int[]{ 0 };
        BusyHandler.setHandler(conn, new BusyHandler() {
            @Override
            protected int callback(int nbPrevInvok) throws SQLException {
                Assert.assertEquals(nbPrevInvok, calls[0]);
                (calls[0])++;
                if (nbPrevInvok <= 1) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        BusyHandlerTest.BusyWork busyWork = new BusyHandlerTest.BusyWork();
        busyWork.start();
        // let busyWork block inside insert
        busyWork.lockedLatch.await();
        try {
            workWork();
            Assert.fail("Should throw SQLITE_BUSY exception");
        } catch (SQLException ex) {
            Assert.assertEquals(code, ex.getErrorCode());
        }
        busyWork.completeLatch.countDown();
        busyWork.join();
        Assert.assertEquals(3, calls[0]);
    }

    @Test
    public void testUnregister() throws Exception {
        final int[] calls = new int[]{ 0 };
        BusyHandler.setHandler(conn, new BusyHandler() {
            @Override
            protected int callback(int nbPrevInvok) throws SQLException {
                Assert.assertEquals(nbPrevInvok, calls[0]);
                (calls[0])++;
                if (nbPrevInvok <= 1) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        BusyHandlerTest.BusyWork busyWork = new BusyHandlerTest.BusyWork();
        busyWork.start();
        // let busyWork block inside insert
        busyWork.lockedLatch.await();
        try {
            workWork();
            Assert.fail("Should throw SQLITE_BUSY exception");
        } catch (SQLException ex) {
            Assert.assertEquals(code, ex.getErrorCode());
        }
        busyWork.completeLatch.countDown();
        busyWork.join();
        Assert.assertEquals(3, calls[0]);
        int totalCalls = calls[0];
        BusyHandler.clearHandler(conn);
        busyWork = new BusyHandlerTest.BusyWork();
        busyWork.start();
        // let busyWork block inside insert
        busyWork.lockedLatch.await();
        try {
            workWork();
            Assert.fail("Should throw SQLITE_BUSY exception");
        } catch (SQLException ex) {
            Assert.assertEquals(code, ex.getErrorCode());
        }
        busyWork.completeLatch.countDown();
        busyWork.join();
        Assert.assertEquals(totalCalls, calls[0]);
    }
}

