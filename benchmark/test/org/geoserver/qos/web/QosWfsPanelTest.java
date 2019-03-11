/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.qos.web;


import WfsQosConfigurationLoader.SPRING_BEAN_NAME;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.geoserver.config.ServiceInfo;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.qos.wfs.WfsQosConfigurationLoader;
import org.geoserver.qos.xml.QosMainConfiguration;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.wfs.WFSInfo;
import org.geoserver.wms.WMSInfo;
import org.junit.Assert;
import org.junit.Test;


public class QosWfsPanelTest extends GeoServerWicketTestSupport {
    @Test
    public void testQosDisabled() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        WfsQosConfigurationLoader loader = ((WfsQosConfigurationLoader) (GeoServerExtensions.bean(SPRING_BEAN_NAME)));
        disableQosConfig();
        QosMainConfiguration conf = loader.getConfiguration(serviceInfo);
        Assert.assertEquals(Boolean.FALSE, conf.getActivated());
        startPage(serviceInfo);
        tester.assertComponent("form", Form.class);
        tester.assertComponent("form:panel:createExtendedCapabilities", CheckBox.class);
        try {
            tester.assertComponent("form:panel:container:configs", WebMarkupContainer.class);
            Assert.fail("Shouldn't have found section for QoS extension configuration");
        } catch (AssertionError e) {
        }
    }

    @Test
    public void testQosEnabled() {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        WfsQosConfigurationLoader loader = ((WfsQosConfigurationLoader) (GeoServerExtensions.bean(SPRING_BEAN_NAME)));
        QosMainConfiguration conf = loader.getConfiguration(serviceInfo);
        Assert.assertEquals(Boolean.FALSE, conf.getActivated());
        conf.setActivated(true);
        loader.setConfiguration(((WFSInfo) (serviceInfo)), conf);
        startPage(serviceInfo);
        tester.assertComponent("form:panel:container:configs", WebMarkupContainer.class);
    }

    @Test
    public void testOperatingInfoConfig() throws Exception {
        setupQosConfig();
        final ServiceInfo serviceInfo = getGeoServer().getService(WMSInfo.class);
        WfsQosConfigurationLoader loader = ((WfsQosConfigurationLoader) (GeoServerExtensions.bean(SPRING_BEAN_NAME)));
        QosMainConfiguration conf = loader.getConfiguration(serviceInfo);
        Assert.assertEquals(Boolean.TRUE, conf.getActivated());
        // Operating info data check
        startPage(serviceInfo);
        tester.assertComponent("form", Form.class);
        // check ExtendedCapabilities on opinfo:
        // titleSelect
        tester.assertComponent("form:panel:container:configs:opInfoListView:0:opinfo:opInfoForm:titleSelect", DropDownChoice.class);
        tester.assertModelValue(("form:panel:container:configs:opInfoListView:0" + ":opinfo:opInfoForm:titleSelect"), "http://def.opengeospatial.org/codelist/qos/status/1.0/operationalStatus.rdf#PreOperational");
        // titleInput
        tester.assertComponent("form:panel:container:configs:opInfoListView:0:opinfo:opInfoForm:titleInput", TextField.class);
        tester.assertModelValue(("form:panel:container:configs:opInfoListView:0" + ":opinfo:opInfoForm:titleInput"), "testbed14");
        // byDaysOfWeek
        tester.assertComponent(("form:panel:container:configs:opInfoListView:0:opinfo:opInfoForm" + ":timeListContainer:timeList:0:timePanel:opInfoTimeForm:daysOfWeekCheckGroup"), CheckGroup.class);
        tester.assertModelValue(("form:panel:container:configs:opInfoListView:0:opinfo:opInfoForm" + ":timeListContainer:timeList:0:timePanel:opInfoTimeForm:daysOfWeekCheckGroup"), conf.getWmsQosMetadata().getOperatingInfo().get(0).getByDaysOfWeek().get(0).getDays());
        // endTime
        tester.assertComponent(("form:panel:container:configs:opInfoListView:0:opinfo:opInfoForm" + ":timeListContainer:timeList:0:timePanel:opInfoTimeForm:endTimeField"), TextField.class);
        tester.assertModelValue(("form:panel:container:configs:opInfoListView:0:opinfo:opInfoForm" + ":timeListContainer:timeList:0:timePanel:opInfoTimeForm:endTimeField"), "21:00:00+03:00");
    }
}

