/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.java.sql;


import java.io.CharArrayReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import junit.framework.TestCase;
import tests.support.DatabaseCreator;


public class SelectFunctionalityTest extends TestCase {
    private static Connection conn;

    private static Statement statement;

    private static Date date;

    private static Time time;

    /**
     * SelectFunctionalityTest#test_SelectSimple(). Selects all records
     *        from the table
     */
    public void test_SelectSimple() throws SQLException {
        String sql = "SELECT * FROM " + (DatabaseCreator.TEST_TABLE2);
        ResultSet result = SelectFunctionalityTest.statement.executeQuery(sql);
        int counter = 0;
        while (result.next()) {
            int id = result.getInt("finteger");
            TestCase.assertEquals("expected value doesn't equal actual", ((DatabaseCreator.defaultString) + id), result.getString("ftext"));
            TestCase.assertEquals("expected value doesn't equal actual", ((DatabaseCreator.defaultCharacter) + id), result.getString("fcharacter"));
            // TODO getBigDecimal is not supported
            // assertEquals("expected value doesn't equal actual", BigDecimal
            // .valueOf(id + 0.1), result.getBigDecimal("fdecimal"));
            // assertEquals("expected value doesn't equal actual", BigDecimal
            // .valueOf(id + 0.1), result.getBigDecimal("fnumeric"));
            // assertEquals("expected value doesn't equal actual", id, result
            // .getInt("fsmallint"));
            TestCase.assertEquals("expected value doesn't equal actual", BigDecimal.valueOf((id + 0.1)).floatValue(), result.getFloat("ffloat"));
            TestCase.assertEquals("expected value doesn't equal actual", BigDecimal.valueOf((id + 0.1)).doubleValue(), result.getDouble("freal"));
            TestCase.assertEquals("expected value doesn't equal actual", BigDecimal.valueOf((id + 0.1)).doubleValue(), result.getDouble("fdouble"));
            TestCase.assertEquals("expected value doesn't equal actual", SelectFunctionalityTest.date.toString(), result.getDate("fdate").toString());
            TestCase.assertEquals("expected value doesn't equal actual", SelectFunctionalityTest.time.toString(), result.getTime("ftime").toString());
            counter++;
        } 
        TestCase.assertEquals("number of rows in ResultSet is wrong", 5, counter);
        result.close();
    }

    /**
     * SelectFunctionalityTest#test_SelectPrepared(). Selects all records
     *        from the table using parametric query
     */
    public void test_SelectPrepared() throws SQLException {
        String sql = ((((("SELECT finteger, ftext, fcharacter, fdecimal, fnumeric," + (" fsmallint, ffloat, freal, fdouble, fdate, ftime" + " FROM ")) + (DatabaseCreator.TEST_TABLE2)) + " WHERE finteger = ? AND ftext = ? AND fcharacter = ? AND") + " fdecimal = ? AND fnumeric = ? AND fsmallint = ? AND") + " freal = ? AND fdouble = ? AND fdate = ?") + " AND ftime = ?";
        PreparedStatement prepStatement = SelectFunctionalityTest.conn.prepareStatement(sql);
        CharArrayReader reader = new CharArrayReader(new String(((DatabaseCreator.defaultCharacter) + "1")).toCharArray());
        prepStatement.setInt(1, 1);
        prepStatement.setString(2, ((DatabaseCreator.defaultString) + "1"));
        // TODO setCharacterStream and setBigDecimal are not supported
        // prepStatement.setCharacterStream(3, reader, 4);
        // prepStatement.setBigDecimal(4, BigDecimal.valueOf(1.1));
        // prepStatement.setBigDecimal(5, BigDecimal.valueOf(1.1));
        prepStatement.setInt(6, 1);
        prepStatement.setDouble(7, 1.1);
        prepStatement.setDouble(8, 1.1);
        prepStatement.setDate(9, SelectFunctionalityTest.date);
        prepStatement.setTime(10, SelectFunctionalityTest.time);
        int counter = 0;
        ResultSet result = prepStatement.executeQuery();
        while (result.next()) {
            int id = result.getInt("finteger");
            TestCase.assertEquals("expected value doesn't equal actual", ((DatabaseCreator.defaultString) + id), result.getString("ftext"));
            TestCase.assertEquals("expected value doesn't equal actual", ((DatabaseCreator.defaultCharacter) + id), result.getString("fcharacter"));
            // TODO getBigDecimal is not supported
            // assertEquals("expected value doesn't equal actual", BigDecimal
            // .valueOf(1.1), result.getBigDecimal("fdecimal"));
            // assertEquals("expected value doesn't equal actual", BigDecimal
            // .valueOf(1.1), result.getBigDecimal("fnumeric"));
            TestCase.assertEquals("expected value doesn't equal actual", id, result.getInt("fsmallint"));
            TestCase.assertEquals("expected value doesn't equal actual", ((float) (id + 0.1)), result.getFloat("ffloat"));
            TestCase.assertEquals("expected value doesn't equal actual", ((double) (id + 0.1)), result.getDouble("freal"));
            TestCase.assertEquals("expected value doesn't equal actual", ((double) (id + 0.1)), result.getDouble("fdouble"));
            TestCase.assertEquals("expected value doesn't equal actual", SelectFunctionalityTest.date.toString(), result.getDate("fdate").toString());
            TestCase.assertEquals("expected value doesn't equal actual", SelectFunctionalityTest.time.toString(), result.getTime("ftime").toString());
            counter++;
        } 
        // TODO query wasn't executed due to "not supported" methods
        // assertEquals("number of rows in ResultSet is wrong", 1, counter);
        prepStatement.close();
        result.close();
    }

