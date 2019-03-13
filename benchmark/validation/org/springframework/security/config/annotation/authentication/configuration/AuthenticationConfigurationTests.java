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
package org.springframework.security.config.annotation.authentication.configuration;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.TestAuthentication;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.configuration.ObjectPostProcessorConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.config.users.AuthenticationTestConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.PasswordEncodedUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


public class AuthenticationConfigurationTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired(required = false)
    private AuthenticationConfigurationTests.Service service;

    @Test
    public void orderingAutowiredOnEnableGlobalMethodSecurity() {
        this.spring.register(AuthenticationTestConfiguration.class, AuthenticationConfigurationTests.GlobalMethodSecurityAutowiredConfig.class, AuthenticationConfigurationTests.ServicesConfig.class).autowire();
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("user", "password", "ROLE_USER"));
        this.service.run();
    }

    @EnableGlobalMethodSecurity(securedEnabled = true)
    static class GlobalMethodSecurityAutowiredConfig {}

    @Test
    public void orderingAutowiredOnEnableWebSecurity() {
        this.spring.register(AuthenticationTestConfiguration.class, AuthenticationConfigurationTests.WebSecurityConfig.class, AuthenticationConfigurationTests.GlobalMethodSecurityAutowiredConfig.class, AuthenticationConfigurationTests.ServicesConfig.class).autowire();
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("user", "password", "ROLE_USER"));
        this.service.run();
    }

    @EnableWebSecurity
    static class WebSecurityConfig {}

    @Test
    public void orderingAutowiredOnEnableWebMvcSecurity() {
        this.spring.register(AuthenticationTestConfiguration.class, AuthenticationConfigurationTests.WebMvcSecurityConfig.class, AuthenticationConfigurationTests.GlobalMethodSecurityAutowiredConfig.class, AuthenticationConfigurationTests.ServicesConfig.class).autowire();
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("user", "password", "ROLE_USER"));
        this.service.run();
    }

    @EnableWebMvcSecurity
    static class WebMvcSecurityConfig {}

    @Test
    public void getAuthenticationManagerWhenNoAuthenticationThenNull() throws Exception {
        this.spring.register(AuthenticationConfiguration.class, ObjectPostProcessorConfiguration.class).autowire();
        assertThat(this.spring.getContext().getBean(AuthenticationConfiguration.class).getAuthenticationManager()).isNull();
    }

    @Test
    public void getAuthenticationManagerWhenNoOpGlobalAuthenticationConfigurerAdapterThenNull() throws Exception {
        this.spring.register(AuthenticationConfiguration.class, ObjectPostProcessorConfiguration.class, AuthenticationConfigurationTests.NoOpGlobalAuthenticationConfigurerAdapter.class).autowire();
        assertThat(this.spring.getContext().getBean(AuthenticationConfiguration.class).getAuthenticationManager()).isNull();
    }

    @Configuration
    static class NoOpGlobalAuthenticationConfigurerAdapter extends GlobalAuthenticationConfigurerAdapter {}

    @Test
    public void getAuthenticationWhenGlobalAuthenticationConfigurerAdapterThenAuthenticates() throws Exception {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user", "password");
        this.spring.register(AuthenticationConfiguration.class, ObjectPostProcessorConfiguration.class, AuthenticationConfigurationTests.UserGlobalAuthenticationConfigurerAdapter.class).autowire();
        AuthenticationManager authentication = this.spring.getContext().getBean(AuthenticationConfiguration.class).getAuthenticationManager();
        assertThat(authentication.authenticate(token).getName()).isEqualTo(token.getName());
    }

    @Configuration
    static class UserGlobalAuthenticationConfigurerAdapter extends GlobalAuthenticationConfigurerAdapter {
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser(PasswordEncodedUser.user());
        }
    }

    @Test
    public void getAuthenticationWhenAuthenticationManagerBeanThenAuthenticates() throws Exception {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user", "password");
        this.spring.register(AuthenticationConfiguration.class, ObjectPostProcessorConfiguration.class, AuthenticationConfigurationTests.AuthenticationManagerBeanConfig.class).autowire();
        AuthenticationManager authentication = this.spring.getContext().getBean(AuthenticationConfiguration.class).getAuthenticationManager();
        Mockito.when(authentication.authenticate(token)).thenReturn(TestAuthentication.authenticatedUser());
        assertThat(authentication.authenticate(token).getName()).isEqualTo(token.getName());
    }

    @Configuration
    static class AuthenticationManagerBeanConfig {
        AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);

        @Bean
        public AuthenticationManager authenticationManager() {
            return this.authenticationManager;
        }
    }

    // 
    // //
    // 
    @Configuration
    static class ServicesConfig {
        @Bean
        public AuthenticationConfigurationTests.Service service() {
            return new AuthenticationConfigurationTests.ServiceImpl();
        }
    }

    interface Service {
        void run();
    }

    static class ServiceImpl implements AuthenticationConfigurationTests.Service {
        @Secured("ROLE_USER")
        public void run() {
        }
    }

    @Test
    public void getAuthenticationWhenMultipleThenOrdered() throws Exception {
        this.spring.register(AuthenticationConfiguration.class, ObjectPostProcessorConfiguration.class, AuthenticationConfigurationTests.AuthenticationManagerBeanConfig.class).autowire();
        AuthenticationConfiguration config = this.spring.getContext().getBean(AuthenticationConfiguration.class);
        config.setGlobalAuthenticationConfigurers(Arrays.asList(new AuthenticationConfigurationTests.LowestOrderGlobalAuthenticationConfigurerAdapter(), new AuthenticationConfigurationTests.HighestOrderGlobalAuthenticationConfigurerAdapter(), new AuthenticationConfigurationTests.DefaultOrderGlobalAuthenticationConfigurerAdapter()));
    }

    static class DefaultOrderGlobalAuthenticationConfigurerAdapter extends GlobalAuthenticationConfigurerAdapter {
        static List<Class<?>> inits = new ArrayList<>();

        static List<Class<?>> configs = new ArrayList<>();

        public void init(AuthenticationManagerBuilder auth) throws Exception {
            AuthenticationConfigurationTests.DefaultOrderGlobalAuthenticationConfigurerAdapter.inits.add(getClass());
        }

        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            AuthenticationConfigurationTests.DefaultOrderGlobalAuthenticationConfigurerAdapter.configs.add(getClass());
        }
    }

    @Order(Ordered.LOWEST_PRECEDENCE)
    static class LowestOrderGlobalAuthenticationConfigurerAdapter extends AuthenticationConfigurationTests.DefaultOrderGlobalAuthenticationConfigurerAdapter {}

    @Order(Ordered.HIGHEST_PRECEDENCE)
    static class HighestOrderGlobalAuthenticationConfigurerAdapter extends AuthenticationConfigurationTests.DefaultOrderGlobalAuthenticationConfigurerAdapter {}

    @Test
    public void getAuthenticationWhenConfiguredThenBootNotTrigger() throws Exception {
        this.spring.register(AuthenticationConfiguration.class, ObjectPostProcessorConfiguration.class).autowire();
        AuthenticationConfiguration config = this.spring.getContext().getBean(AuthenticationConfiguration.class);
        config.setGlobalAuthenticationConfigurers(Arrays.asList(new AuthenticationConfigurationTests.ConfiguresInMemoryConfigurerAdapter(), new AuthenticationConfigurationTests.BootGlobalAuthenticationConfigurerAdapter()));
        AuthenticationManager authenticationManager = config.getAuthenticationManager();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user", "password"));
        assertThatThrownBy(() -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("boot", "password"))).isInstanceOf(AuthenticationException.class);
    }

    @Test
    public void getAuthenticationWhenNotConfiguredThenBootTrigger() throws Exception {
        this.spring.register(AuthenticationConfiguration.class, ObjectPostProcessorConfiguration.class).autowire();
        AuthenticationConfiguration config = this.spring.getContext().getBean(AuthenticationConfiguration.class);
        config.setGlobalAuthenticationConfigurers(Arrays.asList(new AuthenticationConfigurationTests.BootGlobalAuthenticationConfigurerAdapter()));
        AuthenticationManager authenticationManager = config.getAuthenticationManager();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("boot", "password"));
    }

    static class ConfiguresInMemoryConfigurerAdapter extends GlobalAuthenticationConfigurerAdapter {
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser(PasswordEncodedUser.user());
        }
    }

    @Order(Ordered.LOWEST_PRECEDENCE)
    static class BootGlobalAuthenticationConfigurerAdapter extends AuthenticationConfigurationTests.DefaultOrderGlobalAuthenticationConfigurerAdapter {
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.apply(new AuthenticationConfigurationTests.DefaultBootGlobalAuthenticationConfigurerAdapter());
        }
    }

    static class DefaultBootGlobalAuthenticationConfigurerAdapter extends AuthenticationConfigurationTests.DefaultOrderGlobalAuthenticationConfigurerAdapter {
        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            if (auth.isConfigured()) {
                return;
            }
            UserDetails user = User.withUserDetails(PasswordEncodedUser.user()).username("boot").build();
            List<UserDetails> users = Arrays.asList(user);
            InMemoryUserDetailsManager inMemory = new InMemoryUserDetailsManager(users);
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setUserDetailsService(inMemory);
            auth.authenticationProvider(provider);
        }
    }

    // gh-2531
    @Test
    public void getAuthenticationManagerWhenPostProcessThenUsesBeanClassLoaderOnProxyFactoryBean() throws Exception {
        this.spring.register(AuthenticationConfigurationTests.Sec2531Config.class).autowire();
        ObjectPostProcessor<Object> opp = this.spring.getContext().getBean(ObjectPostProcessor.class);
        Mockito.when(opp.postProcess(ArgumentMatchers.any())).thenAnswer(( a) -> a.getArgument(0));
        AuthenticationConfiguration config = this.spring.getContext().getBean(AuthenticationConfiguration.class);
        config.getAuthenticationManager();
        Mockito.verify(opp).postProcess(ArgumentMatchers.any(ProxyFactoryBean.class));
    }

    @Configuration
    @Import(AuthenticationConfiguration.class)
    static class Sec2531Config {
        @Bean
        public ObjectPostProcessor objectPostProcessor() {
            return Mockito.mock(ObjectPostProcessor.class);
        }

        @Bean
        public AuthenticationManager manager() {
            return null;
        }
    }

    @Test
    public void getAuthenticationManagerWhenSec2822ThenCannotForceAuthenticationAlreadyBuilt() throws Exception {
        this.spring.register(AuthenticationConfigurationTests.Sec2822WebSecurity.class, AuthenticationConfigurationTests.Sec2822UseAuth.class, AuthenticationConfigurationTests.Sec2822Config.class).autowire();
        this.spring.getContext().getBean(AuthenticationConfiguration.class).getAuthenticationManager();
        // no exception
    }

    @Configuration
    @Import(AuthenticationConfiguration.class)
    static class Sec2822Config {}

    @Configuration
    @EnableWebSecurity
    static class Sec2822WebSecurity extends WebSecurityConfigurerAdapter {
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication();
        }
    }

    @Configuration
    static class Sec2822UseAuth {
        @Autowired
        public void useAuthenticationManager(AuthenticationConfiguration auth) throws Exception {
            auth.getAuthenticationManager();
        }

        // Ensures that Sec2822UseAuth is initialized before Sec2822WebSecurity
        // must have additional GlobalAuthenticationConfigurerAdapter to trigger SEC-2822
        @Bean
        public static GlobalAuthenticationConfigurerAdapter bootGlobalAuthenticationConfigurerAdapter() {
            return new AuthenticationConfigurationTests.Sec2822UseAuth.BootGlobalAuthenticationConfigurerAdapter();
        }

        static class BootGlobalAuthenticationConfigurerAdapter extends GlobalAuthenticationConfigurerAdapter {}
    }

    // sec-2868
    @Test
    public void getAuthenticationWhenUserDetailsServiceBeanThenAuthenticationManagerUsesUserDetailsServiceBean() throws Exception {
        this.spring.register(AuthenticationConfigurationTests.UserDetailsServiceBeanConfig.class).autowire();
        UserDetailsService uds = this.spring.getContext().getBean(UserDetailsService.class);
        AuthenticationManager am = this.spring.getContext().getBean(AuthenticationConfiguration.class).getAuthenticationManager();
        Mockito.when(uds.loadUserByUsername("user")).thenReturn(PasswordEncodedUser.user(), PasswordEncodedUser.user());
        am.authenticate(new UsernamePasswordAuthenticationToken("user", "password"));
        assertThatThrownBy(() -> am.authenticate(new UsernamePasswordAuthenticationToken("user", "invalid"))).isInstanceOf(AuthenticationException.class);
    }

    @Configuration
    @Import({ AuthenticationConfiguration.class, ObjectPostProcessorConfiguration.class })
    static class UserDetailsServiceBeanConfig {
        UserDetailsService uds = Mockito.mock(UserDetailsService.class);

        @Bean
        UserDetailsService userDetailsService() {
            return this.uds;
        }
    }

    @Test
    public void getAuthenticationWhenUserDetailsServiceAndPasswordEncoderBeanThenEncoderUsed() throws Exception {
        UserDetails user = new User("user", "$2a$10$FBAKClV1zBIOOC9XMXf3AO8RoGXYVYsfvUdoLxGkd/BnXEn4tqT3u", AuthorityUtils.createAuthorityList("ROLE_USER"));
        this.spring.register(AuthenticationConfigurationTests.UserDetailsServiceBeanWithPasswordEncoderConfig.class).autowire();
        UserDetailsService uds = this.spring.getContext().getBean(UserDetailsService.class);
        AuthenticationManager am = this.spring.getContext().getBean(AuthenticationConfiguration.class).getAuthenticationManager();
        Mockito.when(uds.loadUserByUsername("user")).thenReturn(User.withUserDetails(user).build(), User.withUserDetails(user).build());
        am.authenticate(new UsernamePasswordAuthenticationToken("user", "password"));
        assertThatThrownBy(() -> am.authenticate(new UsernamePasswordAuthenticationToken("user", "invalid"))).isInstanceOf(AuthenticationException.class);
    }

    @Configuration
    @Import({ AuthenticationConfiguration.class, ObjectPostProcessorConfiguration.class })
    static class UserDetailsServiceBeanWithPasswordEncoderConfig {
        UserDetailsService uds = Mockito.mock(UserDetailsService.class);

        @Bean
        UserDetailsService userDetailsService() {
            return this.uds;
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Test
    public void getAuthenticationWhenUserDetailsServiceAndPasswordManagerThenManagerUsed() throws Exception {
        UserDetails user = new User("user", "{noop}password", AuthorityUtils.createAuthorityList("ROLE_USER"));
        this.spring.register(AuthenticationConfigurationTests.UserDetailsPasswordManagerBeanConfig.class).autowire();
        AuthenticationConfigurationTests.UserDetailsPasswordManagerBeanConfig.Manager manager = this.spring.getContext().getBean(AuthenticationConfigurationTests.UserDetailsPasswordManagerBeanConfig.Manager.class);
        AuthenticationManager am = this.spring.getContext().getBean(AuthenticationConfiguration.class).getAuthenticationManager();
        Mockito.when(loadUserByUsername("user")).thenReturn(User.withUserDetails(user).build(), User.withUserDetails(user).build());
        Mockito.when(updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(user);
        am.authenticate(new UsernamePasswordAuthenticationToken("user", "password"));
        Mockito.verify(manager).updatePassword(ArgumentMatchers.eq(user), ArgumentMatchers.startsWith("{bcrypt}"));
    }

    @Configuration
    @Import({ AuthenticationConfiguration.class, ObjectPostProcessorConfiguration.class })
    static class UserDetailsPasswordManagerBeanConfig {
        AuthenticationConfigurationTests.UserDetailsPasswordManagerBeanConfig.Manager manager = Mockito.mock(AuthenticationConfigurationTests.UserDetailsPasswordManagerBeanConfig.Manager.class);

        @Bean
        UserDetailsService userDetailsService() {
            return this.manager;
        }

        interface Manager extends UserDetailsPasswordService , UserDetailsService {}
    }

    // gh-3091
    @Test
    public void getAuthenticationWhenAuthenticationProviderBeanThenUsed() throws Exception {
        this.spring.register(AuthenticationConfigurationTests.AuthenticationProviderBeanConfig.class).autowire();
        AuthenticationProvider ap = this.spring.getContext().getBean(AuthenticationProvider.class);
        AuthenticationManager am = this.spring.getContext().getBean(AuthenticationConfiguration.class).getAuthenticationManager();
        Mockito.when(ap.supports(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(ap.authenticate(ArgumentMatchers.any())).thenReturn(TestAuthentication.authenticatedUser());
        am.authenticate(new UsernamePasswordAuthenticationToken("user", "password"));
    }

    @Configuration
    @Import({ AuthenticationConfiguration.class, ObjectPostProcessorConfiguration.class })
    static class AuthenticationProviderBeanConfig {
        AuthenticationProvider provider = Mockito.mock(AuthenticationProvider.class);

        @Bean
        AuthenticationProvider authenticationProvider() {
            return this.provider;
        }
    }

    @Test
    public void getAuthenticationWhenAuthenticationProviderAndUserDetailsBeanThenAuthenticationProviderUsed() throws Exception {
        this.spring.register(AuthenticationConfigurationTests.AuthenticationProviderBeanAndUserDetailsServiceConfig.class).autowire();
        AuthenticationProvider ap = this.spring.getContext().getBean(AuthenticationProvider.class);
        AuthenticationManager am = this.spring.getContext().getBean(AuthenticationConfiguration.class).getAuthenticationManager();
        Mockito.when(ap.supports(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(ap.authenticate(ArgumentMatchers.any())).thenReturn(TestAuthentication.authenticatedUser());
        am.authenticate(new UsernamePasswordAuthenticationToken("user", "password"));
    }

    @Configuration
    @Import({ AuthenticationConfiguration.class, ObjectPostProcessorConfiguration.class })
    static class AuthenticationProviderBeanAndUserDetailsServiceConfig {
        AuthenticationProvider provider = Mockito.mock(AuthenticationProvider.class);

        UserDetailsService uds = Mockito.mock(UserDetailsService.class);

        @Bean
        UserDetailsService userDetailsService() {
            return this.uds;
        }

        @Bean
        AuthenticationProvider authenticationProvider() {
            return this.provider;
        }
    }

    @Test
    public void enableGlobalMethodSecurityWhenPreAuthorizeThenNoException() throws Exception {
        this.spring.register(AuthenticationConfigurationTests.UsesPreAuthorizeMethodSecurityConfig.class, AuthenticationConfigurationTests.AuthenticationManagerBeanConfig.class).autowire();
        // no exception
    }

    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    static class UsesPreAuthorizeMethodSecurityConfig {
        @PreAuthorize("denyAll")
        void run() {
        }
    }

    @Test
    public void enableGlobalMethodSecurityWhenPreAuthorizeThenUsesMethodSecurityService() throws Exception {
        this.spring.register(AuthenticationConfigurationTests.ServicesConfig.class, AuthenticationConfigurationTests.UsesPreAuthorizeMethodSecurityConfig.class, AuthenticationConfigurationTests.AuthenticationManagerBeanConfig.class).autowire();
        // no exception
    }

    @Configuration
    @EnableGlobalMethodSecurity(securedEnabled = true)
    static class UsesServiceMethodSecurityConfig {
        @Autowired
        AuthenticationConfigurationTests.Service service;
    }

    @Test
    public void getAuthenticationManagerBeanWhenMultipleDefinedAndOnePrimaryThenNoException() throws Exception {
        this.spring.register(AuthenticationConfigurationTests.MultipleAuthenticationManagerBeanConfig.class).autowire();
        this.spring.getContext().getBeanFactory().getBean(AuthenticationConfiguration.class).getAuthenticationManager();
    }

    @Configuration
    @Import(AuthenticationConfiguration.class)
    static class MultipleAuthenticationManagerBeanConfig {
        @Bean
        @Primary
        public AuthenticationManager manager1() {
            return Mockito.mock(AuthenticationManager.class);
        }

        @Bean
        public AuthenticationManager manager2() {
            return Mockito.mock(AuthenticationManager.class);
        }
    }
}

