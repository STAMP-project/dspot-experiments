/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.jdbc;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.exec.ExecConstants;
import org.apache.drill.shaded.guava.com.google.common.io.Resources;
import org.apache.drill.test.DrillTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * (Some) unit and integration tests for org.apache.drill.jdbc.Driver.
 */
@Category(JdbcTest.class)
public class DriverTest extends DrillTest {
    // TODO: Move Jetty status server disabling to DrillTest.
    private static final String STATUS_SERVER_PROPERTY_NAME = ExecConstants.HTTP_ENABLE;

    private static final String origJettyPropValue = System.getProperty(DriverTest.STATUS_SERVER_PROPERTY_NAME, "true");

    private Driver uut = new Driver();

    // //////////////////////////////////////
    // Tests of methods defined by JDBC/java.sql.Driver:
    // Tests for connect() (defined by JDBC/java.sql.Driver):
    @Test
    public void test_connect_declinesEmptyUrl() throws SQLException {
        Assert.assertThat(uut.connect("", null), CoreMatchers.nullValue());
    }

    @Test
    public void test_connect_declinesNonUrl() throws SQLException {
        Assert.assertThat(uut.connect("whatever", null), CoreMatchers.nullValue());
    }

    @Test
    public void test_connect_declinesNonJdbcUrl() throws SQLException {
        Assert.assertThat(uut.connect("file:///something", null), CoreMatchers.nullValue());
    }

    @Test
    public void test_connect_declinesNonDrillJdbcUrl() throws SQLException {
        Assert.assertThat(uut.connect("jdbc:somedb:whatever", null), CoreMatchers.nullValue());
    }

    @Test
    public void test_connect_declinesNotQuiteDrillUrl() throws SQLException {
        Assert.assertThat(uut.connect("jdbc:drill", null), CoreMatchers.nullValue());
    }

    @Test
    public void test_connect_acceptsLocalZkDrillJdbcUrl() throws SQLException {
        Connection connection = uut.connect("jdbc:drill:zk=local", null);
        Assert.assertThat(connection, CoreMatchers.not(CoreMatchers.nullValue()));
        connection.close();
    }

    @Test
    public void test_connect_acceptsLocalViaProperties() throws SQLException {
        Properties props = new Properties();
        props.put("zk", "local");
        Connection connection = uut.connect("jdbc:drill:", props);
        Assert.assertThat(connection, CoreMatchers.not(CoreMatchers.nullValue()));
        connection.close();
    }

    // TODO:  Determine which other cases to test, including cases of Properties
    // parameter values to test.
    // Tests for acceptsURL(String) (defined by JDBC/java.sql.Driver):
    @Test
    public void test_acceptsURL_acceptsDrillUrlMinimal() throws SQLException {
        Assert.assertThat(uut.acceptsURL("jdbc:drill:"), CoreMatchers.equalTo(true));
    }

    @Test
    public void test_acceptsURL_acceptsDrillPlusJunk() throws SQLException {
        Assert.assertThat(uut.acceptsURL("jdbc:drill:should it check this?"), CoreMatchers.equalTo(true));
    }

    @Test
    public void test_acceptsURL_rejectsNonDrillJdbcUrl() throws SQLException {
        Assert.assertThat(uut.acceptsURL("jdbc:notdrill:whatever"), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_acceptsURL_rejectsNonDrillJdbc2() throws SQLException {
        Assert.assertThat(uut.acceptsURL("jdbc:optiq:"), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_acceptsURL_rejectsNonJdbcUrl() throws SQLException {
        Assert.assertThat(uut.acceptsURL("drill:"), CoreMatchers.equalTo(false));
    }

    // Tests for getMajorVersion() (defined by JDBC/java.sql.Driver):
    @Test
    public void test_getMajorVersion() throws IOException {
        Properties properties = new Properties();
        properties.load(Resources.getResource("apache-drill-jdbc.properties").openStream());
        Assert.assertThat(uut.getMajorVersion(), CoreMatchers.is(Integer.parseInt(properties.getProperty("driver.version.major"))));
    }

    // Tests for getMinorVersion() (defined by JDBC/java.sql.Driver):
    @Test
    public void test_getMinorVersion() throws IOException {
        Properties properties = new Properties();
        properties.load(Resources.getResource("apache-drill-jdbc.properties").openStream());
        Assert.assertThat(uut.getMinorVersion(), CoreMatchers.is(Integer.parseInt(properties.getProperty("driver.version.minor"))));
    }

    // Tests for XXX (defined by JDBC/java.sql.Driver):
    // Defined by JDBC/java.sql.Driver: "[driver] should create and instance of
    // itself and register it with the DriverManager.
    @Test
    public void test_Driver_registersWithManager() throws SQLException {
        Assert.assertThat(DriverManager.getDriver("jdbc:drill:whatever"), CoreMatchers.instanceOf(Driver.class));
    }
}

