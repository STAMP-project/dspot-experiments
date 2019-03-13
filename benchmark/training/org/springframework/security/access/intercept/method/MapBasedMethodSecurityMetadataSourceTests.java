/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.access.intercept.method;


import java.lang.reflect.Method;
import java.util.List;
import org.junit.Test;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;


/**
 * Tests for {@link MapBasedMethodSecurityMetadataSource}.
 *
 * @author Luke Taylor
 * @since 2.0.4
 */
public class MapBasedMethodSecurityMetadataSourceTests {
    private final List<ConfigAttribute> ROLE_A = SecurityConfig.createList("ROLE_A");

    private final List<ConfigAttribute> ROLE_B = SecurityConfig.createList("ROLE_B");

    private MapBasedMethodSecurityMetadataSource mds;

    private Method someMethodString;

    private Method someMethodInteger;

    @Test
    public void wildcardedMatchIsOverwrittenByMoreSpecificMatch() {
        mds.addSecureMethod(MapBasedMethodSecurityMetadataSourceTests.MockService.class, "some*", ROLE_A);
        mds.addSecureMethod(MapBasedMethodSecurityMetadataSourceTests.MockService.class, "someMethod*", ROLE_B);
        assertThat(mds.getAttributes(someMethodInteger, MapBasedMethodSecurityMetadataSourceTests.MockService.class)).isEqualTo(ROLE_B);
    }

    @Test
    public void methodsWithDifferentArgumentsAreMatchedCorrectly() throws Exception {
        mds.addSecureMethod(MapBasedMethodSecurityMetadataSourceTests.MockService.class, someMethodInteger, ROLE_A);
        mds.addSecureMethod(MapBasedMethodSecurityMetadataSourceTests.MockService.class, someMethodString, ROLE_B);
        assertThat(mds.getAttributes(someMethodInteger, MapBasedMethodSecurityMetadataSourceTests.MockService.class)).isEqualTo(ROLE_A);
        assertThat(mds.getAttributes(someMethodString, MapBasedMethodSecurityMetadataSourceTests.MockService.class)).isEqualTo(ROLE_B);
    }

    @SuppressWarnings("unused")
    private class MockService {
        public void someMethod(String s) {
        }

        public void someMethod(Integer i) {
        }
    }
}

