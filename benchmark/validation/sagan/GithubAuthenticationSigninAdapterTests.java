package sagan;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.github.api.GitHub;
import org.springframework.web.client.RestClientException;
import sagan.SecurityConfig.GithubAuthenticationSigninAdapter;
import sagan.team.MemberProfile;
import sagan.team.support.SignInService;


@RunWith(MockitoJUnitRunner.class)
public class GithubAuthenticationSigninAdapterTests {
    private GithubAuthenticationSigninAdapter adapter;

    @SuppressWarnings("unchecked")
    private Connection<GitHub> connection = Mockito.mock(Connection.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SignInService signInService;

    @Test
    public void signInSunnyDay() {
        MemberProfile newMember = new MemberProfile();
        BDDMockito.given(signInService.getOrCreateMemberProfile(ArgumentMatchers.anyLong(), ArgumentMatchers.any(GitHub.class))).willReturn(newMember);
        BDDMockito.given(connection.getDisplayName()).willReturn("dsyer");
        BDDMockito.given(signInService.isSpringMember(ArgumentMatchers.eq("dsyer"), ArgumentMatchers.any(GitHub.class))).willReturn(true);
        adapter.signIn("12345", connection, new org.springframework.web.context.request.ServletWebRequest(new MockHttpServletRequest()));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull(authentication);
        Assert.assertTrue(authentication.isAuthenticated());
        Mockito.verify(signInService).getOrCreateMemberProfile(ArgumentMatchers.eq(12345L), ArgumentMatchers.any(GitHub.class));
    }

    @Test
    public void signInFailure() {
        BDDMockito.given(connection.getDisplayName()).willReturn("dsyer");
        BDDMockito.given(signInService.isSpringMember(ArgumentMatchers.eq("dsyer"), ArgumentMatchers.any(GitHub.class))).willReturn(false);
        expectedException.expect(BadCredentialsException.class);
        adapter.signIn("12345", connection, new org.springframework.web.context.request.ServletWebRequest(new MockHttpServletRequest()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void signInFailureAfterRestException() {
        BDDMockito.given(connection.getDisplayName()).willReturn("dsyer");
        BDDMockito.given(signInService.isSpringMember(ArgumentMatchers.eq("dsyer"), ArgumentMatchers.any(GitHub.class))).willThrow(RestClientException.class);
        expectedException.expect(BadCredentialsException.class);
        adapter.signIn("12345", connection, new org.springframework.web.context.request.ServletWebRequest(new MockHttpServletRequest()));
    }
}

