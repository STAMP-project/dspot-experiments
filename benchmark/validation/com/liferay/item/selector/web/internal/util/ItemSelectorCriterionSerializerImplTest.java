/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.item.selector.web.internal.util;


import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.web.internal.FlickrItemSelectorCriterion;
import com.liferay.item.selector.web.internal.TestFileEntryItemSelectorReturnType;
import com.liferay.item.selector.web.internal.TestStringItemSelectorReturnType;
import com.liferay.item.selector.web.internal.TestURLItemSelectorReturnType;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Iv?n Zaera
 * @author Roberto D?az
 */
public class ItemSelectorCriterionSerializerImplTest {
    @Test
    public void testGetProperties() {
        List<ItemSelectorReturnType> desiredItemSelectorReturnTypes = new ArrayList<>();
        desiredItemSelectorReturnTypes.add(_testURLItemSelectorReturnType);
        _flickrItemSelectorCriterion.setDesiredItemSelectorReturnTypes(desiredItemSelectorReturnTypes);
        String json = serialize(_flickrItemSelectorCriterion);
        Class<? extends ItemSelectorReturnType> testURLItemSelectorReturnTypeClass = _testURLItemSelectorReturnType.getClass();
        json = _assert((("\"desiredItemSelectorReturnTypes\":\"" + (testURLItemSelectorReturnTypeClass.getName())) + "\""), json);
        json = _assert("\"tags\":[\"me\",\"photo\",\"picture\"]", json);
        json = _assert("\"user\":\"anonymous\"", json);
        Assert.assertEquals("{,,}", json);
    }

    @Test
    public void testSetProperties() {
        Class<? extends ItemSelectorReturnType> testURLItemSelectorReturnTypeClass = _testURLItemSelectorReturnType.getClass();
        String json = (("{\"desiredItemSelectorReturnTypes\":\"" + (testURLItemSelectorReturnTypeClass.getName())) + "\",\"tags\":") + "[\"tag1\",\"tag2\",\"tag3\"],\"user\":\"Joe Bloggs\"}";
        _flickrItemSelectorCriterion = deserialize(_flickrItemSelectorCriterion.getClass(), json);
        Assert.assertEquals("Joe Bloggs", _flickrItemSelectorCriterion.getUser());
        Assert.assertArrayEquals(new String[]{ "tag1", "tag2", "tag3" }, _flickrItemSelectorCriterion.getTags());
        List<ItemSelectorReturnType> expectedDesiredItemSelectorReturnTypes = new ArrayList<>();
        expectedDesiredItemSelectorReturnTypes.add(_testURLItemSelectorReturnType);
        Assert.assertEquals(expectedDesiredItemSelectorReturnTypes, getDesiredItemSelectorReturnTypes());
    }

    private FlickrItemSelectorCriterion _flickrItemSelectorCriterion;

    private final ItemSelectorCriterionSerializerImplTest.StubItemSelectorCriterionSerializerImpl _stubItemSelectorCriterionSerializerImpl = new ItemSelectorCriterionSerializerImplTest.StubItemSelectorCriterionSerializerImpl();

    private final ItemSelectorReturnType _testFileEntryItemSelectorReturnType = new TestFileEntryItemSelectorReturnType();

    private final ItemSelectorReturnType _testStringItemSelectorReturnType = new TestStringItemSelectorReturnType();

    private final ItemSelectorReturnType _testURLItemSelectorReturnType = new TestURLItemSelectorReturnType();

    private class StubItemSelectorCriterionSerializerImpl extends ItemSelectorCriterionSerializerImpl {
        @Override
        public void addItemSelectorReturnType(ItemSelectorReturnType itemSelectorReturnType) {
            super.addItemSelectorReturnType(itemSelectorReturnType);
        }
    }
}

