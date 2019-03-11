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


import StringPool.BLANK;
import StringPool.FALSE;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import java.util.Date;
import java.util.HashMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 *
 *
 * @author Rodrigo Paulino
 * @author Andr? de Oliveira
 */
public class LiferayTypeMappingsDDMKeywordEmptyStringTest {
    @Test
    public void testDDMKeywordEmptyStringInSecondDocument() throws Exception {
        final String field1 = LiferayTypeMappingsDDMKeywordEmptyStringTest.randomDDMKeywordField();
        final String ddmField2 = LiferayTypeMappingsDDMKeywordEmptyStringTest.randomDDMKeywordField();
        final String ddmField3 = LiferayTypeMappingsDDMKeywordEmptyStringTest.randomDDMKeywordField();
        final String ddmField4 = LiferayTypeMappingsDDMKeywordEmptyStringTest.randomDDMKeywordField();
        final String ddmField5 = LiferayTypeMappingsDDMKeywordEmptyStringTest.randomDDMKeywordField();
        final String ddmField6 = LiferayTypeMappingsDDMKeywordEmptyStringTest.randomDDMKeywordField();
        final String ddmField7 = LiferayTypeMappingsDDMKeywordEmptyStringTest.randomDDMKeywordField();
        final String ddmField8 = LiferayTypeMappingsDDMKeywordEmptyStringTest.randomDDMKeywordField();
        final String ddmField9 = LiferayTypeMappingsDDMKeywordEmptyStringTest.randomDDMKeywordField();
        index(new HashMap<String, Object>() {
            {
                put(field1, RandomTestUtil.randomString());
                put(ddmField2, BLANK);
                put(ddmField3, new Date());
                put(ddmField4, "2011-07-01T01:32:33");
                put(ddmField5, 321231312321L);
                put(ddmField6, "321231312321");
                put(ddmField7, true);
                put(ddmField8, "true");
                put(ddmField9, "NULL");
            }
        });
        index(new HashMap<String, Object>() {
            {
                put(field1, BLANK);
                put(ddmField2, BLANK);
                put(ddmField3, BLANK);
                put(ddmField4, BLANK);
                put(ddmField5, BLANK);
                put(ddmField6, BLANK);
                put(ddmField7, BLANK);
                put(ddmField8, BLANK);
                put(ddmField9, BLANK);
            }
        });
        index(new HashMap<String, Object>() {
            {
                put(field1, RandomTestUtil.randomString());
                put(ddmField2, RandomTestUtil.randomString());
                put(ddmField3, RandomTestUtil.randomString());
                put(ddmField4, RandomTestUtil.randomString());
                put(ddmField5, String.valueOf(RandomTestUtil.randomLong()));
                put(ddmField6, RandomTestUtil.randomString());
                put(ddmField7, FALSE);
                put(ddmField8, RandomTestUtil.randomString());
                put(ddmField9, RandomTestUtil.randomString());
            }
        });
        assertType(field1, "keyword");
        assertType(ddmField2, "keyword");
        assertType(ddmField3, "keyword");
        assertType(ddmField4, "keyword");
        assertType(ddmField5, "long");
        assertType(ddmField6, "keyword");
        assertType(ddmField7, "boolean");
        assertType(ddmField8, "keyword");
        assertType(ddmField9, "keyword");
    }

    @Rule
    public TestName testName = new TestName();

    private LiferayIndexFixture _liferayIndexFixture;
}

