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
package org.apache.camel.component.freemarker;


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class FreemarkerTemplateInHeaderTest extends CamelTestSupport {
    @Test
    public void testReceivesFooResponse() throws Exception {
        assertRespondsWith("cheese", "foo", "<hello>foo</hello>");
    }

    @Test
    public void testReceivesBarResponse() throws Exception {
        assertRespondsWith("cheese", "bar", "<hello>bar</hello>");
    }

    @Test
    public void testRespectHeaderNamesUpperCase() throws Exception {
        assertRespondsWith("Cheese", "bar", "<hello>bar</hello>");
    }

    @Test
    public void testRespectHeaderNamesCamelCase() throws Exception {
        assertRespondsWith("CorrelationID", "bar", "<hello>bar</hello>");
    }
}

