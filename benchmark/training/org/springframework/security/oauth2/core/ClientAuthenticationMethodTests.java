/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.security.oauth2.core;


import ClientAuthenticationMethod.BASIC;
import ClientAuthenticationMethod.NONE;
import ClientAuthenticationMethod.POST;
import org.junit.Test;


/**
 * Tests for {@link ClientAuthenticationMethod}.
 *
 * @author Joe Grandja
 */
public class ClientAuthenticationMethodTests {
    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenValueIsNullThenThrowIllegalArgumentException() {
        new ClientAuthenticationMethod(null);
    }

    @Test
    public void getValueWhenAuthenticationMethodBasicThenReturnBasic() {
        assertThat(BASIC.getValue()).isEqualTo("basic");
    }

    @Test
    public void getValueWhenAuthenticationMethodPostThenReturnPost() {
        assertThat(POST.getValue()).isEqualTo("post");
    }

    @Test
    public void getValueWhenAuthenticationMethodNoneThenReturnNone() {
        assertThat(NONE.getValue()).isEqualTo("none");
    }
}

