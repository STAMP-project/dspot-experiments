/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.notification;


import Notification.Action.Add;
import java.util.List;
import org.geoserver.notification.geonode.kombu.KombuMessage;
import org.geoserver.notification.support.BrokerManager;
import org.geoserver.notification.support.Receiver;
import org.geoserver.notification.support.ReceiverService;
import org.geoserver.notification.support.Utils;
import org.geoserver.rest.catalog.CatalogRESTTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class AnonymousIntegrationTest extends CatalogRESTTestSupport {
    private static BrokerManager brokerStarter;

    private static Receiver rc;

    @Test
    public void catalogAddNamespaces() throws Exception {
        ReceiverService service = new ReceiverService(2);
        AnonymousIntegrationTest.rc.receive(service);
        String json = "{'namespace':{ 'prefix':'foo', 'uri':'http://foo.com' }}";
        postAsServletResponse("/rest/namespaces", json, "text/json");
        List<byte[]> ret = service.getMessages();
        Assert.assertEquals(2, ret.size());
        KombuMessage nsMsg = Utils.toKombu(ret.get(0));
        Assert.assertEquals(Add.name(), nsMsg.getAction());
        Assert.assertEquals("Catalog", nsMsg.getType());
        Assert.assertEquals("NamespaceInfo", nsMsg.getSource().getType());
        KombuMessage wsMsg = Utils.toKombu(ret.get(1));
        Assert.assertEquals("Catalog", wsMsg.getType());
        Assert.assertEquals("WorkspaceInfo", wsMsg.getSource().getType());
    }
}

