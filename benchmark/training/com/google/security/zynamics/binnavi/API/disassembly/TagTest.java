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


import TagType.NodeTag;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Tagging.CTag;
import com.google.security.zynamics.zylib.types.trees.ITreeNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class TagTest {
    private final ITreeNode<CTag> tagNode = new com.google.security.zynamics.zylib.types.trees.TreeNode<CTag>(new CTag(1, "Tag Name", "Tag Description", TagType.NODE_TAG, new MockSqlProvider()));

    private final Tag tag = new Tag(tagNode);

    @Test
    public void testConstructor() {
        Assert.assertEquals("Tag 'Tag Name'", tag.toString());
        Assert.assertEquals("Tag Description", tag.getDescription());
        Assert.assertNull(tag.getParent());
        Assert.assertEquals(0, tag.getChildren().size());
        Assert.assertEquals(NodeTag, tag.getType());
    }

    @Test
    public void testSetDescription() throws CouldntSaveDataException {
        final MockTagListener listener = new MockTagListener();
        tag.addListener(listener);
        tag.setDescription("D1");
        Assert.assertEquals("D1", tag.getDescription());
        Assert.assertEquals("D1", tagNode.getObject().getDescription());
        Assert.assertEquals("changedDescription;", listener.events);
        tagNode.getObject().setDescription("D2");
        Assert.assertEquals("D2", tag.getDescription());
        Assert.assertEquals("D2", tagNode.getObject().getDescription());
        Assert.assertEquals("changedDescription;changedDescription;", listener.events);
        tag.removeListener(listener);
    }

    @Test
    public void testSetName() throws CouldntSaveDataException {
        final MockTagListener listener = new MockTagListener();
        tag.addListener(listener);
        tag.setName("N1");
        Assert.assertEquals("N1", tag.getName());
        Assert.assertEquals("N1", tagNode.getObject().getName());
        Assert.assertEquals("changedName;", listener.events);
        tagNode.getObject().setName("N2");
        Assert.assertEquals("N2", tag.getName());
        Assert.assertEquals("N2", tagNode.getObject().getName());
        Assert.assertEquals("changedName;changedName;", listener.events);
        tag.removeListener(listener);
    }
}

