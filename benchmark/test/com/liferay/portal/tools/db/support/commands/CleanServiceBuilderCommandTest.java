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
package com.liferay.portal.tools.db.support.commands;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Di Giorgi
 */
public class CleanServiceBuilderCommandTest extends BaseCommandTestCase {
    public CleanServiceBuilderCommandTest(String mode) throws IOException {
        super(mode);
    }

    @Test
    public void testCleanServiceBuilderDefault() throws Exception {
        String prefix = "default";
        String[] tableNames = new String[]{ "SampleBar", "Sample_Foo", "Sample_User" };
        _createTablesAndPopulate(prefix, tableNames);
        _testCleanServiceBuilder(prefix, tableNames);
    }

    @Test
    public void testCleanServiceBuilderMissingTable() throws Exception {
        String prefix = "default";
        String[] tableNames = new String[]{ "SampleBar", "Sample_Foo", "Sample_User" };
        _createTablesAndPopulate(prefix, tableNames);
        try (Connection connection = DriverManager.getConnection(getUrl())) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(("DROP TABLE " + (tableNames[0])));
            }
        }
        _testCleanServiceBuilder(prefix, tableNames);
    }

    @Test
    public void testCleanServiceBuilderNoAutoNamespace() throws Exception {
        String prefix = "no-auto-namespace";
        String[] tableNames = new String[]{ "Foo", "SampleBar", "User_" };
        _createTablesAndPopulate(prefix, tableNames);
        _testCleanServiceBuilder(prefix, tableNames);
    }

    private static final int _BUILD_NUMBER = 5;

    private static final String _NAMESPACE = "Sample";

    private static final String _SERVLET_CONTEXT_NAME = "com.example.sample.service";
}

