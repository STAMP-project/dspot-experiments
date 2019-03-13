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
package com.liferay.item.selector.web.internal;


import ItemSelectorImpl.PARAMETER_CRITERIA;
import ItemSelectorImpl.PARAMETER_ITEM_SELECTED_EVENT_NAME;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorRendering;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewRenderer;
import com.liferay.item.selector.web.internal.util.ItemSelectorCriterionSerializerImpl;
import com.liferay.portal.kernel.util.StringUtil;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;


/**
 *
 *
 * @author Iv?n Zaera
 * @author Roberto D?az
 */
public class ItemSelectorImplTest extends PowerMockito {
    @Test
    public void testGetItemSelectedEventName() {
        String itemSelectorURL = getItemSelectorURL("testItemSelectedEventName", _mediaItemSelectorCriterion, _flickrItemSelectorCriterion);
        setUpItemSelectionCriterionHandlers();
        Assert.assertEquals("testItemSelectedEventName", _itemSelectorImpl.getItemSelectedEventName(itemSelectorURL));
    }

    @Test
    public void testGetItemSelectorCriteriaFromItemSelectorURL() {
        String itemSelectorURL = getItemSelectorURL(StringUtil.randomString(), _mediaItemSelectorCriterion, _flickrItemSelectorCriterion);
        setUpItemSelectionCriterionHandlers();
        List<ItemSelectorCriterion> itemSelectorCriteria = _itemSelectorImpl.getItemSelectorCriteria(itemSelectorURL);
        Assert.assertEquals(itemSelectorCriteria.toString(), 2, itemSelectorCriteria.size());
        MediaItemSelectorCriterion mediaItemSelectorCriterion = ((MediaItemSelectorCriterion) (itemSelectorCriteria.get(0)));
        Assert.assertEquals("jpg", mediaItemSelectorCriterion.getFileExtension());
        Assert.assertEquals(2048, mediaItemSelectorCriterion.getMaxSize());
        List<ItemSelectorReturnType> desiredItemSelectorReturnTypes = getDesiredItemSelectorReturnTypes();
        Assert.assertEquals(desiredItemSelectorReturnTypes.toString(), 2, desiredItemSelectorReturnTypes.size());
        Assert.assertTrue(((desiredItemSelectorReturnTypes.get(0)) instanceof TestFileEntryItemSelectorReturnType));
        Assert.assertTrue(((desiredItemSelectorReturnTypes.get(1)) instanceof TestURLItemSelectorReturnType));
        Assert.assertTrue(((itemSelectorCriteria.get(1)) instanceof FlickrItemSelectorCriterion));
    }

    @Test
    public void testGetItemSelectorParameters() {
        Map<String, String[]> parameters = _itemSelectorImpl.getItemSelectorParameters("itemSelectedEventName", _mediaItemSelectorCriterion, _flickrItemSelectorCriterion);
        Assert.assertEquals("itemSelectedEventName", parameters.get(PARAMETER_ITEM_SELECTED_EVENT_NAME)[0]);
        Assert.assertEquals((((MediaItemSelectorCriterion.class.getName()) + ",") + (FlickrItemSelectorCriterion.class.getName())), parameters.get(PARAMETER_CRITERIA)[0]);
        Assert.assertNull(parameters.get("0_desiredItemSelectorReturnTypes"));
        Assert.assertNotNull(parameters.get("0_json")[0]);
        Assert.assertNotNull(parameters.get("1_json")[0]);
        Assert.assertEquals(parameters.toString(), 4, parameters.size());
    }

    @Test
    public void testGetItemSelectorRendering() {
        setUpItemSelectionCriterionHandlers();
        ItemSelectorRendering itemSelectorRendering = getItemSelectorRendering();
        Assert.assertEquals("itemSelectedEventName", itemSelectorRendering.getItemSelectedEventName());
        List<ItemSelectorViewRenderer> itemSelectorViewRenderers = itemSelectorRendering.getItemSelectorViewRenderers();
        ItemSelectorViewRenderer mediaItemSelectorViewRenderer = itemSelectorViewRenderers.get(0);
        MediaItemSelectorCriterion mediaItemSelectorCriterion = ((MediaItemSelectorCriterion) (mediaItemSelectorViewRenderer.getItemSelectorCriterion()));
        Assert.assertEquals(_mediaItemSelectorCriterion.getFileExtension(), mediaItemSelectorCriterion.getFileExtension());
        Assert.assertEquals(_mediaItemSelectorCriterion.getMaxSize(), mediaItemSelectorCriterion.getMaxSize());
        Assert.assertTrue((((ItemSelectorView<?>) (mediaItemSelectorViewRenderer.getItemSelectorView())) instanceof MediaItemSelectorView));
        ItemSelectorViewRenderer flickrItemSelectorViewRenderer = itemSelectorViewRenderers.get(1);
        FlickrItemSelectorCriterion flickrItemSelectorCriterion = ((FlickrItemSelectorCriterion) (flickrItemSelectorViewRenderer.getItemSelectorCriterion()));
        Assert.assertEquals(_flickrItemSelectorCriterion.getUser(), flickrItemSelectorCriterion.getUser());
        Assert.assertTrue((((ItemSelectorView<?>) (flickrItemSelectorViewRenderer.getItemSelectorView())) instanceof FlickrItemSelectorView));
        Assert.assertEquals(itemSelectorViewRenderers.toString(), 2, itemSelectorViewRenderers.size());
    }

    private FlickrItemSelectorCriterion _flickrItemSelectorCriterion;

    private ItemSelectorImpl _itemSelectorImpl;

    private MediaItemSelectorCriterion _mediaItemSelectorCriterion;

    private final ItemSelectorImplTest.StubItemSelectorCriterionSerializerImpl _stubItemSelectorCriterionSerializer = new ItemSelectorImplTest.StubItemSelectorCriterionSerializerImpl();

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

