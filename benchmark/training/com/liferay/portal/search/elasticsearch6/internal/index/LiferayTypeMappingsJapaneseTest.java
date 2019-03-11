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
public class LiferayTypeMappingsJapaneseTest {
    @Test
    public void testSearch1() throws Exception {
        String content1 = "?????";
        String content2 = "????";
        String content3 = "??????";
        String content4 = "??????";
        index(content1, content2, content3, content4);
        assertSearch("??", content1, content2);
        assertSearch("??", content3);
        assertSearch("??", content4);
        assertNoHits("?");
    }

    @Test
    public void testSearch2() throws Exception {
        String content1 = "?????";
        String content2 = "????";
        String content3 = "??????";
        String content4 = "??????";
        String content5 = "?????";
        String content6 = "????";
        String content7 = "???";
        String content8 = "????";
        index(content1, content2, content3, content4, content5, content6, content7, content8);
        assertSearch("??", content1, content2, content5);
        assertSearch("??", content2, content6);
        assertSearch("?", content7);
    }

    @Test
    public void testSearch3() throws Exception {
        String content1 = "?????";
        String content2 = "????";
        String content3 = "??????";
        String content4 = "??????";
        String content5 = "?????";
        String content6 = "????";
        String content7 = "???";
        String content8 = "????";
        String content9 = "????";
        index(content1, content2, content3, content4, content5, content6, content7, content8, content9);
        assertSearch("??", content2, content6, content9);
        assertSearch("?", content7);
    }

    @Test
    public void testSearch4() throws Exception {
        String content1 = "?????";
        String content2 = "???";
        String content3 = "????";
        String content4 = "?????????";
        String content5 = "??";
        index(content1, content2, content3, content4, content5);
        assertSearch("??", content3);
        assertSearch("??", content3);
    }

    @Rule
    public TestName testName = new TestName();

    private static final String _PREFIX = LiferayTypeMappingsJapaneseTest.class.getSimpleName();

    private LiferayIndexFixture _liferayIndexFixture;

    private SingleFieldFixture _singleFieldFixture;
}

