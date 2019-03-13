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


import com.liferay.portal.search.elasticsearch6.internal.document.SingleFieldFixture;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class LiferayTypeMappingsPrefixPerLanguageTest {
    @Test
    public void testPrefixEnglish() throws Exception {
        _singleFieldFixture.setField(((LiferayTypeMappingsPrefixPerLanguageTest._PREFIX) + "_en"));
        String content1 = "terrible";
        String content2 = "terrific";
        index(content1, content2);
        assertSearch("terr", content1, content2);
        assertSearch("terrib", content1);
        assertSearch("terrif", content2);
        assertNoHits("terrier");
    }

    @Test
    public void testPrefixJapanese() throws Exception {
        _singleFieldFixture.setField(((LiferayTypeMappingsPrefixPerLanguageTest._PREFIX) + "_ja"));
        String content1 = "?????";
        String content2 = "????";
        String content3 = "??????";
        String content4 = "??????";
        String content5 = "?????";
        String content6 = "????";
        String content7 = "???";
        String content8 = "????";
        index(content1, content2, content3, content4, content5, content6, content7, content8);
        assertSearch("?", content2, content5, content6, content7, content8);
        assertSearch("?", content1, content2, content4, content5, content6);
        assertNoHits("?");
    }

    @Rule
    public TestName testName = new TestName();

    private static final String _PREFIX = LiferayTypeMappingsPrefixPerLanguageTest.class.getSimpleName();

    private LiferayIndexFixture _liferayIndexFixture;

    private SingleFieldFixture _singleFieldFixture;
}

