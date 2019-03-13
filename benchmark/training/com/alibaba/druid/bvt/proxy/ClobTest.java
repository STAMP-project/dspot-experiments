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
package com.alibaba.druid.bvt.proxy;


import com.alibaba.druid.proxy.jdbc.ClobProxy;
import com.alibaba.druid.util.JdbcUtils;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import junit.framework.TestCase;
import org.junit.Assert;


public class ClobTest extends TestCase {
    private static String create_url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=clobTest:jdbc:derby:memory:clobTestDB;create=true";

    public void test_clob() throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(ClobTest.create_url);
            pstmt = conn.prepareStatement("INSERT INTO T_CLOB (ID, DATA) VALUES (?, ?)");
            Clob clob = conn.createClob();
            ClobProxy clobWrapper = ((ClobProxy) (clob));
            Assert.assertNotNull(clobWrapper.getConnectionWrapper());
            clob.setAsciiStream(1);
            clob.setCharacterStream(1);
            clob.setString(1, "ABCAAAAAAAAAAA");
            clob.setString(1, "ABCAAAAAAAAAAA", 1, 5);
            pstmt.setInt(1, 1);
            pstmt.setClob(2, clob);
            int updateCount = pstmt.executeUpdate();
            Assert.assertEquals(1, updateCount);
            pstmt.setInt(1, 1);
            pstmt.setClob(2, new StringReader("XXXXXXX"));
            updateCount = pstmt.executeUpdate();
            Assert.assertEquals(1, updateCount);
            pstmt.setInt(1, 1);
            pstmt.setClob(2, new StringReader("ABCAAAAAAAAAAABCAAAAAAAAAAAAABCAAAAAAAAAAABCAAAAAAAAAAAA"), "ABCAAAAAAAAAAABCAAAAAAAAAAAAABCAAAAAAAAAAABCAAAAAAAAAAAA".length());
            updateCount = pstmt.executeUpdate();
            Assert.assertEquals(1, updateCount);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE, ResultSet.CLOSE_CURSORS_AT_COMMIT);
            // //////
            rs = stmt.executeQuery("SELECT ID, DATA FROM T_CLOB");
            rs.getStatement();// just call

            while (rs.next()) {
                Clob readClob = rs.getClob(2);
                readClob.length();
                readClob.position("abc", 1);
                readClob.getCharacterStream().close();
                readClob.getAsciiStream().close();
                readClob.getCharacterStream(1, 1).close();
                readClob.getSubString(1, 2);
                readClob.truncate(2);
                readClob.free();
            } 
            JdbcUtils.close(rs);
            rs = stmt.executeQuery("SELECT ID, DATA FROM T_CLOB");
            while (rs.next()) {
                rs.getCharacterStream(2).close();
            } 
            JdbcUtils.close(rs);
            rs = stmt.executeQuery("SELECT ID, DATA FROM T_CLOB");
            while (rs.next()) {
                rs.getCharacterStream("DATA").close();
            } 
            JdbcUtils.close(rs);
            rs = stmt.executeQuery("SELECT ID, DATA FROM T_CLOB");
            while (rs.next()) {
                Clob searchstr = conn.createClob();
                searchstr.setString(1, "AB");
                Clob x = rs.getClob("DATA");
                x.position(searchstr, 1);
                x.free();
            } 
            JdbcUtils.close(rs);
            rs = stmt.executeQuery("SELECT ID, DATA FROM T_CLOB");
            while (rs.next()) {
                rs.getAsciiStream(2).close();
            } 
            JdbcUtils.close(rs);
            rs = stmt.executeQuery("SELECT ID, DATA FROM T_CLOB");
            while (rs.next()) {
                rs.getAsciiStream("DATA").close();
            } 
            JdbcUtils.close(rs);
            rs = stmt.executeQuery("SELECT ID, DATA FROM T_CLOB");
            while (rs.next()) {
                Clob x = conn.createClob();
                x.setString(1, "XXSDDSLF");
                rs.updateClob(2, x);
            } 
            JdbcUtils.close(rs);
            rs = stmt.executeQuery("SELECT ID, DATA FROM T_CLOB");
            while (rs.next()) {
                Clob x = conn.createClob();
                x.setString(1, "XXSDDSLF");
                rs.updateClob("DATA", x);
            } 
            JdbcUtils.close(rs);
            rs = stmt.executeQuery("SELECT ID, DATA FROM T_CLOB");
            while (rs.next()) {
                rs.updateClob(2, new StringReader("XDSFLA"));
            } 
            JdbcUtils.close(rs);
            rs = stmt.executeQuery("SELECT ID, DATA FROM T_CLOB");
            while (rs.next()) {
                rs.updateClob("DATA", new StringReader("XDSFLA"));
            } 
            rs = stmt.executeQuery("SELECT ID, DATA FROM T_CLOB");
            while (rs.next()) {
                rs.updateClob(2, new StringReader("XDSFLA"), "XDSFLA".length());
            } 
            JdbcUtils.close(rs);
            rs = stmt.executeQuery("SELECT ID, DATA FROM T_CLOB");
            while (rs.next()) {
                rs.updateClob("DATA", new StringReader("XDSFLA"), "XDSFLA".length());
            } 
            JdbcUtils.close(rs);
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(pstmt);
            JdbcUtils.close(conn);
        }
    }
}

