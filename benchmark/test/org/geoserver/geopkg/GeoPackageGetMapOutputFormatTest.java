/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geopkg;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import org.geoserver.wms.WMSMapContent;
import org.geoserver.wms.WMSTestSupport;
import org.geoserver.wms.WebMap;
import org.geotools.geopkg.GeoPackage;
import org.geotools.geopkg.Tile;
import org.geotools.image.test.ImageAssert;
import org.geotools.util.URLs;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;


/**
 * Test For WMS GetMap Output Format for GeoPackage
 *
 * @author Justin Deoliveira, Boundless
 */
/* public static void main(String[] args) throws Exception {
GeoPackage geopkg = new GeoPackage(new File(
"/Users/jdeolive/geopkg.db"));;
File d = new File("/Users/jdeolive/tiles");
d.mkdir();

TileEntry te = geopkg.tiles().get(0);
TileReader r = geopkg.reader(te, null, null, null, null, null, null);
while(r.hasNext()) {
Tile t = r.next();
File f = new File(d, String.format("%d-%d-%d.png", t.getZoom(), t.getColumn(), t.getRow()));

FileUtils.writeByteArrayToFile(f, t.getData());
}
}
 */
public class GeoPackageGetMapOutputFormatTest extends WMSTestSupport {
    GeoPackageGetMapOutputFormat format;

    @Test
    public void testTileEntries() throws Exception {
        WMSMapContent mapContent = createMapContent(WORLD, LAKES);
        mapContent.getRequest().setBbox(new Envelope((-0.17578125), (-0.087890625), 0.17578125, 0.087890625));
        WebMap map = format.produceMap(mapContent);
        GeoPackage geopkg = createGeoPackage(map);
        Assert.assertTrue(geopkg.features().isEmpty());
        Assert.assertEquals(1, geopkg.tiles().size());
        Assert.assertNotNull(geopkg.tile("World_Lakes"));
    }

    /* From the OGC GeoPackage Specification [1]:

    "The tile coordinate (0,0) always refers to the tile in the upper left corner of the tile matrix at any zoom
    level, regardless of the actual availability of that tile"

    [1]: http://www.geopackage.org/spec/#tile_matrix
     */
    @Test
    public void testTopLeftTile() throws Exception {
        WMSMapContent mapContent = createMapContent(WORLD);
        mapContent.getRequest().setBbox(new Envelope((-180), 180, (-90), 90));
        WebMap map = format.produceMap(mapContent);
        GeoPackage geopkg = createGeoPackage(map);
        Assert.assertTrue(geopkg.features().isEmpty());
        Assert.assertEquals(1, geopkg.tiles().size());
        Tile topLeftTile = geopkg.reader(geopkg.tiles().get(0), 1, 1, 0, 0, 0, 0).next();
        /* FileOutputStream fous = new FileOutputStream("toplefttile.png");
        fous.write(topLeftTile.getData());
        fous.flush();
        fous.close();
         */
        BufferedImage tileImg = ImageIO.read(new ByteArrayInputStream(topLeftTile.getData()));
        ImageAssert.assertEquals(URLs.urlToFile(getClass().getResource("toplefttile.png")), tileImg, 250);
    }
}

