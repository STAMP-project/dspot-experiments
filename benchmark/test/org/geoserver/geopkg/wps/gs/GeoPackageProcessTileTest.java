/**
 * (c) 2014-2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geopkg.wps.gs;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.geoserver.wps.WPSTestSupport;
import org.geotools.geopkg.GeoPackage;
import org.geotools.geopkg.Tile;
import org.geotools.image.test.ImageAssert;
import org.geotools.util.URLs;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class GeoPackageProcessTileTest extends WPSTestSupport {
    @Test
    public void testGeoPackageProcessTilesTopLeftTile() throws Exception {
        String urlPath = string(post("wps", getXmlTilesWorld())).trim();
        String resourceUrl = urlPath.substring("http://localhost:8080/geoserver/".length());
        MockHttpServletResponse response = getAsServletResponse(resourceUrl);
        File file = new File(getDataDirectory().findOrCreateDir("tmp"), "worldtest.gpkg");
        FileUtils.writeByteArrayToFile(file, getBinary(response));
        Assert.assertNotNull(file);
        Assert.assertEquals("worldtest.gpkg", file.getName());
        Assert.assertTrue(file.exists());
        GeoPackage gpkg = new GeoPackage(file);
        Tile topLeftTile = gpkg.reader(gpkg.tiles().get(0), 1, 1, 0, 0, 0, 0).next();
        BufferedImage tileImg = ImageIO.read(new ByteArrayInputStream(topLeftTile.getData()));
        ImageAssert.assertEquals(URLs.urlToFile(getClass().getResource("wps_toplefttile.png")), tileImg, 250);
        gpkg.close();
    }
}

