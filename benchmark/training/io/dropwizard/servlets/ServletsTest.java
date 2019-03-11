package io.dropwizard.servlets;


import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class ServletsTest {
    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private final HttpServletRequest fullRequest = Mockito.mock(HttpServletRequest.class);

    @Test
    public void formatsBasicURIs() throws Exception {
        assertThat(Servlets.getFullUrl(request)).isEqualTo("/one/two");
    }

    @Test
    public void formatsFullURIs() throws Exception {
        assertThat(Servlets.getFullUrl(fullRequest)).isEqualTo("/one/two?one=two&three=four");
    }
}

