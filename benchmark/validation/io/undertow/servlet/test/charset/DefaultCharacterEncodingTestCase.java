/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.servlet.test.charset;


import io.undertow.testutils.DefaultServer;
import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Artemy Osipov
 */
@RunWith(DefaultServer.class)
public class DefaultCharacterEncodingTestCase {
    @Test
    public void testDefaultEncodingNotSet() throws IOException, ServletException {
        testDefaultEncoding(null, "null", "ISO-8859-1");
    }

    @Test
    public void testDefaultEncodingSetEqualDefault() throws IOException, ServletException {
        testDefaultEncoding("ISO-8859-1", "ISO-8859-1", "ISO-8859-1");
    }

    @Test
    public void testDefaultEncodingSetNotEqualDefault() throws IOException, ServletException {
        testDefaultEncoding("UTF-8", "UTF-8", "UTF-8");
    }
}

