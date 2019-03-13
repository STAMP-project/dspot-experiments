package com.alibaba.druid;


import com.alibaba.druid.pool.DruidDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;


/* Copyright 1999-2018 Alibaba Group Holding Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
public class TestRollBack {
    static ComboPooledDataSource c3p0;

    static DruidDataSource druid;

    static Dao dao_c3p0;

    static Dao dao_druid;

    // static String url = "jdbc:oracle:thin:@a.b.c.d:1521:ocnauto";
    // static String user = "alibaba";
    // static String password = "ccbuauto";
    // static String driver = "oracle.jdbc.driver.OracleDriver";
    static String url = "jdbc:jtds:sqlserver://a.b.c.d:1433/druid_db";

    static String user = "sa";

    static String password = "hello123";

    static String driver = "net.sourceforge.jtds.jdbc.Driver";

    @Test
    public void test_c3p0() {
        try {
            // ?????????????????,?????????,?????????,????,?????
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    TestRollBack.dao_c3p0.insert("msg", Chain.make("message", "abc"));
                    TestRollBack.dao_c3p0.insert("msg", Chain.make("message", "1234567"));
                }
            });
        } catch (Exception e) {
        }
        // abc??????
        Assert.assertNull(TestRollBack.dao_c3p0.fetch("msg", Cnd.where("message", "=", "abc")));
    }

    @Test
    public void test_druid() {
        try {
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    TestRollBack.dao_druid.insert("msg", Chain.make("message", "abc"));
                    TestRollBack.dao_druid.insert("msg", Chain.make("message", "1234567"));
                }
            });
        } catch (Exception e) {
            // e.printStackTrace(); // ??????????
        }
        // abc????,????
        Assert.assertNotNull(TestRollBack.dao_druid.fetch("msg", Cnd.where("message", "=", "abc")));
    }
}

