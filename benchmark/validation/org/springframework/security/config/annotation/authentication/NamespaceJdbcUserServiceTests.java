/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.config.annotation.authentication;


import EmbeddedDatabaseType.HSQL;
import JdbcUserDetailsManager.DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY;
import javax.sql.DataSource;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;


/**
 *
 *
 * @author Rob Winch
 */
public class NamespaceJdbcUserServiceTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void jdbcUserService() throws Exception {
        this.spring.register(NamespaceJdbcUserServiceTests.DataSourceConfig.class, NamespaceJdbcUserServiceTests.JdbcUserServiceConfig.class).autowire();
        this.mockMvc.perform(formLogin()).andExpect(authenticated().withUsername("user"));
    }

    @EnableWebSecurity
    static class JdbcUserServiceConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        private DataSource dataSource;

        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.jdbcAuthentication().withDefaultSchema().withUser(org.springframework.security.core.userdetails.PasswordEncodedUser.user()).dataSource(this.dataSource);// jdbc-user-service@data-source-ref

        }
    }

    @Configuration
    static class DataSourceConfig {
        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
            return builder.setType(HSQL).build();
        }
    }

    @Test
    public void jdbcUserServiceCustom() throws Exception {
        this.spring.register(NamespaceJdbcUserServiceTests.CustomDataSourceConfig.class, NamespaceJdbcUserServiceTests.CustomJdbcUserServiceSampleConfig.class).autowire();
        this.mockMvc.perform(formLogin()).andExpect(authenticated().withUsername("user").withRoles("DBA", "USER"));
    }

    @EnableWebSecurity
    static class CustomJdbcUserServiceSampleConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        private DataSource dataSource;

        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            // jdbc-user-service@role-prefix
            // jdbc-user-service@group-authorities-by-username-query
            // jdbc-user-service@authorities-by-username-query
            // jdbc-user-service@users-byusername-query
            // jdbc-user-service@cache-ref
            // jdbc-user-service@dataSource
            auth.jdbcAuthentication().dataSource(this.dataSource).userCache(new NamespaceJdbcUserServiceTests.CustomJdbcUserServiceSampleConfig.CustomUserCache()).usersByUsernameQuery("select principal,credentials,true from users where principal = ?").authoritiesByUsernameQuery("select principal,role from roles where principal = ?").groupAuthoritiesByUsername(DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY).rolePrefix("ROLE_");
        }

        static class CustomUserCache implements UserCache {
            @Override
            public UserDetails getUserFromCache(String username) {
                return null;
            }

            @Override
            public void putUserInCache(UserDetails user) {
            }

            @Override
            public void removeUserFromCache(String username) {
            }
        }
    }

    @Configuration
    static class CustomDataSourceConfig {
        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder builder = // simulate that the DB already has the schema loaded and users in it
            new EmbeddedDatabaseBuilder().addScript("CustomJdbcUserServiceSampleConfig.sql");
            return builder.setType(HSQL).build();
        }
    }
}

