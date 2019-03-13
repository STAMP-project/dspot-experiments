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
package com.liferay.portal.tools.data.partitioning.sql.builder.mysql.exporter;


import com.liferay.portal.tools.data.partitioning.sql.builder.exporter.DataPartitioningExporter;
import com.liferay.portal.tools.data.partitioning.sql.builder.exporter.DataPartitioningExporterFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Manuel de la Pe?a
 */
public class DataPartitioningExporterFactoryTest {
    @Test
    public void testGetDataPartitioningExporterReturnsMySQLProvider() throws Exception {
        DataPartitioningExporter dataPartitioningExporter = DataPartitioningExporterFactory.getDataPartitioningExporter();
        Class<MySQLDataPartitioningExporter> clazz = MySQLDataPartitioningExporter.class;
        Assert.assertTrue(clazz.isInstance(dataPartitioningExporter));
    }
}

