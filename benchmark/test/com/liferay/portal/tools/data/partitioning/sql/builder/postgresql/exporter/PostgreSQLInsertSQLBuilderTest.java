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
package com.liferay.portal.tools.data.partitioning.sql.builder.postgresql.exporter;


import com.liferay.portal.tools.data.partitioning.sql.builder.exporter.InsertSQLBuilder;
import com.liferay.portal.tools.data.partitioning.sql.builder.postgresql.exporter.serializer.PostgreSQLFieldSerializer;
import java.sql.Date;
import java.sql.Timestamp;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Manuel de la Pe?a
 */
public class PostgreSQLInsertSQLBuilderTest {
    @Test
    public void testBuildFieldFieldDate() throws Exception {
        String field = _insertSQLBuilder.buildField(new Date(0L));
        Assert.assertEquals("to_timestamp('1970-01-01 00:01:00', 'YYYY-MM-DD HH24:MI:SS:MS')", field);
    }

    @Test
    public void testBuildFieldFieldDouble() throws Exception {
        String field = _insertSQLBuilder.buildField(Double.valueOf(99.99));
        Assert.assertEquals("'99.99'", field);
    }

    @Test
    public void testBuildFieldFieldFloat() throws Exception {
        String field = _insertSQLBuilder.buildField(Float.valueOf(1));
        Assert.assertEquals("'1.0'", field);
    }

    @Test
    public void testBuildFieldFieldInteger() throws Exception {
        String field = _insertSQLBuilder.buildField(Integer.valueOf(1));
        Assert.assertEquals("'1'", field);
    }

    @Test
    public void testBuildFieldFieldNull() throws Exception {
        String field = _insertSQLBuilder.buildField(null);
        Assert.assertEquals("null", field);
    }

    @Test
    public void testBuildFieldFieldString() throws Exception {
        String field = _insertSQLBuilder.buildField(new String("1"));
        Assert.assertEquals("'1'", field);
    }

    @Test
    public void testBuildFieldFieldStringShouldWithQuotes() throws Exception {
        String field = _insertSQLBuilder.buildField(new String("'1'"));
        Assert.assertEquals("'''1'''", field);
    }

    @Test
    public void testBuildFieldFieldTimestamp() throws Exception {
        String field = _insertSQLBuilder.buildField(new Timestamp(0L));
        Assert.assertEquals("to_timestamp('1970-01-01 00:01:00', 'YYYY-MM-DD HH24:MI:SS:MS')", field);
    }

    private final InsertSQLBuilder _insertSQLBuilder = new InsertSQLBuilder(new PostgreSQLFieldSerializer());
}

