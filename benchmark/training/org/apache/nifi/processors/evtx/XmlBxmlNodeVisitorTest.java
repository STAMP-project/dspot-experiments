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
package org.apache.nifi.processors.evtx;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.nifi.processors.evtx.parser.BxmlNodeVisitor;
import org.apache.nifi.processors.evtx.parser.bxml.AttributeNode;
import org.apache.nifi.processors.evtx.parser.bxml.BxmlNode;
import org.apache.nifi.processors.evtx.parser.bxml.CDataSectionNode;
import org.apache.nifi.processors.evtx.parser.bxml.ConditionalSubstitutionNode;
import org.apache.nifi.processors.evtx.parser.bxml.EntityReferenceNode;
import org.apache.nifi.processors.evtx.parser.bxml.NormalSubstitutionNode;
import org.apache.nifi.processors.evtx.parser.bxml.OpenStartElementNode;
import org.apache.nifi.processors.evtx.parser.bxml.RootNode;
import org.apache.nifi.processors.evtx.parser.bxml.TemplateInstanceNode;
import org.apache.nifi.processors.evtx.parser.bxml.TemplateNode;
import org.apache.nifi.processors.evtx.parser.bxml.ValueNode;
import org.apache.nifi.processors.evtx.parser.bxml.value.BXmlTypeNode;
import org.apache.nifi.processors.evtx.parser.bxml.value.VariantTypeNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class XmlBxmlNodeVisitorTest {
    @Mock
    private XMLStreamWriter xmlStreamWriter;

    @Mock
    private RootNode rootNode;

    @Mock
    private BxmlNode bxmlNode;

    private List<VariantTypeNode> substitutions;

    private List<BxmlNode> children;

    private XmlBxmlNodeVisitor xmlBxmlNodeVisitor;

    @Test
    public void testConstructor() throws IOException {
        Mockito.verify(bxmlNode).accept(xmlBxmlNodeVisitor);
    }

    @Test
    public void testVisitOpenStartElementNode() throws IOException, XMLStreamException {
        String tagName = "open";
        OpenStartElementNode openStartElementNode = Mockito.mock(OpenStartElementNode.class);
        AttributeNode attributeNode = Mockito.mock(AttributeNode.class);
        AttributeNode attributeNode2 = Mockito.mock(AttributeNode.class);
        BxmlNode bxmlNode = Mockito.mock(BxmlNode.class);
        Mockito.when(openStartElementNode.getTagName()).thenReturn(tagName);
        Mockito.when(openStartElementNode.getChildren()).thenReturn(Arrays.asList(attributeNode, bxmlNode, attributeNode2));
        xmlBxmlNodeVisitor.visit(openStartElementNode);
        InOrder inOrder = Mockito.inOrder(xmlStreamWriter, attributeNode, attributeNode2, bxmlNode);
        inOrder.verify(xmlStreamWriter).writeStartElement(tagName);
        inOrder.verify(attributeNode).accept(xmlBxmlNodeVisitor);
        inOrder.verify(attributeNode2).accept(xmlBxmlNodeVisitor);
        inOrder.verify(bxmlNode).accept(xmlBxmlNodeVisitor);
        inOrder.verify(xmlStreamWriter).writeEndElement();
    }

    @Test
    public void testVisitAttributeNodeValueType() throws IOException, XMLStreamException {
        String attributeName = "attributeName";
        AttributeNode attributeNode = Mockito.mock(AttributeNode.class);
        ValueNode valueNode = Mockito.mock(ValueNode.class);
        BxmlNode child = Mockito.mock(BxmlNode.class);
        Mockito.when(attributeNode.getAttributeName()).thenReturn(attributeName);
        Mockito.when(attributeNode.getValue()).thenReturn(valueNode);
        Mockito.when(valueNode.getChildren()).thenReturn(Arrays.asList(child));
        Mockito.doAnswer(( invocation) -> {
            ((BxmlNodeVisitor) (invocation.getArguments()[0])).visit(valueNode);
            return null;
        }).when(valueNode).accept(ArgumentMatchers.any(BxmlNodeVisitor.class));
        xmlBxmlNodeVisitor.visit(attributeNode);
        Mockito.verify(xmlStreamWriter).writeAttribute(attributeName, null);
        Mockito.verify(child).accept(ArgumentMatchers.any(BxmlNodeVisitor.class));
    }

    @Test
    public void testVisitAttributeNodeVariantType() throws IOException, XMLStreamException {
        String attributeName = "attributeName";
        String attributeValue = "attributeValue";
        AttributeNode attributeNode = Mockito.mock(AttributeNode.class);
        VariantTypeNode variantTypeNode = Mockito.mock(VariantTypeNode.class);
        Mockito.when(attributeNode.getAttributeName()).thenReturn(attributeName);
        Mockito.when(attributeNode.getValue()).thenReturn(variantTypeNode);
        Mockito.doAnswer(( invocation) -> {
            ((BxmlNodeVisitor) (invocation.getArguments()[0])).visit(variantTypeNode);
            return null;
        }).when(variantTypeNode).accept(ArgumentMatchers.any(BxmlNodeVisitor.class));
        Mockito.when(variantTypeNode.getValue()).thenReturn(attributeValue);
        xmlBxmlNodeVisitor.visit(attributeNode);
        Mockito.verify(xmlStreamWriter).writeAttribute(attributeName, attributeValue);
    }

    @Test
    public void testVisitAttributeNormalSubstitutionNode() throws IOException, XMLStreamException {
        String attributeName = "attributeName";
        String attributeValue = "attributeValue";
        VariantTypeNode sub = Mockito.mock(VariantTypeNode.class);
        Mockito.when(sub.getValue()).thenReturn(attributeValue);
        substitutions.add(sub);
        AttributeNode attributeNode = Mockito.mock(AttributeNode.class);
        NormalSubstitutionNode normalSubstitutionNode = Mockito.mock(NormalSubstitutionNode.class);
        Mockito.when(attributeNode.getAttributeName()).thenReturn(attributeName);
        Mockito.when(attributeNode.getValue()).thenReturn(normalSubstitutionNode);
        Mockito.doAnswer(( invocation) -> {
            ((BxmlNodeVisitor) (invocation.getArguments()[0])).visit(normalSubstitutionNode);
            return null;
        }).when(normalSubstitutionNode).accept(ArgumentMatchers.any(BxmlNodeVisitor.class));
        Mockito.when(normalSubstitutionNode.getIndex()).thenReturn(0);
        xmlBxmlNodeVisitor.visit(attributeNode);
        Mockito.verify(xmlStreamWriter).writeAttribute(attributeName, attributeValue);
    }

    @Test
    public void testVisitAttributeConditionalSubstitutionNode() throws IOException, XMLStreamException {
        String attributeName = "attributeName";
        String attributeValue = "attributeValue";
        VariantTypeNode sub = Mockito.mock(VariantTypeNode.class);
        Mockito.when(sub.getValue()).thenReturn(attributeValue);
        substitutions.add(sub);
        AttributeNode attributeNode = Mockito.mock(AttributeNode.class);
        ConditionalSubstitutionNode conditionalSubstitutionNode = Mockito.mock(ConditionalSubstitutionNode.class);
        Mockito.when(attributeNode.getAttributeName()).thenReturn(attributeName);
        Mockito.when(attributeNode.getValue()).thenReturn(conditionalSubstitutionNode);
        Mockito.doAnswer(( invocation) -> {
            ((BxmlNodeVisitor) (invocation.getArguments()[0])).visit(conditionalSubstitutionNode);
            return null;
        }).when(conditionalSubstitutionNode).accept(ArgumentMatchers.any(BxmlNodeVisitor.class));
        Mockito.when(conditionalSubstitutionNode.getIndex()).thenReturn(0);
        xmlBxmlNodeVisitor.visit(attributeNode);
        Mockito.verify(xmlStreamWriter).writeAttribute(attributeName, attributeValue);
    }

    @Test
    public void testVisitTemplateInstanceNode() throws IOException {
        TemplateInstanceNode templateInstanceNode = Mockito.mock(TemplateInstanceNode.class);
        TemplateNode templateNode = Mockito.mock(TemplateNode.class);
        Mockito.when(templateInstanceNode.getTemplateNode()).thenReturn(templateNode);
        xmlBxmlNodeVisitor.visit(templateInstanceNode);
        Mockito.verify(templateNode).accept(xmlBxmlNodeVisitor);
    }

    @Test
    public void testVisitTemplateNode() throws IOException {
        TemplateNode templateNode = Mockito.mock(TemplateNode.class);
        BxmlNode child = Mockito.mock(BxmlNode.class);
        Mockito.when(templateNode.getChildren()).thenReturn(Arrays.asList(child));
        xmlBxmlNodeVisitor.visit(templateNode);
        Mockito.verify(child).accept(xmlBxmlNodeVisitor);
    }

    @Test
    public void testVisitCDataSectionNode() throws IOException, XMLStreamException {
        String cdata = "cdata";
        CDataSectionNode cDataSectionNode = Mockito.mock(CDataSectionNode.class);
        Mockito.when(cDataSectionNode.getCdata()).thenReturn(cdata);
        xmlBxmlNodeVisitor.visit(cDataSectionNode);
        Mockito.verify(xmlStreamWriter).writeCData(cdata);
    }

    @Test
    public void testVisitEntityReferenceNode() throws IOException, XMLStreamException {
        String value = "value";
        EntityReferenceNode entityReferenceNode = Mockito.mock(EntityReferenceNode.class);
        Mockito.when(entityReferenceNode.getValue()).thenReturn(value);
        xmlBxmlNodeVisitor.visit(entityReferenceNode);
        Mockito.verify(xmlStreamWriter).writeCharacters(value);
    }

    @Test
    public void testVisitValueNode() throws IOException {
        ValueNode valueNode = Mockito.mock(ValueNode.class);
        BxmlNode child = Mockito.mock(BxmlNode.class);
        Mockito.when(valueNode.getChildren()).thenReturn(Arrays.asList(child));
        xmlBxmlNodeVisitor.visit(valueNode);
        Mockito.verify(child).accept(xmlBxmlNodeVisitor);
    }

    @Test
    public void testVisitConditionalSubstitutionNode() throws IOException {
        ConditionalSubstitutionNode conditionalSubstitutionNode = Mockito.mock(ConditionalSubstitutionNode.class);
        VariantTypeNode sub = Mockito.mock(VariantTypeNode.class);
        substitutions.add(sub);
        Mockito.when(conditionalSubstitutionNode.getIndex()).thenReturn(0);
        xmlBxmlNodeVisitor.visit(conditionalSubstitutionNode);
        Mockito.verify(sub).accept(xmlBxmlNodeVisitor);
    }

    @Test
    public void testVisitNormalSubstitutionNode() throws IOException {
        NormalSubstitutionNode normalSubstitutionNode = Mockito.mock(NormalSubstitutionNode.class);
        VariantTypeNode sub = Mockito.mock(VariantTypeNode.class);
        substitutions.add(sub);
        Mockito.when(normalSubstitutionNode.getIndex()).thenReturn(0);
        xmlBxmlNodeVisitor.visit(normalSubstitutionNode);
        Mockito.verify(sub).accept(xmlBxmlNodeVisitor);
    }

    @Test
    public void testVisitBxmlTypeNode() throws IOException {
        BXmlTypeNode bXmlTypeNode = Mockito.mock(BXmlTypeNode.class);
        RootNode rootNode = Mockito.mock(RootNode.class);
        Mockito.when(bXmlTypeNode.getRootNode()).thenReturn(rootNode);
        xmlBxmlNodeVisitor.visit(bXmlTypeNode);
        Mockito.verify(rootNode).accept(xmlBxmlNodeVisitor);
    }

    @Test
    public void testVisitVariantTypeNode() throws IOException, XMLStreamException {
        String variantValue = "variantValue";
        VariantTypeNode variantTypeNode = Mockito.mock(VariantTypeNode.class);
        Mockito.when(variantTypeNode.getValue()).thenReturn(variantValue);
        xmlBxmlNodeVisitor.visit(variantTypeNode);
        Mockito.verify(xmlStreamWriter).writeCharacters(variantValue);
    }

    @Test
    public void testVisitRootNode() throws IOException {
        RootNode rootNode = Mockito.mock(RootNode.class);
        BxmlNode child = Mockito.mock(BxmlNode.class);
        Mockito.when(rootNode.getChildren()).thenReturn(Arrays.asList(child));
        xmlBxmlNodeVisitor.visit(rootNode);
        ArgumentCaptor<BxmlNodeVisitor> captor = ArgumentCaptor.forClass(BxmlNodeVisitor.class);
        Mockito.verify(child).accept(captor.capture());
        BxmlNodeVisitor value = captor.getValue();
        Assert.assertTrue((value instanceof XmlBxmlNodeVisitor));
        Assert.assertNotEquals(xmlBxmlNodeVisitor, value);
    }
}

