/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.loader.xml;


import com.github.dozermapper.core.AbstractDozerTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class SimpleElementReaderTest extends AbstractDozerTest {
    private SimpleElementReader reader;

    @Test
    public void testGetAttribute() {
        Element element = Mockito.mock(Element.class);
        Mockito.when(element.getAttribute("A")).thenReturn(" B ");
        String result = reader.getAttribute(element, "A");
        Assert.assertEquals("B", result);
    }

    @Test
    public void testGetNodeValue() {
        Element element = Mockito.mock(Element.class);
        Node node = Mockito.mock(Node.class);
        Mockito.when(node.getNodeValue()).thenReturn(" B ");
        Mockito.when(element.getFirstChild()).thenReturn(node);
        String result = reader.getNodeValue(element);
        Assert.assertEquals("B", result);
    }

    @Test
    public void testNodeValueIsNull() {
        Element element = Mockito.mock(Element.class);
        Node node = Mockito.mock(Node.class);
        Mockito.when(node.getNodeValue()).thenReturn(null);
        Mockito.when(element.getFirstChild()).thenReturn(node);
        Assert.assertEquals("", reader.getNodeValue(element));
    }
}

