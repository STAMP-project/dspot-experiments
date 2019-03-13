/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.status.monitoring;


import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.wicket.util.tester.TagTester;
import org.geoserver.status.monitoring.web.SystemStatusMonitorPanel;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.admin.StatusPage;
import org.junit.Assert;
import org.junit.Test;


public class SystemStatusMonitorPanelTest extends GeoServerWicketTestSupport {
    @Test
    public void testLoad() throws Exception {
        tester.assertRenderedPage(StatusPage.class);
        tester.clickLink("tabs:tabs-container:tabs:2:link", true);
        tester.assertContains("Updated at");
    }

    @Test
    public void testUpdate() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(SystemStatusMonitorPanel.datePattern);
        tester.assertRenderedPage(StatusPage.class);
        tester.clickLink("tabs:tabs-container:tabs:2:link", true);
        TagTester time1 = tester.getTagByWicketId("time");
        Assert.assertNotNull(time1);
        Date firstTime = formatter.parse(time1.getValue());
        System.out.println(firstTime.getTime());
        // Execute timer
        Thread.sleep(1000);
        tester.executeAllTimerBehaviors(tester.getLastRenderedPage());
        TagTester time2 = tester.getTagByWicketId("time");
        Assert.assertNotNull(time2);
        Date secondTime = formatter.parse(time2.getValue());
        // Check if update time is changed
        Assert.assertTrue(((secondTime.getTime()) > (firstTime.getTime())));
        Thread.sleep(1000);
        tester.executeAllTimerBehaviors(tester.getLastRenderedPage());
        TagTester time3 = tester.getTagByWicketId("time");
        Assert.assertNotNull(time3);
        Date thirdTime = formatter.parse(time3.getValue());
        // Check if update time is changed (use 500ms due to time imprecision)
        Assert.assertTrue(((thirdTime.getTime()) > (secondTime.getTime())));
    }
}

