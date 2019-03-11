/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.wicket;


import DefaultGeographicCRS.WGS84;
import java.io.Serializable;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class CRSPanelTest extends GeoServerWicketTestSupport {
    @Test
    public void testStandloneUnset() throws Exception {
        GeoServerWicketTestSupport.tester.startPage(new CRSPanelTestPage());
        GeoServerWicketTestSupport.tester.assertComponent("form", Form.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:crs", CRSPanel.class);
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("form");
        ft.submit();
        CRSPanel crsPanel = ((CRSPanel) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:crs")));
        Assert.assertNull(crsPanel.getCRS());
    }

    @Test
    public void testStandaloneUnchanged() throws Exception {
        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        GeoServerWicketTestSupport.tester.startPage(new CRSPanelTestPage(crs));
        // print(new CRSPanelTestPage(crs), true, true);
        GeoServerWicketTestSupport.tester.assertComponent("form", Form.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:crs", CRSPanel.class);
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("form", false);
        ft.submit();
        CRSPanel crsPanel = ((CRSPanel) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:crs")));
        Assert.assertTrue(CRS.equalsIgnoreMetadata(WGS84, crsPanel.getCRS()));
    }

    @Test
    public void testPopupWindow() throws Exception {
        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        GeoServerWicketTestSupport.tester.startPage(new CRSPanelTestPage(crs));
        ModalWindow window = ((ModalWindow) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:crs:popup")));
        Assert.assertFalse(window.isShown());
        GeoServerWicketTestSupport.tester.clickLink("form:crs:wkt", true);
        Assert.assertTrue(window.isShown());
        GeoServerWicketTestSupport.tester.assertModelValue("form:crs:popup:content:wkt", crs.toWKT());
    }

    @Test
    public void testPopupWindowNoCRS() throws Exception {
        // see GEOS-3207
        GeoServerWicketTestSupport.tester.startPage(new CRSPanelTestPage());
        ModalWindow window = ((ModalWindow) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:crs:popup")));
        Assert.assertFalse(window.isShown());
        GeoServerAjaxFormLink link = ((GeoServerAjaxFormLink) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:crs:wkt")));
        Assert.assertFalse(link.isEnabled());
    }

    @Test
    public void testStandaloneChanged() throws Exception {
        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        GeoServerWicketTestSupport.tester.startPage(new CRSPanelTestPage(crs));
        TextField srs = ((TextField) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:crs:srs")));
        srs.setModelObject("EPSG:3005");
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("form", false);
        ft.submit();
        CRSPanel crsPanel = ((CRSPanel) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:crs")));
        Assert.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:3005"), crsPanel.getCRS()));
    }

    @Test
    public void testStandaloneChanged2() throws Exception {
        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        GeoServerWicketTestSupport.tester.startPage(new CRSPanelTestPage(crs));
        // write down the text, submit the form
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("form");
        ft.setValue("crs:srs", "EPSG:3005");
        ft.submit();
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        CRSPanel crsPanel = ((CRSPanel) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:crs")));
        Assert.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:3005"), crsPanel.getCRS()));
    }

    @Test
    public void testRequired() throws Exception {
        GeoServerWicketTestSupport.tester.startPage(new CRSPanelTestPage(((CoordinateReferenceSystem) (null))));
        CRSPanel panel = ((CRSPanel) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:crs")));
        panel.setRequired(true);
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("form");
        ft.submit();
        Assert.assertEquals(1, panel.getFeedbackMessages().size());
        // System.out.println(Session.get().getFeedbackMessages().messageForComponent(panel));
    }

    @Test
    public void testCompoundPropertyUnchanged() throws Exception {
        CRSPanelTest.Foo foo = new CRSPanelTest.Foo(DefaultGeographicCRS.WGS84);
        GeoServerWicketTestSupport.tester.startPage(new CRSPanelTestPage(foo));
        GeoServerWicketTestSupport.tester.assertComponent("form", Form.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:crs", CRSPanel.class);
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("form");
        ft.submit();
        Assert.assertEquals(CRS.decode("EPSG:4326"), foo.crs);
    }

    @Test
    public void testCompoundPropertyChanged() throws Exception {
        CRSPanelTest.Foo foo = new CRSPanelTest.Foo(DefaultGeographicCRS.WGS84);
        GeoServerWicketTestSupport.tester.startPage(new CRSPanelTestPage(foo));
        TextField srs = ((TextField) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:crs:srs")));
        srs.setModelObject("EPSG:3005");
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("form");
        ft.submit();
        Assert.assertEquals(CRS.decode("EPSG:3005"), foo.crs);
    }

    @Test
    public void testPropertyUnchanged() throws Exception {
        CRSPanelTest.Foo foo = new CRSPanelTest.Foo(DefaultGeographicCRS.WGS84);
        GeoServerWicketTestSupport.tester.startPage(new CRSPanelTestPage(new PropertyModel(foo, "crs")));
        GeoServerWicketTestSupport.tester.assertComponent("form", Form.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:crs", CRSPanel.class);
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("form");
        ft.submit();
        Assert.assertEquals(CRS.decode("EPSG:4326"), foo.crs);
    }

    @Test
    public void testPropertyChanged() throws Exception {
        CRSPanelTest.Foo foo = new CRSPanelTest.Foo(DefaultGeographicCRS.WGS84);
        GeoServerWicketTestSupport.tester.startPage(new CRSPanelTestPage(new PropertyModel(foo, "crs")));
        TextField srs = ((TextField) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:crs:srs")));
        srs.setModelObject("EPSG:3005");
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("form");
        ft.submit();
        Assert.assertEquals(CRS.decode("EPSG:3005"), foo.crs);
    }

    static class Foo implements Serializable {
        public CoordinateReferenceSystem crs;

        Foo(CoordinateReferenceSystem crs) {
            this.crs = crs;
        }
    }
}

