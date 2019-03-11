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
import org.apache.nifi.processors.evtx.parser.BinaryReader;
import org.apache.nifi.processors.evtx.parser.BxmlNodeVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TemplateInstanceNodeTest extends BxmlNodeWithTokenTestBase {
    private byte unknown = 23;

    private int templateId = 101;

    private int templateLength = 25;

    private TemplateNode templateNode;

    private TemplateInstanceNode templateInstanceNode;

    @Test
    public void testInit() {
        Assert.assertEquals(getToken(), templateInstanceNode.getToken());
        Assert.assertEquals(10, templateInstanceNode.getHeaderLength());
        Assert.assertEquals(templateNode, templateInstanceNode.getTemplateNode());
    }

    @Test
    public void testHasEndOfStream() {
        Mockito.when(templateNode.hasEndOfStream()).thenReturn(true).thenReturn(false);
        Assert.assertTrue(templateInstanceNode.hasEndOfStream());
        Assert.assertFalse(templateInstanceNode.hasEndOfStream());
    }

    @Test
    public void testVisitor() throws IOException {
        BxmlNodeVisitor mock = Mockito.mock(BxmlNodeVisitor.class);
        templateInstanceNode.accept(mock);
        Mockito.verify(mock).visit(templateInstanceNode);
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void testResidentTemplate() throws IOException {
        super.setup();
        testBinaryReaderBuilder.put(unknown);
        testBinaryReaderBuilder.putDWord(templateId);
        testBinaryReaderBuilder.putDWord(5);
        BinaryReader binaryReader = testBinaryReaderBuilder.build();
        TemplateNode templateNode = Mockito.mock(TemplateNode.class);
        Mockito.when(templateNode.getTemplateId()).thenReturn(UnsignedInteger.valueOf(templateId));
        Mockito.when(templateNode.hasEndOfStream()).thenReturn(true).thenReturn(false);
        Mockito.when(chunkHeader.addTemplateNode(5, binaryReader)).thenAnswer(( invocation) -> {
            binaryReader.skip(templateLength);
            return templateNode;
        });
        templateInstanceNode = new TemplateInstanceNode(binaryReader, chunkHeader, parent);
        Assert.assertEquals(templateLength, ((templateInstanceNode.getHeaderLength()) - 10));
    }
}

