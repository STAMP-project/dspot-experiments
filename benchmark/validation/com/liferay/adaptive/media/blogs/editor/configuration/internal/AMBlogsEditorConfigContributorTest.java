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
package com.liferay.adaptive.media.blogs.editor.configuration.internal;


import StringPool.BLANK;
import com.liferay.adaptive.media.image.item.selector.AMImageFileEntryItemSelectorReturnType;
import com.liferay.blogs.item.selector.criterion.BlogsItemSelectorCriterion;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.item.selector.criteria.upload.criterion.UploadItemSelectorCriterion;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.portlet.PortletURL;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Sergio Gonz?lez
 */
public class AMBlogsEditorConfigContributorTest extends PowerMockito {
    @Test
    public void testAdaptiveMediaFileEntryAttributeNameIsAdded() throws Exception {
        PortletURL itemSelectorPortletURL = mock(PortletURL.class);
        when(itemSelectorPortletURL.toString()).thenReturn("itemSelectorPortletURL");
        when(_itemSelector.getItemSelectorURL(Mockito.any(RequestBackedPortletURLFactory.class), Mockito.anyString(), Mockito.any(ItemSelectorCriterion.class))).thenReturn(itemSelectorPortletURL);
        when(_itemSelector.getItemSelectedEventName(Mockito.anyString())).thenReturn("selectedEventName");
        when(_itemSelector.getItemSelectorCriteria("blogsItemSelectorCriterionFileEntryItemSelectorReturnType")).thenReturn(_getBlogsItemSelectorCriterionFileEntryItemSelectorReturnType());
        JSONObject originalJSONObject = JSONFactoryUtil.createJSONObject();
        originalJSONObject.put("filebrowserImageBrowseLinkUrl", "blogsItemSelectorCriterionFileEntryItemSelectorReturnType");
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        AMBlogsEditorConfigContributor amBlogsEditorConfigContributor = new AMBlogsEditorConfigContributor();
        amBlogsEditorConfigContributor.setItemSelector(_itemSelector);
        amBlogsEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, _themeDisplay, _requestBackedPortletURLFactory);
        Assert.assertEquals("data-fileentryid", jsonObject.get("adaptiveMediaFileEntryAttributeName"));
    }

    @Test
    public void testAdaptiveMediaIsAddedToExtraPlugins() throws Exception {
        PortletURL itemSelectorPortletURL = mock(PortletURL.class);
        when(itemSelectorPortletURL.toString()).thenReturn("itemSelectorPortletURL");
        when(_itemSelector.getItemSelectorURL(Mockito.any(RequestBackedPortletURLFactory.class), Mockito.anyString(), Mockito.any(ItemSelectorCriterion.class))).thenReturn(itemSelectorPortletURL);
        when(_itemSelector.getItemSelectedEventName(Mockito.anyString())).thenReturn("selectedEventName");
        when(_itemSelector.getItemSelectorCriteria("blogsItemSelectorCriterionFileEntryItemSelectorReturnType")).thenReturn(_getBlogsItemSelectorCriterionFileEntryItemSelectorReturnType());
        JSONObject originalJSONObject = JSONFactoryUtil.createJSONObject();
        originalJSONObject.put("filebrowserImageBrowseLinkUrl", "blogsItemSelectorCriterionFileEntryItemSelectorReturnType");
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        AMBlogsEditorConfigContributor amBlogsEditorConfigContributor = new AMBlogsEditorConfigContributor();
        amBlogsEditorConfigContributor.setItemSelector(_itemSelector);
        amBlogsEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, _themeDisplay, _requestBackedPortletURLFactory);
        Assert.assertEquals("adaptivemedia", jsonObject.getString("extraPlugins"));
    }

    @Test
    public void testAdaptiveMediaIsExtraPlugins() throws Exception {
        PortletURL itemSelectorPortletURL = mock(PortletURL.class);
        when(itemSelectorPortletURL.toString()).thenReturn("itemSelectorPortletURL");
        when(_itemSelector.getItemSelectorURL(Mockito.any(RequestBackedPortletURLFactory.class), Mockito.anyString(), Mockito.any(ItemSelectorCriterion.class))).thenReturn(itemSelectorPortletURL);
        when(_itemSelector.getItemSelectedEventName(Mockito.anyString())).thenReturn("selectedEventName");
        when(_itemSelector.getItemSelectorCriteria("blogsItemSelectorCriterionFileEntryItemSelectorReturnType")).thenReturn(_getBlogsItemSelectorCriterionFileEntryItemSelectorReturnType());
        JSONObject originalJSONObject = JSONFactoryUtil.createJSONObject();
        originalJSONObject.put("extraPlugins", "ae_placeholder,ae_selectionregion,ae_uicore");
        originalJSONObject.put("filebrowserImageBrowseLinkUrl", "blogsItemSelectorCriterionFileEntryItemSelectorReturnType");
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        AMBlogsEditorConfigContributor amBlogsEditorConfigContributor = new AMBlogsEditorConfigContributor();
        amBlogsEditorConfigContributor.setItemSelector(_itemSelector);
        amBlogsEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, _themeDisplay, _requestBackedPortletURLFactory);
        Assert.assertEquals("ae_placeholder,ae_selectionregion,ae_uicore,adaptivemedia", jsonObject.getString("extraPlugins"));
    }

    @Test
    public void testAddAMImageFileEntryItemSelectorReturnType() throws Exception {
        AMBlogsEditorConfigContributor amBlogsEditorConfigContributor = new AMBlogsEditorConfigContributor();
        BlogsItemSelectorCriterion blogsItemSelectorCriterion = new BlogsItemSelectorCriterion();
        blogsItemSelectorCriterion.setDesiredItemSelectorReturnTypes(Collections.<ItemSelectorReturnType>singletonList(new FileEntryItemSelectorReturnType()));
        amBlogsEditorConfigContributor.addAMImageFileEntryItemSelectorReturnType(blogsItemSelectorCriterion);
        List<ItemSelectorReturnType> desiredItemSelectorReturnTypes = blogsItemSelectorCriterion.getDesiredItemSelectorReturnTypes();
        Assert.assertEquals(desiredItemSelectorReturnTypes.toString(), 2, desiredItemSelectorReturnTypes.size());
        Assert.assertTrue(((desiredItemSelectorReturnTypes.get(0)) instanceof AMImageFileEntryItemSelectorReturnType));
        Assert.assertTrue(((desiredItemSelectorReturnTypes.get(1)) instanceof FileEntryItemSelectorReturnType));
    }

    @Test
    public void testAMReturnTypeIsAddedToAllItemSelectorCriteria() throws Exception {
        ItemSelectorCriterion[] itemSelectorCriteria = new ItemSelectorCriterion[]{ _initializeItemSelectorCriterion(new BlogsItemSelectorCriterion()), _initializeItemSelectorCriterion(new FileItemSelectorCriterion()), _initializeItemSelectorCriterion(new ImageItemSelectorCriterion()), _initializeItemSelectorCriterion(new UploadItemSelectorCriterion()) };
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
        jsonObject.put("filebrowserImageBrowseLinkUrl", RandomTestUtil.randomString());
        when(_itemSelector.getItemSelectorCriteria(Mockito.anyString())).thenReturn(Arrays.asList(itemSelectorCriteria));
        when(_itemSelector.getItemSelectedEventName(Mockito.anyString())).thenReturn(RandomTestUtil.randomString());
        when(_itemSelector.getItemSelectorURL(Mockito.any(RequestBackedPortletURLFactory.class), Mockito.anyString(), Mockito.<ItemSelectorCriterion>anyVararg())).thenReturn(_portletURL);
        when(_portletURL.toString()).thenReturn(RandomTestUtil.randomString());
        AMBlogsEditorConfigContributor amBlogsEditorConfigContributor = new AMBlogsEditorConfigContributor();
        amBlogsEditorConfigContributor.setItemSelector(_itemSelector);
        amBlogsEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, _themeDisplay, _requestBackedPortletURLFactory);
        for (ItemSelectorCriterion itemSelectorCriterion : itemSelectorCriteria) {
            List<ItemSelectorReturnType> desiredItemSelectorReturnTypes = itemSelectorCriterion.getDesiredItemSelectorReturnTypes();
            Stream<ItemSelectorReturnType> itemSelectorReturnTypeStream = desiredItemSelectorReturnTypes.stream();
            Assert.assertTrue(itemSelectorReturnTypeStream.allMatch(( itemSelectorReturnType) -> itemSelectorReturnType instanceof AMImageFileEntryItemSelectorReturnType));
        }
    }

    @Test
    public void testImgIsAddedToAllowedContent() throws Exception {
        JSONObject originalJSONObject = JSONFactoryUtil.createJSONObject();
        originalJSONObject.put("allowedContent", "a[*](*); div(*);");
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        AMBlogsEditorConfigContributor amBlogsEditorConfigContributor = new AMBlogsEditorConfigContributor();
        amBlogsEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, _themeDisplay, _requestBackedPortletURLFactory);
        JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject();
        expectedJSONObject.put("allowedContent", "a[*](*); div(*); img[*](*){*};");
        JSONAssert.assertEquals(expectedJSONObject.toJSONString(), jsonObject.toJSONString(), true);
    }

    @Test
    public void testImgIsAllowedContent() throws Exception {
        JSONObject originalJSONObject = JSONFactoryUtil.createJSONObject();
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        AMBlogsEditorConfigContributor amBlogsEditorConfigContributor = new AMBlogsEditorConfigContributor();
        amBlogsEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, _themeDisplay, _requestBackedPortletURLFactory);
        JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject();
        JSONAssert.assertEquals(expectedJSONObject.toJSONString(), jsonObject.toJSONString(), true);
    }

    @Test
    public void testImgIsNotAddedToAllowedContentIfAlreadyPresent() throws Exception {
        JSONObject originalJSONObject = JSONFactoryUtil.createJSONObject();
        originalJSONObject.put("allowedContent", "a[*](*); div(*); img[*](*){*};");
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        AMBlogsEditorConfigContributor amBlogsEditorConfigContributor = new AMBlogsEditorConfigContributor();
        amBlogsEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, _themeDisplay, _requestBackedPortletURLFactory);
        JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject();
        expectedJSONObject.put("allowedContent", "a[*](*); div(*); img[*](*){*};");
        JSONAssert.assertEquals(expectedJSONObject.toJSONString(), jsonObject.toJSONString(), true);
    }

    @Test
    public void testImgIsNotAddedToAllowedContentIfAnyContentAllowed() throws Exception {
        JSONObject originalJSONObject = JSONFactoryUtil.createJSONObject();
        originalJSONObject.put("allowedContent", Boolean.TRUE.toString());
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        AMBlogsEditorConfigContributor amBlogsEditorConfigContributor = new AMBlogsEditorConfigContributor();
        amBlogsEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, _themeDisplay, _requestBackedPortletURLFactory);
        JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject();
        expectedJSONObject.put("allowedContent", Boolean.TRUE.toString());
        JSONAssert.assertEquals(expectedJSONObject.toJSONString(), jsonObject.toJSONString(), true);
    }

    @Test
    public void testItemSelectorURLWhenNoFileBrowserImageBrowseLinkUrl() throws Exception {
        JSONObject originalJSONObject = JSONFactoryUtil.createJSONObject();
        originalJSONObject.put("filebrowserImageBrowseLinkUrl", BLANK);
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        AMBlogsEditorConfigContributor amBlogsEditorConfigContributor = new AMBlogsEditorConfigContributor();
        amBlogsEditorConfigContributor.setItemSelector(_itemSelector);
        amBlogsEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, _themeDisplay, _requestBackedPortletURLFactory);
        Mockito.verify(_itemSelector, Mockito.never()).getItemSelectorURL(Mockito.any(RequestBackedPortletURLFactory.class), Mockito.anyString(), Mockito.any(ItemSelectorCriterion.class));
        JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        JSONAssert.assertEquals(expectedJSONObject.toJSONString(), jsonObject.toJSONString(), true);
    }

    @Test
    public void testItemSelectorURLWithAudioItemSelectorCriterion() throws Exception {
        when(_itemSelector.getItemSelectorCriteria("audioItemSelectorCriterionFileEntryItemSelectorReturnType")).thenReturn(_getAudioItemSelectorCriterionFileEntryItemSelectorReturnType());
        JSONObject originalJSONObject = JSONFactoryUtil.createJSONObject();
        originalJSONObject.put("filebrowserImageBrowseLinkUrl", "audioItemSelectorCriterionFileEntryItemSelectorReturnType");
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        AMBlogsEditorConfigContributor amBlogsEditorConfigContributor = new AMBlogsEditorConfigContributor();
        amBlogsEditorConfigContributor.setItemSelector(_itemSelector);
        amBlogsEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, _themeDisplay, _requestBackedPortletURLFactory);
        Mockito.verify(_itemSelector, Mockito.never()).getItemSelectorURL(Mockito.any(RequestBackedPortletURLFactory.class), Mockito.anyString(), Mockito.any(ItemSelectorCriterion.class));
        JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        JSONAssert.assertEquals(expectedJSONObject.toJSONString(), jsonObject.toJSONString(), true);
    }

    @Test
    public void testItemSelectorURLWithBlogsItemSelectorCriterion() throws Exception {
        PortletURL itemSelectorPortletURL = mock(PortletURL.class);
        when(itemSelectorPortletURL.toString()).thenReturn("itemSelectorPortletURL");
        when(_itemSelector.getItemSelectorURL(Mockito.any(RequestBackedPortletURLFactory.class), Mockito.anyString(), Mockito.any(ItemSelectorCriterion.class))).thenReturn(itemSelectorPortletURL);
        when(_itemSelector.getItemSelectedEventName(Mockito.anyString())).thenReturn("selectedEventName");
        when(_itemSelector.getItemSelectorCriteria("blogsItemSelectorCriterionFileEntryItemSelectorReturnType")).thenReturn(_getBlogsItemSelectorCriterionFileEntryItemSelectorReturnType());
        JSONObject originalJSONObject = JSONFactoryUtil.createJSONObject();
        originalJSONObject.put("filebrowserImageBrowseLinkUrl", "blogsItemSelectorCriterionFileEntryItemSelectorReturnType");
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        AMBlogsEditorConfigContributor amBlogsEditorConfigContributor = new AMBlogsEditorConfigContributor();
        amBlogsEditorConfigContributor.setItemSelector(_itemSelector);
        amBlogsEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, _themeDisplay, _requestBackedPortletURLFactory);
        Mockito.verify(_itemSelector).getItemSelectorURL(Mockito.any(RequestBackedPortletURLFactory.class), Mockito.anyString(), Mockito.any(ItemSelectorCriterion.class));
    }

    @Test
    public void testItemSelectorURLWithFileItemSelectorCriterion() throws Exception {
        PortletURL itemSelectorPortletURL = mock(PortletURL.class);
        when(itemSelectorPortletURL.toString()).thenReturn("itemSelectorPortletURL");
        when(_itemSelector.getItemSelectorURL(Mockito.any(RequestBackedPortletURLFactory.class), Mockito.anyString(), Mockito.any(ItemSelectorCriterion.class))).thenReturn(itemSelectorPortletURL);
        when(_itemSelector.getItemSelectedEventName(Mockito.anyString())).thenReturn("selectedEventName");
        when(_itemSelector.getItemSelectorCriteria("fileItemSelectorCriterionFileEntryItemSelectorReturnType")).thenReturn(_getFileItemSelectorCriterionFileEntryItemSelectorReturnType());
        JSONObject originalJSONObject = JSONFactoryUtil.createJSONObject();
        originalJSONObject.put("filebrowserImageBrowseLinkUrl", "fileItemSelectorCriterionFileEntryItemSelectorReturnType");
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(originalJSONObject.toJSONString());
        AMBlogsEditorConfigContributor amBlogsEditorConfigContributor = new AMBlogsEditorConfigContributor();
        amBlogsEditorConfigContributor.setItemSelector(_itemSelector);
        amBlogsEditorConfigContributor.populateConfigJSONObject(jsonObject, _inputEditorTaglibAttributes, _themeDisplay, _requestBackedPortletURLFactory);
        Mockito.verify(_itemSelector).getItemSelectorURL(Mockito.any(RequestBackedPortletURLFactory.class), Mockito.anyString(), Mockito.any(ItemSelectorCriterion.class));
    }

    private final Map<String, Object> _inputEditorTaglibAttributes = new HashMap<>();

    @Mock
    private ItemSelector _itemSelector;

    @Mock
    private PortletURL _portletURL;

    @Mock
    private RequestBackedPortletURLFactory _requestBackedPortletURLFactory;

    @Mock
    private ThemeDisplay _themeDisplay;
}

