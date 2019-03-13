/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.API.disassembly;


import EdgeType.JumpUnconditional;
import java.awt.Color;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class ViewEdgeTest {
    private ViewEdge m_edge;

    private TextNode m_source;

    private TextNode m_target;

    @Test
    public void testConstructor() {
        Assert.assertEquals(m_source, m_edge.getSource());
        Assert.assertEquals(m_target, m_edge.getTarget());
        Assert.assertEquals(JumpUnconditional, m_edge.getType());
        Assert.assertTrue(m_edge.isVisible());
        Assert.assertEquals("View Edge [Text Node with: '1' comments. -> Text Node with: '1' comments.]", m_edge.toString());
    }

    @Test
    public void testSetColor() {
        final MockViewEdgeListener listener = new MockViewEdgeListener();
        m_edge.addListener(listener);
        m_edge.setColor(Color.RED);
        Assert.assertEquals(Color.RED, m_edge.getColor());
        Assert.assertEquals("changedColor;", listener.events);
        m_edge.removeListener(listener);
    }

    @Test
    public void testSetVisibility() {
        final MockViewEdgeListener listener = new MockViewEdgeListener();
        m_edge.addListener(listener);
        m_edge.setVisible(false);
        Assert.assertFalse(m_edge.isVisible());
        Assert.assertEquals("changedVisibility;", listener.events);
        m_edge.removeListener(listener);
    }
}

