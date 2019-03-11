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
package com.liferay.portal.tools.sample.sql.builder;


import DBType.HYPERSONIC;
import SystemProperties.TMP_DIR;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.SortedProperties;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.test.rule.LogAssertionTestRule;
import com.liferay.portal.tools.ToolDependencies;
import java.io.File;
import java.util.Properties;
import org.junit.ClassRule;
import org.junit.Test;


/**
 *
 *
 * @author Tina Tian
 */
public class SampleSQLBuilderTest {
    @ClassRule
    public static final LogAssertionTestRule logAssertionTestRule = LogAssertionTestRule.INSTANCE;

    @Test
    public void testGenerateAndInsertSampleSQL() throws Exception {
        ToolDependencies.wireBasic();
        DBManagerUtil.setDB(HYPERSONIC, null);
        Properties properties = new SortedProperties();
        File tempDir = new File(SystemProperties.get(TMP_DIR), String.valueOf(System.currentTimeMillis()));
        _initProperties(properties, tempDir.getAbsolutePath());
        try {
            new SampleSQLBuilder(properties, new DataFactory(properties));
            _loadHypersonic("../../../sql", tempDir.getAbsolutePath());
        } finally {
            FileUtil.deltree(tempDir);
        }
    }
}

