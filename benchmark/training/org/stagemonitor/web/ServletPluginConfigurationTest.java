package org.stagemonitor.web;


import ServletPlugin.STAGEMONITOR_SHOW_WIDGET;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.web.servlet.ServletPlugin;


public class ServletPluginConfigurationTest {
    private ServletPlugin config;

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(true, config.isCollectHttpHeaders());
        Assert.assertEquals(new java.util.LinkedHashSet(Arrays.asList("cookie", "authorization", STAGEMONITOR_SHOW_WIDGET)), config.getExcludeHeaders());
        final Collection<Pattern> confidentialQueryParams = config.getRequestParamsConfidential();
        final List<String> confidentialQueryParamsAsString = new ArrayList<String>(confidentialQueryParams.size());
        for (Pattern confidentialQueryParam : confidentialQueryParams) {
            confidentialQueryParamsAsString.add(confidentialQueryParam.toString());
        }
        Assert.assertEquals(Arrays.asList("(?i).*pass.*", "(?i).*credit.*", "(?i).*pwd.*"), confidentialQueryParamsAsString);
        final Map<Pattern, String> groupUrls = config.getGroupUrls();
        final Map<String, String> groupUrlsAsString = new HashMap<String, String>();
        for (Map.Entry<Pattern, String> entry : groupUrls.entrySet()) {
            groupUrlsAsString.put(entry.getKey().pattern(), entry.getValue());
        }
        final Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("(.*).js$", "*.js");
        expectedMap.put("(.*).css$", "*.css");
        expectedMap.put("(.*).jpg$", "*.jpg");
        expectedMap.put("(.*).jpeg$", "*.jpeg");
        expectedMap.put("(.*).png$", "*.png");
        Assert.assertEquals(expectedMap, groupUrlsAsString);
        Assert.assertEquals(false, config.isClientSpanCollectionEnabled());
        Assert.assertEquals(true, config.isClientSpanCollectionInjectionEnabled());
        Assert.assertEquals(false, config.isCollectPageLoadTimesPerRequest());
        Assert.assertEquals(false, config.isMonitorOnlySpringMvcRequests());
    }
}

