package org.testcontainers.jdbc;


import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * This Test class validates that all supported JDBC URL's can be parsed by ConnectionUrl class.
 *
 * @author ManikMagar
 */
@RunWith(Parameterized.class)
public class ConnectionUrlDriversTests {
    @Parameterized.Parameter
    public String jdbcUrl;

    @Parameterized.Parameter(1)
    public String databaseType;

    @Parameterized.Parameter(2)
    public Optional<String> tag;

    @Parameterized.Parameter(3)
    public String dbHostString;

    @Parameterized.Parameter(4)
    public String databaseName;

    @Test
    public void test() {
        ConnectionUrl url = ConnectionUrl.newInstance(jdbcUrl);
        assertEquals("Database Type is as expected", databaseType, url.getDatabaseType());
        assertEquals("Image tag is as expected", tag, url.getImageTag());
        assertEquals("Database Host String is as expected", dbHostString, url.getDbHostString());
        assertEquals("Database Name is as expected", databaseName, url.getDatabaseName().orElse(""));
    }
}

