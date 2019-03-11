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


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import java.awt.Color;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class ViewNodeTest {
    private FunctionNode m_node;

    private Tag m_initialTag;

    private TagManager m_nodeTagManager;

    private View m_view;

    @Test
    public void testBorderColor() {
        final MockViewNodeListener listener = new MockViewNodeListener();
        m_node.addListener(listener);
        Assert.assertEquals(Color.BLACK, m_node.getBorderColor());
        m_node.setBorderColor(Color.WHITE);
        Assert.assertEquals(Color.WHITE, m_node.getBorderColor());
        Assert.assertEquals("changedBorderColor;", listener.events);
        m_node.removeListener(listener);
    }

    @Test
    public void testColor() {
        final MockViewNodeListener listener = new MockViewNodeListener();
        m_node.addListener(listener);
        Assert.assertEquals(Color.MAGENTA, m_node.getColor());
        m_node.setColor(Color.WHITE);
        Assert.assertEquals(Color.WHITE, m_node.getColor());
        Assert.assertEquals("changedColor;", listener.events);
        m_node.removeListener(listener);
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals(m_view, m_node.getView());
        Assert.assertTrue(m_node.isVisible());
    }

    @Test
    public void testNeighbors() {
        Assert.assertEquals(1, m_node.getOutgoingEdges().size());
        Assert.assertEquals(1, m_node.getOutgoingEdges().get(0).getTarget().getIncomingEdges().size());
        Assert.assertEquals(1, m_node.getChildren().size());
        Assert.assertEquals(1, m_node.getChildren().get(0).getParents().size());
    }

    @Test
    public void testSelected() {
        final MockViewNodeListener listener = new MockViewNodeListener();
        m_node.addListener(listener);
        Assert.assertFalse(m_node.isSelected());
        m_node.setSelected(true);
        Assert.assertTrue(m_node.isSelected());
        Assert.assertEquals("changedSelection;", listener.events);
        m_node.removeListener(listener);
    }

    @Test
    public void testTagging() throws CouldntSaveDataException {
        final MockViewNodeListener listener = new MockViewNodeListener();
        m_node.addListener(listener);
        Assert.assertEquals(1, m_node.getTags().size());
        Assert.assertEquals(m_initialTag, m_node.getTags().get(0));
        Assert.assertTrue(m_node.isTagged(m_initialTag));
        final Tag newTag = m_nodeTagManager.addTag(null, "New Tag");
        Assert.assertFalse(m_node.isTagged(newTag));
        m_node.addTag(newTag);
        Assert.assertEquals(2, m_node.getTags().size());
        Assert.assertTrue(m_node.isTagged(m_initialTag));
        Assert.assertTrue(m_node.isTagged(newTag));
        Assert.assertEquals("addedTag;", listener.events);
        m_node.removeTag(newTag);
        Assert.assertEquals(1, m_node.getTags().size());
        Assert.assertEquals(m_initialTag, m_node.getTags().get(0));
        Assert.assertTrue(m_node.isTagged(m_initialTag));
        Assert.assertFalse(m_node.isTagged(newTag));
        Assert.assertEquals("addedTag;removedTag;", listener.events);
        m_node.removeTag(m_initialTag);
        Assert.assertEquals(0, m_node.getTags().size());
        Assert.assertFalse(m_node.isTagged(m_initialTag));
        Assert.assertFalse(m_node.isTagged(newTag));
        Assert.assertEquals("addedTag;removedTag;removedTag;", listener.events);
        m_node.removeListener(listener);
    }

    @Test
    public void testVisibility() {
        final MockViewNodeListener listener = new MockViewNodeListener();
        m_node.addListener(listener);
        Assert.assertTrue(m_node.isVisible());
        m_node.getNative().setVisible(false);
        Assert.assertFalse(m_node.isVisible());
        Assert.assertEquals("changedVisibility;", listener.events);
        m_node.removeListener(listener);
    }

    @Test
    public void testX() {
        final MockViewNodeListener listener = new MockViewNodeListener();
        m_node.addListener(listener);
        Assert.assertEquals(10, m_node.getX(), 0.1);
        m_node.setX(15);
        Assert.assertEquals(15, m_node.getX(), 0.1);
        Assert.assertEquals("changedX;", listener.events);
        m_node.removeListener(listener);
    }

    @Test
    public void testY() {
        final MockViewNodeListener listener = new MockViewNodeListener();
        m_node.addListener(listener);
        Assert.assertEquals(20, m_node.getY(), 0.1);
        m_node.setY(15);
        Assert.assertEquals(15, m_node.getY(), 0.1);
        Assert.assertEquals("changedY;", listener.events);
        m_node.removeListener(listener);
    }
}