    /**
     * SelectFunctionalityTest#test_SubSelect(). Selects records from the
     *        table using subselect
     */
    public void test_SubSelect() throws SQLException {
        String sql = (((("SELECT finteger," + " (SELECT ftext FROM ") + (DatabaseCreator.TEST_TABLE2)) + " WHERE finteger = 1) as ftext") + " FROM ") + (DatabaseCreator.TEST_TABLE2);
        ResultSet result = SelectFunctionalityTest.statement.executeQuery(sql);
        HashMap<Integer, String> value = new HashMap<Integer, String>();
        value.put(1, ((DatabaseCreator.defaultString) + "1"));
        value.put(2, ((DatabaseCreator.defaultString) + "1"));
        value.put(3, ((DatabaseCreator.defaultString) + "1"));
        value.put(4, ((DatabaseCreator.defaultString) + "1"));
        value.put(5, ((DatabaseCreator.defaultString) + "1"));
        while (result.next()) {
            int key = result.getInt("finteger");
            String val = result.getString("ftext");
            TestCase.assertTrue("wrong value of finteger field", value.containsKey(key));
            TestCase.assertEquals("wrong value of ftext field", value.get(key), val);
            value.remove(key);
        } 
        TestCase.assertTrue("expected rows number doesn't equal actual rows number", value.isEmpty());
        result.close();
    }

    /**
     * SelectFunctionalityTest#test_SelectThreeTables(). Selects records
     *        from a few tables
     */
    public void test_SelectThreeTables() throws SQLException {
        String sql = (((((((((((((((((((((("SELECT onum, " + (DatabaseCreator.ORDERS_TABLE)) + ".cnum") + " FROM ") + (DatabaseCreator.SALESPEOPLE_TABLE)) + ", ") + (DatabaseCreator.CUSTOMERS_TABLE)) + ", ") + (DatabaseCreator.ORDERS_TABLE)) + " WHERE ") + (DatabaseCreator.CUSTOMERS_TABLE)) + ".city <> ") + (DatabaseCreator.SALESPEOPLE_TABLE)) + ".city") + " AND ") + (DatabaseCreator.ORDERS_TABLE)) + ".cnum = ") + (DatabaseCreator.CUSTOMERS_TABLE)) + ".cnum") + " AND ") + (DatabaseCreator.ORDERS_TABLE)) + ".snum = ") + (DatabaseCreator.SALESPEOPLE_TABLE)) + ".snum";
        ResultSet result = SelectFunctionalityTest.statement.executeQuery(sql);
        HashMap<Integer, Integer> value = new HashMap<Integer, Integer>();
        value.put(3001, 2008);
        value.put(3002, 2007);
        value.put(3006, 2008);
        value.put(3009, 2002);
        value.put(3007, 2004);
        value.put(3010, 2004);
        while (result.next()) {
            int key = result.getInt("onum");
            int val = result.getInt("cnum");
            TestCase.assertTrue("wrong value of onum field", value.containsKey(key));
            TestCase.assertEquals("wrong value of cnum field", value.get(key), ((Integer) (val)));
            value.remove(key);
        } 
        TestCase.assertTrue("expected rows number doesn't equal actual rows number", value.isEmpty());
        result.close();
    }

