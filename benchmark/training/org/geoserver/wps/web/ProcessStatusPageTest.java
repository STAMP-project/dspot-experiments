/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.web;


import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geoserver.wps.MonkeyProcess;
import org.geoserver.wps.executor.ExecutionStatus;
import org.geotools.process.Processors;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class ProcessStatusPageTest extends WPSPagesTestSupport {
    static {
        Processors.addProcessFactory(MonkeyProcess.getFactory());
    }

    @Test
    public void test() throws Exception {
        login();
        // submit a monkey process
        String request = "wps?service=WPS&version=1.0.0&request=Execute&Identifier=gs:Monkey&storeExecuteResponse=true&DataInputs=" + (URLEncoder.encode("id=x2", "ASCII"));
        Document dom = getAsDOM(request);
        // print(dom);
        assertXpathExists("//wps:ProcessAccepted", dom);
        MonkeyProcess.progress("x2", 10.0F, true);
        // start the page, should have one process running
        tester.startPage(new ProcessStatusPage());
        // print(tester.getLastRenderedPage(), true, true);
        tester.assertLabel("table:listContainer:items:1:itemProperties:3:component", "gs:Monkey");
        tester.assertLabel("table:listContainer:items:1:itemProperties:5:component", "RUNNING");
        List<ExecutionStatus> executions = getItems();
        Assert.assertEquals(1, executions.size());
        ExecutionStatus status = executions.get(0);
        DateFormat df = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss.SSS 'GMT'", tester.getSession().getLocale());
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        tester.assertLabel("table:listContainer:items:1:itemProperties:7:component", df.format(status.getExpirationDate()));
        tester.assertLabel("table:listContainer:items:1:itemProperties:8:component", df.format(status.getEstimatedCompletion()));
        tester.assertLabel("table:listContainer:items:1:itemProperties:9:component", df.format(status.getNextPoll()));
        // select the process and delete it
        GeoServerTablePanel<?> table = ((GeoServerTablePanel<?>) (tester.getComponentFromLastRenderedPage("table")));
        table.selectIndex(0);
        tester.getComponentFromLastRenderedPage("headerPanel:dismissSelected").setEnabled(true);
        tester.clickLink("headerPanel:dismissSelected");
        // this submits the dialog
        tester.clickLink("dialog:dialog:content:form:submit", true);
        // this makes the dialog actually close
        tester.getComponentFromLastRenderedPage("dialog:dialog").getBehaviors().forEach(( b) -> {
            final String name = b.getClass().getSimpleName();
            if (name.contains("WindowClosedBehavior")) {
                tester.executeBehavior(((AbstractAjaxBehavior) (b)));
            }
        });
        // check the table is refreshed and process is dismissing
        tester.assertComponentOnAjaxResponse("table");
        tester.assertLabel("table:listContainer:items:2:itemProperties:3:component", "gs:Monkey");
        tester.assertLabel("table:listContainer:items:2:itemProperties:5:component", "DISMISSING");
        // let the process exit to ensure clean shutdown
        MonkeyProcess.exit("x2", null, true);
    }
}

