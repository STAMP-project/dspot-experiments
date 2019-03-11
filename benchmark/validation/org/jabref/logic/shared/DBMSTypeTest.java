package org.jabref.logic.shared;


import DBMSType.MYSQL;
import DBMSType.ORACLE;
import DBMSType.POSTGRESQL;
import org.jabref.model.database.shared.DBMSType;
import org.jabref.testutils.category.DatabaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@DatabaseTest
public class DBMSTypeTest {
    @Test
    public void testToString() {
        Assertions.assertEquals("MySQL", MYSQL.toString());
        Assertions.assertEquals("Oracle", ORACLE.toString());
        Assertions.assertEquals("PostgreSQL", POSTGRESQL.toString());
    }

    @Test
    public void testGetDriverClassPath() {
        Assertions.assertEquals("com.mysql.jdbc.Driver", MYSQL.getDriverClassPath());
        Assertions.assertEquals("oracle.jdbc.driver.OracleDriver", ORACLE.getDriverClassPath());
        Assertions.assertEquals("com.impossibl.postgres.jdbc.PGDriver", POSTGRESQL.getDriverClassPath());
    }

    @Test
    public void testFromString() {
        Assertions.assertEquals(MYSQL, DBMSType.fromString("MySQL").get());
        Assertions.assertEquals(ORACLE, DBMSType.fromString("Oracle").get());
        Assertions.assertEquals(POSTGRESQL, DBMSType.fromString("PostgreSQL").get());
        Assertions.assertFalse(DBMSType.fromString("XXX").isPresent());
    }

    @Test
    public void testGetUrl() {
        Assertions.assertEquals("jdbc:mysql://localhost:3306/xe", MYSQL.getUrl("localhost", 3306, "xe"));
        Assertions.assertEquals("jdbc:oracle:thin:@localhost:1521:xe", ORACLE.getUrl("localhost", 1521, "xe"));
        Assertions.assertEquals("jdbc:pgsql://localhost:5432/xe", POSTGRESQL.getUrl("localhost", 5432, "xe"));
    }

    @Test
    public void testGetDefaultPort() {
        Assertions.assertEquals(3306, MYSQL.getDefaultPort());
        Assertions.assertEquals(5432, POSTGRESQL.getDefaultPort());
        Assertions.assertEquals(1521, ORACLE.getDefaultPort());
    }
}

