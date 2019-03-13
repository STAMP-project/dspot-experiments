package org.sqlite;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the JSON1 extension using the examples listed in the documentation.
 *
 * @see <a href="http://sqlite.org/json1.html">http://sqlite.org/json1.html</a>
 */
public class JSON1Test {
    @Test
    public void json_Test() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json(\' { \"this\" : \"is\", \"a\": [ \"test\" ] } \')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"this\":\"is\",\"a\":[\"test\"]}", rs.getString(1));
    }

    @Test
    public void json_object_Test1() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_object('ex','[52,3.14159]')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"ex\":\"[52,3.14159]\"}", rs.getString(1));
    }

    @Test
    public void json_object_Test2() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_object('ex',json('[52,3.14159]'))");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"ex\":[52,3.14159]}", rs.getString(1));
    }

    @Test
    public void json_object_Test3() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_object('ex',json_array(52,3.14159))");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"ex\":[52,3.14159]}", rs.getString(1));
    }

    @Test
    public void json_object_Test4() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_object('a',2,'c',4)");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"a\":2,\"c\":4}", rs.getString(1));
    }

    @Test
    public void json_object_Test5() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_object('a',2,'c','{e:5}')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"a\":2,\"c\":\"{e:5}\"}", rs.getString(1));
    }

    @Test
    public void json_object_Test6() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_object('a',2,'c',json_object('e',5))");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"a\":2,\"c\":{\"e\":5}}", rs.getString(1));
    }

    @Test
    public void json_array_Test1() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_array(1,2,'3',4)");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("[1,2,\"3\",4]", rs.getString(1));
    }

    @Test
    public void json_array_Test2() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_array('[1,2]')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("[\"[1,2]\"]", rs.getString(1));
    }

    @Test
    public void json_array_Test3() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_array(json_array(1,2))");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("[[1,2]]", rs.getString(1));
    }

    @Test
    public void json_array_Test4() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_array(1,null,\'3\',\'[4,5]\',\'{\"six\":7.7}\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("[1,null,\"3\",\"[4,5]\",\"{\\\"six\\\":7.7}\"]", rs.getString(1));
    }

    @Test
    public void json_array_Test5() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_array(1,null,\'3\',json(\'[4,5]\'),json(\'{\"six\":7.7}\'))");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("[1,null,\"3\",[4,5],{\"six\":7.7}]", rs.getString(1));
    }

    @Test
    public void json_array_length_Test1() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_array_length('[1,2,3,4]')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(4, rs.getInt(1));
    }

    @Test
    public void json_array_length_Test2() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_array_length('[1,2,3,4]', '$')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(4, rs.getInt(1));
    }

    @Test
    public void json_array_length_Test3() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_array_length('[1,2,3,4]', '$[2]')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(0, rs.getInt(1));
    }

    @Test
    public void json_array_length_Test4() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_array_length(\'{\"one\":[1,2,3]}\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(0, rs.getInt(1));
    }

    @Test
    public void json_array_length_Test5() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_array_length(\'{\"one\":[1,2,3]}\', \'$.one\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(3, rs.getInt(1));
    }

    @Test
    public void json_array_length_Test6() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_array_length(\'{\"one\":[1,2,3]}\', \'$.two\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(null, rs.getObject(1));
    }

    @Test
    public void json_extract_Test1() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_extract(\'{\"a\":2,\"c\":[4,5,{\"f\":7}]}\', \'$\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"a\":2,\"c\":[4,5,{\"f\":7}]}", rs.getString(1));
    }

    @Test
    public void json_extract_Test2() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_extract(\'{\"a\":2,\"c\":[4,5,{\"f\":7}]}\', \'$.c\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("[4,5,{\"f\":7}]", rs.getString(1));
    }

    @Test
    public void json_extract_Test3() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_extract(\'{\"a\":2,\"c\":[4,5,{\"f\":7}]}\', \'$.c[2]\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"f\":7}", rs.getString(1));
    }

    @Test
    public void json_extract_Test4() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_extract(\'{\"a\":2,\"c\":[4,5,{\"f\":7}]}\', \'$.c[2].f\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(7, rs.getInt(1));
    }

    @Test
    public void json_extract_Test5() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_extract(\'{\"a\":2,\"c\":[4,5],\"f\":7}\',\'$.c\',\'$.a\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("[[4,5],2]", rs.getString(1));
    }

    @Test
    public void json_extract_Test6() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_extract(\'{\"a\":2,\"c\":[4,5,{\"f\":7}]}\', \'$.x\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(null, rs.getString(1));
    }

    @Test
    public void json_extract_Test7() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_extract(\'{\"a\":2,\"c\":[4,5,{\"f\":7}]}\', \'$.x\', \'$.a\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("[null,2]", rs.getString(1));
    }

    @Test
    public void json_insert_Test1() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_insert(\'{\"a\":2,\"c\":4}\', \'$.a\', 99)");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"a\":2,\"c\":4}", rs.getString(1));
    }

    @Test
    public void json_insert_Test2() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_insert(\'{\"a\":2,\"c\":4}\', \'$.e\', 99)");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"a\":2,\"c\":4,\"e\":99}", rs.getString(1));
    }

    @Test
    public void json_replace_Test1() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_replace(\'{\"a\":2,\"c\":4}\', \'$.a\', 99)");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"a\":99,\"c\":4}", rs.getString(1));
    }

    @Test
    public void json_replace_Test2() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_replace(\'{\"a\":2,\"c\":4}\', \'$.e\', 99)");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"a\":2,\"c\":4}", rs.getString(1));
    }

    @Test
    public void json_set_Test1() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_set(\'{\"a\":2,\"c\":4}\', \'$.a\', 99)");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"a\":99,\"c\":4}", rs.getString(1));
    }

    @Test
    public void json_set_Test2() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_set(\'{\"a\":2,\"c\":4}\', \'$.e\', 99)");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"a\":2,\"c\":4,\"e\":99}", rs.getString(1));
    }

    @Test
    public void json_set_Test3() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_set(\'{\"a\":2,\"c\":4}\', \'$.c\', \'[97,96]\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"a\":2,\"c\":\"[97,96]\"}", rs.getString(1));
    }

    @Test
    public void json_set_Test4() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_set(\'{\"a\":2,\"c\":4}\', \'$.c\', json(\'[97,96]\'))");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"a\":2,\"c\":[97,96]}", rs.getString(1));
    }

    @Test
    public void json_set_Test5() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_set(\'{\"a\":2,\"c\":4}\', \'$.c\', json_array(97,96))");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"a\":2,\"c\":[97,96]}", rs.getString(1));
    }

    @Test
    public void json_remove_Test1() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_remove('[0,1,2,3,4]','$[2]')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("[0,1,3,4]", rs.getString(1));
    }

    @Test
    public void json_remove_Test2() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_remove('[0,1,2,3,4]','$[2]','$[0]')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("[1,3,4]", rs.getString(1));
    }

    @Test
    public void json_remove_Test3() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_remove('[0,1,2,3,4]','$[0]','$[2]')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("[1,2,4]", rs.getString(1));
    }

    @Test
    public void json_remove_Test4() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_remove(\'{\"x\":25,\"y\":42}\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"x\":25,\"y\":42}", rs.getString(1));
    }

    @Test
    public void json_remove_Test5() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_remove(\'{\"x\":25,\"y\":42}\',\'$.z\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"x\":25,\"y\":42}", rs.getString(1));
    }

    @Test
    public void json_remove_Test6() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_remove(\'{\"x\":25,\"y\":42}\',\'$.y\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("{\"x\":25}", rs.getString(1));
    }

    @Test
    public void json_remove_Test7() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_remove(\'{\"x\":25,\"y\":42}\',\'$\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(null, rs.getString(1));
    }

    @Test
    public void json_type_Test1() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_type(\'{\"a\":[2,3.5,true,false,null,\"x\"]}\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("object", rs.getString(1));
    }

    @Test
    public void json_type_Test2() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_type(\'{\"a\":[2,3.5,true,false,null,\"x\"]}\',\'$\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("object", rs.getString(1));
    }

    @Test
    public void json_type_Test3() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_type(\'{\"a\":[2,3.5,true,false,null,\"x\"]}\',\'$.a\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("array", rs.getString(1));
    }

    @Test
    public void json_type_Test4() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_type(\'{\"a\":[2,3.5,true,false,null,\"x\"]}\',\'$.a[0]\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("integer", rs.getString(1));
    }

    @Test
    public void json_type_Test5() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_type(\'{\"a\":[2,3.5,true,false,null,\"x\"]}\',\'$.a[1]\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("real", rs.getString(1));
    }

    @Test
    public void json_type_Test6() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_type(\'{\"a\":[2,3.5,true,false,null,\"x\"]}\',\'$.a[2]\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("true", rs.getString(1));
    }

    @Test
    public void json_type_Test7() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_type(\'{\"a\":[2,3.5,true,false,null,\"x\"]}\',\'$.a[3]\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("false", rs.getString(1));
    }

    @Test
    public void json_type_Test8() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_type(\'{\"a\":[2,3.5,true,false,null,\"x\"]}\',\'$.a[4]\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("null", rs.getString(1));
    }

    @Test
    public void json_type_Test9() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_type(\'{\"a\":[2,3.5,true,false,null,\"x\"]}\',\'$.a[5]\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("text", rs.getString(1));
    }

    @Test
    public void json_type_Test10() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_type(\'{\"a\":[2,3.5,true,false,null,\"x\"]}\',\'$.a[6]\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(null, rs.getString(1));
    }

    @Test
    public void json_valid_Test1() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_valid(\'{\"x\":35}\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(1, rs.getInt(1));
    }

    @Test
    public void json_valid_Test2() throws SQLException {
        Connection conn = getConnection();
        ResultSet rs = conn.createStatement().executeQuery("select json_valid(\'{\"x\":35\')");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(0, rs.getInt(1));
    }

    @Test
    public void json_each_Test1() throws SQLException {
        Connection conn = getConnection();
        conn.createStatement().execute("create table user (name, phone)");
        conn.createStatement().execute("insert into user values('james', json_array('704-100-0000','604-100-0000'))");
        conn.createStatement().execute("insert into user values('sally', json_array('604-200-0000','404-200-0000'))");
        conn.createStatement().execute("insert into user values('frank', json_array('704-200-0000','604-200-0000'))");
        conn.createStatement().execute("insert into user values('harry', json_array('504-200-0000','304-200-0000'))");
        String q = "SELECT DISTINCT user.name" + (" FROM user, json_each(user.phone)" + " where json_each.value LIKE '704-%'");
        ResultSet rs = conn.createStatement().executeQuery(q);
        Assert.assertTrue(rs.next());
        Assert.assertEquals("james", rs.getString(1));
        Assert.assertTrue(rs.next());
        Assert.assertEquals("frank", rs.getString(1));
        Assert.assertFalse(rs.next());
    }

    @Test
    public void json_each_Test2() throws SQLException {
        Connection conn = getConnection();
        conn.createStatement().execute("create table user (name, phone)");
        conn.createStatement().execute("insert into user values('james', json_array('704-100-0000','604-100-0000'))");
        conn.createStatement().execute("insert into user values('sally', '604-200-0000')");
        conn.createStatement().execute("insert into user values('frank', '704-200-0000')");
        conn.createStatement().execute("insert into user values('harry', json_array('504-200-0000','304-200-0000'))");
        String q = "SELECT name FROM user WHERE phone LIKE '705-%'" + ((((" UNION" + " SELECT user.name") + " FROM user, json_each(user.phone)") + " WHERE json_valid(user.phone)") + " AND json_each.value LIKE '704-%'");
        ResultSet rs = conn.createStatement().executeQuery(q);
        Assert.assertTrue(rs.next());
        Assert.assertEquals("james", rs.getString(1));
        Assert.assertFalse(rs.next());
    }

    @Test
    public void json_tree_Test1() throws SQLException {
        Connection conn = getConnection();
        conn.createStatement().execute("create table big (json JSON)");
        conn.createStatement().execute("insert into big values(json_object('a',2,'c',4))");
        String q = "SELECT big.rowid, fullkey, value" + (" FROM big, json_tree(big.json)" + " WHERE json_tree.type NOT IN ('object', 'array')");
        ResultSet rs = conn.createStatement().executeQuery(q);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(1, rs.getInt(1));
        Assert.assertEquals("$.a", rs.getString(2));
        Assert.assertEquals(2, rs.getInt(3));
        Assert.assertTrue(rs.next());
        Assert.assertEquals(1, rs.getInt(1));
        Assert.assertEquals("$.c", rs.getString(2));
        Assert.assertEquals(4, rs.getInt(3));
        Assert.assertFalse(rs.next());
    }

    @Test
    public void json_tree_Test2() throws SQLException {
        Connection conn = getConnection();
        conn.createStatement().execute("create table big (json JSON)");
        conn.createStatement().execute("insert into big values(json_object('a',2,'c',4))");
        String q = "SELECT big.rowid, fullkey, atom" + (" FROM big, json_tree(big.json)" + " WHERE atom IS NOT NULL");
        ResultSet rs = conn.createStatement().executeQuery(q);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(1, rs.getInt(1));
        Assert.assertEquals("$.a", rs.getString(2));
        Assert.assertEquals(2, rs.getInt(3));
        Assert.assertTrue(rs.next());
        Assert.assertEquals(1, rs.getInt(1));
        Assert.assertEquals("$.c", rs.getString(2));
        Assert.assertEquals(4, rs.getInt(3));
        Assert.assertFalse(rs.next());
    }
}

