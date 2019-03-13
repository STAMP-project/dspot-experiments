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
package com.liferay.wiki.editor.configuration.internal;


import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Roberto D?az
 */
@RunWith(PowerMockRunner.class)
public class WikiLinksCKEditorEditorConfigContributorTest extends PowerMockito {
    @Test
    public void testItemSelectorURLWhenNullWikiPageAndValidNode() throws Exception {
        populateInputEditorWikiPageAttributes(0, 1);
        JSONObject originalJSONObject = getJSONObjectWithDefaultItemSelectorURL();
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        _wikiLinksCKEditorEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, null, null);
        JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject();
        expectedJSONObject.put("filebrowserBrowseUrl", "oneTabItemSelectorPortletURL");
        expectedJSONObject.put("removePlugins", "plugin1");
        JSONAssert.assertEquals(expectedJSONObject.toJSONString(), jsonObject.toJSONString(), true);
    }

    @Test
    public void testItemSelectorURLWhenValidWikiPageAndNode() throws Exception {
        populateInputEditorWikiPageAttributes(1, 1);
        JSONObject originalJSONObject = getJSONObjectWithDefaultItemSelectorURL();
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        _wikiLinksCKEditorEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, null, null);
        JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject();
        expectedJSONObject.put("filebrowserBrowseUrl", "twoTabsItemSelectorPortletURL");
        expectedJSONObject.put("removePlugins", "plugin1");
        JSONAssert.assertEquals(expectedJSONObject.toJSONString(), jsonObject.toJSONString(), true);
    }

    @Test
    public void testItemSelectorURLWhenValidWikiPageAndNullNode() throws Exception {
        populateInputEditorWikiPageAttributes(1, 0);
        JSONObject originalJSONObject = getJSONObjectWithDefaultItemSelectorURL();
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        _wikiLinksCKEditorEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, null, null);
        JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject();
        expectedJSONObject.put("filebrowserBrowseUrl", "oneTabItemSelectorPortletURL");
        expectedJSONObject.put("removePlugins", "plugin1");
        JSONAssert.assertEquals(expectedJSONObject.toJSONString(), jsonObject.toJSONString(), true);
    }

    private final Map<String, Object> _inputEditorTaglibAttributes = new HashMap<>();

    @Mock
    private ItemSelector _itemSelector;

    private WikiLinksCKEditorConfigContributor _wikiLinksCKEditorEditorConfigContributor;
}

