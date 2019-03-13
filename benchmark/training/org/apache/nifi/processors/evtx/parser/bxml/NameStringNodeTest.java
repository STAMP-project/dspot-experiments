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


import com.google.common.primitives.UnsignedInteger;
import java.io.IOException;
import org.apache.nifi.processors.evtx.parser.BxmlNodeVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class NameStringNodeTest extends BxmlNodeTestBase {
    private int nextOffset = 300;

    private int hash = 100;

    private String string = "test string";

    private NameStringNode nameStringNode;

    @Test
    public void testInit() {
        Assert.assertEquals(UnsignedInteger.valueOf(nextOffset), nameStringNode.getNextOffset());
        Assert.assertEquals(hash, nameStringNode.getHash());
        Assert.assertEquals(string, nameStringNode.getString());
    }

    @Test
    public void testVisitor() throws IOException {
        BxmlNodeVisitor mock = Mockito.mock(BxmlNodeVisitor.class);
        nameStringNode.accept(mock);
        Mockito.verify(mock).visit(nameStringNode);
        Mockito.verifyNoMoreInteractions(mock);
    }
}

