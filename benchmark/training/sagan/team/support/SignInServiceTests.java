package sagan.team.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.github.api.UserOperations;


@RunWith(MockitoJUnitRunner.class)
public class SignInServiceTests {
    @Mock
    private GitHub gitHub;

    @Mock
    private TeamService teamService;

    private SignInService signInService;

    private String username = "user";

    private String name = "Full Name";

    private String location = "London";

    private String email = "user@example.com";

    private String avatarUrl = "http://gravatar.com/avatar/ABC";

    @Test
    public void createOrUpdateMemberProfileOnLogin() {
        GitHubUserProfile userProfile = new GitHubUserProfile(1234L, username, name, location, "", "", email, avatarUrl, null);
        UserOperations userOperations = Mockito.mock(UserOperations.class);
        BDDMockito.given(userOperations.getUserProfile()).willReturn(userProfile);
        BDDMockito.given(gitHub.userOperations()).willReturn(userOperations);
        signInService.getOrCreateMemberProfile(1234L, gitHub);
        Mockito.verify(teamService).createOrUpdateMemberProfile(1234L, username, avatarUrl, name);
    }

    @Test
    public void isSpringMember() {
        mockIsMemberOfTeam(true);
        MatcherAssert.assertThat(signInService.isSpringMember("member", gitHub), Matchers.is(true));
    }

    @Test
    public void isNotSpringMember() {
        mockIsMemberOfTeam(false);
        MatcherAssert.assertThat(signInService.isSpringMember("notmember", gitHub), Matchers.is(false));
    }
}

