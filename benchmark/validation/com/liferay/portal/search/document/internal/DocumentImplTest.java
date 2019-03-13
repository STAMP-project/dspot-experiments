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
package com.liferay.portal.search.document.internal;


import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import java.util.Date;
import org.junit.Test;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class DocumentImplTest {
    @Test
    public void testAddDate() throws Exception {
        clearDateFormat();
        documentImpl.addDate(RandomTestUtil.randomString(), new Date());
    }

    @Test
    public void testAddDateSortable() throws Exception {
        clearDateFormat();
        documentImpl.addDateSortable(RandomTestUtil.randomString(), new Date());
    }

    protected DocumentFixture documentFixture = new DocumentFixture();

    protected DocumentImpl documentImpl;
}

