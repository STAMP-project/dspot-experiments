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
package com.liferay.portal.search.elasticsearch6.internal.facet;


import com.liferay.portal.search.test.util.facet.BaseModifiedFacetTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Bryan Engler
 */
public class ModifiedFacetTest extends BaseModifiedFacetTestCase {
    @Test
    public void testSearchEngineDateMath() throws Exception {
        addDocument("17760704000000");
        addDocument("27760704000000");
        String dateMathExpressionWithAlphabeticalOrderSwitched = "[now-500y TO now]";
        doTestSearchEngineDateMath(dateMathExpressionWithAlphabeticalOrderSwitched, 1);
    }
}

