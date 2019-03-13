/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.config.annotation.authentication.configurers.provisioning;


import java.util.Arrays;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


/**
 *
 *
 * @author Rob Winch
 * @author Adolfo Eloy
 */
public class UserDetailsManagerConfigurerTests {
    private InMemoryUserDetailsManager userDetailsManager;

    @Test
    public void allAttributesSupported() {
        UserDetails userDetails = new UserDetailsManagerConfigurer<org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder, InMemoryUserDetailsManagerConfigurer<org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder>>(userDetailsManager).withUser("user").password("password").roles("USER").disabled(true).accountExpired(true).accountLocked(true).credentialsExpired(true).build();
        assertThat(userDetails.getUsername()).isEqualTo("user");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.getAuthorities().stream().findFirst().get().getAuthority()).isEqualTo("ROLE_USER");
        assertThat(userDetails.isAccountNonExpired()).isFalse();
        assertThat(userDetails.isAccountNonLocked()).isFalse();
        assertThat(userDetails.isCredentialsNonExpired()).isFalse();
        assertThat(userDetails.isEnabled()).isFalse();
    }

    @Test
    public void authoritiesWithGrantedAuthorityWorks() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        UserDetails userDetails = new UserDetailsManagerConfigurer<org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder, InMemoryUserDetailsManagerConfigurer<org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder>>(userDetailsManager).withUser("user").password("password").authorities(authority).build();
        assertThat(userDetails.getAuthorities().stream().findFirst().get()).isEqualTo(authority);
    }

    @Test
    public void authoritiesWithStringAuthorityWorks() {
        String authority = "ROLE_USER";
        UserDetails userDetails = new UserDetailsManagerConfigurer<org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder, InMemoryUserDetailsManagerConfigurer<org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder>>(userDetailsManager).withUser("user").password("password").authorities(authority).build();
        assertThat(userDetails.getAuthorities().stream().findFirst().get().getAuthority()).isEqualTo(authority);
    }

    @Test
    public void authoritiesWithAListOfGrantedAuthorityWorks() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        UserDetails userDetails = new UserDetailsManagerConfigurer<org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder, InMemoryUserDetailsManagerConfigurer<org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder>>(userDetailsManager).withUser("user").password("password").authorities(Arrays.asList(authority)).build();
        assertThat(userDetails.getAuthorities().stream().findFirst().get()).isEqualTo(authority);
    }
}

