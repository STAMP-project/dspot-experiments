/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import junit.framework.TestCase;


public class OnlineSQLTest extends TestCase {
    private String url = "jdbc:mysql://a.b.c.d/dragoon_v25monitordb_online";

    private String user = "dragoon";

    private String password = "dragoon";

    public void test_list_sql() throws Exception {
        // reset();
        // ??????
        // update(7216, "", 4);
        // update(7223, "", 4);
        // update(8387, "", 4);
        // ????
        // update(17018, "", 4); //alarm_type&?
        // update(17841, "", 4); //alarm_type&?
        // update(17845, "", 4); //alarm_type&?
        // update(18247, "", 4); //alarm_type&?
        // update(19469, "", 4); //alarm_type&?
        update(19730, "", 4);// alarm_type&?

        update(20164, "", 4);// alarm_type&?

        update(20386, "", 4);// alarm_type&?

        update(20440, "", 4);// alarm_type&?

        update(21208, "", 4);// alarm_type&?

        // IBATIS NAME
        update(18035, "", 4);// alarm_type&?

        Connection conn = DriverManager.getConnection(url, user, password);
        int count = 0;
        String sql = "SELECT id, value FROM m_sql_const WHERE flag IS NULL LIMIT 100";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt(1);
            String value = rs.getString(2);
            if ((value.indexOf('?')) != (-1)) {
                update(id, "", 4);
                continue;
            }
            System.out.println(value);
            System.out.println();
            try {
                validateOracle(id, value);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(("id : " + id));
                continue;
            }
            count++;
        } 
        rs.close();
        stmt.close();
        System.out.println(("COUNT : " + count));
        conn.close();
    }
}

