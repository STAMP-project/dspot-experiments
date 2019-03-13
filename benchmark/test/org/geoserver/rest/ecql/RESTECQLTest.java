/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.ecql;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.geoserver.catalog.rest.CatalogRESTTestSupport;
import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;


/**
 * Unit test for evaluating the ECQL REST PathMapper.
 */
public class RESTECQLTest extends CatalogRESTTestSupport {
    private static SimpleFeatureType type;

    private static List<String> fileNames;

    static {
        try {
            // Feature type associated to the path variable inside the cql expressions
            RESTECQLTest.type = DataUtilities.createType("type", "path:string,name:string");
        } catch (SchemaException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
        // Populating the filename list
        RESTECQLTest.fileNames = new ArrayList<String>();
        RESTECQLTest.fileNames.add("NCOM_wattemp_000_20081031T0000000_12.tiff");
        RESTECQLTest.fileNames.add("NCOM_wattemp_000_20081101T0000000_12.tiff");
        RESTECQLTest.fileNames.add("NCOM_wattemp_100_20081031T0000000_12.tiff");
        RESTECQLTest.fileNames.add("NCOM_wattemp_100_20081101T0000000_12.tiff");
    }

    @Test
    public void testRegExp() throws Exception {
        // RegExp expression
        String expression = "stringTemplate(path, \'(\\w{4})_(\\w{7})_(\\d{3})_(\\d{4})(\\d{2})(\\d{2})T(\\d{7})_(\\d{2})\\.(\\w{4})\', " + "'/${1}/${4}/${5}/${6}/${0}')";
        // Testing of the defined exception
        testExpression("test", "mosaic_test", expression, RESTECQLTest.fileNames);
    }

    @Test
    public void testSubString() throws Exception {
        // SubString expression
        String expression = "if_then_else(strEndsWith(name,'.tiff'),Concatenate(strSubstring(path, 0, 4),'/',name),'')";
        // Testing of the defined exception
        testExpression("test2", "mosaic_test2", expression, RESTECQLTest.fileNames);
    }
}

