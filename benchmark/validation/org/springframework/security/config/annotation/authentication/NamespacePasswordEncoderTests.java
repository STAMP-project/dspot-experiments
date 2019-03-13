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
import javax.sql.DataSource;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;


/**
 *
 *
 * @author Rob Winch
 */
public class NamespacePasswordEncoderTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void passwordEncoderRefWithInMemory() throws Exception {
        this.spring.register(NamespacePasswordEncoderTests.PasswordEncoderWithInMemoryConfig.class).autowire();
        this.mockMvc.perform(formLogin()).andExpect(authenticated());
    }

    @EnableWebSecurity
    static class PasswordEncoderWithInMemoryConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            auth.inMemoryAuthentication().withUser("user").password(encoder.encode("password")).roles("USER").and().passwordEncoder(encoder);
        }
    }

    @Test
    public void passwordEncoderRefWithJdbc() throws Exception {
        this.spring.register(NamespacePasswordEncoderTests.PasswordEncoderWithJdbcConfig.class).autowire();
        this.mockMvc.perform(formLogin()).andExpect(authenticated());
    }

    @EnableWebSecurity
    static class PasswordEncoderWithJdbcConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            auth.jdbcAuthentication().withDefaultSchema().dataSource(dataSource()).withUser("user").password(encoder.encode("password")).roles("USER").and().passwordEncoder(encoder);
        }

        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
            return builder.setType(HSQL).build();
        }
    }

    @Test
    public void passwordEncoderRefWithUserDetailsService() throws Exception {
        this.spring.register(NamespacePasswordEncoderTests.PasswordEncoderWithUserDetailsServiceConfig.class).autowire();
        this.mockMvc.perform(formLogin()).andExpect(authenticated());
    }

    @EnableWebSecurity
    static class PasswordEncoderWithUserDetailsServiceConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            UserDetails user = User.withUsername("user").passwordEncoder(encoder::encode).password("password").roles("USER").build();
            InMemoryUserDetailsManager uds = new InMemoryUserDetailsManager(user);
            auth.userDetailsService(uds).passwordEncoder(encoder);
        }

        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
            return builder.setType(HSQL).build();
        }
    }
}

