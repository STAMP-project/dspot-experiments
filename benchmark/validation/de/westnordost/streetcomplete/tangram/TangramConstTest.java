package de.westnordost.streetcomplete.tangram;


import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.OsmLatLon;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TangramConstTest {
    @Test
    public void convertSingle() {
        double lng = 10;
        double lat = 5;
        OsmLatLon pos = new OsmLatLon(lat, lng);
        LatLon pos2 = TangramConst.toLatLon(TangramConst.toLngLat(pos));
        Assert.assertEquals(pos, pos2);
    }

    @Test
    public void convertLists() {
        List<List<LatLon>> positionLists = new ArrayList<>();
        List<LatLon> positions1 = new ArrayList<>();
        positionLists.add(positions1);
        List<LatLon> positions2 = new ArrayList<>();
        positions2.add(new OsmLatLon(1, 2));
        positions2.add(new OsmLatLon(3, 4));
        positionLists.add(positions2);
        List<LatLon> positions3 = new ArrayList<>();
        positions3.add(new OsmLatLon(5, 6));
        positionLists.add(positions3);
        List<List<LatLon>> positionLists2 = TangramConst.toLatLon(TangramConst.toLngLat(positionLists));
        Assert.assertEquals(positionLists, positionLists2);
    }
}

