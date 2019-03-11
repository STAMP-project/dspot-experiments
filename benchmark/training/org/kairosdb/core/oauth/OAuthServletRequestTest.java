package org.kairosdb.core.oauth;


import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.kairosdb.core.oauth.OAuthFilter.OAuthServletRequest;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class OAuthServletRequestTest {
    @Test
    public void test_getParameterValues() {
        String[] parameters = new String[]{ "a", "b" };
        HttpServletRequest mockServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockServletRequest.getParameterValues(ArgumentMatchers.anyString())).thenReturn(parameters);
        OAuthServletRequest oAuthServletRequest = new OAuthServletRequest(mockServletRequest);
        List<String> parameterValues = oAuthServletRequest.getParameterValues("foo");
        MatcherAssert.assertThat(parameterValues, CoreMatchers.equalTo(Arrays.asList(parameters)));
    }
}