    /**
     * SelectFunctionalityTest#test_SelectThreeTables(). Selects records
     *        from a table using union
     */
    public void test_SelectUnionItself() throws SQLException {
        String sql = (((((("SELECT b.cnum, b.cname" + " FROM ") + (DatabaseCreator.CUSTOMERS_TABLE)) + " a, ") + (DatabaseCreator.CUSTOMERS_TABLE)) + " b") + " WHERE a.snum = 1002") + " AND b.city = a.city";
        ResultSet result = SelectFunctionalityTest.statement.executeQuery(sql);
        HashMap<Integer, String> value = new HashMap<Integer, String>();
        value.put(2003, "Liu");
        value.put(2004, "Grass");
        value.put(2008, "Cisneros");
        while (result.next()) {
            int key = result.getInt("cnum");
            String val = result.getString("cname");
            TestCase.assertTrue("wrong value of cnum field", value.containsKey(key));
            TestCase.assertEquals("wrong value of cname field", value.get(key), val);
            value.remove(key);
        } 
        TestCase.assertTrue("expected rows number doesn't equal actual rows number", value.isEmpty());
        result.close();
    }

    /**
     * SelectFunctionalityTest#test_SelectLeftOuterJoin(). Selects
     *        records from a table using left join
     */
    public void test_SelectLeftOuterJoin() throws SQLException {
        String sql = ((("SELECT distinct s.snum as ssnum, c.snum as ccnum FROM " + (DatabaseCreator.CUSTOMERS_TABLE)) + " c left outer join ") + (DatabaseCreator.SALESPEOPLE_TABLE)) + " s on s.snum=c.snum";
        ResultSet result = SelectFunctionalityTest.statement.executeQuery(sql);
        HashMap<Integer, Integer> value = new HashMap<Integer, Integer>();
        value.put(1001, 1001);
        value.put(1002, 1002);
        value.put(1003, 1003);
        value.put(1004, 1004);
        value.put(1007, 1007);
        while (result.next()) {
            int key = result.getInt("ssnum");
            Object val = result.getObject("ccnum");
            TestCase.assertTrue("wrong value of ssnum field", value.containsKey(key));
            TestCase.assertEquals("wrong value of ccnum field", value.get(key), ((Integer) (val)));
            value.remove(key);
        } 
        TestCase.assertTrue("expected rows number doesn't equal actual rows number", value.isEmpty());
        result.close();
    }

    /**
     * SelectFunctionalityTest#test_SelectGroupBy(). Selects records from
     *        a table using group by
     */
    public void test_SelectGroupBy() throws SQLException {
        String selectQuery = ("SELECT rating, SUM(snum) AS sum FROM " + (DatabaseCreator.CUSTOMERS_TABLE)) + " GROUP BY rating";
        ResultSet result = SelectFunctionalityTest.statement.executeQuery(selectQuery);
        HashMap<Integer, Integer> values = new HashMap<Integer, Integer>();
        values.put(100, 3006);
        values.put(200, 2005);
        values.put(300, 2009);
        while (result.next()) {
            int rating = result.getInt("rating");
            int sum = result.getInt("sum");
            TestCase.assertTrue("Wrong value of rating field", values.containsKey(rating));
            TestCase.assertEquals("Wrong value of sum field", values.get(rating), new Integer(sum));
            TestCase.assertEquals(new Integer(sum), values.remove(rating));
        } 
        result.close();
        TestCase.assertTrue("Result set has wrong size", values.isEmpty());
    }

