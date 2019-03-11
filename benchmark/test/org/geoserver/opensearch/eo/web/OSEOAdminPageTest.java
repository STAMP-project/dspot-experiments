/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.opensearch.eo.web;


import FeedbackMessage.ERROR;
import OSEOInfo.DEFAULT_MAXIMUM_RECORDS;
import OSEOInfo.DEFAULT_RECORDS_PER_PAGE;
import org.junit.Assert;
import org.junit.Test;


public class OSEOAdminPageTest extends OSEOWebTestSupport {
    @Test
    public void smokeTest() throws Exception {
        tester.assertNoErrorMessage();
        tester.assertModelValue("form:maximumRecordsPerPage", DEFAULT_MAXIMUM_RECORDS);
        tester.assertModelValue("form:recordsPerPage", DEFAULT_RECORDS_PER_PAGE);
        // print(tester.getLastRenderedPage(), true, true);
    }

    @Test
    public void verifyRequiredFields() throws Exception {
        checkRequired("maximumRecordsPerPage");
        checkRequired("recordsPerPage");
    }

    @Test
    public void testPagingValidation() throws Exception {
        setupPagingValues((-10), 100);
        Assert.assertEquals(1, tester.getMessages(ERROR).size());
        setupPagingValues(100, (-10));
        Assert.assertEquals(1, tester.getMessages(ERROR).size());
        setupPagingValues(100, 10);
        Assert.assertEquals(1, tester.getMessages(ERROR).size());
        setupPagingValues(2, 1000);
        tester.assertNoErrorMessage();
    }
}

