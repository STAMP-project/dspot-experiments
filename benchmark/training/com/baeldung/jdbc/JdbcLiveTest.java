package com.baeldung.jdbc;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class JdbcLiveTest {
    private static final Logger LOG = Logger.getLogger(JdbcLiveTest.class);

    private Connection con;

    @Test
    public void whenInsertUpdateRecord_thenCorrect() throws SQLException {
        Statement stmt = con.createStatement();
        String insertSql = "INSERT INTO employees(name, position, salary) values ('john', 'developer', 2000)";
        stmt.executeUpdate(insertSql);
        String selectSql = "SELECT * FROM employees";
        ResultSet resultSet = stmt.executeQuery(selectSql);
        List<Employee> employees = new ArrayList<>();
        while (resultSet.next()) {
            Employee emp = new Employee();
            emp.setId(resultSet.getInt("emp_id"));
            emp.setName(resultSet.getString("name"));
            emp.setSalary(resultSet.getDouble("salary"));
            emp.setPosition(resultSet.getString("position"));
            employees.add(emp);
        } 
        Assert.assertEquals("employees list size incorrect", 1, employees.size());
        Assert.assertEquals("name incorrect", "john", employees.iterator().next().getName());
        Assert.assertEquals("position incorrect", "developer", employees.iterator().next().getPosition());
        Assert.assertEquals("salary incorrect", 2000, employees.iterator().next().getSalary(), 0.1);
        Statement updatableStmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet updatableResultSet = updatableStmt.executeQuery(selectSql);
        updatableResultSet.moveToInsertRow();
        updatableResultSet.updateString("name", "mark");
        updatableResultSet.updateString("position", "analyst");
        updatableResultSet.updateDouble("salary", 2000);
        updatableResultSet.insertRow();
        String updatePositionSql = "UPDATE employees SET position=? WHERE emp_id=?";
        PreparedStatement pstmt = con.prepareStatement(updatePositionSql);
        pstmt.setString(1, "lead developer");
        pstmt.setInt(2, 1);
        String updateSalarySql = "UPDATE employees SET salary=? WHERE emp_id=?";
        PreparedStatement pstmt2 = con.prepareStatement(updateSalarySql);
        pstmt.setDouble(1, 3000);
        pstmt.setInt(2, 1);
        boolean autoCommit = con.getAutoCommit();
        try {
            con.setAutoCommit(false);
            pstmt.executeUpdate();
            pstmt2.executeUpdate();
            con.commit();
        } catch (SQLException exc) {
            con.rollback();
        } finally {
            con.setAutoCommit(autoCommit);
        }
    }

    @Test
    public void whenCallProcedure_thenCorrect() {
        try {
            String preparedSql = "{call insertEmployee(?,?,?,?)}";
            CallableStatement cstmt = con.prepareCall(preparedSql);
            cstmt.setString(2, "ana");
            cstmt.setString(3, "tester");
            cstmt.setDouble(4, 2000);
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.execute();
            int new_id = cstmt.getInt(1);
            Assert.assertTrue((new_id > 0));
        } catch (SQLException exc) {
            JdbcLiveTest.LOG.error("Procedure incorrect or does not exist!");
        }
    }

    @Test
    public void whenReadMetadata_thenCorrect() throws SQLException {
        DatabaseMetaData dbmd = con.getMetaData();
        ResultSet tablesResultSet = dbmd.getTables(null, null, "%", null);
        while (tablesResultSet.next()) {
            JdbcLiveTest.LOG.info(tablesResultSet.getString("TABLE_NAME"));
        } 
        String selectSql = "SELECT * FROM employees";
        Statement stmt = con.createStatement();
        ResultSet resultSet = stmt.executeQuery(selectSql);
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int nrColumns = rsmd.getColumnCount();
        Assert.assertEquals(nrColumns, 4);
        IntStream.range(1, nrColumns).forEach(( i) -> {
            try {
                JdbcLiveTest.LOG.info(rsmd.getColumnName(i));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}

