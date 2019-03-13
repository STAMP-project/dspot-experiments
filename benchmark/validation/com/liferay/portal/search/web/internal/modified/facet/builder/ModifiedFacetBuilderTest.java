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
package com.liferay.portal.search.web.internal.modified.facet.builder;


import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.CalendarFactory;
import com.liferay.portal.kernel.util.DateFormatFactory;
import com.liferay.portal.search.filter.FilterBuilders;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Adam Brandizzi
 */
public class ModifiedFacetBuilderTest {
    @Test
    public void testBuiltInNamedRange() {
        Mockito.doReturn(new GregorianCalendar(2018, Calendar.MARCH, 1, 15, 19, 23)).when(calendarFactory).getCalendar();
        ModifiedFacetBuilder modifiedFacetBuilder = createModifiedFacetBuilder();
        modifiedFacetBuilder.setSelectedRanges("past-24-hours");
        assertRange("20180228151923", "20180301151923", modifiedFacetBuilder.build());
    }

    @Test
    public void testCustomRange() {
        ModifiedFacetBuilder modifiedFacetBuilder = createModifiedFacetBuilder();
        modifiedFacetBuilder.setCustomRangeFrom("20180131");
        modifiedFacetBuilder.setCustomRangeTo("20180228");
        assertRange("20180131000000", "20180228235959", modifiedFacetBuilder.build());
    }

    @Test
    public void testCustomRangeSetsSearchContextAttribute() {
        ModifiedFacetBuilder modifiedFacetBuilder = createModifiedFacetBuilder();
        modifiedFacetBuilder.setCustomRangeFrom("20180131");
        modifiedFacetBuilder.setCustomRangeTo("20180228");
        modifiedFacetBuilder.build();
        assertRange("20180131000000", "20180228235959", searchContext);
    }

    @Test
    public void testSelectUserDefinedNamedRange() {
        ModifiedFacetBuilder modifiedFacetBuilder = createModifiedFacetBuilder();
        JSONArray rangesJSONArray = createRangesJSONArray("eighties=[19800101000000 TO 19891231235959]");
        modifiedFacetBuilder.setRangesJSONArray(rangesJSONArray);
        modifiedFacetBuilder.setSelectedRanges("eighties");
        assertRange("19800101000000", "19891231235959", modifiedFacetBuilder.build());
    }

    @Test
    public void testUserDefinedNamedRanges() {
        ModifiedFacetBuilder modifiedFacetBuilder = createModifiedFacetBuilder();
        JSONArray rangesJSONArray = createRangesJSONArray("past-hour    =[20180215120000 TO 20180215140000]", "past-24-hours=[20180214130000 TO 20180215140000]", "past-week    =[20180208130000 TO 20180215140000]", "past-month   =[20180115130000 TO 20180215140000]", "past-year    =[20170215130000 TO 20180215140000]");
        modifiedFacetBuilder.setRangesJSONArray(rangesJSONArray);
        assertRangesJSONArray(rangesJSONArray, modifiedFacetBuilder.build());
    }

    protected CalendarFactory calendarFactory;

    protected DateFormatFactory dateFormatFactory;

    protected FilterBuilders filterBuilders;

    protected JSONFactory jsonFactory;

    protected SearchContext searchContext;
}

