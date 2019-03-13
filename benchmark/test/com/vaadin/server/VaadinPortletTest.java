package com.vaadin.server;


import com.vaadin.server.VaadinPortlet.VaadinGateInRequest;
import com.vaadin.server.VaadinPortlet.VaadinLiferayRequest;
import com.vaadin.server.VaadinPortlet.VaadinWebSpherePortalRequest;
import javax.portlet.PortalContext;
import javax.portlet.PortletRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;


public class VaadinPortletTest {
    private VaadinPortlet sut;

    private PortletRequest portletRequest;

    private PortalContext portalContext;

    @Test
    public void gateInRequestIsCreated() {
        mockPortalInfo("gatein");
        VaadinPortletRequest request = createRequest();
        MatcherAssert.assertThat(request, IsInstanceOf.instanceOf(VaadinGateInRequest.class));
    }

    @Test
    public void liferayRequestIsCreated() {
        mockPortalInfo("liferay");
        VaadinPortletRequest request = createRequest();
        MatcherAssert.assertThat(request, IsInstanceOf.instanceOf(VaadinLiferayRequest.class));
    }

    @Test
    public void webspherePortalRequestIsCreated() {
        mockPortalInfo("websphere portal");
        VaadinPortletRequest request = createRequest();
        MatcherAssert.assertThat(request, IsInstanceOf.instanceOf(VaadinWebSpherePortalRequest.class));
    }

    @Test
    public void defaultPortletRequestIsCreated() {
        mockPortalInfo("foobar");
        VaadinPortletRequest request = createRequest();
        MatcherAssert.assertThat(request, IsInstanceOf.instanceOf(VaadinPortletRequest.class));
    }
}

