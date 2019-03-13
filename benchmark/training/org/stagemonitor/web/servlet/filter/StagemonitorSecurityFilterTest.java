package org.stagemonitor.web.servlet.filter;


import org.junit.Test;


public class StagemonitorSecurityFilterTest {
    @Test
    public void testStagemonitorSecurityFilter() throws Exception {
        // url                            password   widget     header      req attr    allowed
        testFilter("/stagemonitor/public", null, true, null, null, true);
        testFilter("/stagemonitor/foo", null, true, null, null, true);
        testFilter("/stagemonitor/public", "", true, null, null, true);
        testFilter("/stagemonitor/foo", "", true, null, null, true);
        testFilter("/stagemonitor/public", "", false, null, null, true);
        testFilter("/stagemonitor/foo", "", false, null, null, true);
        testFilter("/stagemonitor/foo", "", false, "pw", null, true);
        testFilter("/stagemonitor/public", "pw", true, null, null, true);
        testFilter("/stagemonitor/foo", "pw", true, null, null, true);
        testFilter("/stagemonitor/public", "pw", false, null, null, true);
        testFilter("/stagemonitor/foo", "pw", false, null, null, false);
        testFilter("/stagemonitor/foo", "pw", false, "wp", null, false);
        testFilter("/stagemonitor/foo", "pw", false, "pw", null, true);
        testFilter("/stagemonitor/foo", "pw", false, null, true, true);
        testFilter("/stagemonitor/foo", "pw", true, null, null, true);
        testFilter("/stagemonitor/foo", "pw", false, null, false, false);
        testFilter("/stagemonitor/foo", "pw", false, "pw", false, false);
        testFilter("/stagemonitor/foo", "pw", true, null, false, false);
        testFilter("/stagemonitor/foo", "", true, null, false, false);
        testFilter("/stagemonitor/foo", null, true, null, false, false);
        testFilter("/stagemonitor/public/bar", "pw", false, null, false, true);
        testFilter("/stagemonitor/public/bar", "pw", false, "pw", false, true);
        testFilter("/stagemonitor/public/bar", "pw", true, null, false, true);
        testFilter("/stagemonitor/public/bar", "", true, null, false, true);
        testFilter("/stagemonitor/public/bar", null, true, null, false, true);
    }
}

