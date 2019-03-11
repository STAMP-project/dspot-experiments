/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.security.web.bind.support;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Test;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;


/**
 *
 *
 * @author Rob Winch
 */
@SuppressWarnings("deprecation")
public class AuthenticationPrincipalArgumentResolverTests {
    private Object expectedPrincipal;

    private AuthenticationPrincipalArgumentResolver resolver;

    @Test
    public void supportsParameterNoAnnotation() throws Exception {
        assertThat(resolver.supportsParameter(showUserNoAnnotation())).isFalse();
    }

    @Test
    public void supportsParameterAnnotation() throws Exception {
        assertThat(resolver.supportsParameter(showUserAnnotationObject())).isTrue();
    }

    @Test
    public void supportsParameterCustomAnnotation() throws Exception {
        assertThat(resolver.supportsParameter(showUserCustomAnnotation())).isTrue();
    }

    @Test
    public void resolveArgumentNullAuthentication() throws Exception {
        assertThat(resolver.resolveArgument(showUserAnnotationString(), null, null, null)).isNull();
    }

    @Test
    public void resolveArgumentNullPrincipal() throws Exception {
        setAuthenticationPrincipal(null);
        assertThat(resolver.resolveArgument(showUserAnnotationString(), null, null, null)).isNull();
    }

    @Test
    public void resolveArgumentString() throws Exception {
        setAuthenticationPrincipal("john");
        assertThat(resolver.resolveArgument(showUserAnnotationString(), null, null, null)).isEqualTo(expectedPrincipal);
    }

    @Test
    public void resolveArgumentPrincipalStringOnObject() throws Exception {
        setAuthenticationPrincipal("john");
        assertThat(resolver.resolveArgument(showUserAnnotationObject(), null, null, null)).isEqualTo(expectedPrincipal);
    }

    @Test
    public void resolveArgumentUserDetails() throws Exception {
        setAuthenticationPrincipal(new org.springframework.security.core.userdetails.User("user", "password", AuthorityUtils.createAuthorityList("ROLE_USER")));
        assertThat(resolver.resolveArgument(showUserAnnotationUserDetails(), null, null, null)).isEqualTo(expectedPrincipal);
    }

    @Test
    public void resolveArgumentCustomUserPrincipal() throws Exception {
        setAuthenticationPrincipal(new AuthenticationPrincipalArgumentResolverTests.CustomUserPrincipal());
        assertThat(resolver.resolveArgument(showUserAnnotationCustomUserPrincipal(), null, null, null)).isEqualTo(expectedPrincipal);
    }

    @Test
    public void resolveArgumentCustomAnnotation() throws Exception {
        setAuthenticationPrincipal(new AuthenticationPrincipalArgumentResolverTests.CustomUserPrincipal());
        assertThat(resolver.resolveArgument(showUserCustomAnnotation(), null, null, null)).isEqualTo(expectedPrincipal);
    }

    @Test
    public void resolveArgumentNullOnInvalidType() throws Exception {
        setAuthenticationPrincipal(new AuthenticationPrincipalArgumentResolverTests.CustomUserPrincipal());
        assertThat(resolver.resolveArgument(showUserAnnotationString(), null, null, null)).isNull();
    }

    @Test(expected = ClassCastException.class)
    public void resolveArgumentErrorOnInvalidType() throws Exception {
        setAuthenticationPrincipal(new AuthenticationPrincipalArgumentResolverTests.CustomUserPrincipal());
        resolver.resolveArgument(showUserAnnotationErrorOnInvalidType(), null, null, null);
    }

    @Test(expected = ClassCastException.class)
    public void resolveArgumentCustomserErrorOnInvalidType() throws Exception {
        setAuthenticationPrincipal(new AuthenticationPrincipalArgumentResolverTests.CustomUserPrincipal());
        resolver.resolveArgument(showUserAnnotationCurrentUserErrorOnInvalidType(), null, null, null);
    }

    @Test
    public void resolveArgumentObject() throws Exception {
        setAuthenticationPrincipal(new Object());
        assertThat(resolver.resolveArgument(showUserAnnotationObject(), null, null, null)).isEqualTo(expectedPrincipal);
    }

    @Target({ ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    @AuthenticationPrincipal
    static @interface CurrentUser {}

    @Target({ ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    @AuthenticationPrincipal(errorOnInvalidType = true)
    static @interface CurrentUserErrorOnInvalidType {}

    public static class TestController {
        public void showUserNoAnnotation(String user) {
        }

        public void showUserAnnotation(@AuthenticationPrincipal
        String user) {
        }

        public void showUserAnnotationErrorOnInvalidType(@AuthenticationPrincipal(errorOnInvalidType = true)
        String user) {
        }

        public void showUserAnnotationCurrentUserErrorOnInvalidType(@AuthenticationPrincipalArgumentResolverTests.CurrentUserErrorOnInvalidType
        String user) {
        }

        public void showUserAnnotation(@AuthenticationPrincipal
        UserDetails user) {
        }

        public void showUserAnnotation(@AuthenticationPrincipal
        AuthenticationPrincipalArgumentResolverTests.CustomUserPrincipal user) {
        }

        public void showUserCustomAnnotation(@AuthenticationPrincipalArgumentResolverTests.CurrentUser
        AuthenticationPrincipalArgumentResolverTests.CustomUserPrincipal user) {
        }

        public void showUserAnnotation(@AuthenticationPrincipal
        Object user) {
        }
    }

    private static class CustomUserPrincipal {}
}

