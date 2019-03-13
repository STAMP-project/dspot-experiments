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
package org.springframework.web.servlet.i18n;


import java.util.Locale;
import org.junit.Test;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 20.03.2003
 */
public class LocaleResolverTests {
    @Test
    public void testAcceptHeaderLocaleResolver() {
        doTest(new AcceptHeaderLocaleResolver(), false);
    }

    @Test
    public void testFixedLocaleResolver() {
        doTest(new FixedLocaleResolver(Locale.UK), false);
    }

    @Test
    public void testCookieLocaleResolver() {
        doTest(new CookieLocaleResolver(), true);
    }

    @Test
    public void testSessionLocaleResolver() {
        doTest(new SessionLocaleResolver(), true);
    }
}

