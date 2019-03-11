package sagan.team.support;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import sagan.search.support.SearchService;
import sagan.search.types.SitePage;
import sagan.team.MemberProfile;


@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTests {
    @Mock
    private TeamRepository teamRepository;

    @Mock
    private SearchService searchService;

    @Mock
    private MemberProfileSearchEntryMapper mapper;

    private TeamService service;

    @Test
    public void updateMemberProfileSavesProfileToSearchIndex() {
        MemberProfile savedProfile = new MemberProfile();
        BDDMockito.given(teamRepository.findById(1234L)).willReturn(savedProfile);
        SitePage searchEntry = new SitePage();
        BDDMockito.given(mapper.map(savedProfile)).willReturn(searchEntry);
        service.updateMemberProfile(1234L, new MemberProfile());
        Mockito.verify(searchService).saveToIndex(searchEntry);
    }

    @Test
    public void updateMemberProfileUpdatesAvatarUrlFromGravatarEmail() {
        MemberProfile savedProfile = new MemberProfile();
        BDDMockito.given(teamRepository.findById(1234L)).willReturn(savedProfile);
        SitePage searchEntry = new SitePage();
        BDDMockito.given(mapper.map(savedProfile)).willReturn(searchEntry);
        MemberProfile updatedProfile = new MemberProfile();
        updatedProfile.setGravatarEmail("test@example.com");
        service.updateMemberProfile(1234L, updatedProfile);
        MatcherAssert.assertThat(savedProfile.getGravatarEmail(), CoreMatchers.equalTo("test@example.com"));
        MatcherAssert.assertThat(savedProfile.getAvatarUrl(), CoreMatchers.equalTo("https://gravatar.com/avatar/55502f40dc8b7c769880b10874abc9d0"));
    }

    @Test
    public void updateMemberProfileDoesNotUpdateAvatarUrlIfGravatarEmailIsEmpty() {
        MemberProfile savedProfile = new MemberProfile();
        savedProfile.setAvatarUrl("http://example.com/image.png");
        BDDMockito.given(teamRepository.findById(1234L)).willReturn(savedProfile);
        SitePage searchEntry = new SitePage();
        BDDMockito.given(mapper.map(savedProfile)).willReturn(searchEntry);
        MemberProfile updatedProfile = new MemberProfile();
        updatedProfile.setGravatarEmail("");
        service.updateMemberProfile(1234L, updatedProfile);
        MatcherAssert.assertThat(savedProfile.getAvatarUrl(), CoreMatchers.equalTo("http://example.com/image.png"));
    }
}

