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
package org.springframework.security.test.context.support;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import reactor.core.publisher.Mono;


@RunWith(MockitoJUnitRunner.class)
public class WithUserDetailsSecurityContextFactoryTests {
    @Mock
    private ReactiveUserDetailsService reactiveUserDetailsService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private BeanFactory beans;

    @Mock
    private WithUserDetails withUserDetails;

    private WithUserDetailsSecurityContextFactory factory;

    @Test(expected = IllegalArgumentException.class)
    public void createSecurityContextNullValue() {
        factory.createSecurityContext(withUserDetails);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSecurityContextEmptyValue() {
        Mockito.when(withUserDetails.value()).thenReturn("");
        factory.createSecurityContext(withUserDetails);
    }

    @Test
    public void createSecurityContextWithExistingUser() {
        String username = "user";
        Mockito.when(this.beans.getBean(ReactiveUserDetailsService.class)).thenThrow(new NoSuchBeanDefinitionException(""));
        Mockito.when(beans.getBean(UserDetailsService.class)).thenReturn(userDetailsService);
        Mockito.when(withUserDetails.value()).thenReturn(username);
        Mockito.when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        SecurityContext context = factory.createSecurityContext(withUserDetails);
        assertThat(context.getAuthentication()).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(context.getAuthentication().getPrincipal()).isEqualTo(userDetails);
        Mockito.verify(beans).getBean(UserDetailsService.class);
    }

    // gh-3346
    @Test
    public void createSecurityContextWithUserDetailsServiceName() {
        String beanName = "secondUserDetailsServiceBean";
        String username = "user";
        Mockito.when(this.beans.getBean(beanName, ReactiveUserDetailsService.class)).thenThrow(new BeanNotOfRequiredTypeException("", ReactiveUserDetailsService.class, UserDetailsService.class));
        Mockito.when(withUserDetails.value()).thenReturn(username);
        Mockito.when(withUserDetails.userDetailsServiceBeanName()).thenReturn(beanName);
        Mockito.when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        Mockito.when(beans.getBean(beanName, UserDetailsService.class)).thenReturn(userDetailsService);
        SecurityContext context = factory.createSecurityContext(withUserDetails);
        assertThat(context.getAuthentication()).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(context.getAuthentication().getPrincipal()).isEqualTo(userDetails);
        Mockito.verify(beans).getBean(beanName, UserDetailsService.class);
    }

    @Test
    public void createSecurityContextWithReactiveUserDetailsService() {
        String username = "user";
        Mockito.when(withUserDetails.value()).thenReturn(username);
        Mockito.when(this.beans.getBean(ReactiveUserDetailsService.class)).thenReturn(this.reactiveUserDetailsService);
        Mockito.when(this.reactiveUserDetailsService.findByUsername(username)).thenReturn(Mono.just(userDetails));
        SecurityContext context = factory.createSecurityContext(withUserDetails);
        assertThat(context.getAuthentication()).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(context.getAuthentication().getPrincipal()).isEqualTo(userDetails);
        Mockito.verify(this.beans).getBean(ReactiveUserDetailsService.class);
    }

    @Test
    public void createSecurityContextWithReactiveUserDetailsServiceAndBeanName() {
        String beanName = "secondUserDetailsServiceBean";
        String username = "user";
        Mockito.when(withUserDetails.value()).thenReturn(username);
        Mockito.when(withUserDetails.userDetailsServiceBeanName()).thenReturn(beanName);
        Mockito.when(this.beans.getBean(beanName, ReactiveUserDetailsService.class)).thenReturn(this.reactiveUserDetailsService);
        Mockito.when(this.reactiveUserDetailsService.findByUsername(username)).thenReturn(Mono.just(userDetails));
        SecurityContext context = factory.createSecurityContext(withUserDetails);
        assertThat(context.getAuthentication()).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(context.getAuthentication().getPrincipal()).isEqualTo(userDetails);
        Mockito.verify(this.beans).getBean(beanName, ReactiveUserDetailsService.class);
    }
}

