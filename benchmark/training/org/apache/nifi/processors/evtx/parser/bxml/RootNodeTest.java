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
import org.apache.nifi.processors.evtx.parser.BxmlNodeVisitor;
import org.apache.nifi.processors.evtx.parser.bxml.value.VariantTypeNode;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class RootNodeTest extends BxmlNodeTestBase {
    private String testString = "testString";

    private RootNode rootNode;

    @Test
    public void testInit() {
        List<BxmlNode> children = rootNode.getChildren();
        Assert.assertEquals(1, children.size());
        Assert.assertTrue(((children.get(0)) instanceof EndOfStreamNode));
        List<VariantTypeNode> substitutions = rootNode.getSubstitutions();
        Assert.assertEquals(1, substitutions.size());
        Assert.assertEquals(testString, substitutions.get(0).getValue());
    }

    @Test
    public void testVisitor() throws IOException {
        BxmlNodeVisitor mock = Mockito.mock(BxmlNodeVisitor.class);
        rootNode.accept(mock);
        Mockito.verify(mock).visit(rootNode);
        Mockito.verifyNoMoreInteractions(mock);
    }
}

