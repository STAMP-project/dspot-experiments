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
package com.liferay.portal.search.internal.sort;


import ContributorConstants.ENTRY_CLASS_NAME_PROPERTY_KEY;
import Sort.DOUBLE_TYPE;
import Sort.FLOAT_TYPE;
import Sort.INT_TYPE;
import Sort.LONG_TYPE;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.contributor.sort.SortFieldNameTranslator;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Michael C. Han
 */
public class SortFieldBuilderImplTest {
    @Test
    public void testGetSortFieldWithNoSortFieldTranslator() {
        Mockito.when(_indexer.getSortField(Mockito.anyString())).thenAnswer(( invocation) -> invocation.getArgumentAt(0, .class));
        String sortFieldName = _sortFieldBuilderImpl.getSortField("modelClassName", "testField");
        Assert.assertEquals("testField", sortFieldName);
        sortFieldName = _sortFieldBuilderImpl.getSortField("modelClassName", "firstName");
        Assert.assertEquals(Field.getSortableFieldName("firstName"), sortFieldName);
        sortFieldName = _sortFieldBuilderImpl.getSortField("modelClassName", "testField", DOUBLE_TYPE);
        Assert.assertEquals(Field.getSortableFieldName("testField"), sortFieldName);
        sortFieldName = _sortFieldBuilderImpl.getSortField("modelClassName", "testField", FLOAT_TYPE);
        Assert.assertEquals(Field.getSortableFieldName("testField"), sortFieldName);
        sortFieldName = _sortFieldBuilderImpl.getSortField("modelClassName", "testField", INT_TYPE);
        Assert.assertEquals(Field.getSortableFieldName("testField"), sortFieldName);
        sortFieldName = _sortFieldBuilderImpl.getSortField("modelClassName", "testField", LONG_TYPE);
        Assert.assertEquals(Field.getSortableFieldName("testField"), sortFieldName);
    }

    @Test
    public void testGetSortFieldWithSortFieldTranslator() {
        SortFieldNameTranslator sortFieldNameTranslator = Mockito.mock(SortFieldNameTranslator.class);
        Mockito.when(sortFieldNameTranslator.getSortFieldName(Mockito.anyString())).then(( invocation) -> {
            String orderByCol = invocation.getArgumentAt(0, .class);
            return StringUtil.upperCaseFirstLetter(orderByCol);
        });
        Map<String, Object> properties = new HashMap<>();
        properties.put(ENTRY_CLASS_NAME_PROPERTY_KEY, "modelClassName");
        _sortFieldBuilderImpl.addSortFieldNameTranslator(sortFieldNameTranslator, properties);
        Mockito.when(_indexer.getSortField(Mockito.anyString())).thenAnswer(( invocation) -> invocation.getArgumentAt(0, .class));
        String sortFieldName = _sortFieldBuilderImpl.getSortField("modelClassName", "testField");
        Assert.assertEquals("TestField", sortFieldName);
        sortFieldName = _sortFieldBuilderImpl.getSortField("modelClassName", "firstName");
        Assert.assertEquals("FirstName", sortFieldName);
        sortFieldName = _sortFieldBuilderImpl.getSortField("modelClassName", "testField", DOUBLE_TYPE);
        Assert.assertEquals(Field.getSortableFieldName("testField"), sortFieldName);
        sortFieldName = _sortFieldBuilderImpl.getSortField("modelClassName", "testField", FLOAT_TYPE);
        Assert.assertEquals(Field.getSortableFieldName("testField"), sortFieldName);
        sortFieldName = _sortFieldBuilderImpl.getSortField("modelClassName", "testField", INT_TYPE);
        Assert.assertEquals(Field.getSortableFieldName("testField"), sortFieldName);
        sortFieldName = _sortFieldBuilderImpl.getSortField("modelClassName", "testField", LONG_TYPE);
        Assert.assertEquals(Field.getSortableFieldName("testField"), sortFieldName);
    }

    @Mock
    private Indexer _indexer;

    @Mock
    private IndexerRegistry _indexerRegistry;

    private SortFieldBuilderImpl _sortFieldBuilderImpl;
}

