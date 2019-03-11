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
package com.liferay.portal.search.web.internal.result.display.builder;


import Field.CREATE_DATE;
import Field.ROOT_ENTRY_CLASS_PK;
import LocaleUtil.BRAZIL;
import LocaleUtil.CHINA;
import LocaleUtil.GERMANY;
import LocaleUtil.HUNGARY;
import LocaleUtil.ITALY;
import LocaleUtil.JAPAN;
import LocaleUtil.NETHERLANDS;
import LocaleUtil.SPAIN;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactory;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext;
import com.liferay.portal.util.FastDateFormatFactoryImpl;
import java.util.Locale;
import javax.portlet.PortletURL;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Lino Alves
 * @author Andr? de Oliveira
 */
public class SearchResultSummaryDisplayBuilderTest {
    @Test
    public void testClassFieldsWithoutAssetTagsOrCategories() throws Exception {
        PortletURL portletURL = Mockito.mock(PortletURL.class);
        Mockito.doReturn(portletURL).when(portletURLFactory).getPortletURL();
        String entryClassName = RandomTestUtil.randomString();
        long entryClassPK = RandomTestUtil.randomLong();
        whenAssetRendererFactoryGetAssetRenderer(entryClassPK, assetRenderer);
        whenAssetRendererFactoryLookupGetAssetRendererFactoryByClassName(entryClassName);
        SearchResultSummaryDisplayContext searchResultSummaryDisplayContext = build(createDocument(entryClassName, entryClassPK));
        Assert.assertEquals(entryClassName, searchResultSummaryDisplayContext.getClassName());
        Assert.assertEquals(entryClassPK, searchResultSummaryDisplayContext.getClassPK());
        Assert.assertEquals(portletURL, searchResultSummaryDisplayContext.getPortletURL());
    }

    @Test
    public void testCreationDate() throws Exception {
        String entryClassName = RandomTestUtil.randomString();
        long entryClassPK = RandomTestUtil.randomLong();
        whenAssetRendererFactoryGetAssetRenderer(entryClassPK, assetRenderer);
        whenAssetRendererFactoryLookupGetAssetRendererFactoryByClassName(entryClassName);
        Document document = createDocument(entryClassName, entryClassPK);
        assertCreationDateMissing(document);
        document.addKeyword(CREATE_DATE, "20180425171442");
        assertCreationDate("Apr 25, 2018 5:14 PM", document);
        assertCreationDate(BRAZIL, "25/04/2018 17:14", document);
        assertCreationDate(CHINA, "2018-4-25 ??5:14", document);
        assertCreationDate(GERMANY, "25.04.2018 17:14", document);
        assertCreationDate(HUNGARY, "2018.04.25. 17:14", document);
        assertCreationDate(ITALY, "25-apr-2018 17.14", document);
        assertCreationDate(JAPAN, "2018/04/25 17:14", document);
        assertCreationDate(NETHERLANDS, "25-apr-2018 17:14", document);
        assertCreationDate(SPAIN, "25-abr-2018 17:14", document);
    }

    @Test
    public void testResultIsTemporarilyUnavailable() throws Exception {
        ruinAssetRendererFactoryLookup();
        SearchResultSummaryDisplayContext searchResultSummaryDisplayContext = build(Mockito.mock(Document.class));
        Assert.assertTrue(searchResultSummaryDisplayContext.isTemporarilyUnavailable());
    }

    @Test
    public void testTagsURLDownloadAndUserPortraitFromResult() throws Exception {
        long userId = RandomTestUtil.randomLong();
        AssetEntry assetEntry = createAssetEntryWithTagsPresent(userId);
        String className = RandomTestUtil.randomString();
        long entryClassPK = RandomTestUtil.randomLong();
        whenAssetEntryLocalServiceFetchEntry(className, entryClassPK, assetEntry);
        whenAssetRendererFactoryGetAssetRenderer(entryClassPK, assetRenderer);
        whenAssetRendererFactoryLookupGetAssetRendererFactoryByClassName(className);
        String urlDownload = RandomTestUtil.randomString();
        whenAssetRendererGetURLDownload(assetRenderer, urlDownload);
        whenIndexerRegistryGetIndexer(className, createIndexer());
        SearchResultSummaryDisplayContext searchResultSummaryDisplayContext = build(createDocument(className, entryClassPK));
        assertAssetRendererURLDownloadVisible(urlDownload, searchResultSummaryDisplayContext);
        assertTagsVisible(entryClassPK, searchResultSummaryDisplayContext);
        assertUserPortraitVisible(userId, searchResultSummaryDisplayContext);
    }

    @Test
    public void testUserPortraitFromResultButTagsAndURLDownloadFromRoot() throws Exception {
        long userId = RandomTestUtil.randomInt(2, Integer.MAX_VALUE);
        AssetEntry assetEntry = createAssetEntry(userId);
        long rootUserId = userId - 1;
        AssetEntry rootAssetEntry = createAssetEntryWithTagsPresent(rootUserId);
        String className = RandomTestUtil.randomString();
        long entryClassPK = RandomTestUtil.randomInt(2, Integer.MAX_VALUE);
        long rootEntryClassPK = entryClassPK - 1;
        whenAssetEntryLocalServiceFetchEntry(className, entryClassPK, assetEntry);
        whenAssetEntryLocalServiceFetchEntry(className, rootEntryClassPK, rootAssetEntry);
        whenAssetRendererFactoryGetAssetRenderer(entryClassPK, assetRenderer);
        AssetRenderer<?> rootAssetRenderer = Mockito.mock(AssetRenderer.class);
        whenAssetRendererFactoryGetAssetRenderer(rootEntryClassPK, rootAssetRenderer);
        whenAssetRendererFactoryLookupGetAssetRendererFactoryByClassName(className);
        String rootURLDownload = RandomTestUtil.randomString();
        whenAssetRendererGetURLDownload(rootAssetRenderer, rootURLDownload);
        whenIndexerRegistryGetIndexer(className, createIndexer());
        Document document = createDocument(className, entryClassPK);
        document.addKeyword(ROOT_ENTRY_CLASS_PK, rootEntryClassPK);
        SearchResultSummaryDisplayContext searchResultSummaryDisplayContext = build(document);
        assertAssetRendererURLDownloadVisible(rootURLDownload, searchResultSummaryDisplayContext);
        assertTagsVisible(rootEntryClassPK, searchResultSummaryDisplayContext);
        assertUserPortraitVisible(userId, searchResultSummaryDisplayContext);
    }

    @Mock
    protected AssetEntryLocalService assetEntryLocalService;

    @Mock
    protected AssetRenderer<?> assetRenderer;

    @Mock
    protected AssetRendererFactory<?> assetRendererFactory;

    @Mock
    protected AssetRendererFactoryLookup assetRendererFactoryLookup;

    protected FastDateFormatFactory fastDateFormatFactory = new FastDateFormatFactoryImpl();

    @Mock
    protected IndexerRegistry indexerRegistry;

    protected Locale locale = Locale.US;

    @Mock
    protected PermissionChecker permissionChecker;

    @Mock
    protected PortletURLFactory portletURLFactory;

    protected ThemeDisplay themeDisplay;
}

