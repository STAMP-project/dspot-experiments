/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.evtx.parser.bxml.value;


import java.util.List;
import org.apache.nifi.processors.evtx.parser.bxml.BxmlNode;
import org.apache.nifi.processors.evtx.parser.bxml.BxmlNodeTestBase;
import org.apache.nifi.processors.evtx.parser.bxml.EndOfStreamNode;
import org.apache.nifi.processors.evtx.parser.bxml.RootNode;
import org.junit.Assert;
import org.junit.Test;


public class BXmlTypeNodeTest extends BxmlNodeTestBase {
    private BXmlTypeNode bXmlTypeNode;

    @Test
    public void testInit() {
        RootNode rootNode = bXmlTypeNode.getRootNode();
        List<BxmlNode> children = rootNode.getChildren();
        Assert.assertEquals(1, children.size());
        Assert.assertTrue(((children.get(0)) instanceof EndOfStreamNode));
        Assert.assertEquals(0, rootNode.getSubstitutions().size());
        Assert.assertEquals(rootNode.toString(), bXmlTypeNode.getValue());
    }
}

