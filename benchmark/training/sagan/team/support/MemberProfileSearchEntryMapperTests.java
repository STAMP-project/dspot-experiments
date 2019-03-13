package sagan.team.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.search.types.SearchEntry;
import sagan.team.MemberProfile;


public class MemberProfileSearchEntryMapperTests {
    private MemberProfile profile = new MemberProfile();

    private MemberProfileSearchEntryMapper mapper = new MemberProfileSearchEntryMapper();

    private SearchEntry searchEntry;

    @Test
    public void mapFullNameToTitle() {
        MatcherAssert.assertThat(searchEntry.getTitle(), Matchers.equalTo("First Last"));
    }

    @Test
    public void mapBioToSummary() {
        MatcherAssert.assertThat(searchEntry.getSummary(), Matchers.equalTo("A very good developer!"));
    }

    @Test
    public void mapBioToContent() {
        MatcherAssert.assertThat(searchEntry.getRawContent(), Matchers.equalTo("A very good developer!"));
    }

    @Test
    public void setPath() {
        MatcherAssert.assertThat(searchEntry.getPath(), Matchers.equalTo("/team/jdoe"));
    }
}

