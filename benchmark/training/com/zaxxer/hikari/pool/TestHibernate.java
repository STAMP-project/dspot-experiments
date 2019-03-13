/**
 * Copyright (C) 2013, 2014 Brett Wooldridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zaxxer.hikari.pool;


import com.zaxxer.hikari.hibernate.HikariConnectionProvider;
import java.sql.Connection;
import java.util.Properties;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.junit.Assert;
import org.junit.Test;


public class TestHibernate {
    @Test
    public void testConnectionProvider() throws Exception {
        HikariConnectionProvider provider = new HikariConnectionProvider();
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/hibernate.properties"));
        provider.configure(props);
        Connection connection = provider.getConnection();
        provider.closeConnection(connection);
        Assert.assertNotNull(provider.unwrap(HikariConnectionProvider.class));
        Assert.assertFalse(provider.supportsAggressiveRelease());
        try {
            provider.unwrap(TestHibernate.class);
            Assert.fail("Expected exception");
        } catch (UnknownUnwrapTypeException e) {
        }
        provider.stop();
    }
}

