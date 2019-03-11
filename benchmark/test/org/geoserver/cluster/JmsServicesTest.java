/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster;


import JMSEventType.ADDED;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import javax.jms.Message;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.geoserver.cluster.impl.events.configuration.JMSServiceModifyEvent;
import org.geoserver.config.ServiceInfo;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.WMSInfoImpl;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests related with services events.
 */
public final class JmsServicesTest extends GeoServerSystemTestSupport {
    private static final String SERVICE_EVENT_HANDLER_KEY = "JMSServiceHandlerSPI";

    private WorkspaceInfoImpl workspace;

    private static JMSEventHandler<String, JMSServiceModifyEvent> serviceHandler;

    @Test
    public void testAddService() throws Exception {
        // create a WMS service for the test workspace
        WMSInfoImpl serviceInfo = new WMSInfoImpl();
        serviceInfo.setName("TEST-WMS-NAME");
        serviceInfo.setId("TEST-WMS-ID");
        serviceInfo.setWorkspace(workspace);
        serviceInfo.setAbstract("TEST-WMS-ABSTRACT");
        // add the new service to GeoServer
        getGeoServer().add(serviceInfo);
        // waiting for a service add event
        List<Message> messages = JmsEventsListener.getMessagesByHandlerKey(5000, ( selected) -> (selected.size()) >= 1, JmsServicesTest.SERVICE_EVENT_HANDLER_KEY);
        // let's check if the new added service was correctly published
        Assert.assertThat(messages.size(), CoreMatchers.is(1));
        List<JMSServiceModifyEvent> serviceEvents = JmsEventsListener.getMessagesForHandler(messages, JmsServicesTest.SERVICE_EVENT_HANDLER_KEY, JmsServicesTest.serviceHandler);
        Assert.assertThat(serviceEvents.size(), CoreMatchers.is(1));
        Assert.assertThat(serviceEvents.get(0).getEventType(), CoreMatchers.is(ADDED));
        // check the service content
        ServiceInfo publishedService = serviceEvents.get(0).getSource();
        Assert.assertThat(publishedService.getName(), CoreMatchers.is("TEST-WMS-NAME"));
        Assert.assertThat(publishedService.getId(), CoreMatchers.is("TEST-WMS-ID"));
        Assert.assertThat(publishedService.getAbstract(), CoreMatchers.is("TEST-WMS-ABSTRACT"));
    }

    @Test
    public void testModifyService() throws Exception {
        // modify the abstract of the WMS service
        WMSInfo serviceInfo = getGeoServer().getService(WMSInfo.class);
        Assert.assertThat(serviceInfo, CoreMatchers.notNullValue());
        String newAbstract = UUID.randomUUID().toString();
        serviceInfo.setAbstract(newAbstract);
        getGeoServer().save(serviceInfo);
        // waiting for the service modify events
        List<Message> messages = JmsEventsListener.getMessagesByHandlerKey(5000, ( selected) -> (selected.size()) >= 2, JmsServicesTest.SERVICE_EVENT_HANDLER_KEY);
        // checking if we got the correct events, modify event and a post modify event
        Assert.assertThat(messages.size(), CoreMatchers.is(2));
        List<JMSServiceModifyEvent> serviceEvents = JmsEventsListener.getMessagesForHandler(messages, JmsServicesTest.SERVICE_EVENT_HANDLER_KEY, JmsServicesTest.serviceHandler);
        Assert.assertThat(serviceEvents.size(), CoreMatchers.is(2));
        // check the modify event
        JMSServiceModifyEvent modifyEvent = serviceEvents.stream().filter(( event) -> (event.getEventType()) == JMSEventType.MODIFIED).findFirst().orElse(null);
        Assert.assertThat(modifyEvent, CoreMatchers.notNullValue());
        ServiceInfo modifiedService = serviceEvents.get(0).getSource();
        Assert.assertThat(modifiedService.getName(), CoreMatchers.is(serviceInfo.getName()));
        Assert.assertThat(modifiedService.getId(), CoreMatchers.is(serviceInfo.getId()));
        Assert.assertThat(modifiedService.getAbstract(), CoreMatchers.is(newAbstract));
        // check the post modify event
        JMSServiceModifyEvent postModifyEvent = serviceEvents.stream().filter(( event) -> (event.getEventType()) == JMSEventType.ADDED).findFirst().orElse(null);
        Assert.assertThat(postModifyEvent, CoreMatchers.notNullValue());
        ServiceInfo postModifiedService = serviceEvents.get(0).getSource();
        Assert.assertThat(postModifiedService.getName(), CoreMatchers.is(serviceInfo.getName()));
        Assert.assertThat(postModifiedService.getId(), CoreMatchers.is(serviceInfo.getId()));
        Assert.assertThat(postModifiedService.getAbstract(), CoreMatchers.is(newAbstract));
    }
}

