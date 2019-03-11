package org.schabi.newpipe.local.subscription.services;


import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.schabi.newpipe.extractor.subscription.SubscriptionExtractor;
import org.schabi.newpipe.extractor.subscription.SubscriptionItem;
import org.schabi.newpipe.local.subscription.ImportExportJsonHelper;


/**
 *
 *
 * @see ImportExportJsonHelper
 */
public class ImportExportJsonHelperTest {
    @Test
    public void testEmptySource() throws Exception {
        String emptySource = "{\"app_version\":\"0.11.6\",\"app_version_int\": 47,\"subscriptions\":[]}";
        List<SubscriptionItem> items = ImportExportJsonHelper.readFrom(new ByteArrayInputStream(emptySource.getBytes("UTF-8")), null);
        Assert.assertTrue(items.isEmpty());
    }

    @Test
    public void testInvalidSource() {
        List<String> invalidList = Arrays.asList("{}", "", null, "gibberish");
        for (String invalidContent : invalidList) {
            try {
                if (invalidContent != null) {
                    byte[] bytes = invalidContent.getBytes("UTF-8");
                    ImportExportJsonHelper.readFrom(new ByteArrayInputStream(bytes), null);
                } else {
                    ImportExportJsonHelper.readFrom(null, null);
                }
                Assert.fail("didn't throw exception");
            } catch (Exception e) {
                boolean isExpectedException = e instanceof SubscriptionExtractor.InvalidSourceException;
                Assert.assertTrue((("\"" + (e.getClass().getSimpleName())) + "\" is not the expected exception"), isExpectedException);
            }
        }
    }

    @Test
    public void ultimateTest() throws Exception {
        // Read from file
        final List<SubscriptionItem> itemsFromFile = readFromFile();
        // Test writing to an output
        final String jsonOut = testWriteTo(itemsFromFile);
        // Read again
        final List<SubscriptionItem> itemsSecondRead = readFromWriteTo(jsonOut);
        // Check if both lists have the exact same items
        if ((itemsFromFile.size()) != (itemsSecondRead.size())) {
            Assert.fail("The list of items were different from each other");
        }
        for (int i = 0; i < (itemsFromFile.size()); i++) {
            final SubscriptionItem item1 = itemsFromFile.get(i);
            final SubscriptionItem item2 = itemsSecondRead.get(i);
            final boolean equals = (((item1.getServiceId()) == (item2.getServiceId())) && (item1.getUrl().equals(item2.getUrl()))) && (item1.getName().equals(item2.getName()));
            if (!equals) {
                Assert.fail("The list of items were different from each other");
            }
        }
    }
}

