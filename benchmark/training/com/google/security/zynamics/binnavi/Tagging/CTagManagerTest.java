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
package com.google.security.zynamics.binnavi.Tagging;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.zylib.types.trees.DepthFirstSorter;
import com.google.security.zynamics.zylib.types.trees.ITreeNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CTagManagerTest {
    @Test
    public void test_C_Constructors() {
        try {
            new CTagManager(null, TagType.VIEW_TAG, new MockSqlProvider());
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            new CTagManager(new com.google.security.zynamics.zylib.types.trees.Tree<CTag>(new com.google.security.zynamics.zylib.types.trees.TreeNode<CTag>(new CTag(1, "Foo", "Bar", TagType.VIEW_TAG, new MockSqlProvider()))), null, new MockSqlProvider());
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            new CTagManager(new com.google.security.zynamics.zylib.types.trees.Tree<CTag>(new com.google.security.zynamics.zylib.types.trees.TreeNode<CTag>(new CTag(1, "Foo", "Bar", TagType.VIEW_TAG, new MockSqlProvider()))), TagType.VIEW_TAG, null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        final MockSqlProvider sql = new MockSqlProvider();
        new CTagManager(new com.google.security.zynamics.zylib.types.trees.Tree<CTag>(new com.google.security.zynamics.zylib.types.trees.TreeNode<CTag>(new CTag(1, "Foo", "Bar", TagType.VIEW_TAG, sql))), TagType.VIEW_TAG, sql);
    }

    @Test
    public void testTags() throws CouldntDeleteException, CouldntSaveDataException {
        final MockSqlProvider sql = new MockSqlProvider();
        final CTagManager manager = new CTagManager(new com.google.security.zynamics.zylib.types.trees.Tree<CTag>(new com.google.security.zynamics.zylib.types.trees.TreeNode<CTag>(new CTag(1, "Foo", "Bar", TagType.VIEW_TAG, sql))), TagType.VIEW_TAG, sql);
        final MockTagManagerListener listener = new MockTagManagerListener();
        // ----------------------------------------- ADDING TAGS ---------------------------------------
        manager.addListener(listener);
        try {
            manager.addTag(null, "Tag I");
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        Assert.assertEquals("", listener.eventList);
        try {
            manager.addTag(manager.getRootTag(), null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        Assert.assertEquals("", listener.eventList);
        final ITreeNode<CTag> tag1 = manager.addTag(manager.getRootTag(), "Tag I");
        // Check listener events
        Assert.assertEquals("addedTag/", listener.eventList);
        // Check tag
        Assert.assertEquals(manager.getRootTag(), tag1.getParent());
        Assert.assertTrue(manager.getRootTag().getChildren().contains(tag1));
        final ITreeNode<CTag> tag2 = manager.addTag(manager.getRootTag(), "Tag II");
        // Check listener events
        Assert.assertEquals("addedTag/addedTag/", listener.eventList);
        // Check tag
        Assert.assertEquals(manager.getRootTag(), tag1.getParent());
        Assert.assertTrue(manager.getRootTag().getChildren().contains(tag1));
        Assert.assertEquals(manager.getRootTag(), tag2.getParent());
        Assert.assertTrue(manager.getRootTag().getChildren().contains(tag2));
        final ITreeNode<CTag> tag3 = manager.addTag(tag2, "Tag III");
        // Check listener events
        Assert.assertEquals("addedTag/addedTag/addedTag/", listener.eventList);
        // Check tag
        Assert.assertEquals(manager.getRootTag(), tag1.getParent());
        Assert.assertTrue(manager.getRootTag().getChildren().contains(tag1));
        Assert.assertEquals(manager.getRootTag(), tag2.getParent());
        Assert.assertTrue(manager.getRootTag().getChildren().contains(tag2));
        Assert.assertEquals(tag2, tag3.getParent());
        Assert.assertTrue(tag2.getChildren().contains(tag3));
        // ------------------------------------------- INSERT TAGS
        // ---------------------------------------------------
        try {
            manager.insertTag(null, "Foo");
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            manager.insertTag(manager.getRootTag(), null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        final ITreeNode<CTag> tag4 = manager.insertTag(manager.getRootTag(), "Tag IV");
        // Check listener events
        Assert.assertEquals("addedTag/addedTag/addedTag/insertedTag/", listener.eventList);
        // Check tag
        Assert.assertEquals(tag4, tag1.getParent());
        Assert.assertFalse(manager.getRootTag().getChildren().contains(tag1));
        Assert.assertTrue(tag4.getChildren().contains(tag1));
        Assert.assertEquals(tag4, tag2.getParent());
        Assert.assertFalse(manager.getRootTag().getChildren().contains(tag2));
        Assert.assertTrue(tag4.getChildren().contains(tag2));
        Assert.assertEquals(tag2, tag3.getParent());
        Assert.assertTrue(tag2.getChildren().contains(tag3));
        final ITreeNode<CTag> tag5 = manager.insertTag(tag3, "Tag V");
        // Check listener events
        Assert.assertEquals("addedTag/addedTag/addedTag/insertedTag/insertedTag/", listener.eventList);
        // Check tag
        Assert.assertEquals(tag4, tag1.getParent());
        Assert.assertFalse(manager.getRootTag().getChildren().contains(tag1));
        Assert.assertTrue(tag4.getChildren().contains(tag1));
        Assert.assertEquals(tag4, tag2.getParent());
        Assert.assertFalse(manager.getRootTag().getChildren().contains(tag2));
        Assert.assertTrue(tag4.getChildren().contains(tag2));
        Assert.assertEquals(tag2, tag3.getParent());
        Assert.assertTrue(tag2.getChildren().contains(tag3));
        Assert.assertEquals(tag3, tag5.getParent());
        Assert.assertTrue(tag3.getChildren().contains(tag5));
        // ------------------------------------------------ DELETE TAGS
        // -----------------------------------------------
        try {
            manager.deleteTag(null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            manager.deleteTag(manager.getRootTag());
            Assert.fail();
        } catch (final IllegalArgumentException exception) {
        }
        manager.deleteTag(tag5);
        // Check listener events
        Assert.assertEquals("addedTag/addedTag/addedTag/insertedTag/insertedTag/deletedTag/", listener.eventList);
        // Check tag
        Assert.assertEquals(tag4, tag1.getParent());
        Assert.assertFalse(manager.getRootTag().getChildren().contains(tag1));
        Assert.assertTrue(tag4.getChildren().contains(tag1));
        Assert.assertEquals(tag4, tag2.getParent());
        Assert.assertFalse(manager.getRootTag().getChildren().contains(tag2));
        Assert.assertTrue(tag4.getChildren().contains(tag2));
        Assert.assertEquals(tag2, tag3.getParent());
        Assert.assertTrue(tag2.getChildren().contains(tag3));
        Assert.assertNull(tag5.getParent());
        Assert.assertFalse(tag3.getChildren().contains(tag5));
        Assert.assertNull(CTagHelpers.findTag(manager.getRootTag(), 5));
        manager.deleteTag(tag4);
        // Check listener events
        Assert.assertEquals("addedTag/addedTag/addedTag/insertedTag/insertedTag/deletedTag/deletedTag/", listener.eventList);
        // Check tag
        Assert.assertEquals(manager.getRootTag(), tag1.getParent());
        Assert.assertTrue(manager.getRootTag().getChildren().contains(tag1));
        Assert.assertEquals(manager.getRootTag(), tag2.getParent());
        Assert.assertTrue(manager.getRootTag().getChildren().contains(tag2));
        Assert.assertEquals(tag2, tag3.getParent());
        Assert.assertTrue(tag2.getChildren().contains(tag3));
        Assert.assertNull(tag4.getParent());
        Assert.assertFalse(manager.getRootTag().getChildren().contains(tag4));
        Assert.assertNull(CTagHelpers.findTag(manager.getRootTag(), 4));
    }

    @Test
    public void testTags2() throws CouldntDeleteException, CouldntSaveDataException {
        final MockSqlProvider sql = new MockSqlProvider();
        final CTagManager manager = new CTagManager(new com.google.security.zynamics.zylib.types.trees.Tree<CTag>(new com.google.security.zynamics.zylib.types.trees.TreeNode<CTag>(new CTag(1, "Foo", "Bar", TagType.VIEW_TAG, sql))), TagType.VIEW_TAG, sql);
        final MockTagManagerListener listener = new MockTagManagerListener();
        manager.addListener(listener);
        // ----------------------------------------- ADDING TAGS ---------------------------------------
        final ITreeNode<CTag> tag1 = manager.addTag(manager.getRootTag(), "Tag I");
        final ITreeNode<CTag> tag2 = manager.addTag(manager.getRootTag(), "Tag II");
        final ITreeNode<CTag> tag3 = manager.addTag(tag2, "Tag III");
        // ------------------------------------------- INSERT TAGS
        // ---------------------------------------------------
        final ITreeNode<CTag> tag4 = manager.insertTag(manager.getRootTag(), "Tag IV");
        final ITreeNode<CTag> tag5 = manager.insertTag(tag3, "Tag V");
        // ------------------------------------------------ DELETE TAGS
        // -----------------------------------------------
        try {
            manager.deleteTagSubTree(null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            manager.deleteTagSubTree(manager.getRootTag());
            Assert.fail();
        } catch (final IllegalArgumentException exception) {
        }
        manager.deleteTagSubTree(tag5);
        // Check listener events
        Assert.assertEquals("addedTag/addedTag/addedTag/insertedTag/insertedTag/deletedTag/", listener.eventList);
        // Check tag
        Assert.assertEquals(4, DepthFirstSorter.getSortedList(manager.getRootTag()).size());
        Assert.assertEquals(tag4, tag1.getParent());
        Assert.assertFalse(manager.getRootTag().getChildren().contains(tag1));
        Assert.assertTrue(tag4.getChildren().contains(tag1));
        Assert.assertEquals(tag4, tag2.getParent());
        Assert.assertFalse(manager.getRootTag().getChildren().contains(tag2));
        Assert.assertTrue(tag4.getChildren().contains(tag2));
        Assert.assertEquals(tag2, tag3.getParent());
        Assert.assertTrue(tag2.getChildren().contains(tag3));
        Assert.assertNull(tag5.getParent());
        Assert.assertFalse(tag3.getChildren().contains(tag5));
        Assert.assertNull(CTagHelpers.findTag(manager.getRootTag(), 5));
        manager.deleteTagSubTree(tag4);
        // Check listener events
        Assert.assertEquals("addedTag/addedTag/addedTag/insertedTag/insertedTag/deletedTag/deletedSubtree/", listener.eventList);
        // Check tag
        Assert.assertEquals(0, DepthFirstSorter.getSortedList(manager.getRootTag()).size());
    }
}

