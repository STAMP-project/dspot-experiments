/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster.impl.handlers.catalog;


import com.thoughtworks.xstream.XStream;
import java.util.Arrays;
import org.geoserver.catalog.event.CatalogEvent;
import org.geoserver.catalog.event.CatalogModifyEvent;
import org.geoserver.catalog.event.impl.CatalogModifyEventImpl;
import org.geoserver.catalog.impl.CatalogImpl;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;


public final class JMSCatalogModifyEventHandlerTest {
    @Test
    public void testCatalogModifyEventHandling() throws Exception {
        // create a catalog modify event that include properties of type catalog
        CatalogModifyEventImpl catalogModifyEvent = new CatalogModifyEventImpl();
        catalogModifyEvent.setPropertyNames(Arrays.asList("propertyA", "propertyB", "propertyC", "propertyD"));
        catalogModifyEvent.setOldValues(Arrays.asList("value", new CatalogImpl(), 50, null));
        catalogModifyEvent.setNewValues(Arrays.asList("new_value", new CatalogImpl(), null, new CatalogImpl()));
        // serialise the event and deserialize it
        JMSCatalogModifyEventHandlerSPI handler = new JMSCatalogModifyEventHandlerSPI(0, null, new XStream(), null);
        String serializedEvent = handler.createHandler().serialize(catalogModifyEvent);
        CatalogEvent newEvent = handler.createHandler().deserialize(serializedEvent);
        // check the deserialized event
        Assert.assertThat(newEvent, CoreMatchers.notNullValue());
        Assert.assertThat(newEvent, IsInstanceOf.instanceOf(CatalogModifyEvent.class));
        CatalogModifyEvent newModifyEvent = ((CatalogModifyEvent) (newEvent));
        // check properties names
        Assert.assertThat(newModifyEvent.getPropertyNames().size(), CoreMatchers.is(2));
        Assert.assertThat(newModifyEvent.getPropertyNames(), CoreMatchers.hasItems("propertyA", "propertyC"));
        // check old values
        Assert.assertThat(newModifyEvent.getOldValues().size(), CoreMatchers.is(2));
        Assert.assertThat(newModifyEvent.getOldValues(), CoreMatchers.hasItems("value", 50));
        // check new values
        Assert.assertThat(newModifyEvent.getNewValues().size(), CoreMatchers.is(2));
        Assert.assertThat(newModifyEvent.getNewValues(), CoreMatchers.hasItems("new_value", null));
    }
}

