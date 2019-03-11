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
package com.liferay.dynamic.data.lists.service.impl;


import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.Test;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class DDLRecordLocalServiceImplTest {
    @Test
    public void testToFieldWithBoolean() throws Exception {
        boolean fieldValue = RandomTestUtil.randomBoolean();
        Field field = toField(fieldValue);
        assertFieldValues(Arrays.asList(fieldValue), field.getValues(Locale.US));
    }

    @Test
    public void testToFieldWithBooleanArray() throws Exception {
        boolean[] fieldValues = new boolean[]{ RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean() };
        Field field = toField(fieldValues);
        assertFieldValues(toList(fieldValues), field.getValues(Locale.US));
    }

    @Test
    public void testToFieldWithBooleanList() throws Exception {
        List<Serializable> fieldValues = new ArrayList<>();
        fieldValues.add(RandomTestUtil.randomBoolean());
        fieldValues.add(RandomTestUtil.randomBoolean());
        Field field = toField(((Serializable) (fieldValues)));
        assertFieldValues(fieldValues, field.getValues(Locale.US));
    }

    @Test
    public void testToFieldWithBooleanSet() throws Exception {
        Set<Serializable> fieldValues = new HashSet<>();
        fieldValues.add(RandomTestUtil.randomBoolean());
        fieldValues.add(RandomTestUtil.randomBoolean());
        Field field = toField(((Serializable) (fieldValues)));
        assertFieldValues(fieldValues, field.getValues(Locale.US));
    }

    @Test
    public void testToFieldWithInteger() throws Exception {
        int fieldValue = RandomTestUtil.randomInt();
        Field field = toField(fieldValue);
        assertFieldValues(Arrays.asList(fieldValue), field.getValues(Locale.US));
    }

    @Test
    public void testToFieldWithIntegerArray() throws Exception {
        int[] fieldValues = new int[]{ RandomTestUtil.randomInt(), RandomTestUtil.randomInt(), RandomTestUtil.randomInt() };
        Field field = toField(fieldValues);
        assertFieldValues(toList(fieldValues), field.getValues(Locale.US));
    }

    @Test
    public void testToFieldWithIntegerList() throws Exception {
        List<Serializable> fieldValues = new ArrayList<>();
        fieldValues.add(RandomTestUtil.randomInt());
        fieldValues.add(RandomTestUtil.randomInt());
        Field field = toField(((Serializable) (fieldValues)));
        assertFieldValues(fieldValues, field.getValues(Locale.US));
    }

    @Test
    public void testToFieldWithIntegerSet() throws Exception {
        Set<Serializable> fieldValues = new HashSet<>();
        fieldValues.add(RandomTestUtil.randomInt());
        fieldValues.add(RandomTestUtil.randomInt());
        Field field = toField(((Serializable) (fieldValues)));
        assertFieldValues(fieldValues, field.getValues(Locale.US));
    }

    @Test
    public void testToFieldWithString() throws Exception {
        String fieldValue = StringUtil.randomString();
        Field field = toField(fieldValue);
        assertFieldValues(Arrays.asList(fieldValue), field.getValues(Locale.US));
    }

    @Test
    public void testToFieldWithStringArray() throws Exception {
        String[] fieldValues = new String[]{ StringUtil.randomString(), StringUtil.randomString(), StringUtil.randomString() };
        Field field = toField(fieldValues);
        assertFieldValues(toList(fieldValues), field.getValues(Locale.US));
    }

    @Test
    public void testToFieldWithStringList() throws Exception {
        List<Serializable> fieldValues = new ArrayList<>();
        fieldValues.add(StringUtil.randomString());
        fieldValues.add(StringUtil.randomString());
        Field field = toField(((Serializable) (fieldValues)));
        assertFieldValues(fieldValues, field.getValues(Locale.US));
    }

    @Test
    public void testToFieldWithStringSet() throws Exception {
        Set<Serializable> fieldValues = new HashSet<>();
        fieldValues.add(StringUtil.randomString());
        fieldValues.add(StringUtil.randomString());
        Field field = toField(((Serializable) (fieldValues)));
        assertFieldValues(fieldValues, field.getValues(Locale.US));
    }

    private final DDLRecordLocalServiceImpl _recordLocalServiceImpl = new DDLRecordLocalServiceImpl();
}

