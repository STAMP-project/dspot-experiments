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
package org.apache.camel.parser.xml;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.camel.parser.XmlRouteParser;
import org.apache.camel.parser.model.CamelNodeDetails;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XmlParseTreeTest {
    private static final Logger LOG = LoggerFactory.getLogger(XmlParseTreeTest.class);

    @Test
    public void testXmlTree() throws Exception {
        InputStream is = new FileInputStream("src/test/resources/org/apache/camel/parser/xml/mycamel.xml");
        String fqn = "src/test/resources/org/apache/camel/camel/parser/xml/mycamel.xml";
        String baseDir = "src/test/resources";
        List<CamelNodeDetails> list = XmlRouteParser.parseXmlRouteTree(is, baseDir, fqn);
        Assert.assertEquals(1, list.size());
        CamelNodeDetails details = list.get(0);
        Assert.assertEquals("src/test/resources/org/apache/camel/camel/parser/xml/mycamel.xml", details.getFileName());
        Assert.assertEquals("myRoute", details.getRouteId());
        Assert.assertEquals(null, details.getMethodName());
        Assert.assertEquals(null, details.getClassName());
        String tree = details.dump(0);
        XmlParseTreeTest.LOG.info(("\n" + tree));
        Assert.assertTrue(tree.contains("32\tfrom"));
        Assert.assertTrue(tree.contains("35\t  transform"));
        Assert.assertTrue(tree.contains("39\t  to"));
    }
}

