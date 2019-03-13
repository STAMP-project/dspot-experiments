package org.sqlite;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests User Defined Functions.
 */
public class UDFTest {
    private static int val = 0;

    private static byte[] b1 = new byte[]{ 2, 5, -4, 8, -1, 3, -5 };

    private static int gotTrigger = 0;

    private Connection conn;

    private Statement stat;

    @Test
    public void calling() throws SQLException {
        Function.create(conn, "f1", new Function() {
            @Override
            public void xFunc() throws SQLException {
                UDFTest.val = 4;
            }
        });
        stat.executeQuery("select f1();").close();
        Assert.assertEquals(UDFTest.val, 4);
    }

    @Test
    public void returning() throws SQLException {
        Function.create(conn, "f2", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(4);
            }
        });
        ResultSet rs = stat.executeQuery("select f2();");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 4);
        rs.close();
        for (int i = 0; i < 20; i++) {
            rs = stat.executeQuery((("select (f2() + " + i) + ");"));
            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getInt(1), (4 + i));
            rs.close();
        }
    }

    @Test
    public void accessArgs() throws SQLException {
        Function.create(conn, "f3", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(value_int(0));
            }
        });
        for (int i = 0; i < 15; i++) {
            ResultSet rs = stat.executeQuery((("select f3(" + i) + ");"));
            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getInt(1), i);
            rs.close();
        }
    }

    @Test
    public void multipleArgs() throws SQLException {
        Function.create(conn, "f4", new Function() {
            @Override
            public void xFunc() throws SQLException {
                int ret = 0;
                for (int i = 0; i < (args()); i++)
                    ret += value_int(i);

                result(ret);
            }
        });
        ResultSet rs = stat.executeQuery("select f4(2, 3, 9, -5);");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 9);
        rs.close();
        rs = stat.executeQuery("select f4(2);");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 2);
        rs.close();
        rs = stat.executeQuery("select f4(-3, -4, -5);");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), (-12));
    }

    @Test
    public void returnTypes() throws SQLException {
        Function.create(conn, "f5", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result("Hello World");
            }
        });
        ResultSet rs = stat.executeQuery("select f5();");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString(1), "Hello World");
        Function.create(conn, "f6", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(Long.MAX_VALUE);
            }
        });
        rs.close();
        rs = stat.executeQuery("select f6();");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getLong(1), Long.MAX_VALUE);
        Function.create(conn, "f7", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(Double.MAX_VALUE);
            }
        });
        rs.close();
        rs = stat.executeQuery("select f7();");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getDouble(1), Double.MAX_VALUE, 1.0E-4);
        Function.create(conn, "f8", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(UDFTest.b1);
            }
        });
        rs.close();
        rs = stat.executeQuery("select f8();");
        Assert.assertTrue(rs.next());
        assertArrayEq(rs.getBytes(1), UDFTest.b1);
    }

    @Test
    public void returnArgInt() throws SQLException {
        Function.create(conn, "farg_int", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(value_int(0));
            }
        });
        PreparedStatement prep = conn.prepareStatement("select farg_int(?);");
        prep.setInt(1, Integer.MAX_VALUE);
        ResultSet rs = prep.executeQuery();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), Integer.MAX_VALUE);
        prep.close();
    }

    @Test
    public void returnArgLong() throws SQLException {
        Function.create(conn, "farg_long", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(value_long(0));
            }
        });
        PreparedStatement prep = conn.prepareStatement("select farg_long(?);");
        prep.setLong(1, Long.MAX_VALUE);
        ResultSet rs = prep.executeQuery();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getLong(1), Long.MAX_VALUE);
        prep.close();
    }

    @Test
    public void returnArgDouble() throws SQLException {
        Function.create(conn, "farg_doub", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(value_double(0));
            }
        });
        PreparedStatement prep = conn.prepareStatement("select farg_doub(?);");
        prep.setDouble(1, Double.MAX_VALUE);
        ResultSet rs = prep.executeQuery();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getDouble(1), Double.MAX_VALUE, 1.0E-4);
        prep.close();
    }

    @Test
    public void returnArgBlob() throws SQLException {
        Function.create(conn, "farg_blob", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(value_blob(0));
            }
        });
        PreparedStatement prep = conn.prepareStatement("select farg_blob(?);");
        prep.setBytes(1, UDFTest.b1);
        ResultSet rs = prep.executeQuery();
        Assert.assertTrue(rs.next());
        assertArrayEq(rs.getBytes(1), UDFTest.b1);
        prep.close();
    }

    @Test
    public void returnArgString() throws SQLException {
        Function.create(conn, "farg_str", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(value_text(0));
            }
        });
        PreparedStatement prep = conn.prepareStatement("select farg_str(?);");
        prep.setString(1, "Hello");
        ResultSet rs = prep.executeQuery();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString(1), "Hello");
        prep.close();
    }

    @Test(expected = SQLException.class)
    public void customErr() throws SQLException {
        Function.create(conn, "f9", new Function() {
            @Override
            public void xFunc() throws SQLException {
                throw new SQLException("myErr");
            }
        });
        stat.executeQuery("select f9();");
    }

    @Test
    public void trigger() throws SQLException {
        Function.create(conn, "inform", new Function() {
            @Override
            protected void xFunc() throws SQLException {
                UDFTest.gotTrigger = value_int(0);
            }
        });
        stat.executeUpdate("create table trigtest (c1);");
        stat.executeUpdate(("create trigger trigt after insert on trigtest" + " begin select inform(new.c1); end;"));
        stat.executeUpdate("insert into trigtest values (5);");
        Assert.assertEquals(UDFTest.gotTrigger, 5);
    }

    @Test
    public void aggregate() throws SQLException {
        Function.create(conn, "mySum", new Function.Aggregate() {
            private int val = 0;

            @Override
            protected void xStep() throws SQLException {
                for (int i = 0; i < (args()); i++)
                    val += value_int(i);

            }

            @Override
            protected void xFinal() throws SQLException {
                result(val);
            }
        });
        stat.executeUpdate("create table t (c1);");
        stat.executeUpdate("insert into t values (5);");
        stat.executeUpdate("insert into t values (3);");
        stat.executeUpdate("insert into t values (8);");
        stat.executeUpdate("insert into t values (2);");
        stat.executeUpdate("insert into t values (7);");
        ResultSet rs = stat.executeQuery("select mySum(c1), sum(c1) from t;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), rs.getInt(2));
    }

    @Test
    public void window() throws SQLException {
        Function.create(conn, "mySum", new Function.Window() {
            private int val = 0;

            @Override
            protected void xStep() throws SQLException {
                for (int i = 0; i < (args()); i++)
                    val += value_int(i);

            }

            @Override
            protected void xInverse() throws SQLException {
                for (int i = 0; i < (args()); i++)
                    val -= value_int(i);

            }

            @Override
            protected void xValue() throws SQLException {
                result(val);
            }

            @Override
            protected void xFinal() throws SQLException {
                result(val);
            }
        });
        stat.executeUpdate("create table t (x);");
        stat.executeUpdate("insert into t values(1);");
        stat.executeUpdate("insert into t values(2);");
        stat.executeUpdate("insert into t values(3);");
        stat.executeUpdate("insert into t values(4);");
        stat.executeUpdate("insert into t values(5);");
        ResultSet rs = stat.executeQuery("select mySum(x) over (order by x rows between 1 preceding and 1 following) from t order by x;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(3, rs.getInt(1));
        Assert.assertTrue(rs.next());
        Assert.assertEquals(6, rs.getInt(1));
        Assert.assertTrue(rs.next());
        Assert.assertEquals(9, rs.getInt(1));
        Assert.assertTrue(rs.next());
        Assert.assertEquals(12, rs.getInt(1));
        Assert.assertTrue(rs.next());
        Assert.assertEquals(9, rs.getInt(1));
    }

    @Test
    public void destroy() throws SQLException {
        Function.create(conn, "f1", new Function() {
            @Override
            public void xFunc() throws SQLException {
                UDFTest.val = 9;
            }
        });
        stat.executeQuery("select f1();").close();
        Assert.assertEquals(UDFTest.val, 9);
        Function.destroy(conn, "f1");
        Function.destroy(conn, "f1");
    }

    @Test
    public void manyfunctions() throws SQLException {
        Function.create(conn, "f1", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(1);
            }
        });
        Function.create(conn, "f2", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(2);
            }
        });
        Function.create(conn, "f3", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(3);
            }
        });
        Function.create(conn, "f4", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(4);
            }
        });
        Function.create(conn, "f5", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(5);
            }
        });
        Function.create(conn, "f6", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(6);
            }
        });
        Function.create(conn, "f7", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(7);
            }
        });
        Function.create(conn, "f8", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(8);
            }
        });
        Function.create(conn, "f9", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(9);
            }
        });
        Function.create(conn, "f10", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(10);
            }
        });
        Function.create(conn, "f11", new Function() {
            @Override
            public void xFunc() throws SQLException {
                result(11);
            }
        });
        ResultSet rs = stat.executeQuery(("select f1() + f2() + f3() + f4() + f5() + f6()" + " + f7() + f8() + f9() + f10() + f11();"));
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), ((((((((((1 + 2) + 3) + 4) + 5) + 6) + 7) + 8) + 9) + 10) + 11));
        rs.close();
    }

    @Test
    public void multipleThreads() throws Exception {
        Function func = new Function() {
            int sum = 0;

            @Override
            protected void xFunc() {
                try {
                    sum += value_int(1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return String.valueOf(sum);
            }
        };
        Function.create(conn, "func", func);
        stat.executeUpdate("create table foo (col integer);");
        stat.executeUpdate(("create trigger foo_trigger after insert on foo begin" + " select func(new.rowid, new.col); end;"));
        int times = 1000;
        List<Thread> threads = new LinkedList<Thread>();
        for (int tn = 0; tn < times; tn++) {
            threads.add(new Thread(("func thread " + tn)) {
                @Override
                public void run() {
                    try {
                        Statement s = conn.createStatement();
                        s.executeUpdate("insert into foo values (1);");
                        s.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        for (Thread thread : threads)
            thread.start();

        for (Thread thread : threads)
            thread.join();

        // check that all of the threads successfully executed
        ResultSet rs = stat.executeQuery("select sum(col) from foo;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), times);
        rs.close();
        // check that custom function was executed each time
        Assert.assertEquals(Integer.parseInt(func.toString()), times);
    }
}

