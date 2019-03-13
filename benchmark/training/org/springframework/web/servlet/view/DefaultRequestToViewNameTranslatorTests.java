/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.web.servlet.view;


import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;


/**
 *
 *
 * @author Rick Evans
 * @author Juergen Hoeller
 */
public class DefaultRequestToViewNameTranslatorTests {
    private static final String VIEW_NAME = "apple";

    private static final String EXTENSION = ".html";

    private static final String CONTEXT_PATH = "/sundays";

    private DefaultRequestToViewNameTranslator translator;

    private MockHttpServletRequest request;

    @Test
    public void testGetViewNameLeavesLeadingSlashIfSoConfigured() {
        request.setRequestURI(((((DefaultRequestToViewNameTranslatorTests.CONTEXT_PATH) + "/") + (DefaultRequestToViewNameTranslatorTests.VIEW_NAME)) + "/"));
        this.translator.setStripLeadingSlash(false);
        assertViewName(("/" + (DefaultRequestToViewNameTranslatorTests.VIEW_NAME)));
    }

    @Test
    public void testGetViewNameLeavesTrailingSlashIfSoConfigured() {
        request.setRequestURI(((((DefaultRequestToViewNameTranslatorTests.CONTEXT_PATH) + "/") + (DefaultRequestToViewNameTranslatorTests.VIEW_NAME)) + "/"));
        this.translator.setStripTrailingSlash(false);
        assertViewName(((DefaultRequestToViewNameTranslatorTests.VIEW_NAME) + "/"));
    }

    @Test
    public void testGetViewNameLeavesExtensionIfSoConfigured() {
        request.setRequestURI(((((DefaultRequestToViewNameTranslatorTests.CONTEXT_PATH) + "/") + (DefaultRequestToViewNameTranslatorTests.VIEW_NAME)) + (DefaultRequestToViewNameTranslatorTests.EXTENSION)));
        this.translator.setStripExtension(false);
        assertViewName(((DefaultRequestToViewNameTranslatorTests.VIEW_NAME) + (DefaultRequestToViewNameTranslatorTests.EXTENSION)));
    }

    @Test
    public void testGetViewNameWithDefaultConfiguration() {
        request.setRequestURI((((DefaultRequestToViewNameTranslatorTests.CONTEXT_PATH) + (DefaultRequestToViewNameTranslatorTests.VIEW_NAME)) + (DefaultRequestToViewNameTranslatorTests.EXTENSION)));
        assertViewName(DefaultRequestToViewNameTranslatorTests.VIEW_NAME);
    }

    @Test
    public void testGetViewNameWithCustomSeparator() {
        request.setRequestURI(((((DefaultRequestToViewNameTranslatorTests.CONTEXT_PATH) + (DefaultRequestToViewNameTranslatorTests.VIEW_NAME)) + "/fiona") + (DefaultRequestToViewNameTranslatorTests.EXTENSION)));
        this.translator.setSeparator("_");
        assertViewName(((DefaultRequestToViewNameTranslatorTests.VIEW_NAME) + "_fiona"));
    }

    @Test
    public void testGetViewNameWithNoExtension() {
        request.setRequestURI(((DefaultRequestToViewNameTranslatorTests.CONTEXT_PATH) + (DefaultRequestToViewNameTranslatorTests.VIEW_NAME)));
        assertViewName(DefaultRequestToViewNameTranslatorTests.VIEW_NAME);
    }

    @Test
    public void testGetViewNameWithSemicolonContent() {
        request.setRequestURI((((DefaultRequestToViewNameTranslatorTests.CONTEXT_PATH) + (DefaultRequestToViewNameTranslatorTests.VIEW_NAME)) + ";a=A;b=B"));
        assertViewName(DefaultRequestToViewNameTranslatorTests.VIEW_NAME);
    }

    @Test
    public void testGetViewNameWithPrefix() {
        final String prefix = "fiona_";
        request.setRequestURI(((DefaultRequestToViewNameTranslatorTests.CONTEXT_PATH) + (DefaultRequestToViewNameTranslatorTests.VIEW_NAME)));
        this.translator.setPrefix(prefix);
        assertViewName((prefix + (DefaultRequestToViewNameTranslatorTests.VIEW_NAME)));
    }

    @Test
    public void testGetViewNameWithNullPrefix() {
        request.setRequestURI(((DefaultRequestToViewNameTranslatorTests.CONTEXT_PATH) + (DefaultRequestToViewNameTranslatorTests.VIEW_NAME)));
        this.translator.setPrefix(null);
        assertViewName(DefaultRequestToViewNameTranslatorTests.VIEW_NAME);
    }

    @Test
    public void testGetViewNameWithSuffix() {
        final String suffix = ".fiona";
        request.setRequestURI(((DefaultRequestToViewNameTranslatorTests.CONTEXT_PATH) + (DefaultRequestToViewNameTranslatorTests.VIEW_NAME)));
        this.translator.setSuffix(suffix);
        assertViewName(((DefaultRequestToViewNameTranslatorTests.VIEW_NAME) + suffix));
    }

    @Test
    public void testGetViewNameWithNullSuffix() {
        request.setRequestURI(((DefaultRequestToViewNameTranslatorTests.CONTEXT_PATH) + (DefaultRequestToViewNameTranslatorTests.VIEW_NAME)));
        this.translator.setSuffix(null);
        assertViewName(DefaultRequestToViewNameTranslatorTests.VIEW_NAME);
    }

    @Test
    public void testTrySetUrlPathHelperToNull() {
        try {
            this.translator.setUrlPathHelper(null);
        } catch (IllegalArgumentException expected) {
        }
    }
}

