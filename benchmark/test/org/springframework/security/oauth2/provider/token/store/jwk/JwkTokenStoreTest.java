/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.security.oauth2.provider.token.store.jwk;


import JwkDefinition.CryptoAlgorithm.RS256;
import JwkDefinitionSource.JwkDefinitionHolder;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.ReflectionUtils;


/**
 *
 *
 * @author Joe Grandja
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(JwkTokenStore.class)
public class JwkTokenStoreTest {
    private JwkTokenStore jwkTokenStore = new JwkTokenStore("https://identity.server1.io/token_keys");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void readAuthenticationUsingOAuth2AccessTokenWhenCalledThenDelegateCalled() throws Exception {
        JwkTokenStore spy = Mockito.spy(this.jwkTokenStore);
        JwtTokenStore delegate = Mockito.mock(JwtTokenStore.class);
        Mockito.when(delegate.readAuthentication(ArgumentMatchers.any(OAuth2AccessToken.class))).thenReturn(null);
        Field field = ReflectionUtils.findField(spy.getClass(), "delegate");
        field.setAccessible(true);
        ReflectionUtils.setField(field, spy, delegate);
        spy.readAuthentication(Mockito.mock(OAuth2AccessToken.class));
        Mockito.verify(delegate).readAuthentication(ArgumentMatchers.any(OAuth2AccessToken.class));
    }

    @Test
    public void readAuthenticationUsingAccessTokenStringWhenCalledThenDelegateCalled() throws Exception {
        JwkTokenStore spy = Mockito.spy(this.jwkTokenStore);
        JwtTokenStore delegate = Mockito.mock(JwtTokenStore.class);
        Mockito.when(delegate.readAuthentication(ArgumentMatchers.anyString())).thenReturn(null);
        Field field = ReflectionUtils.findField(spy.getClass(), "delegate");
        field.setAccessible(true);
        ReflectionUtils.setField(field, spy, delegate);
        spy.readAuthentication(ArgumentMatchers.anyString());
        Mockito.verify(delegate).readAuthentication(ArgumentMatchers.anyString());
    }

    // gh-1015
    @Test
    public void readAuthenticationUsingCustomAccessTokenConverterThenAuthenticationDetailsContainsClaims() throws Exception {
        AccessTokenConverter customAccessTokenConverter = Mockito.mock(AccessTokenConverter.class);
        Mockito.when(customAccessTokenConverter.extractAuthentication(ArgumentMatchers.anyMapOf(String.class, String.class))).thenAnswer(new Answer<OAuth2Authentication>() {
            @Override
            public OAuth2Authentication answer(InvocationOnMock invocation) throws Throwable {
                Map<String, String> claims = ((Map<String, String>) (invocation.getArguments()[0]));
                OAuth2Authentication authentication = new OAuth2Authentication(Mockito.mock(OAuth2Request.class), null);
                authentication.setDetails(claims);
                return authentication;
            }
        });
        JwkVerifyingJwtAccessTokenConverter jwtVerifyingAccessTokenConverter = new JwkVerifyingJwtAccessTokenConverter(Mockito.mock(JwkDefinitionSource.class));
        jwtVerifyingAccessTokenConverter = Mockito.spy(jwtVerifyingAccessTokenConverter);
        jwtVerifyingAccessTokenConverter.setAccessTokenConverter(customAccessTokenConverter);
        Map<String, String> claims = new LinkedHashMap<String, String>();
        claims.put("claim1", "value1");
        claims.put("claim2", "value2");
        claims.put("claim3", "value3");
        Mockito.doReturn(claims).when(jwtVerifyingAccessTokenConverter).decode(ArgumentMatchers.anyString());
        JwkTokenStore spy = Mockito.spy(this.jwkTokenStore);
        JwtTokenStore delegate = new JwtTokenStore(jwtVerifyingAccessTokenConverter);
        Field field = ReflectionUtils.findField(spy.getClass(), "delegate");
        field.setAccessible(true);
        ReflectionUtils.setField(field, spy, delegate);
        OAuth2Authentication authentication = spy.readAuthentication(ArgumentMatchers.anyString());
        Assert.assertEquals(claims, authentication.getDetails());
    }

    // gh-1111
    @Test
    public void readAccessTokenWhenJwtClaimsSetVerifierIsSetThenVerifyIsCalled() throws Exception {
        JwkDefinition jwkDefinition = Mockito.mock(JwkDefinition.class);
        Mockito.when(jwkDefinition.getAlgorithm()).thenReturn(RS256);
        JwkDefinitionSource.JwkDefinitionHolder jwkDefinitionHolder = Mockito.mock(JwkDefinitionHolder.class);
        Mockito.when(jwkDefinitionHolder.getJwkDefinition()).thenReturn(jwkDefinition);
        Mockito.when(jwkDefinitionHolder.getSignatureVerifier()).thenReturn(Mockito.mock(SignatureVerifier.class));
        JwkDefinitionSource jwkDefinitionSource = Mockito.mock(JwkDefinitionSource.class);
        Mockito.when(jwkDefinitionSource.getDefinitionLoadIfNecessary(ArgumentMatchers.anyString())).thenReturn(jwkDefinitionHolder);
        JwkVerifyingJwtAccessTokenConverter jwtVerifyingAccessTokenConverter = new JwkVerifyingJwtAccessTokenConverter(jwkDefinitionSource);
        JwtClaimsSetVerifier jwtClaimsSetVerifier = Mockito.mock(JwtClaimsSetVerifier.class);
        jwtVerifyingAccessTokenConverter.setJwtClaimsSetVerifier(jwtClaimsSetVerifier);
        JwkTokenStore spy = Mockito.spy(this.jwkTokenStore);
        JwtTokenStore delegate = new JwtTokenStore(jwtVerifyingAccessTokenConverter);
        Field field = ReflectionUtils.findField(spy.getClass(), "delegate");
        field.setAccessible(true);
        ReflectionUtils.setField(field, spy, delegate);
        OAuth2AccessToken accessToken = spy.readAccessToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IjEifQ==.eyJ1c2VyX25hbWUiOiJ0ZXN0MiIsImp0aSI6IkZPTyIsImNsaWVudF9pZCI6ImZvbyJ9.b43ob1ALSIwr_J2oEnfMhsXvYkr1qVBNhigNH2zlaE1OQLhLfT-DMlFtHcyUlyap0C2n0q61SPaGE_z715TV0uTAv2YKDN4fKZz2bMR7eHLsvaaCuvs7KCOi_aSROaUG");
        Mockito.verify(jwtClaimsSetVerifier).verify(ArgumentMatchers.anyMap());
    }

    @Test
    public void readAccessTokenWhenCalledThenDelegateCalled() throws Exception {
        JwkTokenStore spy = Mockito.spy(this.jwkTokenStore);
        JwtTokenStore delegate = Mockito.mock(JwtTokenStore.class);
        Mockito.when(delegate.readAccessToken(ArgumentMatchers.anyString())).thenReturn(null);
        Field field = ReflectionUtils.findField(spy.getClass(), "delegate");
        field.setAccessible(true);
        ReflectionUtils.setField(field, spy, delegate);
        spy.readAccessToken(ArgumentMatchers.anyString());
        Mockito.verify(delegate).readAccessToken(ArgumentMatchers.anyString());
    }

    @Test
    public void removeAccessTokenWhenCalledThenDelegateCalled() throws Exception {
        JwkTokenStore spy = Mockito.spy(this.jwkTokenStore);
        JwtTokenStore delegate = Mockito.mock(JwtTokenStore.class);
        Mockito.doNothing().when(delegate).removeAccessToken(ArgumentMatchers.any(OAuth2AccessToken.class));
        Field field = ReflectionUtils.findField(spy.getClass(), "delegate");
        field.setAccessible(true);
        ReflectionUtils.setField(field, spy, delegate);
        spy.removeAccessToken(ArgumentMatchers.any(OAuth2AccessToken.class));
        Mockito.verify(delegate).removeAccessToken(ArgumentMatchers.any(OAuth2AccessToken.class));
    }

    @Test
    public void storeAccessTokenWhenCalledThenThrowJwkException() throws Exception {
        this.setUpExpectedJwkException();
        this.jwkTokenStore.storeAccessToken(null, null);
    }

    @Test
    public void storeRefreshTokenWhenCalledThenThrowJwkException() throws Exception {
        this.setUpExpectedJwkException();
        this.jwkTokenStore.storeRefreshToken(null, null);
    }

    @Test
    public void readRefreshTokenWhenCalledThenThrowJwkException() throws Exception {
        this.setUpExpectedJwkException();
        this.jwkTokenStore.readRefreshToken(null);
    }

    @Test
    public void readAuthenticationForRefreshTokenWhenCalledThenThrowJwkException() throws Exception {
        this.setUpExpectedJwkException();
        this.jwkTokenStore.readAuthenticationForRefreshToken(null);
    }

    @Test
    public void removeRefreshTokenWhenCalledThenThrowJwkException() throws Exception {
        this.setUpExpectedJwkException();
        this.jwkTokenStore.removeRefreshToken(null);
    }

    @Test
    public void removeAccessTokenUsingRefreshTokenWhenCalledThenThrowJwkException() throws Exception {
        this.setUpExpectedJwkException();
        this.jwkTokenStore.removeAccessTokenUsingRefreshToken(null);
    }

    @Test
    public void getAccessTokenWhenCalledThenThrowJwkException() throws Exception {
        this.setUpExpectedJwkException();
        this.jwkTokenStore.getAccessToken(null);
    }

    @Test
    public void findTokensByClientIdAndUserNameWhenCalledThenThrowJwkException() throws Exception {
        this.setUpExpectedJwkException();
        this.jwkTokenStore.findTokensByClientIdAndUserName(null, null);
    }

    @Test
    public void findTokensByClientIdWhenCalledThenThrowJwkException() throws Exception {
        this.setUpExpectedJwkException();
        this.jwkTokenStore.findTokensByClientId(null);
    }
}

