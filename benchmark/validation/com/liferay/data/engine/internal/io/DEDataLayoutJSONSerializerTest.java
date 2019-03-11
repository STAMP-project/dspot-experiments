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
package com.liferay.data.engine.internal.io;


import DEDataLayoutSerializerApplyRequest.Builder;
import DEDataLayoutSerializerException.InvalidDefaultLanguageId;
import DEDataLayoutSerializerException.InvalidPageTitle;
import DEDataLayoutSerializerException.InvalidPaginationMode;
import StringPool.BLANK;
import com.liferay.data.engine.exception.DEDataLayoutSerializerException;
import com.liferay.data.engine.io.DEDataLayoutSerializerApplyRequest;
import com.liferay.data.engine.io.DEDataLayoutSerializerApplyResponse;
import com.liferay.data.engine.model.DEDataLayout;
import com.liferay.data.engine.model.DEDataLayoutPage;
import java.io.IOException;
import java.util.Queue;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Jeyvison Nascimento
 */
public class DEDataLayoutJSONSerializerTest extends BaseTestCase {
    @Test
    public void testSerialize() throws DEDataLayoutSerializerException, IOException, JSONException {
        DEDataLayout deDataLayout = _createDEDataLayout("layout", "this is a layout", "wizard", "en_US");
        DEDataLayoutSerializerApplyRequest deDataLayoutSerializerApplyRequest = Builder.of(deDataLayout);
        DEDataLayoutSerializerApplyResponse deDataLayoutSerializerApplyResponse = _deDataLayoutJSONSerializer.apply(deDataLayoutSerializerApplyRequest);
        JSONAssert.assertEquals(read("data-layout-page-serializer.json"), deDataLayoutSerializerApplyResponse.getContent(), false);
    }

    @Test(expected = InvalidPaginationMode.class)
    public void testSerializeWithInvalidPaginationMode() throws DEDataLayoutSerializerException {
        DEDataLayout deDataLayout = _createDEDataLayout("layout", "this is a layout", "DummyPagination", "en_US");
        DEDataLayoutSerializerApplyRequest deDataLayoutSerializerApplyRequest = Builder.of(deDataLayout);
        _deDataLayoutJSONSerializer.apply(deDataLayoutSerializerApplyRequest);
    }

    @Test(expected = InvalidDefaultLanguageId.class)
    public void testSerializeWithoutDefaultLanguageId() throws DEDataLayoutSerializerException {
        DEDataLayout deDataLayout = _createDEDataLayout("layout", "this is a layout", "wizard", BLANK);
        DEDataLayoutSerializerApplyRequest deDataLayoutSerializerApplyRequest = Builder.of(deDataLayout);
        _deDataLayoutJSONSerializer.apply(deDataLayoutSerializerApplyRequest);
    }

    @Test(expected = InvalidPageTitle.class)
    public void testSerializeWithoutPageTitle() throws DEDataLayoutSerializerException {
        DEDataLayout deDataLayout = _createDEDataLayout("layout", "this is a layout", "pagination", "en_US");
        Queue<DEDataLayoutPage> deDataLayoutPages = deDataLayout.getDEDataLayoutPages();
        DEDataLayoutPage deDataLayoutPage = deDataLayoutPages.element();
        deDataLayoutPage.setTitle(null);
        DEDataLayoutSerializerApplyRequest deDataLayoutSerializerApplyRequest = Builder.of(deDataLayout);
        _deDataLayoutJSONSerializer.apply(deDataLayoutSerializerApplyRequest);
    }

    @Test(expected = InvalidPaginationMode.class)
    public void testSerializeWithoutPaginationMode() throws DEDataLayoutSerializerException {
        DEDataLayout deDataLayout = _createDEDataLayout("layout", "this is a layout", BLANK, "en_US");
        DEDataLayoutSerializerApplyRequest deDataLayoutSerializerApplyRequest = Builder.of(deDataLayout);
        _deDataLayoutJSONSerializer.apply(deDataLayoutSerializerApplyRequest);
    }

    private DEDataLayoutJSONSerializer _deDataLayoutJSONSerializer;
}

