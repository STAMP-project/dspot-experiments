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
package org.springframework.security.web.util.matcher;


import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(MockitoJUnitRunner.class)
public class NegatedRequestMatcherTests {
    @Mock
    private RequestMatcher delegate;

    @Mock
    private HttpServletRequest request;

    private RequestMatcher matcher;

    @Test(expected = IllegalArgumentException.class)
    public void constructorNull() {
        new NegatedRequestMatcher(null);
    }

    @Test
    public void matchesDelegateFalse() {
        Mockito.when(delegate.matches(request)).thenReturn(false);
        matcher = new NegatedRequestMatcher(delegate);
        assertThat(matcher.matches(request)).isTrue();
    }

    @Test
    public void matchesDelegateTrue() {
        Mockito.when(delegate.matches(request)).thenReturn(true);
        matcher = new NegatedRequestMatcher(delegate);
        assertThat(matcher.matches(request)).isFalse();
    }
}

