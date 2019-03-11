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
package io.undertow.servlet.test.response.contenttype;


import io.undertow.testutils.DefaultServer;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class ContentTypeCharsetTestCase {
    @Test
    public void testCharsetAndContentType() throws Exception {
        runtest("text/html", "UTF8", "text/html;charset=UTF8", "text/html;charset=UTF8\nUTF8");
        runtest("text/html", "", "text/html;charset=ISO-8859-1", "text/html;charset=ISO-8859-1\nISO-8859-1");
        runtest("text/html;   charset=UTF8", "", "text/html;charset=UTF8", "text/html;charset=UTF8\nUTF8");
        runtest("text/html;   charset=\"UTF8\"", "", "text/html;charset=UTF8", "text/html;charset=UTF8\nUTF8");
        runtest("text/html;   charset=UTF8; boundary=someString;", "", "text/html; boundary=someString;charset=UTF8", "text/html; boundary=someString;charset=UTF8\nUTF8");
        runtest("text/html;   charset=UTF8; boundary=someString;   ", "", "text/html; boundary=someString;charset=UTF8", "text/html; boundary=someString;charset=UTF8\nUTF8");
        runtest("multipart/related; type=\"text/xml\"; boundary=\"uuid:ce7d652a-d035-42fa-962c-5b8315084e32\"; start=\"<root.message@cxf.apache.org>\"; start-info=\"text/xml\"", "", "multipart/related; type=\"text/xml\"; boundary=\"uuid:ce7d652a-d035-42fa-962c-5b8315084e32\"; start=\"<root.message@cxf.apache.org>\"; start-info=\"text/xml\";charset=ISO-8859-1", "multipart/related; type=\"text/xml\"; boundary=\"uuid:ce7d652a-d035-42fa-962c-5b8315084e32\"; start=\"<root.message@cxf.apache.org>\"; start-info=\"text/xml\";charset=ISO-8859-1\nISO-8859-1");
    }
}

