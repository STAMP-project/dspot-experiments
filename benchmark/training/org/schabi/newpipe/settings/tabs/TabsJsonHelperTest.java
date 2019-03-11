package org.schabi.newpipe.settings.tabs;


import Tab.BlankTab;
import Tab.ChannelTab;
import Tab.KioskTab;
import Tab.SubscriptionsTab;
import Tab.Type.BLANK;
import TabsJsonHelper.InvalidJsonException;
import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;

import static TabsJsonHelper.FALLBACK_INITIAL_TABS_LIST;


public class TabsJsonHelperTest {
    private static final String JSON_TABS_ARRAY_KEY = "tabs";

    private static final String JSON_TAB_ID_KEY = "tab_id";

    @Test
    public void testEmptyAndNullRead() throws InvalidJsonException {
        final String emptyTabsJson = ("{\"" + (TabsJsonHelperTest.JSON_TABS_ARRAY_KEY)) + "\":[]}";
        List<Tab> items = TabsJsonHelper.getTabsFromJson(emptyTabsJson);
        // Check if instance is the same
        Assert.assertTrue((items == (FALLBACK_INITIAL_TABS_LIST)));
        final String nullSource = null;
        items = TabsJsonHelper.getTabsFromJson(nullSource);
        Assert.assertTrue((items == (FALLBACK_INITIAL_TABS_LIST)));
    }

    @Test
    public void testInvalidIdRead() throws InvalidJsonException {
        final int blankTabId = BLANK.getTabId();
        final String emptyTabsJson = (((((((((((("{\"" + (TabsJsonHelperTest.JSON_TABS_ARRAY_KEY)) + "\":[") + "{\"") + (TabsJsonHelperTest.JSON_TAB_ID_KEY)) + "\":") + blankTabId) + "},") + "{\"") + (TabsJsonHelperTest.JSON_TAB_ID_KEY)) + "\":") + 12345678) + "}") + "]}";
        final List<Tab> items = TabsJsonHelper.getTabsFromJson(emptyTabsJson);
        Assert.assertEquals("Should ignore the tab with invalid id", 1, items.size());
        Assert.assertEquals(blankTabId, items.get(0).getTabId());
    }

    @Test
    public void testInvalidRead() {
        final List<String> invalidList = Arrays.asList("{\"notTabsArray\":[]}", "{invalidJSON]}", "{}");
        for (String invalidContent : invalidList) {
            try {
                TabsJsonHelper.getTabsFromJson(invalidContent);
                Assert.fail("didn't throw exception");
            } catch (Exception e) {
                boolean isExpectedException = e instanceof TabsJsonHelper.InvalidJsonException;
                Assert.assertTrue((("\"" + (e.getClass().getSimpleName())) + "\" is not the expected exception"), isExpectedException);
            }
        }
    }

    @Test
    public void testEmptyAndNullSave() throws JsonParserException {
        final List<Tab> emptyList = Collections.emptyList();
        String returnedJson = TabsJsonHelper.getJsonToSave(emptyList);
        Assert.assertTrue(isTabsArrayEmpty(returnedJson));
        final List<Tab> nullList = null;
        returnedJson = TabsJsonHelper.getJsonToSave(nullList);
        Assert.assertTrue(isTabsArrayEmpty(returnedJson));
    }

    @Test
    public void testSaveAndReading() throws JsonParserException {
        // Saving
        final Tab.BlankTab blankTab = new Tab.BlankTab();
        final Tab.SubscriptionsTab subscriptionsTab = new Tab.SubscriptionsTab();
        final Tab.ChannelTab channelTab = new Tab.ChannelTab(666, "https://example.org", "testName");
        final Tab.KioskTab kioskTab = new Tab.KioskTab(123, "trending_key");
        final List<Tab> tabs = Arrays.asList(blankTab, subscriptionsTab, channelTab, kioskTab);
        String returnedJson = TabsJsonHelper.getJsonToSave(tabs);
        // Reading
        final JsonObject jsonObject = JsonParser.object().from(returnedJson);
        Assert.assertTrue(jsonObject.containsKey(TabsJsonHelperTest.JSON_TABS_ARRAY_KEY));
        final JsonArray tabsFromArray = jsonObject.getArray(TabsJsonHelperTest.JSON_TABS_ARRAY_KEY);
        Assert.assertEquals(tabs.size(), tabsFromArray.size());
        final Tab.BlankTab blankTabFromReturnedJson = Objects.requireNonNull(((Tab.BlankTab) (Tab.from(((JsonObject) (tabsFromArray.get(0)))))));
        Assert.assertEquals(blankTab.getTabId(), blankTabFromReturnedJson.getTabId());
        final Tab.SubscriptionsTab subscriptionsTabFromReturnedJson = Objects.requireNonNull(((Tab.SubscriptionsTab) (Tab.from(((JsonObject) (tabsFromArray.get(1)))))));
        Assert.assertEquals(subscriptionsTab.getTabId(), subscriptionsTabFromReturnedJson.getTabId());
        final Tab.ChannelTab channelTabFromReturnedJson = Objects.requireNonNull(((Tab.ChannelTab) (Tab.from(((JsonObject) (tabsFromArray.get(2)))))));
        Assert.assertEquals(channelTab.getTabId(), channelTabFromReturnedJson.getTabId());
        Assert.assertEquals(channelTab.getChannelServiceId(), channelTabFromReturnedJson.getChannelServiceId());
        Assert.assertEquals(channelTab.getChannelUrl(), channelTabFromReturnedJson.getChannelUrl());
        Assert.assertEquals(channelTab.getChannelName(), channelTabFromReturnedJson.getChannelName());
        final Tab.KioskTab kioskTabFromReturnedJson = Objects.requireNonNull(((Tab.KioskTab) (Tab.from(((JsonObject) (tabsFromArray.get(3)))))));
        Assert.assertEquals(kioskTab.getTabId(), kioskTabFromReturnedJson.getTabId());
        Assert.assertEquals(kioskTab.getKioskServiceId(), kioskTabFromReturnedJson.getKioskServiceId());
        Assert.assertEquals(kioskTab.getKioskId(), kioskTabFromReturnedJson.getKioskId());
    }
}

