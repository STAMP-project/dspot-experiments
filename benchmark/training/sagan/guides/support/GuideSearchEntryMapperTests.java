package sagan.guides.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import sagan.guides.GettingStartedGuide;
import sagan.search.types.SearchEntry;


public class GuideSearchEntryMapperTests {
    private GettingStartedGuide guide;

    private GuideSearchEntryMapper guideMapper = new GuideSearchEntryMapper();

    private SearchEntry searchEntry;

    @Test
    public void mapsRawContent() throws Exception {
        MatcherAssert.assertThat(searchEntry.getRawContent(), IsEqual.equalTo("Some Guide Content"));
    }

    @Test
    public void mapsTitle() throws Exception {
        MatcherAssert.assertThat(searchEntry.getTitle(), IsEqual.equalTo("Guide XYZ Title"));
    }

    @Test
    public void mapsSubTitle() throws Exception {
        MatcherAssert.assertThat(searchEntry.getSubTitle(), IsEqual.equalTo("Getting Started Guide"));
    }
}

