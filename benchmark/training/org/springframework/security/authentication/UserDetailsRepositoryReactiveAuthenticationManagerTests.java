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
package org.springframework.security.authentication;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
@RunWith(MockitoJUnitRunner.class)
public class UserDetailsRepositoryReactiveAuthenticationManagerTests {
    @Mock
    private ReactiveUserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private ReactiveUserDetailsPasswordService userDetailsPasswordService;

    @Mock
    private Scheduler scheduler;

    @Mock
    private UserDetailsChecker postAuthenticationChecks;

    private UserDetails user = User.withUsername("user").password("password").roles("USER").build();

    private UserDetailsRepositoryReactiveAuthenticationManager manager;

    @Test
    public void setSchedulerWhenNullThenIllegalArgumentException() {
        assertThatCode(() -> this.manager.setScheduler(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void authentiateWhenCustomSchedulerThenUsed() {
        Mockito.when(this.userDetailsService.findByUsername(ArgumentMatchers.any())).thenReturn(Mono.just(this.user));
        Mockito.when(this.encoder.matches(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
        this.manager.setScheduler(this.scheduler);
        this.manager.setPasswordEncoder(this.encoder);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(this.user, this.user.getPassword());
        Authentication result = this.manager.authenticate(token).block();
        Mockito.verify(this.scheduler).schedule(ArgumentMatchers.any());
    }

    @Test
    public void authenticateWhenPasswordServiceThenUpdated() {
        String encodedPassword = "encoded";
        Mockito.when(this.userDetailsService.findByUsername(ArgumentMatchers.any())).thenReturn(Mono.just(this.user));
        Mockito.when(this.encoder.matches(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(this.encoder.upgradeEncoding(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(this.encoder.encode(ArgumentMatchers.any())).thenReturn(encodedPassword);
        Mockito.when(this.userDetailsPasswordService.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.just(this.user));
        this.manager.setPasswordEncoder(this.encoder);
        this.manager.setUserDetailsPasswordService(this.userDetailsPasswordService);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(this.user, this.user.getPassword());
        Authentication result = this.manager.authenticate(token).block();
        Mockito.verify(this.encoder).encode(this.user.getPassword());
        Mockito.verify(this.userDetailsPasswordService).updatePassword(ArgumentMatchers.eq(this.user), ArgumentMatchers.eq(encodedPassword));
    }

    @Test
    public void authenticateWhenPasswordServiceAndBadCredentialsThenNotUpdated() {
        Mockito.when(this.userDetailsService.findByUsername(ArgumentMatchers.any())).thenReturn(Mono.just(this.user));
        Mockito.when(this.encoder.matches(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(false);
        this.manager.setPasswordEncoder(this.encoder);
        this.manager.setUserDetailsPasswordService(this.userDetailsPasswordService);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(this.user, this.user.getPassword());
        assertThatThrownBy(() -> this.manager.authenticate(token).block()).isInstanceOf(BadCredentialsException.class);
        Mockito.verifyZeroInteractions(this.userDetailsPasswordService);
    }

    @Test
    public void authenticateWhenPasswordServiceAndUpgradeFalseThenNotUpdated() {
        Mockito.when(this.userDetailsService.findByUsername(ArgumentMatchers.any())).thenReturn(Mono.just(this.user));
        Mockito.when(this.encoder.matches(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(this.encoder.upgradeEncoding(ArgumentMatchers.any())).thenReturn(false);
        this.manager.setPasswordEncoder(this.encoder);
        this.manager.setUserDetailsPasswordService(this.userDetailsPasswordService);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(this.user, this.user.getPassword());
        Authentication result = this.manager.authenticate(token).block();
        Mockito.verifyZeroInteractions(this.userDetailsPasswordService);
    }

    @Test
    public void authenticateWhenPostAuthenticationChecksFail() {
        Mockito.when(this.userDetailsService.findByUsername(ArgumentMatchers.any())).thenReturn(Mono.just(this.user));
        Mockito.doThrow(new LockedException("account is locked")).when(this.postAuthenticationChecks).check(ArgumentMatchers.any());
        Mockito.when(this.encoder.matches(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
        this.manager.setPasswordEncoder(this.encoder);
        this.manager.setPostAuthenticationChecks(this.postAuthenticationChecks);
        assertThatExceptionOfType(LockedException.class).isThrownBy(() -> this.manager.authenticate(new UsernamePasswordAuthenticationToken(this.user, this.user.getPassword())).block()).withMessage("account is locked");
        Mockito.verify(this.postAuthenticationChecks).check(ArgumentMatchers.eq(this.user));
    }

    @Test
    public void authenticateWhenPostAuthenticationChecksNotSet() {
        Mockito.when(this.userDetailsService.findByUsername(ArgumentMatchers.any())).thenReturn(Mono.just(this.user));
        Mockito.when(this.encoder.matches(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
        this.manager.setPasswordEncoder(this.encoder);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(this.user, this.user.getPassword());
        this.manager.authenticate(token).block();
        Mockito.verifyZeroInteractions(this.postAuthenticationChecks);
    }
}

