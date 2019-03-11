/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.jndi;


import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.ActiveMQXASslConnectionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ActiveMQSslInitialContextFactoryTest {
    protected Context context;

    protected boolean isXa;

    /**
     *
     *
     * @param isXa
     * 		
     */
    public ActiveMQSslInitialContextFactoryTest(boolean isXa) {
        super();
        this.isXa = isXa;
    }

    @Test
    public void testCreateXaConnectionFactory() throws NamingException {
        ActiveMQConnectionFactory factory = ((ActiveMQConnectionFactory) (context.lookup("ConnectionFactory")));
        Assert.assertTrue((factory instanceof ActiveMQSslConnectionFactory));
        if (isXa) {
            Assert.assertTrue((factory instanceof ActiveMQXASslConnectionFactory));
        } else {
            Assert.assertFalse((factory instanceof ActiveMQXASslConnectionFactory));
        }
    }

    @Test
    public void testAssertConnectionFactoryProperties() throws NamingException {
        Object c = context.lookup("ConnectionFactory");
        if (c instanceof ActiveMQSslConnectionFactory) {
            ActiveMQSslConnectionFactory factory = ((ActiveMQSslConnectionFactory) (c));
            Assert.assertEquals(factory.getKeyStore(), "keystore.jks");
            Assert.assertEquals(factory.getKeyStorePassword(), "test");
            Assert.assertEquals(factory.getKeyStoreType(), "JKS");
            Assert.assertEquals(factory.getTrustStore(), "truststore.jks");
            Assert.assertEquals(factory.getTrustStorePassword(), "test");
            Assert.assertEquals(factory.getTrustStoreType(), "JKS");
        } else {
            Assert.fail("Did not find an ActiveMQSslConnectionFactory");
        }
    }
}

