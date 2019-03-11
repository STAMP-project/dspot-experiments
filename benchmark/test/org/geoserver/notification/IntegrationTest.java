/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.notification;


import Notification.Action.Add;
import Notification.Action.Remove;
import Notification.Action.Update;
import NotificationTransactionListener.BOUNDS;
import NotificationTransactionListener.INSERTED;
import NotifierInitializer.THREAD_NAME;
import StyleInfo.DEFAULT_POLYGON;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;
import net.sf.json.JSONObject;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.catalog.WMSStoreInfo;
import org.geoserver.notification.common.Bounds;
import org.geoserver.notification.geonode.kombu.KombuCoverageInfo;
import org.geoserver.notification.geonode.kombu.KombuLayerGroupInfo;
import org.geoserver.notification.geonode.kombu.KombuLayerInfo;
import org.geoserver.notification.geonode.kombu.KombuLayerSimpleInfo;
import org.geoserver.notification.geonode.kombu.KombuMessage;
import org.geoserver.notification.geonode.kombu.KombuStoreInfo;
import org.geoserver.notification.geonode.kombu.KombuWMSLayerInfo;
import org.geoserver.notification.support.BrokerManager;
import org.geoserver.notification.support.Receiver;
import org.geoserver.notification.support.ReceiverService;
import org.geoserver.notification.support.Utils;
import org.geoserver.rest.catalog.CatalogRESTTestSupport;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;


public class IntegrationTest extends CatalogRESTTestSupport {
    private static BrokerManager brokerStarter;

    private static Receiver rc;

