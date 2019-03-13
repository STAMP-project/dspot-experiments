/**
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.session.data.redis.config.annotation.web.http;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration.EnableRedisKeyspaceNotificationsInitializer;


@RunWith(MockitoJUnitRunner.class)
public class RedisHttpSessionConfigurationMockTests {
    @Mock
    RedisConnectionFactory factory;

    @Mock
    RedisConnection connection;

    @Test
    public void enableRedisKeyspaceNotificationsInitializerAfterPropertiesSetWhenNoOpThenNoInteractionWithConnectionFactory() throws Exception {
        EnableRedisKeyspaceNotificationsInitializer init = new EnableRedisKeyspaceNotificationsInitializer(this.factory, ConfigureRedisAction.NO_OP);
        init.afterPropertiesSet();
        Mockito.verifyZeroInteractions(this.factory);
    }

    @Test
    public void enableRedisKeyspaceNotificationsInitializerAfterPropertiesSetWhenExceptionThenCloseConnection() throws Exception {
        ConfigureRedisAction action = Mockito.mock(ConfigureRedisAction.class);
        BDDMockito.willThrow(new RuntimeException()).given(action).configure(this.connection);
        EnableRedisKeyspaceNotificationsInitializer init = new EnableRedisKeyspaceNotificationsInitializer(this.factory, action);
        try {
            init.afterPropertiesSet();
            failBecauseExceptionWasNotThrown(Throwable.class);
        } catch (Throwable success) {
        }
        Mockito.verify(this.connection).close();
    }

    @Test
    public void enableRedisKeyspaceNotificationsInitializerAfterPropertiesSetWhenNoExceptionThenCloseConnection() throws Exception {
        ConfigureRedisAction action = Mockito.mock(ConfigureRedisAction.class);
        EnableRedisKeyspaceNotificationsInitializer init = new EnableRedisKeyspaceNotificationsInitializer(this.factory, action);
        init.afterPropertiesSet();
        Mockito.verify(this.connection).close();
    }
}

