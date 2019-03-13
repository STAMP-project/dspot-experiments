/**
 * Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.datasource.jndi;


import JndiDataSourceFactory.DATA_SOURCE;
import JndiDataSourceFactory.INITIAL_CONTEXT;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.sql.DataSource;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.junit.Assert;
import org.junit.Test;

import static JndiDataSourceFactory.ENV_PREFIX;


public class JndiDataSourceFactoryTest extends BaseDataTest {
    private static final String TEST_INITIAL_CONTEXT_FACTORY = JndiDataSourceFactoryTest.MockContextFactory.class.getName();

    private static final String TEST_INITIAL_CONTEXT = "/mypath/path/";

    private static final String TEST_DATA_SOURCE = "myDataSource";

    private UnpooledDataSource expectedDataSource;

    @Test
    public void shouldRetrieveDataSourceFromJNDI() throws Exception {
        createJndiDataSource();
        JndiDataSourceFactory factory = new JndiDataSourceFactory();
        factory.setProperties(new Properties() {
            {
                setProperty(((ENV_PREFIX) + (Context.INITIAL_CONTEXT_FACTORY)), JndiDataSourceFactoryTest.TEST_INITIAL_CONTEXT_FACTORY);
                setProperty(INITIAL_CONTEXT, JndiDataSourceFactoryTest.TEST_INITIAL_CONTEXT);
                setProperty(DATA_SOURCE, JndiDataSourceFactoryTest.TEST_DATA_SOURCE);
            }
        });
        DataSource actualDataSource = factory.getDataSource();
        Assert.assertEquals(expectedDataSource, actualDataSource);
    }

    public static class MockContextFactory implements InitialContextFactory {
        public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
            return new JndiDataSourceFactoryTest.MockContext(false);
        }
    }

    public static class MockContext extends InitialContext {
        private static Map<String, Object> bindings = new HashMap<String, Object>();

        public MockContext(boolean lazy) throws NamingException {
            super(lazy);
        }

        public Object lookup(String name) throws NamingException {
            return JndiDataSourceFactoryTest.MockContext.bindings.get(name);
        }

        public void bind(String name, Object obj) throws NamingException {
            JndiDataSourceFactoryTest.MockContext.bindings.put(name, obj);
        }
    }
}