    /* @Override protected void onSetUp(SystemTestData testData) throws Exception { super.onSetUp(testData);

    }
     */
    @Test
    public void catalogAddNamespaces() throws Exception {
        ReceiverService service = new ReceiverService(2);
        IntegrationTest.rc.receive(service);
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

    @Test
    public void catalogChangeLayerStyle() throws Exception {
        ReceiverService service = new ReceiverService(1);
        IntegrationTest.rc.receive(service);
        LayerInfo l = catalog.getLayerByName("cite:Buildings");
        Assert.assertEquals("Buildings", l.getDefaultStyle().getName());
        JSONObject json = ((JSONObject) (getAsJSON("/rest/layers/cite:Buildings.json")));
        JSONObject layer = ((JSONObject) (json.get("layer")));
        JSONObject style = ((JSONObject) (layer.get("defaultStyle")));
        style.put("name", "polygon");
        style.put("href", "http://localhost:8080/geoserver/rest/styles/polygon.json");
        String updatedJson = json.toString();
        putAsServletResponse("/rest/layers/cite:Buildings", updatedJson, "application/json");
        List<byte[]> ret = service.getMessages();
        Assert.assertEquals(1, ret.size());
        KombuMessage nsMsg = Utils.toKombu(ret.get(0));
        Assert.assertEquals(Update.name(), nsMsg.getAction());
        Assert.assertEquals("Catalog", nsMsg.getType());
        KombuLayerInfo source = ((KombuLayerInfo) (nsMsg.getSource()));
        Assert.assertEquals("LayerInfo", source.getType());
        Assert.assertEquals("polygon", source.getDefaultStyle());
    }

    @Test
    public void catalogChangeLayerStyles() throws Exception {
        ReceiverService service = new ReceiverService(1);
        IntegrationTest.rc.receive(service);
        String xml = "<style>" + (("<name>foo</name>" + "<filename>foo.sld</filename>") + "</style>");
        postAsServletResponse("/rest/workspaces/cite/styles", xml);
        xml = "<layer>" + ((((((("<styles>" + "<style>") + "<name>foo</name>") + "<workspace>cite</workspace>") + "</style>") + "</styles>") + "<enabled>true</enabled>") + "</layer>");
        putAsServletResponse("/rest/layers/cite:Buildings", xml, "application/xml");
        List<byte[]> ret = service.getMessages();
        Assert.assertEquals(1, ret.size());
        KombuMessage updateMsg = Utils.toKombu(ret.get(0));
        Assert.assertEquals("Catalog", updateMsg.getType());
        Assert.assertEquals(Update.name(), updateMsg.getAction());
        KombuLayerInfo source = ((KombuLayerInfo) (updateMsg.getSource()));
        Assert.assertEquals("LayerInfo", source.getType());
        Assert.assertEquals("foo", source.getStyles());
    }

    @Test
    public void catalogAddAndDeleteWMSLayer() throws Exception {
        ReceiverService service = new ReceiverService(3);
        IntegrationTest.rc.receive(service);
        CatalogBuilder cb = new CatalogBuilder(catalog);
        cb.setWorkspace(catalog.getWorkspaceByName("sf"));
        WMSStoreInfo wms = cb.buildWMSStore("demo");
        wms.setCapabilitiesURL("http://demo.opengeo.org/geoserver/wms?");
        catalog.add(wms);
        addStatesWmsLayer();
        Assert.assertNotNull(catalog.getResourceByName("sf", "states", WMSLayerInfo.class));
        deleteAsServletResponse("/rest/workspaces/sf/wmsstores/demo/wmslayers/states");
        List<byte[]> ret = service.getMessages();
        Assert.assertEquals(3, ret.size());
        KombuMessage addStrMsg = Utils.toKombu(ret.get(0));
        Assert.assertEquals(Add.name(), addStrMsg.getAction());
        KombuStoreInfo source1 = ((KombuStoreInfo) (addStrMsg.getSource()));
        Assert.assertEquals("StoreInfo", source1.getType());
        KombuMessage addLayerMsg = Utils.toKombu(ret.get(1));
        Assert.assertEquals(Add.name(), addLayerMsg.getAction());
        KombuWMSLayerInfo source2 = ((KombuWMSLayerInfo) (addLayerMsg.getSource()));
        Assert.assertEquals("WMSLayerInfo", source2.getType());
        KombuMessage deleteMsg = Utils.toKombu(ret.get(2));
        Assert.assertEquals("Catalog", deleteMsg.getType());
        Assert.assertEquals(Remove.name(), deleteMsg.getAction());
        KombuWMSLayerInfo source3 = ((KombuWMSLayerInfo) (deleteMsg.getSource()));
        Assert.assertEquals("WMSLayerInfo", source3.getType());
        Assert.assertEquals("states", source3.getName());
        catalog.remove(wms);
    }

    @Test
    public void cpuLoadTest() throws Exception {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        threadMXBean.setThreadContentionMonitoringEnabled(true);
        threadMXBean.setThreadCpuTimeEnabled(true);
        ReceiverService service = new ReceiverService(3);
        IntegrationTest.rc.receive(service);
        CatalogBuilder cb = new CatalogBuilder(catalog);
        cb.setWorkspace(catalog.getWorkspaceByName("sf"));
        WMSStoreInfo wms = cb.buildWMSStore("demo");
        wms.setCapabilitiesURL("http://demo.opengeo.org/geoserver/wms?");
        try {
            catalog.add(wms);
        } catch (Exception e) {
        }
        addStatesWmsLayer();
        Assert.assertNotNull(catalog.getResourceByName("sf", "states", WMSLayerInfo.class));
        deleteAsServletResponse("/rest/workspaces/sf/wmsstores/demo/wmslayers/states");
        List<byte[]> ret = service.getMessages();
        Assert.assertEquals(3, ret.size());
        Thread.sleep(1000);
        ThreadInfo[] threadInfo = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds());
        for (ThreadInfo threadInfo2 : threadInfo) {
            if (threadInfo2.getThreadName().equals(THREAD_NAME)) {
                long blockedTime = threadInfo2.getBlockedTime();
                long waitedTime = threadInfo2.getWaitedTime();
                long cpuTime = threadMXBean.getThreadCpuTime(threadInfo2.getThreadId());
                long userTime = threadMXBean.getThreadUserTime(threadInfo2.getThreadId());
                String msg = String.format("%s: %d ms cpu time, %d ms user time, blocked for %d ms, waited %d ms", threadInfo2.getThreadName(), (cpuTime / 1000000), (userTime / 1000000), blockedTime, waitedTime);
                System.out.println(msg);
                Assert.assertTrue((waitedTime > 0));
                break;
            }
        }
        catalog.remove(wms);
    }

