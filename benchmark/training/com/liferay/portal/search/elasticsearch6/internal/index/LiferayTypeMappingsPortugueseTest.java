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
package com.liferay.portal.search.elasticsearch6.internal.index;


import com.liferay.portal.kernel.test.util.RandomTestUtil;
import java.util.HashMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class LiferayTypeMappingsPortugueseTest {
    @Test
    public void testPortugueseDynamicTemplatesMatchAnalyzers() throws Exception {
        String field_pt = (RandomTestUtil.randomString()) + "_pt";
        String field_pt_BR = (RandomTestUtil.randomString()) + "_pt_BR";
        String field_pt_PT = (RandomTestUtil.randomString()) + "_pt_PT";
        _liferayIndexFixture.index(new HashMap<String, Object>() {
            {
                put(field_pt, RandomTestUtil.randomString());
                put(field_pt_BR, RandomTestUtil.randomString());
                put(field_pt_PT, RandomTestUtil.randomString());
            }
        });
        assertAnalyzer(field_pt, "portuguese");
        assertAnalyzer(field_pt_BR, "brazilian");
        assertAnalyzer(field_pt_PT, "portuguese");
    }

    @Rule
    public TestName testName = new TestName();

    private LiferayIndexFixture _liferayIndexFixture;
}

