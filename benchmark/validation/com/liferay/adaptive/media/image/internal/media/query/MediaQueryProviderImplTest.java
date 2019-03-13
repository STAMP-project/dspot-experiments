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
package com.liferay.adaptive.media.image.internal.media.query;


import StringPool.BLANK;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.finder.AMImageFinder;
import com.liferay.adaptive.media.image.media.query.Condition;
import com.liferay.adaptive.media.image.media.query.MediaQuery;
import com.liferay.adaptive.media.image.url.AMImageURLFactory;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @author Alejandro Tard?n
 */
@RunWith(MockitoJUnitRunner.class)
public class MediaQueryProviderImplTest {
    @Test
    public void testCreatesAMediaQuery() throws Exception {
        _addConfigs(_createAMImageConfigurationEntry("uuid", 800, 1989, "adaptiveURL"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 1, mediaQueries.size());
        MediaQuery mediaQuery = mediaQueries.get(0);
        Assert.assertEquals("adaptiveURL", mediaQuery.getSrc());
        List<Condition> conditions = mediaQuery.getConditions();
        Assert.assertEquals(conditions.toString(), 1, conditions.size());
        _assertCondition(conditions.get(0), "max-width", "1989px");
    }

    @Test
    public void testCreatesSeveralMediaQueries() throws Exception {
        _addConfigs(_createAMImageConfigurationEntry("uuid1", 800, 1986, "adaptiveURL1"), _createAMImageConfigurationEntry("uuid2", 800, 1989, "adaptiveURL2"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());
        MediaQuery mediaQuery1 = mediaQueries.get(0);
        Assert.assertEquals("adaptiveURL1", mediaQuery1.getSrc());
        List<Condition> conditions1 = mediaQuery1.getConditions();
        Assert.assertEquals(conditions1.toString(), 1, conditions1.size());
        _assertCondition(conditions1.get(0), "max-width", "1986px");
        MediaQuery mediaQuery2 = mediaQueries.get(1);
        Assert.assertEquals("adaptiveURL2", mediaQuery2.getSrc());
        List<Condition> conditions2 = mediaQuery2.getConditions();
        Assert.assertEquals(conditions2.toString(), 2, conditions2.size());
        _assertCondition(conditions2.get(0), "max-width", "1989px");
        _assertCondition(conditions2.get(1), "min-width", "1986px");
    }

    @Test
    public void testCreatesSeveralMediaQueriesSortedByWidth() throws Exception {
        _addConfigs(_createAMImageConfigurationEntry("uuid2", 800, 1989, "adaptiveURL2"), _createAMImageConfigurationEntry("uuid1", 800, 1986, "adaptiveURL1"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());
        MediaQuery mediaQuery1 = mediaQueries.get(0);
        Assert.assertEquals("adaptiveURL1", mediaQuery1.getSrc());
        List<Condition> conditions1 = mediaQuery1.getConditions();
        Assert.assertEquals(conditions1.toString(), 1, conditions1.size());
        _assertCondition(conditions1.get(0), "max-width", "1986px");
        MediaQuery mediaQuery2 = mediaQueries.get(1);
        Assert.assertEquals("adaptiveURL2", mediaQuery2.getSrc());
        List<Condition> conditions2 = mediaQuery2.getConditions();
        Assert.assertEquals(conditions2.toString(), 2, conditions2.size());
        _assertCondition(conditions2.get(0), "max-width", "1989px");
        _assertCondition(conditions2.get(1), "min-width", "1986px");
    }

    @Test
    public void testFiltersOutAdaptiveMediasWithNoWidth() throws Exception {
        int auto = 0;
        _addConfigs(_createAMImageConfigurationEntry("normal", 2048, 1024, BLANK), _createAMImageConfigurationEntry("wauto", 900, auto, BLANK));
        _addAdaptiveMedias(_fileEntry, _createAdaptiveMedia("normal", 1334, 750, "normalURL"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 1, mediaQueries.size());
        _assertMediaQuery(mediaQueries.get(0), "normalURL", 750);
    }

    @Test
    public void testHDMediaQueriesApplies() throws Exception {
        _addConfigs(_createAMImageConfigurationEntry("uuid1", 450, 800, "http://small.adaptive.com"), _createAMImageConfigurationEntry("uuid2", 900, 1600, "http://small.hd.adaptive.com"), _createAMImageConfigurationEntry("uuid3", 1900, 2500, "http://big.adaptive.com"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 3, mediaQueries.size());
        MediaQuery mediaQuery1 = mediaQueries.get(0);
        Assert.assertEquals("http://small.adaptive.com, http://small.hd.adaptive.com 2x", mediaQuery1.getSrc());
        List<Condition> conditions1 = mediaQuery1.getConditions();
        Assert.assertEquals(conditions1.toString(), 1, conditions1.size());
        _assertCondition(conditions1.get(0), "max-width", "800px");
        MediaQuery mediaQuery2 = mediaQueries.get(1);
        Assert.assertEquals("http://small.hd.adaptive.com", mediaQuery2.getSrc());
        List<Condition> conditions2 = mediaQuery2.getConditions();
        Assert.assertEquals(conditions2.toString(), 2, conditions2.size());
        _assertCondition(conditions2.get(0), "max-width", "1600px");
        _assertCondition(conditions2.get(1), "min-width", "800px");
        MediaQuery mediaQuery3 = mediaQueries.get(2);
        Assert.assertEquals("http://big.adaptive.com", mediaQuery3.getSrc());
        List<Condition> conditions3 = mediaQuery3.getConditions();
        Assert.assertEquals(conditions3.toString(), 2, conditions3.size());
        _assertCondition(conditions3.get(0), "max-width", "2500px");
        _assertCondition(conditions3.get(1), "min-width", "1600px");
    }

    @Test
    public void testHDMediaQueryAppliesWhenHeightHas1PXLessThanExpected() throws Exception {
        _addConfigs(_createAMImageConfigurationEntry("uuid1", 450, 800, "http://small.adaptive.com"), _createAMImageConfigurationEntry("uuid2", 899, 1600, "http://small.hd.adaptive.com"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());
        MediaQuery mediaQuery1 = mediaQueries.get(0);
        Assert.assertEquals("http://small.adaptive.com, http://small.hd.adaptive.com 2x", mediaQuery1.getSrc());
        List<Condition> conditions1 = mediaQuery1.getConditions();
        Assert.assertEquals(conditions1.toString(), 1, conditions1.size());
        _assertCondition(conditions1.get(0), "max-width", "800px");
        MediaQuery mediaQuery2 = mediaQueries.get(1);
        Assert.assertEquals("http://small.hd.adaptive.com", mediaQuery2.getSrc());
        List<Condition> conditions2 = mediaQuery2.getConditions();
        Assert.assertEquals(conditions2.toString(), 2, conditions2.size());
        _assertCondition(conditions2.get(0), "max-width", "1600px");
        _assertCondition(conditions2.get(1), "min-width", "800px");
    }

    @Test
    public void testHDMediaQueryAppliesWhenHeightHas1PXMoreThanExpected() throws Exception {
        _addConfigs(_createAMImageConfigurationEntry("uuid1", 450, 800, "http://small.adaptive.com"), _createAMImageConfigurationEntry("uuid2", 901, 1600, "http://small.hd.adaptive.com"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());
        MediaQuery mediaQuery1 = mediaQueries.get(0);
        Assert.assertEquals("http://small.adaptive.com, http://small.hd.adaptive.com 2x", mediaQuery1.getSrc());
        List<Condition> conditions1 = mediaQuery1.getConditions();
        Assert.assertEquals(conditions1.toString(), 1, conditions1.size());
        _assertCondition(conditions1.get(0), "max-width", "800px");
        MediaQuery mediaQuery2 = mediaQueries.get(1);
        Assert.assertEquals("http://small.hd.adaptive.com", mediaQuery2.getSrc());
        List<Condition> conditions2 = mediaQuery2.getConditions();
        Assert.assertEquals(conditions2.toString(), 2, conditions2.size());
        _assertCondition(conditions2.get(0), "max-width", "1600px");
        _assertCondition(conditions2.get(1), "min-width", "800px");
    }

    @Test
    public void testHDMediaQueryAppliesWhenWidthHas1PXLessThanExpected() throws Exception {
        _addConfigs(_createAMImageConfigurationEntry("uuid1", 450, 800, "http://small.adaptive.com"), _createAMImageConfigurationEntry("uuid2", 900, 1599, "http://small.hd.adaptive.com"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());
        MediaQuery mediaQuery1 = mediaQueries.get(0);
        Assert.assertEquals("http://small.adaptive.com, http://small.hd.adaptive.com 2x", mediaQuery1.getSrc());
        List<Condition> conditions1 = mediaQuery1.getConditions();
        Assert.assertEquals(conditions1.toString(), 1, conditions1.size());
        _assertCondition(conditions1.get(0), "max-width", "800px");
        Condition condition = conditions1.get(0);
        Assert.assertEquals("max-width", condition.getAttribute());
        Assert.assertEquals("800px", condition.getValue());
        MediaQuery mediaQuery2 = mediaQueries.get(1);
        List<Condition> conditions2 = mediaQuery2.getConditions();
        Assert.assertEquals("http://small.hd.adaptive.com", mediaQuery2.getSrc());
        Assert.assertEquals(conditions2.toString(), 2, conditions2.size());
        _assertCondition(conditions2.get(0), "max-width", "1599px");
        _assertCondition(conditions2.get(1), "min-width", "800px");
    }

    @Test
    public void testHDMediaQueryAppliesWhenWidthHas1PXMoreThanExpected() throws Exception {
        _addConfigs(_createAMImageConfigurationEntry("uuid", 450, 800, "http://small.adaptive.com"), _createAMImageConfigurationEntry("uuid", 900, 1601, "http://small.hd.adaptive.com"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());
        MediaQuery mediaQuery1 = mediaQueries.get(0);
        Assert.assertEquals("http://small.adaptive.com, http://small.hd.adaptive.com 2x", mediaQuery1.getSrc());
        List<Condition> conditions1 = mediaQuery1.getConditions();
        Assert.assertEquals(conditions1.toString(), 1, conditions1.size());
        _assertCondition(conditions1.get(0), "max-width", "800px");
        MediaQuery mediaQuery2 = mediaQueries.get(1);
        Assert.assertEquals("http://small.hd.adaptive.com", mediaQuery2.getSrc());
        List<Condition> conditions2 = mediaQuery2.getConditions();
        Assert.assertEquals(conditions2.toString(), 2, conditions2.size());
        _assertCondition(conditions2.get(0), "max-width", "1601px");
        _assertCondition(conditions2.get(1), "min-width", "800px");
    }

    @Test
    public void testHDMediaQueryNotAppliesWhenHeightHas2PXLessThanExpected() throws Exception {
        _addConfigs(_createAMImageConfigurationEntry("uuid", 450, 800, "http://small.adaptive.com"), _createAMImageConfigurationEntry("uuid", 898, 1600, "http://small.hd.adaptive.com"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());
        MediaQuery mediaQuery1 = mediaQueries.get(0);
        Assert.assertEquals("http://small.adaptive.com", mediaQuery1.getSrc());
        List<Condition> conditions1 = mediaQuery1.getConditions();
        Assert.assertEquals(conditions1.toString(), 1, conditions1.size());
        _assertCondition(conditions1.get(0), "max-width", "800px");
        MediaQuery mediaQuery2 = mediaQueries.get(1);
        Assert.assertEquals("http://small.hd.adaptive.com", mediaQuery2.getSrc());
        List<Condition> conditions2 = mediaQuery2.getConditions();
        Assert.assertEquals(conditions2.toString(), 2, conditions2.size());
        _assertCondition(conditions2.get(0), "max-width", "1600px");
        _assertCondition(conditions2.get(1), "min-width", "800px");
    }

    @Test
    public void testHDMediaQueryNotAppliesWhenHeightHas2PXMoreThanExpected() throws Exception {
        _addConfigs(_createAMImageConfigurationEntry("uuid", 450, 800, "http://small.adaptive.com"), _createAMImageConfigurationEntry("uuid", 902, 1600, "http://small.hd.adaptive.com"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());
        MediaQuery mediaQuery1 = mediaQueries.get(0);
        Assert.assertEquals("http://small.adaptive.com", mediaQuery1.getSrc());
        List<Condition> conditions1 = mediaQuery1.getConditions();
        Assert.assertEquals(conditions1.toString(), 1, conditions1.size());
        _assertCondition(conditions1.get(0), "max-width", "800px");
        MediaQuery mediaQuery2 = mediaQueries.get(1);
        Assert.assertEquals("http://small.hd.adaptive.com", mediaQuery2.getSrc());
        List<Condition> conditions2 = mediaQuery2.getConditions();
        Assert.assertEquals(conditions2.toString(), 2, conditions2.size());
        _assertCondition(conditions2.get(0), "max-width", "1600px");
        _assertCondition(conditions2.get(1), "min-width", "800px");
    }

    @Test
    public void testHDMediaQueryNotAppliesWhenWidthHas2PXLessThanExpected() throws Exception {
        _addConfigs(_createAMImageConfigurationEntry("uuid", 450, 800, "http://small.adaptive.com"), _createAMImageConfigurationEntry("uuid", 900, 1598, "http://small.hd.adaptive.com"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());
        MediaQuery mediaQuery1 = mediaQueries.get(0);
        Assert.assertEquals("http://small.adaptive.com", mediaQuery1.getSrc());
        List<Condition> conditions1 = mediaQuery1.getConditions();
        Assert.assertEquals(conditions1.toString(), 1, conditions1.size());
        _assertCondition(conditions1.get(0), "max-width", "800px");
        MediaQuery mediaQuery2 = mediaQueries.get(1);
        Assert.assertEquals("http://small.hd.adaptive.com", mediaQuery2.getSrc());
        List<Condition> conditions2 = mediaQuery2.getConditions();
        Assert.assertEquals(conditions2.toString(), 2, conditions2.size());
        _assertCondition(conditions2.get(0), "max-width", "1598px");
        _assertCondition(conditions2.get(1), "min-width", "800px");
    }

    @Test
    public void testHDMediaQueryNotAppliesWhenWidthHas2PXMoreThanExpected() throws Exception {
        _addConfigs(_createAMImageConfigurationEntry("uuid", 450, 800, "http://small.adaptive.com"), _createAMImageConfigurationEntry("uuid", 900, 1602, "http://small.hd.adaptive.com"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());
        MediaQuery mediaQuery1 = mediaQueries.get(0);
        Assert.assertEquals("http://small.adaptive.com", mediaQuery1.getSrc());
        List<Condition> conditions1 = mediaQuery1.getConditions();
        Assert.assertEquals(conditions1.toString(), 1, conditions1.size());
        _assertCondition(conditions1.get(0), "max-width", "800px");
        MediaQuery mediaQuery2 = mediaQueries.get(1);
        Assert.assertEquals("http://small.hd.adaptive.com", mediaQuery2.getSrc());
        List<Condition> conditions2 = mediaQuery2.getConditions();
        Assert.assertEquals(conditions2.toString(), 2, conditions2.size());
        _assertCondition(conditions2.get(0), "max-width", "1602px");
        _assertCondition(conditions2.get(1), "min-width", "800px");
    }

    @Test
    public void testReturnsNoMediaQueriesIfThereAreNoConfigs() throws Exception {
        _addConfigs();
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 0, mediaQueries.size());
    }

    @Test
    public void testUsesTheValuesFromConfigIfNoAdaptiveMediasArePresent() throws Exception {
        int auto = 0;
        _addConfigs(_createAMImageConfigurationEntry("hauto", auto, 600, "hautoURL"), _createAMImageConfigurationEntry("low", 300, 300, "lowURL"), _createAMImageConfigurationEntry("normal", 2048, 1024, "normalURL"), _createAMImageConfigurationEntry("wauto", 900, auto, "wautoURL"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 3, mediaQueries.size());
        _assertMediaQuery(mediaQueries.get(0), "lowURL", 300);
        _assertMediaQuery(mediaQueries.get(1), "hautoURL", 300, 600);
        _assertMediaQuery(mediaQueries.get(2), "normalURL", 600, 1024);
    }

    @Test
    public void testUsesTheValuesFromTheAdaptiveMediasIfPresent() throws Exception {
        int auto = 0;
        _addConfigs(_createAMImageConfigurationEntry("hauto", auto, 600, BLANK), _createAMImageConfigurationEntry("low", 300, 300, BLANK), _createAMImageConfigurationEntry("normal", 2048, 1024, BLANK), _createAMImageConfigurationEntry("wauto", 900, auto, BLANK));
        _addAdaptiveMedias(_fileEntry, _createAdaptiveMedia("low", 300, 169, "lowURL"), _createAdaptiveMedia("wauto", 900, 506, "wautoURL"), _createAdaptiveMedia("hauto", 1067, 600, "hautoURL"), _createAdaptiveMedia("normal", 1334, 750, "normalURL"));
        List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(_fileEntry);
        Assert.assertEquals(mediaQueries.toString(), 4, mediaQueries.size());
        _assertMediaQuery(mediaQueries.get(0), "lowURL", 169);
        _assertMediaQuery(mediaQueries.get(1), "wautoURL", 169, 506);
        _assertMediaQuery(mediaQueries.get(2), "hautoURL", 506, 600);
        _assertMediaQuery(mediaQueries.get(3), "normalURL", 600, 750);
    }

    private static final long _COMPANY_ID = 1L;

    @Mock
    private AMImageConfigurationHelper _amImageConfigurationHelper;

    @Mock
    private AMImageFinder _amImageFinder;

    @Mock
    private AMImageURLFactory _amImageURLFactory;

    @Mock
    private FileEntry _fileEntry;

    @Mock
    private FileVersion _fileVersion;

    private final MediaQueryProviderImpl _mediaQueryProvider = new MediaQueryProviderImpl();
}