    /**
     * SelectFunctionalityTest#test_SelectOrderBy(). Selects records from
     *        a table using order by
     */
    public void test_SelectOrderBy() throws SQLException {
        String selectQuery = ("SELECT onum FROM " + (DatabaseCreator.ORDERS_TABLE)) + " ORDER BY onum";
        ResultSet result = SelectFunctionalityTest.statement.executeQuery(selectQuery);
        ArrayList<Integer> values = new ArrayList<Integer>();
        values.add(Integer.valueOf(3001));
        values.add(Integer.valueOf(3002));
        values.add(Integer.valueOf(3003));
        values.add(Integer.valueOf(3005));
        values.add(Integer.valueOf(3006));
        values.add(Integer.valueOf(3007));
        values.add(Integer.valueOf(3008));
        values.add(Integer.valueOf(3009));
        values.add(Integer.valueOf(3010));
        values.add(Integer.valueOf(3011));
        int index = 0;
        while (result.next()) {
            Integer onum = result.getInt("onum");
            TestCase.assertTrue("result set doesn't contain value", values.contains(onum));
            TestCase.assertEquals("result set is not sorted", index, values.indexOf(onum));
            index++;
        } 
        result.close();
    }

    /**
     * SelectFunctionalityTest#test_SelectDistinct(). Selects records
     *        from a table using distinct
     */
    public void test_SelectDistinct() throws SQLException {
        String selectQuery = "SELECT DISTINCT rating FROM " + (DatabaseCreator.CUSTOMERS_TABLE);
        ResultSet result = SelectFunctionalityTest.statement.executeQuery(selectQuery);
        HashSet<Integer> values = new HashSet<Integer>();
        values.add(Integer.valueOf(100));
        values.add(Integer.valueOf(200));
        values.add(Integer.valueOf(300));
        while (result.next()) {
            Integer rating = result.getInt("rating");
            TestCase.assertTrue("result set doesn't contain value", values.contains(rating));
            TestCase.assertTrue("wrong value in the result set", values.remove(rating));
        } 
        result.close();
        TestCase.assertTrue("Result set has wrong size", values.isEmpty());
    }

    /**
     * SelectFunctionalityTest#test_SelectAgregateFunctions(). Selects
     *        records from a table using agregate functions
     */
    public void test_SelectAgregateFunctions() throws SQLException {
        String selectCount = "SELECT count(onum) as count FROM " + (DatabaseCreator.ORDERS_TABLE);
        String selectSum = "SELECT sum(onum) as sum FROM " + (DatabaseCreator.ORDERS_TABLE);
        String selectAvg = "SELECT avg(onum) as avg FROM " + (DatabaseCreator.ORDERS_TABLE);
        String selectMax = "SELECT max(onum) as max FROM " + (DatabaseCreator.ORDERS_TABLE);
        String selectMin = "SELECT min(onum) as min FROM " + (DatabaseCreator.ORDERS_TABLE);
        func("count", selectCount, 10);
        func("sum", selectSum, 30062);
        func("avg", selectAvg, 3006);
        func("max", selectMax, 3011);
        func("min", selectMin, 3001);
    }

    /**
     * SelectFunctionalityTest#test_SelectHaving(). Selects records from
     *        a table using having
     */
    public void test_SelectHaving() throws SQLException {
        String selectQuery = ("SELECT snum, max(amt) AS max FROM " + (DatabaseCreator.ORDERS_TABLE)) + " GROUP BY snum HAVING max(amt) > 3000";
        ResultSet result = SelectFunctionalityTest.statement.executeQuery(selectQuery);
        HashSet<Double> values = new HashSet<Double>();
        values.add(Double.valueOf(9891.88));
        values.add(Double.valueOf(5160.45));
        while (result.next()) {
            Double max = result.getDouble("max");
            TestCase.assertTrue("result set doesn't contain value", values.contains(max));
            TestCase.assertTrue("wrong value in the result set", values.remove(max));
        } 
        result.close();
        TestCase.assertTrue("Result set has wrong size", values.isEmpty());
    }
}

