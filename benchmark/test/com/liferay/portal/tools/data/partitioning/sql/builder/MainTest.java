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
package com.liferay.portal.tools.data.partitioning.sql.builder;


import com.beust.jcommander.ParameterException;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author Manuel de la Pe?a
 */
public class MainTest {
    @Test(expected = IllegalArgumentException.class)
    public void testValidateEmptyArguments() throws Exception {
        Main.main(new String[0]);
    }

    @Test(expected = ParameterException.class)
    public void testValidateInvalidCompanyId() throws Exception {
        String[] arguments = new String[]{ "-C", "foo", "-O", "bar", "-P", "foo.properties", "-S", MainTest._SCHEMA_NAME };
        Main.main(arguments);
    }

    @Test(expected = ParameterException.class)
    public void testValidateInvalidOptionArguments() throws Exception {
        Main.main(new String[]{ "-X", "foo" });
    }

    @Test(expected = ParameterException.class)
    public void testValidateNonexistingOutputDirectory() throws Exception {
        String[] arguments = new String[]{ "-C", MainTest._COMPANY_ID, "-O", "foo", "-P", "foo.properties", "-S", MainTest._SCHEMA_NAME };
        Main.main(arguments);
    }

    @Test(expected = ParameterException.class)
    public void testValidateNonexistingPropertiesFile() throws Exception {
        String[] arguments = new String[]{ "-C", MainTest._COMPANY_ID, "-O", "bar", "-P", "foo.properties", "-S", MainTest._SCHEMA_NAME };
        Main.main(arguments);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateNullArguments() throws Exception {
        Main.main(null);
    }

    @Test(expected = ParameterException.class)
    public void testValidateReadOnlyOutputDirectory() throws Exception {
        File readOnlyDir = temporaryFolder.newFolder();
        readOnlyDir.setReadable(false);
        readOnlyDir.setWritable(false);
        String[] arguments = new String[]{ "-C", MainTest._COMPANY_ID, "-O", readOnlyDir.getAbsolutePath(), "-P", "foo.properties", "-S", MainTest._SCHEMA_NAME };
        Main.main(arguments);
    }

    @Test
    public void testValidateRequiredArguments() throws Exception {
        String[][] requiredArguments = new String[][]{ new String[]{ "-C", "" }, new String[]{ "-O", "" }, new String[]{ "-P", "" }, new String[]{ "-S", "" } };
        for (String[] requiredArgument : requiredArguments) {
            try {
                Main.main(requiredArgument);
            } catch (ParameterException pe) {
            }
        }
    }

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String _COMPANY_ID = "20156";

    private static final String _SCHEMA_NAME = "lportal";
}

