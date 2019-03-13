package com.vaadin.server;


import com.vaadin.server.VaadinPortlet.VaadinHttpAndPortletRequest;
import java.util.Enumeration;
import java.util.Map;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;


public abstract class VaadinHttpAndPortletRequestTestBase<T extends VaadinHttpAndPortletRequest> {
    protected VaadinHttpAndPortletRequest sut;

    protected HttpServletRequest servletRequest;

    protected PortletRequest portletRequest;

    protected VaadinPortletService vaadinPortletService;

    @Test
    public void parameterIsFetchedFromServletRequest() {
        Mockito.when(servletRequest.getParameter("foo")).thenReturn("bar");
        String parameter = sut.getParameter("foo");
        MatcherAssert.assertThat(parameter, Is.is("bar"));
    }

    @Test
    public void originalParameterIsOverridden() {
        Mockito.when(servletRequest.getParameter("foo")).thenReturn("braa");
        Mockito.when(portletRequest.getParameter("foo")).thenReturn("bar");
        String parameter = sut.getParameter("foo");
        MatcherAssert.assertThat(parameter, Is.is("bar"));
    }

    @Test
    public void remoteAddressIsFetchedFromServletRequest() {
        Mockito.when(servletRequest.getRemoteAddr()).thenReturn("foo");
        String remoteAddr = sut.getRemoteAddr();
        MatcherAssert.assertThat(remoteAddr, Is.is("foo"));
    }

    @Test
    public void remoteHostIsFetchedFromServletRequest() {
        Mockito.when(servletRequest.getRemoteHost()).thenReturn("foo");
        String remoteHost = sut.getRemoteHost();
        MatcherAssert.assertThat(remoteHost, Is.is("foo"));
    }

    @Test
    public void remotePortIsFetchedFromServletRequest() {
        Mockito.when(servletRequest.getRemotePort()).thenReturn(12345);
        int remotePort = sut.getRemotePort();
        MatcherAssert.assertThat(remotePort, Is.is(12345));
    }

    @Test
    public void headerIsFetchedFromServletRequest() {
        Mockito.when(servletRequest.getHeader("foo")).thenReturn("bar");
        String header = sut.getHeader("foo");
        MatcherAssert.assertThat(header, Is.is("bar"));
    }

    @Test
    public void headerNamesAreFetchedFromServletRequest() {
        Enumeration expectedHeaderNames = Mockito.mock(Enumeration.class);
        Mockito.when(servletRequest.getHeaderNames()).thenReturn(expectedHeaderNames);
        Enumeration<String> actualHeaderNames = sut.getHeaderNames();
        MatcherAssert.assertThat(actualHeaderNames, Is.is(expectedHeaderNames));
    }

    @Test
    public void headersAreFetchedFromServletRequest() {
        Enumeration expectedHeaders = Mockito.mock(Enumeration.class);
        Mockito.when(servletRequest.getHeaders("foo")).thenReturn(expectedHeaders);
        Enumeration<String> actualHeaders = sut.getHeaders("foo");
        MatcherAssert.assertThat(actualHeaders, Is.is(expectedHeaders));
    }

    @Test
    public void parameterMapIsFetchedFromServletRequest() {
        Map expectedParameterMap = Mockito.mock(Map.class);
        Mockito.when(servletRequest.getParameterMap()).thenReturn(expectedParameterMap);
        Map<String, String[]> actualParameterMap = sut.getParameterMap();
        MatcherAssert.assertThat(actualParameterMap, Is.is(expectedParameterMap));
    }
}

