package io.hawt.web;


import io.hawt.web.auth.RelativeRequestUri;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;


@RunWith(Parameterized.class)
public class RelativeRequestUriTest {
    @Mock
    private HttpServletRequest request;

    private final String contextPath;

    private final int pathIndex;

    private final String requestUri;

    private final String expectedPrefix;

    private final String expectedUri;

    private final String[] expectedComponents;

    private final String expectedLastComponent;

    public RelativeRequestUriTest(final String contextPath, final int pathIndex, final String requestUri, final String expectedPrefix, final String expectedUri, final String[] expectedComponents) {
        this.contextPath = contextPath;
        this.pathIndex = pathIndex;
        this.requestUri = requestUri;
        this.expectedPrefix = expectedPrefix;
        this.expectedUri = expectedUri;
        this.expectedComponents = expectedComponents;
        this.expectedLastComponent = ((expectedComponents.length) == 0) ? null : expectedComponents[((expectedComponents.length) - 1)];
    }

    @Test
    public void test() {
        final RelativeRequestUri underTest = new RelativeRequestUri(request, pathIndex);
        Assert.assertEquals(expectedPrefix, underTest.getPrefix());
        Assert.assertEquals(expectedUri, underTest.getUri());
        Assert.assertEquals(expectedUri, underTest.toString());
        Assert.assertArrayEquals(expectedComponents, underTest.getComponents());
        Assert.assertEquals(expectedLastComponent, underTest.getLastComponent());
    }
}

