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
package io.undertow.server.handlers.form;


import io.undertow.server.HttpHandler;
import io.undertow.testutils.DefaultServer;
import junit.textui.TestRunner;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.Parameterized.class)
public class FormDataParserTestCase {
    static class AggregateRunner extends TestRunner {}

    private final HttpHandler rootHandler;

    public FormDataParserTestCase(final HttpHandler rootHandler) {
        this.rootHandler = rootHandler;
    }

    @Test
    public void testFormDataParsing() throws Exception {
        runTest(new BasicNameValuePair("name", "A Value"));
        runTest(new BasicNameValuePair("name", "A Value"), new BasicNameValuePair("Single-value", null));
        runTest(new BasicNameValuePair("name", "A Value"), new BasicNameValuePair("A/name/with_special*chars", "A $ value&& with=SomeCharacters"));
        runTest(new BasicNameValuePair("name", "A Value"), new BasicNameValuePair("Single-value", null), new BasicNameValuePair("A/name/with_special*chars", "A $ value&& with=SomeCharacters"));
    }
}

