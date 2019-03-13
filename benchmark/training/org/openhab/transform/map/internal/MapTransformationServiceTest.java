/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.transform.map.internal;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ga?l L'hopital
 */
public class MapTransformationServiceTest {
    private static final String SOURCE_CLOSED = "CLOSED";

    private static final String SOURCE_UNKNOWN = "UNKNOWN";

    private static final String EXISTING_FILENAME_DE = "map/doorstatus_de.map";

    private static final String SHOULD_BE_LOCALIZED_FILENAME = "map/doorstatus.map";

    private static final String DEFAULTED_FILENAME = "map/doorstatus_defaulted.map";

    private static final String INEXISTING_FILENAME = "map/de.map";

    private static final String BASE_FOLDER = "target";

    private static final String SRC_FOLDER = "conf";

    private static final String CONFIG_FOLDER = ((MapTransformationServiceTest.BASE_FOLDER) + (File.separator)) + (MapTransformationServiceTest.SRC_FOLDER);

    private static final String USED_FILENAME = (((MapTransformationServiceTest.CONFIG_FOLDER) + (File.separator)) + "transform/") + (MapTransformationServiceTest.EXISTING_FILENAME_DE);

    private MapTransformationService processor;

    @Test
    public void testTransformByMap() throws Exception {
        // Test that we find a translation in an existing file
        String transformedResponse = processor.transform(MapTransformationServiceTest.EXISTING_FILENAME_DE, MapTransformationServiceTest.SOURCE_CLOSED);
        Assert.assertEquals("zu", transformedResponse);
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(MapTransformationServiceTest.USED_FILENAME);FileWriter writer = new FileWriter(MapTransformationServiceTest.USED_FILENAME)) {
            properties.load(reader);
            properties.setProperty(MapTransformationServiceTest.SOURCE_CLOSED, "changevalue");
            properties.store(writer, "");
            // This tests that the requested transformation file has been removed from
            // the cache
            waitForAssert(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    final String transformedResponse = processor.transform(MapTransformationServiceTest.EXISTING_FILENAME_DE, MapTransformationServiceTest.SOURCE_CLOSED);
                    Assert.assertEquals("changevalue", transformedResponse);
                    return null;
                }
            }, 10000, 100);
            properties.setProperty(MapTransformationServiceTest.SOURCE_CLOSED, "zu");
            properties.store(writer, "");
            waitForAssert(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    final String transformedResponse = processor.transform(MapTransformationServiceTest.EXISTING_FILENAME_DE, MapTransformationServiceTest.SOURCE_CLOSED);
                    Assert.assertEquals("zu", transformedResponse);
                    return null;
                }
            }, 10000, 100);
        } catch (IOException e1) {
            e1.printStackTrace(System.err);
        }
        // Checks that an unknown input in an existing file give the expected
        // transformed response that shall be empty string (Issue #1107) if not found in the file
        transformedResponse = processor.transform(MapTransformationServiceTest.EXISTING_FILENAME_DE, MapTransformationServiceTest.SOURCE_UNKNOWN);
        Assert.assertEquals("", transformedResponse);
        // Test that an inexisting file raises correct exception as expected
        try {
            transformedResponse = processor.transform(MapTransformationServiceTest.INEXISTING_FILENAME, MapTransformationServiceTest.SOURCE_CLOSED);
            Assert.fail();
        } catch (Exception e) {
            // That's what we expect.
        }
        // Test that we find a localized version of desired file
        transformedResponse = processor.transform(MapTransformationServiceTest.SHOULD_BE_LOCALIZED_FILENAME, MapTransformationServiceTest.SOURCE_CLOSED);
        // as we don't know the real locale at the moment the
        // test is run, we test that the string has actually been transformed
        Assert.assertNotEquals(MapTransformationServiceTest.SOURCE_CLOSED, transformedResponse);
        transformedResponse = processor.transform(MapTransformationServiceTest.SHOULD_BE_LOCALIZED_FILENAME, MapTransformationServiceTest.SOURCE_CLOSED);
        Assert.assertNotEquals(MapTransformationServiceTest.SOURCE_CLOSED, transformedResponse);
    }

    @Test
    public void testTransformByMapWithDefault() throws Exception {
        // Standard behaviour with no default value
        String transformedResponse = processor.transform(MapTransformationServiceTest.SHOULD_BE_LOCALIZED_FILENAME, "toBeDefaulted");
        Assert.assertEquals("", transformedResponse);
        // Modified behaviour with a file containing default value definition
        transformedResponse = processor.transform(MapTransformationServiceTest.DEFAULTED_FILENAME, "toBeDefaulted");
        Assert.assertEquals("Default Value", transformedResponse);
    }
}

