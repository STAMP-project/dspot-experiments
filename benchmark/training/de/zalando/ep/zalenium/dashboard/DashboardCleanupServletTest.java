package de.zalando.ep.zalenium.dashboard;


import de.zalando.ep.zalenium.util.CommonProxyUtilities;
import de.zalando.ep.zalenium.util.TestUtils;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class DashboardCleanupServletTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private HttpServletRequest request;

    private HttpServletResponse response;

    private DashboardCleanupServlet dashboardCleanupServlet;

    @Test
    public void getDoCleanup() throws IOException {
        try {
            CommonProxyUtilities proxyUtilities = TestUtils.mockCommonProxyUtilitiesForDashboardTesting(temporaryFolder);
            Dashboard.setCommonProxyUtilities(proxyUtilities);
            Mockito.when(request.getParameter("action")).thenReturn("doCleanup");
            dashboardCleanupServlet.doGet(request, response);
            Assert.assertEquals("SUCCESS", response.getOutputStream().toString());
        } finally {
            Dashboard.restoreCommonProxyUtilities();
        }
    }

    @Test
    public void postDoCleanup() throws IOException {
        try {
            CommonProxyUtilities proxyUtilities = TestUtils.mockCommonProxyUtilitiesForDashboardTesting(temporaryFolder);
            Dashboard.setCommonProxyUtilities(proxyUtilities);
            Mockito.when(request.getParameter("action")).thenReturn("doCleanup");
            dashboardCleanupServlet.doPost(request, response);
            Assert.assertEquals("SUCCESS", response.getOutputStream().toString());
        } finally {
            Dashboard.restoreCommonProxyUtilities();
        }
    }

    @Test
    public void getDoReset() throws IOException {
        try {
            CommonProxyUtilities proxyUtilities = TestUtils.mockCommonProxyUtilitiesForDashboardTesting(temporaryFolder);
            Dashboard.setCommonProxyUtilities(proxyUtilities);
            Mockito.when(request.getParameter("action")).thenReturn("doReset");
            dashboardCleanupServlet.doGet(request, response);
            Assert.assertEquals("SUCCESS", response.getOutputStream().toString());
        } finally {
            Dashboard.restoreCommonProxyUtilities();
        }
    }

    @Test
    public void postDoReset() throws IOException {
        try {
            CommonProxyUtilities proxyUtilities = TestUtils.mockCommonProxyUtilitiesForDashboardTesting(temporaryFolder);
            Dashboard.setCommonProxyUtilities(proxyUtilities);
            Mockito.when(request.getParameter("action")).thenReturn("doReset");
            dashboardCleanupServlet.doPost(request, response);
            Assert.assertEquals("SUCCESS", response.getOutputStream().toString());
        } finally {
            Dashboard.restoreCommonProxyUtilities();
        }
    }

    @Test
    public void postMissingParameter() throws IOException {
        dashboardCleanupServlet.doPost(request, response);
        Assert.assertEquals("ERROR action not implemented. Given action=null", response.getOutputStream().toString());
    }

    @Test
    public void postUnsupportedParameter() throws IOException {
        Mockito.when(request.getParameter("action")).thenReturn("anyValue");
        dashboardCleanupServlet.doPost(request, response);
        Assert.assertEquals("ERROR action not implemented. Given action=anyValue", response.getOutputStream().toString());
    }
}

