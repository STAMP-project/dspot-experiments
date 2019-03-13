/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.web.servlet.theme;


import AbstractThemeResolver.ORIGINAL_DEFAULT_THEME_NAME;
import org.junit.Test;


/**
 *
 *
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @since 19.06.2003
 */
public class ThemeResolverTests {
    private static final String TEST_THEME_NAME = "test.theme";

    private static final String DEFAULT_TEST_THEME_NAME = "default.theme";

    @Test
    public void fixedThemeResolver() {
        internalTest(new FixedThemeResolver(), false, ORIGINAL_DEFAULT_THEME_NAME);
    }

    @Test
    public void cookieThemeResolver() {
        internalTest(new CookieThemeResolver(), true, ORIGINAL_DEFAULT_THEME_NAME);
    }

    @Test
    public void sessionThemeResolver() {
        internalTest(new SessionThemeResolver(), true, ORIGINAL_DEFAULT_THEME_NAME);
    }

    @Test
    public void sessionThemeResolverWithDefault() {
        SessionThemeResolver tr = new SessionThemeResolver();
        tr.setDefaultThemeName(ThemeResolverTests.DEFAULT_TEST_THEME_NAME);
        internalTest(tr, true, ThemeResolverTests.DEFAULT_TEST_THEME_NAME);
    }
}

