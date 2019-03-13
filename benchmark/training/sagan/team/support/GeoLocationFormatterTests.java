package sagan.team.support;


import java.text.ParseException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.team.GeoLocation;


public class GeoLocationFormatterTests {
    private GeoLocationFormatter formatter;

    @Test
    public void testParse() throws Exception {
        assertLatLon("1,1", 1.0F, 1.0F);
        assertLatLon("1.1,1.1", 1.1F, 1.1F);
        assertLatLon("-90.0,-180", (-90.0F), (-180.0F));
        assertLatLon("1.1 , 1.1", 1.1F, 1.1F);
    }

    @Test(expected = ParseException.class)
    public void testNoParse() throws Exception {
        formatter.parse("afslk", null);
    }

    @Test
    public void testPrint() throws Exception {
        MatcherAssert.assertThat(formatter.print(new GeoLocation((-10.3F), 87.42F), null), Matchers.equalTo("-10.300000,87.419998"));
    }
}

