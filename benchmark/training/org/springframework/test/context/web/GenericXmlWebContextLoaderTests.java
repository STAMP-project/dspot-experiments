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
package org.springframework.test.context.web;


import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link GenericXmlWebContextLoader}.
 *
 * @author Sam Brannen
 * @since 4.0.4
 */
public class GenericXmlWebContextLoaderTests {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void configMustNotContainAnnotatedClasses() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.containsString("does not support annotated classes"));
        GenericXmlWebContextLoader loader = new GenericXmlWebContextLoader();
        WebMergedContextConfiguration mergedConfig = new WebMergedContextConfiguration(getClass(), GenericXmlWebContextLoaderTests.EMPTY_STRING_ARRAY, new Class<?>[]{ getClass() }, null, GenericXmlWebContextLoaderTests.EMPTY_STRING_ARRAY, GenericXmlWebContextLoaderTests.EMPTY_STRING_ARRAY, GenericXmlWebContextLoaderTests.EMPTY_STRING_ARRAY, "resource/path", loader, null, null);
        loader.loadContext(mergedConfig);
    }
}

