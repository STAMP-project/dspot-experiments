/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.config;


import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Test;


public class GeoServerPropertyOverrideConfigurerTest {
    @Test
    public void testPropertyOverrider() {
        // on easymock 3.6 + jdk11 this test does not work, waiting for 3.7. to be released
        Assume.assumeFalse(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9));
        // corner cases
        testPropertyOverride("", "", "");
        testPropertyOverride("some text", "data dir", "some text");
        testPropertyOverride("some ${GEOSERVER_DATA_DIR} text", "", "some  text");
        testPropertyOverride("some ${GEOSERVER_DATA_DIR} text", "\\$/", "some \\$/ text");
        // linux paths
        testPropertyOverride("before/${GEOSERVER_DATA_DIR}/after", "", "before//after");
        testPropertyOverride("${GEOSERVER_DATA_DIR}", "/linux/path/", "/linux/path/");
        testPropertyOverride("before/${GEOSERVER_DATA_DIR}/after", "linux/path", "before/linux/path/after");
        testPropertyOverride("before/a space/${GEOSERVER_DATA_DIR}/after/another space", "linux/path", "before/a space/linux/path/after/another space");
        testPropertyOverride("before/a space/${GEOSERVER_DATA_DIR}/after/another space", "linux/a space/path", "before/a space/linux/a space/path/after/another space");
        // windows paths
        testPropertyOverride("before\\${GEOSERVER_DATA_DIR}\\after", "", "before\\\\after");
        testPropertyOverride("${GEOSERVER_DATA_DIR}", "\\linux\\path\\", "\\linux\\path\\");
        testPropertyOverride("before\\${GEOSERVER_DATA_DIR}\\after", "linux\\path", "before\\linux\\path\\after");
        testPropertyOverride("before\\a space\\${GEOSERVER_DATA_DIR}\\after\\another space", "linux\\path", "before\\a space\\linux\\path\\after\\another space");
        testPropertyOverride("before\\a space\\${GEOSERVER_DATA_DIR}\\after\\another space", "linux\\a space\\path", "before\\a space\\linux\\a space\\path\\after\\another space");
        // non ascii paths
        testPropertyOverride("/Entit\u00e9G\u00e9n\u00e9rique/${GEOSERVER_DATA_DIR}/\u901a\u7528\u5b9e\u4f53", "some\u00e4/\u00dftext", "/Entit\u00e9G\u00e9n\u00e9rique/some\u00e4/\u00dftext/\u901a\u7528\u5b9e\u4f53");
        testPropertyOverride("\\Entit\u00e9G\u00e9n\u00e9rique\\${GEOSERVER_DATA_DIR}\\\u901a\u7528\u5b9e\u4f53", "some\u00e4\\\u00dftext", "\\Entit\u00e9G\u00e9n\u00e9rique\\some\u00e4\\\u00dftext\\\u901a\u7528\u5b9e\u4f53");
    }
}

