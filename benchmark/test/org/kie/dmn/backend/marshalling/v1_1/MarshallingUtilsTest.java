/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.backend.marshalling.v1_1;


import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MarshallingUtilsTest {
    protected static final Logger logger = LoggerFactory.getLogger(MarshallingUtilsTest.class);

    @Test
    public void testLocal() throws Exception {
        QName qname = new QName("local1");
        checkAndRoundTrip(qname, "local1");
    }

    @Test
    public void testPrefixLocal() throws Exception {
        QName qname = new QName(XMLConstants.NULL_NS_URI, "local2", "prefix");
        checkAndRoundTrip(qname, "prefix:local2");
    }

    @Test
    public void testNamespaceLocal() throws Exception {
        QName qname = new QName("http://namespace", "local3");
        checkAndRoundTrip(qname, "{http://namespace}local3");
    }

    @Test
    public void testNamespaceLocal_b() throws Exception {
        QName qname = new QName("http://namespace", "local3", XMLConstants.DEFAULT_NS_PREFIX);
        checkAndRoundTrip(qname, "{http://namespace}local3");
    }

    @Test
    public void testNamespacePrefixLocal() throws Exception {
        QName qname = new QName("http://namespace", "local4", "prefix");
        checkAndRoundTrip(qname, "prefix:local4");
    }
}

