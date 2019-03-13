/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.database.util;


import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DatabaseUtilTest {
    private Context context;

    private String testName;

    @Test(expected = NamingException.class)
    public void testNullName() throws NamingException {
        DatabaseUtil.getDataSourceFromJndi(null, context);
    }

    @Test(expected = NamingException.class)
    public void testEmptyName() throws NamingException {
        DatabaseUtil.getDataSourceFromJndi("", context);
    }

    @Test(expected = NamingException.class)
    public void testWrongType() throws NamingException {
        Mockito.when(context.lookup(ArgumentMatchers.anyString())).thenReturn(new Object());
        DatabaseUtil.getDataSourceFromJndi(testName, context);
    }

    @Test(expected = NamingException.class)
    public void testNotFound() throws NamingException {
        Mockito.when(context.lookup(ArgumentMatchers.anyString())).thenThrow(new NamingException());
        try {
            DatabaseUtil.getDataSourceFromJndi(testName, context);
        } catch (NamingException ne) {
            Mockito.verify(context.lookup(testName));
            Mockito.verify(context.lookup(("java:" + (testName))));
            Mockito.verify(context.lookup(("java:comp/env/jdbc/" + (testName))));
            Mockito.verify(context.lookup(("jdbc/" + (testName))));
            throw ne;
        }
    }

    @Test
    public void testCl() throws NamingException {
        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(context.lookup(testName)).thenReturn(dataSource);
        DatabaseUtil util = new DatabaseUtil();
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        ClassLoader cl = Mockito.mock(ClassLoader.class);
        try {
            Thread.currentThread().setContextClassLoader(cl);
            util.getNamedDataSource(testName);
        } catch (Exception ex) {
        } finally {
            try {
                Mockito.verify(cl, Mockito.never()).loadClass(ArgumentMatchers.anyString());
                Mockito.verify(cl, Mockito.never()).getResource(ArgumentMatchers.anyString());
                Mockito.verify(cl, Mockito.never()).getResourceAsStream(ArgumentMatchers.anyString());
            } catch (Exception ex) {
            }
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    @Test
    public void testNormal() throws NamingException {
        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(context.lookup(testName)).thenReturn(dataSource);
        Assert.assertEquals(dataSource, DatabaseUtil.getDataSourceFromJndi(testName, context));
    }

    @Test
    public void testJBoss() throws NamingException {
        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(context.lookup(("java:" + (testName)))).thenReturn(dataSource);
        Assert.assertEquals(dataSource, DatabaseUtil.getDataSourceFromJndi(testName, context));
    }

    @Test
    public void testTomcat() throws NamingException {
        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(context.lookup(("java:comp/env/jdbc/" + (testName)))).thenReturn(dataSource);
        Assert.assertEquals(dataSource, DatabaseUtil.getDataSourceFromJndi(testName, context));
    }

    @Test
    public void testOther() throws NamingException {
        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(context.lookup(("jdbc/" + (testName)))).thenReturn(dataSource);
        Assert.assertEquals(dataSource, DatabaseUtil.getDataSourceFromJndi(testName, context));
    }

    @Test
    public void testCaching() throws NamingException {
        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(context.lookup(testName)).thenReturn(dataSource).thenThrow(new NullPointerException());
        Assert.assertEquals(dataSource, DatabaseUtil.getDataSourceFromJndi(testName, context));
        Assert.assertEquals(dataSource, DatabaseUtil.getDataSourceFromJndi(testName, context));
    }
}

