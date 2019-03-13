/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventsourcing.eventstore.jpa;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import org.axonframework.common.AxonConfigurationException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Martin Tilma
 */
public class SQLErrorCodesResolverTest {
    @Test
    public void testIsDuplicateKey() {
        SQLErrorCodesResolver sqlErrorCodesResolver = new SQLErrorCodesResolver(new ArrayList());
        boolean isDuplicateKey = sqlErrorCodesResolver.isDuplicateKeyViolation(new PersistenceException("error", new RuntimeException()));
        Assert.assertFalse(isDuplicateKey);
    }

    @Test
    public void testIsDuplicateKey_isDuplicateKey_usingSetDuplicateKeyCodes() {
        List<Integer> errorCodes = new ArrayList<>();
        errorCodes.add((-104));
        SQLErrorCodesResolver sqlErrorCodesResolver = new SQLErrorCodesResolver(errorCodes);
        SQLException sqlException = new SQLException("test", "error", errorCodes.get(0));
        boolean isDuplicateKey = sqlErrorCodesResolver.isDuplicateKeyViolation(new PersistenceException("error", sqlException));
        Assert.assertTrue(isDuplicateKey);
    }

    @Test
    public void testIsDuplicateKey_isDuplicateKey_usingDataSource() throws Exception {
        String databaseProductName = "HSQL Database Engine";
        DataSource dataSource = createMockDataSource(databaseProductName);
        SQLErrorCodesResolver sqlErrorCodesResolver = new SQLErrorCodesResolver(dataSource);
        SQLException sqlException = new SQLException("test", "error", (-104));
        boolean isDuplicateKey = sqlErrorCodesResolver.isDuplicateKeyViolation(new PersistenceException("error", sqlException));
        Assert.assertTrue(isDuplicateKey);
    }

    @Test
    public void testIsDuplicateKey_isDuplicateKey_usingAS400DataSource() throws Exception {
        String databaseProductName = "DB2 UDB for AS/400";
        DataSource dataSource = createMockDataSource(databaseProductName);
        SQLErrorCodesResolver sqlErrorCodesResolver = new SQLErrorCodesResolver(dataSource);
        SQLException sqlException = new SQLException("test", "error", (-803));
        boolean isDuplicateKey = sqlErrorCodesResolver.isDuplicateKeyViolation(new PersistenceException("error", sqlException));
        Assert.assertTrue(isDuplicateKey);
    }

    @Test
    public void testIsDuplicateKey_isDuplicateKey_usingDB2LinuxDataSource() throws Exception {
        String databaseProductName = "DB2/LINUXX8664";
        DataSource dataSource = createMockDataSource(databaseProductName);
        SQLErrorCodesResolver sqlErrorCodesResolver = new SQLErrorCodesResolver(dataSource);
        SQLException sqlException = new SQLException("test", "error", (-803));
        boolean isDuplicateKey = sqlErrorCodesResolver.isDuplicateKeyViolation(new PersistenceException("error", sqlException));
        Assert.assertTrue(isDuplicateKey);
    }

    @Test
    public void testIsDuplicateKey_isDuplicateKey_usingRandomDb2DataSource() throws Exception {
        String databaseProductName = "DB2 Completely unexpected value";
        DataSource dataSource = createMockDataSource(databaseProductName);
        SQLErrorCodesResolver sqlErrorCodesResolver = new SQLErrorCodesResolver(dataSource);
        SQLException sqlException = new SQLException("test", "error", (-803));
        boolean isDuplicateKey = sqlErrorCodesResolver.isDuplicateKeyViolation(new PersistenceException("error", sqlException));
        Assert.assertTrue(isDuplicateKey);
    }

    @Test
    public void testIsDuplicateKey_isDuplicateKey_usingProductName() {
        String databaseProductName = "HSQL Database Engine";
        SQLErrorCodesResolver sqlErrorCodesResolver = new SQLErrorCodesResolver(databaseProductName);
        SQLException sqlException = new SQLException("test", "error", (-104));
        boolean isDuplicateKey = sqlErrorCodesResolver.isDuplicateKeyViolation(new PersistenceException("error", sqlException));
        Assert.assertTrue(isDuplicateKey);
    }

    @Test
    public void testIsDuplicateKey_isDuplicateKey_usingCustomProperties() throws Exception {
        Properties props = new Properties();
        props.setProperty("MyCustom_Database_Engine.duplicateKeyCodes", "-104");
        String databaseProductName = "MyCustom Database Engine";
        DataSource dataSource = createMockDataSource(databaseProductName);
        SQLErrorCodesResolver sqlErrorCodesResolver = new SQLErrorCodesResolver(props, dataSource);
        SQLException sqlException = new SQLException("test", "error", (-104));
        boolean isDuplicateKey = sqlErrorCodesResolver.isDuplicateKeyViolation(new PersistenceException("error", sqlException));
        Assert.assertTrue(isDuplicateKey);
    }

    @Test(expected = AxonConfigurationException.class)
    public void testInitialization_UnknownProductName() throws Exception {
        DataSource dataSource = createMockDataSource("Some weird unknown DB type");
        new SQLErrorCodesResolver(dataSource);
    }

    @Test
    public void testIsDuplicateKey_isDuplicateKey_usingSqlState() {
        Properties props = new Properties();
        props.setProperty("MyCustom_Database_Engine.duplicateKeyCodes", "-104");
        String databaseProductName = "MyCustom Database Engine";
        SQLErrorCodesResolver sqlErrorCodesResolver = new SQLErrorCodesResolver(props, databaseProductName);
        SQLException sqlException = new SQLException("test", "-104");
        boolean isDuplicateKey = sqlErrorCodesResolver.isDuplicateKeyViolation(new PersistenceException("error", sqlException));
        Assert.assertTrue(isDuplicateKey);
    }

    @Test
    public void testIsDuplicateKey_isDuplicateKey_usingNonIntSqlState() {
        Properties props = new Properties();
        props.setProperty("MyCustom_Database_Engine.duplicateKeyCodes", "-104");
        String databaseProductName = "MyCustom Database Engine";
        SQLErrorCodesResolver sqlErrorCodesResolver = new SQLErrorCodesResolver(props, databaseProductName);
        SQLException sqlException = new SQLException("test", "thisIsNotAnInt");
        boolean isDuplicateKey = sqlErrorCodesResolver.isDuplicateKeyViolation(new PersistenceException("error", sqlException));
        Assert.assertFalse(isDuplicateKey);
    }

    @Test
    public void testIsDuplicateKey_isDuplicateKey_usingNonNullSqlState() {
        Properties props = new Properties();
        props.setProperty("MyCustom_Database_Engine.duplicateKeyCodes", "-104");
        String databaseProductName = "MyCustom Database Engine";
        SQLErrorCodesResolver sqlErrorCodesResolver = new SQLErrorCodesResolver(props, databaseProductName);
        SQLException sqlException = new SQLException("test", ((String) (null)));
        boolean isDuplicateKey = sqlErrorCodesResolver.isDuplicateKeyViolation(new PersistenceException("error", sqlException));
        Assert.assertFalse(isDuplicateKey);
    }
}

