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


import TagType.NODE_TAG;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CTagTest {
    private CTag m_tag;

    private final MockTagListener m_listener = new MockTagListener();

    @Test
    public void test_C_Constructor() {
        try {
            new CTag((-1), "Tag Name", "Tag Description", TagType.NODE_TAG, new MockSqlProvider());
            Assert.fail();
        } catch (final IllegalArgumentException exception) {
        }
        try {
            new CTag(0, null, "Tag Description", TagType.NODE_TAG, new MockSqlProvider());
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            new CTag(0, "Tag Name", null, TagType.NODE_TAG, new MockSqlProvider());
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            new CTag(0, "Tag Name", "Tag Description", null, new MockSqlProvider());
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            new CTag(0, "Tag Name", "Tag Description", TagType.NODE_TAG, null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        Assert.assertEquals(1, m_tag.getId());
        Assert.assertEquals("Tag Name", m_tag.getName());
        Assert.assertEquals("Tag Description", m_tag.getDescription());
        Assert.assertEquals(NODE_TAG, m_tag.getType());
    }

    @Test
    public void testSetDescription() throws CouldntSaveDataException {
        try {
            m_tag.setDescription(null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        Assert.assertEquals("Tag Description", m_tag.getDescription());
        m_tag.setDescription("Test Description");
        // Check listener events
        Assert.assertEquals("changedDescription=Test Description/", m_listener.eventList);
        // Check module
        Assert.assertEquals("Test Description", m_tag.getDescription());
        m_tag.setDescription("Tag Description");
        Assert.assertEquals("changedDescription=Test Description/changedDescription=Tag Description/", m_listener.eventList);
        m_tag.setDescription("Tag Description");
        Assert.assertEquals("changedDescription=Test Description/changedDescription=Tag Description/", m_listener.eventList);
        Assert.assertEquals("Tag Description", m_tag.getDescription());
    }

    @Test
    public void testSetName() throws CouldntSaveDataException {
        try {
            m_tag.setName(null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        Assert.assertEquals("Tag Name", m_tag.getName());
        m_tag.setName("Test Name");
        // Check listener events
        Assert.assertEquals("changedName=Test Name/", m_listener.eventList);
        // Check module
        Assert.assertEquals("Test Name", m_tag.getName());
        m_tag.setName("Tag Name");
        Assert.assertEquals("changedName=Test Name/changedName=Tag Name/", m_listener.eventList);
        m_tag.setName("Tag Name");
        Assert.assertEquals("changedName=Test Name/changedName=Tag Name/", m_listener.eventList);
        Assert.assertEquals("Tag Name", m_tag.getName());
    }
}

