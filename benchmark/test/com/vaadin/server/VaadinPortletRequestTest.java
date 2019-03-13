package com.vaadin.server;


import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class VaadinPortletRequestTest {
    private PortletRequest request;

    private VaadinPortletRequest sut;

    private VaadinPortletService service;

    private PortletPreferences preferences;

    @Test
    public void portletPreferenceIsFetched() {
        Mockito.when(preferences.getValue(ArgumentMatchers.eq("foo"), ArgumentMatchers.anyString())).thenReturn("bar");
        String value = sut.getPortletPreference("foo");
        Assert.assertThat(value, CoreMatchers.is("bar"));
    }

    @Test
    public void defaultValueForPortletPreferenceIsNull() {
        Mockito.when(preferences.getValue(ArgumentMatchers.anyString(), ArgumentMatchers.isNull(String.class))).thenReturn(null);
        String value = sut.getPortletPreference("foo");
        Assert.assertNull(value);
    }
}

