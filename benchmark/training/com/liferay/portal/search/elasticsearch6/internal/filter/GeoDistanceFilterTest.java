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
package com.liferay.portal.search.elasticsearch6.internal.filter;


import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.geolocation.GeoLocationPoint;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Bryan Engler
 */
public class GeoDistanceFilterTest extends BaseIndexingTestCase {
    @Test
    public void testGeoDistanceFilter() throws Exception {
        index(33.9987, (-117.8129));
        index(34.0003, (-117.8127));
        GeoLocationPoint geoLocationPoint = new GeoLocationPoint(33.9977, (-117.8145));
        assertCountWithinDistance(2, 500.0, geoLocationPoint);
        assertCountWithinDistance(1, 250.0, geoLocationPoint);
        assertCountWithinDistance(0, 100.0, geoLocationPoint);
    }

    @Test
    public void testGeoDistanceRangeFilter() throws Exception {
        index(33.9987, (-117.8129));
        index(34.0003, (-117.8127));
        GeoLocationPoint geoLocationPoint = new GeoLocationPoint(33.9977, (-117.8145));
        assertCountWithinDistanceRange(2, 100.0, 600.0, geoLocationPoint);
        assertCountWithinDistanceRange(1, 160.0, 300.0, geoLocationPoint);
        assertCountWithinDistanceRange(0, 50.0, 150.0, geoLocationPoint);
    }

    protected static final String FIELD = Field.GEO_LOCATION;
}