    @Test
    public void catalogAddCoverage() throws Exception {
        ReceiverService service = new ReceiverService(4);
        IntegrationTest.rc.receive(service);
        addCoverageStore();
        NamespaceInfo ns = catalog.getFactory().createNamespace();
        ns.setPrefix("bar");
        ns.setURI("http://bar");
        catalog.add(ns);
        CoverageInfo ft = catalog.getFactory().createCoverage();
        ft.setName("foo");
        ft.setNamespace(ns);
        ft.setStore(catalog.getCoverageStoreByName("acme", "foostore"));
        catalog.add(ft);
        List<byte[]> ret = service.getMessages();
        Assert.assertEquals(4, ret.size());
        KombuMessage coverageMsg = Utils.toKombu(ret.get(3));
        Assert.assertEquals("Catalog", coverageMsg.getType());
        Assert.assertEquals(Add.name(), coverageMsg.getAction());
        KombuCoverageInfo source = ((KombuCoverageInfo) (coverageMsg.getSource()));
        Assert.assertEquals("CoverageInfo", source.getType());
        Assert.assertEquals(ft.getName(), source.getName());
    }

    @Test
    public void catalogAddLayerGroup() throws Exception {
        ReceiverService service = new ReceiverService(1);
        IntegrationTest.rc.receive(service);
        LayerGroupInfo lg = catalog.getFactory().createLayerGroup();
        lg.setName("sfLayerGroup");
        LayerInfo l = catalog.getLayerByName("cite:Buildings");
        lg.getLayers().add(l);
        lg.getStyles().add(catalog.getStyleByName(DEFAULT_POLYGON));
        lg.setBounds(new org.geotools.geometry.jts.ReferencedEnvelope((-180), (-90), 180, 90, CRS.decode("EPSG:4326")));
        catalog.add(lg);
        List<byte[]> ret = service.getMessages();
        Assert.assertEquals(1, ret.size());
        KombuMessage groupMsg = Utils.toKombu(ret.get(0));
        Assert.assertEquals("Catalog", groupMsg.getType());
        Assert.assertEquals(Add.name(), groupMsg.getAction());
        KombuLayerGroupInfo source = ((KombuLayerGroupInfo) (groupMsg.getSource()));
        Assert.assertEquals("LayerGroupInfo", source.getType());
        Assert.assertEquals(1, source.getLayers().size());
        KombuLayerSimpleInfo kl = source.getLayers().get(0);
        Assert.assertEquals(l.getName(), kl.getName());
        Assert.assertEquals(l.getDefaultStyle().getName(), kl.getStyle());
    }

    @Test
    public void transactionDoubleAdd() throws Exception {
        ReceiverService service = new ReceiverService(1);
        IntegrationTest.rc.receive(service);
        String xml = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" " + (((((((((((((((((((((((((((("xmlns:cgf=\"http://www.opengis.net/cite/geometry\" " + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "xmlns:gml=\"http://www.opengis.net/gml\"> ") + "<wfs:Insert handle='insert-1'> ") + "<cgf:Lines>") + "<cgf:lineStringProperty>") + "<gml:LineString>") + "<gml:coordinates decimal=\".\" cs=\",\" ts=\" \">") + "5,5 6,6") + "</gml:coordinates>") + "</gml:LineString>") + "</cgf:lineStringProperty>") + "<cgf:id>t0001</cgf:id>") + "</cgf:Lines>") + "</wfs:Insert>") + "<wfs:Insert handle='insert-2'> ") + "<cgf:Lines>") + "<cgf:lineStringProperty>") + "<gml:LineString>") + "<gml:coordinates decimal=\".\" cs=\",\" ts=\" \">") + "7,7 8,8") + "</gml:coordinates>") + "</gml:LineString>") + "</cgf:lineStringProperty>") + "<cgf:id>t0002</cgf:id>") + "</cgf:Lines>") + "</wfs:Insert>") + "</wfs:Transaction>");
        postAsDOM("wfs", xml);
        List<byte[]> ret = service.getMessages();
        Assert.assertEquals(1, ret.size());
        KombuMessage tMsg = Utils.toKombu(ret.get(0));
        Assert.assertEquals("Data", tMsg.getType());
        Assert.assertEquals(2, tMsg.getProperties().get(INSERTED));
        Assert.assertNotNull(tMsg.getProperties().get(BOUNDS));
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(ALLOW_UNQUOTED_FIELD_NAMES, true);
        Bounds b = mapper.convertValue(tMsg.getProperties().get(BOUNDS), Bounds.class);
        Assert.assertEquals(5.0, b.getMinx().doubleValue(), 0);
        Assert.assertEquals(5.0, b.getMiny().doubleValue(), 0);
        Assert.assertEquals(8.0, b.getMaxx().doubleValue(), 0);
        Assert.assertEquals(8.0, b.getMaxy().doubleValue(), 0);
    }
}

