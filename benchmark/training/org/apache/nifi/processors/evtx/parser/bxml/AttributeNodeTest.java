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
package org.apache.nifi.processors.evtx.parser.bxml;


import java.io.IOException;
import java.util.List;
import org.apache.nifi.processors.evtx.parser.BinaryReader;
import org.apache.nifi.processors.evtx.parser.BxmlNodeVisitor;
import org.apache.nifi.processors.evtx.parser.bxml.value.NullTypeNode;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AttributeNodeTest extends BxmlNodeWithTokenAndStringTestBase {
    public static final String ATTRIBUTE_NAME = "AttributeName";

    private BinaryReader binaryReader;

    private AttributeNode attributeNode;

    @Test
    public void testInit() {
        Assert.assertEquals(AttributeNodeTest.ATTRIBUTE_NAME, attributeNode.getAttributeName());
        BxmlNode attributeNodeValue = attributeNode.getValue();
        Assert.assertTrue((attributeNodeValue instanceof ValueNode));
        List<BxmlNode> children = getChildren();
        Assert.assertEquals(1, children.size());
        Assert.assertTrue(((children.get(0)) instanceof NullTypeNode));
    }

    @Test
    public void testVisit() throws IOException {
        BxmlNodeVisitor mock = Mockito.mock(BxmlNodeVisitor.class);
        attributeNode.accept(mock);
        Mockito.verify(mock).visit(attributeNode);
        Mockito.verifyNoMoreInteractions(mock);
    }
}

