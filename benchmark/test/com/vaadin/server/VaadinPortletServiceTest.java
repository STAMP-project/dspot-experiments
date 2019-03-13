package com.vaadin.server;


import Constants.DEFAULT_THEME_NAME;
import Constants.DEFAULT_WIDGETSET;
import UIConstants.UI_ID_PARAMETER;
import com.vaadin.ui.UI;
import java.util.concurrent.locks.ReentrantLock;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class VaadinPortletServiceTest {
    private VaadinPortletService sut;

    private VaadinPortletRequest request;

    private DeploymentConfiguration conf;

    @Test
    public void preferencesOverrideDeploymentConfiguration() {
        mockFileLocationPreference("prefs");
        mockLocationDeploymentConfiguration("conf");
        String location = getStaticFileLocation();
        MatcherAssert.assertThat(location, Is.is("prefs"));
    }

    @Test
    public void deploymentConfigurationOverridesProperties() {
        mockFileLocationPreference(null);
        mockLocationDeploymentConfiguration("conf");
        mockFileLocationProperty("props");
        String location = getStaticFileLocation();
        MatcherAssert.assertThat(location, Is.is("conf"));
    }

    @Test
    public void defaultFileLocationIsSet() {
        mockFileLocationPreference(null);
        mockLocationDeploymentConfiguration(null);
        mockFileLocationProperty(null);
        String location = getStaticFileLocation();
        MatcherAssert.assertThat(location, Is.is("/html"));
    }

    @Test
    public void trailingSlashesAreTrimmedFromStaticFileLocation() {
        mockFileLocationPreference("/content////");
        String staticFileLocation = getStaticFileLocation();
        MatcherAssert.assertThat(staticFileLocation, Is.is("/content"));
    }

    @Test
    public void themeCanBeOverridden() {
        mockThemeProperty("foobar");
        String theme = getTheme();
        MatcherAssert.assertThat(theme, Is.is("foobar"));
    }

    @Test
    public void defaultThemeIsSet() {
        mockThemeProperty(null);
        String theme = getTheme();
        MatcherAssert.assertThat(theme, Is.is(DEFAULT_THEME_NAME));
    }

    @Test
    public void defaultWidgetsetIsSet() {
        mockWidgetsetProperty(null);
        mockWidgetsetConfiguration(null);
        String widgetset = getWidgetset();
        MatcherAssert.assertThat(widgetset, Is.is(DEFAULT_WIDGETSET));
    }

    @Test
    public void configurationWidgetsetOverridesProperty() {
        mockWidgetsetProperty("foo");
        mockWidgetsetConfiguration("bar");
        String widgetset = getWidgetset();
        MatcherAssert.assertThat(widgetset, Is.is("bar"));
    }

    @Test
    public void oldDefaultWidgetsetIsMappedToDefaultWidgetset() {
        mockWidgetsetConfiguration(null);
        mockWidgetsetProperty("com.vaadin.portal.gwt.PortalDefaultWidgetSet");
        String widgetset = getWidgetset();
        MatcherAssert.assertThat(widgetset, Is.is(DEFAULT_WIDGETSET));
    }

    @Test
    public void oldDefaultWidgetSetIsNotMappedToDefaultWidgetset() {
        mockWidgetsetConfiguration("com.vaadin.portal.gwt.PortalDefaultWidgetSet");
        mockWidgetsetProperty(null);
        String widgetset = getWidgetset();
        MatcherAssert.assertThat(widgetset, Is.is("com.vaadin.portal.gwt.PortalDefaultWidgetSet"));
    }

    @Test
    public void findUIDoesntThrowNPE() {
        try {
            ReentrantLock mockLock = Mockito.mock(ReentrantLock.class);
            Mockito.when(mockLock.isHeldByCurrentThread()).thenReturn(true);
            WrappedSession emptyWrappedSession = Mockito.mock(WrappedPortletSession.class);
            Mockito.when(emptyWrappedSession.getAttribute("null.lock")).thenReturn(mockLock);
            VaadinRequest requestWithUIIDSet = Mockito.mock(VaadinRequest.class);
            Mockito.when(requestWithUIIDSet.getParameter(UI_ID_PARAMETER)).thenReturn("1");
            Mockito.when(requestWithUIIDSet.getWrappedSession()).thenReturn(emptyWrappedSession);
            UI ui = sut.findUI(requestWithUIIDSet);
            Assert.assertNull("Unset session did not return null", ui);
        } catch (NullPointerException e) {
            Assert.fail("findUI threw a NullPointerException");
        }
    }
}

