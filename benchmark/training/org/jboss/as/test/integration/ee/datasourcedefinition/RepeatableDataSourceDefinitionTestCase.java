/**
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.ee.datasourcedefinition;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that @DataSourceDefinition works, and that the datasource is automatically enlisted in the transaction
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class RepeatableDataSourceDefinitionTestCase {
    @ArquillianResource
    private InitialContext ctx;

    @Test
    public void testDataSourceDefinition() throws SQLException, NamingException {
        RepeatedAnnotationDataSourceBean bean = lookup();
        DataSource ds = bean.getDataSource();
        Connection c = ds.getConnection();
        ResultSet result = c.createStatement().executeQuery("select 1");
        Assert.assertTrue(result.next());
        c.close();
    }

    @Test
    public void testTransactionEnlistment() throws SQLException, NamingException {
        RepeatedAnnotationDataSourceBean bean = lookup();
        try {
            bean.insert1RolledBack();
            Assert.fail("expect exception");
        } catch (RuntimeException expected) {
        }
        DataSource ds = bean.getDataSource();
        Connection c = ds.getConnection();
        ResultSet result = c.createStatement().executeQuery("select id from coffee where id=1;");
        Assert.assertFalse(result.next());
        c.close();
    }

    @Test
    public void testTransactionEnlistment2() throws SQLException, NamingException {
        RepeatedAnnotationDataSourceBean bean = lookup();
        bean.insert2();
        DataSource ds = bean.getDataSource();
        Connection c = ds.getConnection();
        ResultSet result = c.createStatement().executeQuery("select id from coffee where id=2;");
        Assert.assertTrue(result.next());
        c.close();
    }

    @Test
    public void testResourceInjectionWithSameName() throws NamingException {
        RepeatedAnnotationDataSourceBean bean = lookup();
        Assert.assertNotNull(bean.getDataSource2());
        Assert.assertNotNull(bean.getDataSource3());
        Assert.assertNotNull(bean.getDataSource4());
    }

    /**
     * Tests an embedded datasource resource def.
     *
     * @throws NamingException
     * 		
     * @throws SQLException
     * 		
     */
    @Test
    public void testEmbeddedDatasource() throws SQLException, NamingException {
        RepeatedAnnotationDataSourceBean bean = lookup();
        Assert.assertEquals(bean.getDataSource5().getConnection().nativeSQL("dse"), "dse");
    }
}

