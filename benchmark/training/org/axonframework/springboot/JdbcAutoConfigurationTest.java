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
package org.axonframework.springboot;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jdbc.JdbcTokenStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.axonframework.modelling.saga.repository.jdbc.JdbcSagaStore;
import org.axonframework.springboot.autoconfig.AxonServerAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Tests JDBC auto-configuration.
 *
 * @author Milan Savic
 */
@ContextConfiguration(classes = JdbcAutoConfigurationTest.Context.class)
@EnableAutoConfiguration(exclude = { JpaRepositoriesAutoConfiguration.class, HibernateJpaAutoConfiguration.class, AxonServerAutoConfiguration.class })
@RunWith(SpringRunner.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class JdbcAutoConfigurationTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testContextInitialization() {
        Assert.assertNotNull(applicationContext);
        Assert.assertTrue(((applicationContext.getBean(EventStorageEngine.class)) instanceof JdbcEventStorageEngine));
        Assert.assertTrue(((applicationContext.getBean(TokenStore.class)) instanceof JdbcTokenStore));
        Assert.assertTrue(((applicationContext.getBean(SagaStore.class)) instanceof JdbcSagaStore));
    }

    @Configuration
    public static class Context {
        @Bean
        public DataSource dataSource() throws SQLException {
            DatabaseMetaData databaseMetaData = Mockito.mock(DatabaseMetaData.class);
            Mockito.when(databaseMetaData.getDatabaseProductName()).thenReturn("H2");
            Connection connection = Mockito.mock(Connection.class);
            Mockito.when(connection.getMetaData()).thenReturn(databaseMetaData);
            DataSource dataSource = Mockito.mock(DataSource.class);
            Mockito.when(dataSource.getConnection()).thenReturn(connection);
            return dataSource;
        }
    }
}

