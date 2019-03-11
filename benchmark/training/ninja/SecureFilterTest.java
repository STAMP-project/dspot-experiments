/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ninja;


import NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN;
import SecureFilter.USERNAME;
import ninja.session.Session;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SecureFilterTest {
    @Mock
    private Context context;

    @Mock
    private Session sessionCookie;

    @Mock
    private FilterChain filterChain;

    @Mock
    Ninja ninja;

    @Mock
    Result result;

    SecureFilter secureFilter;

    @Test
    public void testSecureFilter() {
        Mockito.when(context.getSession()).thenReturn(null);
        // filter that
        secureFilter.filter(filterChain, context);
        Mockito.verifyZeroInteractions(filterChain);
    }

    @Test
    public void testSessionIsNotReturingWhenUserNameMissing() {
        Mockito.when(context.getSession()).thenReturn(sessionCookie);
        Mockito.when(sessionCookie.get(USERNAME)).thenReturn(null);
        // filter that
        Result result = secureFilter.filter(filterChain, context);
        Assert.assertEquals(LOCATION_VIEW_FTL_HTML_FORBIDDEN, result.getTemplate());
        Mockito.verifyZeroInteractions(filterChain);
    }

    @Test
    public void testWorkingSessionWhenUsernameIsThere() {
        Mockito.when(context.getSession()).thenReturn(sessionCookie);
        Mockito.when(sessionCookie.get(USERNAME)).thenReturn("myname");
        // filter that
        secureFilter.filter(filterChain, context);
        Mockito.verify(filterChain).next(context);
    }

    @Test
    public void testThatUSERNAMEIsCorrect() {
        Assert.assertThat(USERNAME, CoreMatchers.equalTo("username"));
    }
}

