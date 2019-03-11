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
import StringPool.STAR;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import java.util.Collections;
import org.elasticsearch.indices.InvalidIndexNameException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class CompanyIdIndexNameBuilderTest {
    @Test
    public void testActivate() throws Exception {
        CompanyIdIndexNameBuilder companyIdIndexNameBuilder = new CompanyIdIndexNameBuilder();
        companyIdIndexNameBuilder.activate(Collections.singletonMap("indexNamePrefix", ((Object) ("UPPERCASE"))));
        Assert.assertEquals("uppercase0", companyIdIndexNameBuilder.getIndexName(0));
    }

    @Test
    public void testIndexNamePrefixBlank() throws Exception {
        assertIndexNamePrefix(BLANK, BLANK);
    }

    @Test(expected = InvalidIndexNameException.class)
    public void testIndexNamePrefixInvalidIndexName() throws Exception {
        createIndices(STAR, 0);
    }

    @Test
    public void testIndexNamePrefixNull() throws Exception {
        assertIndexNamePrefix(null, BLANK);
    }

    @Test
    public void testIndexNamePrefixTrim() throws Exception {
        String string = RandomTestUtil.randomString();
        assertIndexNamePrefix((((StringPool.TAB) + string) + (StringPool.SPACE)), StringUtil.toLowerCase(string));
    }

    @Test
    public void testIndexNamePrefixUppercase() throws Exception {
        assertIndexNamePrefix("UPPERCASE", "uppercase");
    }

    private ElasticsearchFixture _elasticsearchFixture;
}

