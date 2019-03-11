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
package com.liferay.gradle.plugins.util;


import AlloyTaglibDefaultsPlugin.PORTAL_TOOL_NAME;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Di Giorgi
 */
public class PortalToolsTest {
    @Test
    public void testVersions() {
        for (Map.Entry<String, String> entry : PortalToolsTest._dependencyPortalToolNamesMap.entrySet()) {
            String dependencyName = entry.getKey();
            String portalToolName = entry.getValue();
            String dependency = PortalToolsTest._dependencies.getProperty(dependencyName);
            String[] tokens = dependency.split(":");
            String dependencyVersion = tokens[2];
            Assert.assertEquals(((((("Please update \"" + portalToolName) + "\" version to ") + dependencyVersion) + " in ") + (PortalToolsTest._file.getAbsolutePath())), dependencyVersion, PortalToolsTest._versions.get(portalToolName));
        }
    }

    private static final Properties _dependencies = new Properties();

    private static final Map<String, String> _dependencyPortalToolNamesMap = Collections.singletonMap("alloy-taglib", PORTAL_TOOL_NAME);

    private static File _file;

    private static final Properties _versions = new Properties();
}

