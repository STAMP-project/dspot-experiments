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
package org.springframework.security.web.method.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Test;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;


/**
 *
 *
 * @author Rob Winch
 */
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
    public void resolveArgumentSpel() throws Exception {
        AuthenticationPrincipalArgumentResolverTests.CustomUserPrincipal principal = new AuthenticationPrincipalArgumentResolverTests.CustomUserPrincipal();
        setAuthenticationPrincipal(principal);
        this.expectedPrincipal = principal.property;
        assertThat(this.resolver.resolveArgument(showUserSpel(), null, null, null)).isEqualTo(this.expectedPrincipal);
    }

    @Test
    public void resolveArgumentSpelCopy() throws Exception {
        AuthenticationPrincipalArgumentResolverTests.CopyUserPrincipal principal = new AuthenticationPrincipalArgumentResolverTests.CopyUserPrincipal("property");
        setAuthenticationPrincipal(principal);
        Object resolveArgument = this.resolver.resolveArgument(showUserSpelCopy(), null, null, null);
        assertThat(resolveArgument).isEqualTo(principal);
        assertThat(resolveArgument).isNotSameAs(principal);
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

        public void showUserSpel(@AuthenticationPrincipal(expression = "property")
        String user) {
        }

        public void showUserSpelCopy(@AuthenticationPrincipal(expression = "new org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolverTests$CopyUserPrincipal(#this)")
        AuthenticationPrincipalArgumentResolverTests.CopyUserPrincipal user) {
        }
    }

    static class CustomUserPrincipal {
        public final String property = "property";
    }

    static class CopyUserPrincipal {
        public final String property;

        CopyUserPrincipal(String property) {
            this.property = property;
        }

        public CopyUserPrincipal(AuthenticationPrincipalArgumentResolverTests.CopyUserPrincipal toCopy) {
            this.property = toCopy.property;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((this.property) == null ? 0 : this.property.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            AuthenticationPrincipalArgumentResolverTests.CopyUserPrincipal other = ((AuthenticationPrincipalArgumentResolverTests.CopyUserPrincipal) (obj));
            if ((this.property) == null) {
                if ((other.property) != null) {
                    return false;
                }
            } else
                if (!(this.property.equals(other.property))) {
                    return false;
                }

            return true;
        }
    }
}

