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
package com.liferay.portal.search.web.internal.modified.facet.display.context;


import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.CalendarFactory;
import com.liferay.portal.kernel.util.DateFormatFactory;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.web.internal.modified.facet.builder.DateRangeFactory;
import com.liferay.portal.util.HttpImpl;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


/**
 *
 *
 * @author Adam Brandizzi
 */
public class ModifiedFacetDisplayBuilderTest {
    @Test
    public void testCustomRangeHasFrequency() {
        String from = "2018-01-01";
        String to = "2018-01-31";
        TermCollector termCollector = mockTermCollector(_dateRangeFactory.getRangeString(from, to));
        int frequency = RandomTestUtil.randomInt();
        mockTermCollectorFrequency(termCollector, frequency);
        ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder = createDisplayBuilder();
        modifiedFacetDisplayBuilder.setFromParameterValue(from);
        modifiedFacetDisplayBuilder.setToParameterValue(to);
        ModifiedFacetDisplayContext modifiedFacetDisplayContext = modifiedFacetDisplayBuilder.build();
        ModifiedFacetTermDisplayContext modifiedFacetTermDisplayContext = modifiedFacetDisplayContext.getCustomRangeModifiedFacetTermDisplayContext();
        Assert.assertEquals(frequency, modifiedFacetTermDisplayContext.getFrequency());
    }

    @Test
    public void testCustomRangeHasTermCollectorFrequency() {
        int frequency = RandomTestUtil.randomInt();
        TermCollector termCollector = mockTermCollector();
        mockTermCollectorFrequency(termCollector, frequency);
        ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder = createDisplayBuilder();
        modifiedFacetDisplayBuilder.setFromParameterValue("2018-01-01");
        modifiedFacetDisplayBuilder.setToParameterValue("2018-01-31");
        ModifiedFacetDisplayContext modifiedFacetDisplayContext = modifiedFacetDisplayBuilder.build();
        ModifiedFacetTermDisplayContext modifiedFacetTermDisplayContext = modifiedFacetDisplayContext.getCustomRangeModifiedFacetTermDisplayContext();
        Assert.assertEquals(frequency, modifiedFacetTermDisplayContext.getFrequency());
    }

    @Test
    public void testIsNothingSelected() {
        ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder = createDisplayBuilder();
        ModifiedFacetDisplayContext modifiedFacetDisplayContext = modifiedFacetDisplayBuilder.build();
        Assert.assertTrue(modifiedFacetDisplayContext.isNothingSelected());
    }

    @Test
    public void testIsNothingSelectedWithFromAndToAttributes() {
        ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder = createDisplayBuilder();
        modifiedFacetDisplayBuilder.setFromParameterValue("2018-01-01");
        modifiedFacetDisplayBuilder.setToParameterValue("2018-01-31");
        ModifiedFacetDisplayContext modifiedFacetDisplayContext = modifiedFacetDisplayBuilder.build();
        Assert.assertFalse(modifiedFacetDisplayContext.isNothingSelected());
    }

    @Test
    public void testIsNothingSelectedWithSelectedRange() {
        ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder = createDisplayBuilder();
        modifiedFacetDisplayBuilder.setParameterValues("past-24-hours");
        ModifiedFacetDisplayContext modifiedFacetDisplayContext = modifiedFacetDisplayBuilder.build();
        Assert.assertFalse(modifiedFacetDisplayContext.isNothingSelected());
    }

    @Test
    public void testIsRenderNothingFalseWithFromAndTo() {
        ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder = createDisplayBuilder();
        modifiedFacetDisplayBuilder.setFromParameterValue("2018-01-01");
        modifiedFacetDisplayBuilder.setToParameterValue("2018-01-31");
        modifiedFacetDisplayBuilder.setTotalHits(0);
        ModifiedFacetDisplayContext modifiedFacetDisplayContext = modifiedFacetDisplayBuilder.build();
        Assert.assertFalse(modifiedFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testIsRenderNothingFalseWithHits() {
        ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder = createDisplayBuilder();
        modifiedFacetDisplayBuilder.setTotalHits(1);
        ModifiedFacetDisplayContext modifiedFacetDisplayContext = modifiedFacetDisplayBuilder.build();
        Assert.assertFalse(modifiedFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testIsRenderNothingFalseWithSelectedRange() {
        ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder = createDisplayBuilder();
        modifiedFacetDisplayBuilder.setParameterValues("past-24-hours");
        modifiedFacetDisplayBuilder.setTotalHits(0);
        ModifiedFacetDisplayContext modifiedFacetDisplayContext = modifiedFacetDisplayBuilder.build();
        Assert.assertFalse(modifiedFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testIsRenderNothingTrueWithNoHits() {
        ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder = createDisplayBuilder();
        modifiedFacetDisplayBuilder.setTotalHits(0);
        ModifiedFacetDisplayContext modifiedFacetDisplayContext = modifiedFacetDisplayBuilder.build();
        Assert.assertTrue(modifiedFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testMissingFromAndToParameters() {
        ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder = createDisplayBuilder();
        modifiedFacetDisplayBuilder.setCurrentURL("/?modifiedFrom=2018-01-01&modifiedTo=2018-01-31");
        ModifiedFacetDisplayContext modifiedFacetDisplayContext = modifiedFacetDisplayBuilder.build();
        assertTermDisplayContextsDoNotHaveFromAndToParameters(modifiedFacetDisplayContext.getModifiedFacetTermDisplayContexts());
    }

    @Test
    public void testModifiedFacetTermDisplayContexts() {
        ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder = createDisplayBuilder();
        mockFacetConfiguration("past-hour=[20180515225959 TO 20180515235959]", "some-time-ago=[20180508235959 TO 20180514235959]");
        ModifiedFacetDisplayContext modifiedFacetDisplayContext = modifiedFacetDisplayBuilder.build();
        List<ModifiedFacetTermDisplayContext> modifiedFacetTermDisplayContexts = modifiedFacetDisplayContext.getModifiedFacetTermDisplayContexts();
        Assert.assertEquals(modifiedFacetTermDisplayContexts.toString(), 2, modifiedFacetTermDisplayContexts.size());
        ModifiedFacetTermDisplayContext modifiedFacetTermDisplayContext = modifiedFacetTermDisplayContexts.get(0);
        Assert.assertEquals("past-hour", modifiedFacetTermDisplayContext.getLabel());
        Assert.assertEquals("[20180515225959 TO 20180515235959]", modifiedFacetTermDisplayContext.getRange());
        modifiedFacetTermDisplayContext = modifiedFacetTermDisplayContexts.get(1);
        Assert.assertEquals("some-time-ago", modifiedFacetTermDisplayContext.getLabel());
        Assert.assertEquals("[20180508235959 TO 20180514235959]", modifiedFacetTermDisplayContext.getRange());
    }

    @Mock
    protected Portal portal;

    private CalendarFactory _calendarFactory;

    private DateFormatFactory _dateFormatFactory;

    private DateRangeFactory _dateRangeFactory;

    @Mock
    private Facet _facet;

    @Mock
    private FacetCollector _facetCollector;

    private HttpImpl _http;

    private JSONFactoryImpl _jsonFactory;
}

