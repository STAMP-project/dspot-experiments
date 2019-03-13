package net.osmand.search;


import java.io.IOException;
import net.osmand.data.LatLon;
import org.junit.Test;


public class LocationSearchTest {
    @Test
    public void testGeo() throws IOException {
        search("geo:34.99393,-106.61568 (Treasure Island, other irrelevant info) ", new LatLon(34.99393, (-106.61568)));
        search("http://download.osmand.net/go?lat=34.99393&lon=-106.61568&z=11", new LatLon(34.99393, (-106.61568)));
    }

    @Test
    public void testBasicCommaSearch() throws IOException {
        search("5.0,3.0", new LatLon(5, 3));
        search("5.445,3.523", new LatLon(5.445, 3.523));
        search("5:1:1,3:1", new LatLon(((5 + (1 / 60.0F)) + (1 / 3600.0F)), (3 + (1 / 60.0F))));
    }

    @Test
    public void testUTMSearch() throws IOException {
        search("17N6734294749123", new LatLon(42.875017, (-78.87659050764749)));
        search("17 N 673429 4749123", new LatLon(42.875017, (-78.87659050764749)));
        search("36N 609752 5064037", new LatLon(45.721184, 34.410328));
    }

    @Test
    public void testBasicSpaceSearch() throws IOException {
        search("5.0 3.0", new LatLon(5, 3));
        search("-5.0 -3.0", new LatLon((-5), (-3)));
        search("-45.5 3.0S", new LatLon((-45.5), (-3)));
        search("45.5S 3.0 W", new LatLon((-45.5), (-3)));
        search("5.445 3.523", new LatLon(5.445, 3.523));
        search("5:1:1 3:1", new LatLon(((5 + (1 / 60.0F)) + (1 / 3600.0F)), (3 + (1 / 60.0F))));
        search("5:1#1 3#1", new LatLon(((5 + (1 / 60.0F)) + (1 / 3600.0F)), (3 + (1 / 60.0F))));
        search("5#1#1 3#1", new LatLon(((5 + (1 / 60.0F)) + (1 / 3600.0F)), (3 + (1 / 60.0F))));
        search("5'1'1 3'1", new LatLon(((5 + (1 / 60.0F)) + (1 / 3600.0F)), (3 + (1 / 60.0F))));
        search("Lat: 5.0 Lon: 3.0", new LatLon(5, 3));
    }

    @Test
    public void testSimpleURLSearch() throws IOException {
        search("ftp://simpleurl?lat=34.23&lon=-53.2&z=15", new LatLon(34.23, (-53.2)));
        search("ftp://simpleurl?z=15&lat=34.23&lon=-53.2", new LatLon(34.23, (-53.2)));
    }

    @Test
    public void testAdvancedSpaceSearch() throws IOException {
        search("5 30 30 N 4 30 W", new LatLon((5.5 + (30 / 3600.0F)), (-4.5)));
        search("5 30  -4 30", new LatLon(5.5, (-4.5)));
        search("S 5 30  4 30 W", new LatLon((-5.5), (-4.5)));
        search("S5.4232  4.30W", new LatLon((-5.4232), (-4.3)));
        search("S5.4232  W4.30", new LatLon((-5.4232), (-4.3)));
        search("5.4232, W4.30", new LatLon(5.4232, (-4.3)));
        search("5.4232N, 45 30.5W", new LatLon(5.4232, (-(45 + (30.5 / 60.0F)))));
    }

    @Test
    public void testArcgisSpaceSearch() throws IOException {
        search("43?S 79?23?13.7?W", new LatLon((-43), (-((79 + (23 / 60.0F)) + (13.7 / 3600.0F)))));
        search("43?38?33.24?N 79?23?13.7?W", new LatLon(((43 + (38 / 60.0F)) + (33.24 / 3600.0F)), (-((79 + (23 / 60.0F)) + (13.7 / 3600.0F)))));
        search("45\u00b0 30\'30\"W 3.0", new LatLon(((45 + 0.5) + (1 / 120.0F)), (-3)));
        search("43? 79?23?13.7?E", new LatLon(43, ((79 + (23 / 60.0F)) + (13.7 / 3600.0F))));
        search("43?38? 79?23?13.7?E", new LatLon((43 + (38 / 60.0F)), ((79 + (23 / 60.0F)) + (13.7 / 3600.0F))));
        search("43\u00b038\u203223\" 79\u00b023\u203213.7\u2033E", new LatLon(((43 + (38 / 60.0F)) + (23 / 3600.0F)), ((79 + (23 / 60.0F)) + (13.7 / 3600.0F))));
    }
}

