/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.mail;


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class MimeMultipartAlternativeWithContentTypeTest extends CamelTestSupport {
    private String alternativeBody = "hello world! (plain text)";

    private String htmlBody = "<html><body><h1>Hello</h1>World</body></html>";

    @Test
    public void testMultipartEmailContentType() throws Exception {
        sendMultipartEmail();
        verifyTheRecivedEmail("Content-Type: text/plain; charset=UTF-8");
        verifyTheRecivedEmail("Content-Type: text/html; charset=UTF-8");
    }
}

