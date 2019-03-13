package com.kickstarter.libs.utils;


import DiscoveryParams.Sort.POPULAR;
import com.kickstarter.libs.RefTag;
import com.kickstarter.mock.factories.CategoryFactory;
import com.kickstarter.mock.factories.LocationFactory;
import com.kickstarter.services.DiscoveryParams;
import org.junit.Assert;
import org.junit.Test;


public class DiscoveryParamsUtilsTest {
    @Test
    public void testRefTag() {
        Assert.assertEquals(DiscoveryParamsUtils.refTag(DiscoveryParams.builder().category(CategoryFactory.artCategory()).build()), RefTag.category());
        Assert.assertEquals(RefTag.category(POPULAR), DiscoveryParamsUtils.refTag(DiscoveryParams.builder().category(CategoryFactory.artCategory()).sort(POPULAR).build()));
        Assert.assertEquals(RefTag.city(), DiscoveryParamsUtils.refTag(DiscoveryParams.builder().location(LocationFactory.germany()).build()));
        Assert.assertEquals(RefTag.recommended(), DiscoveryParamsUtils.refTag(DiscoveryParams.builder().staffPicks(true).build()));
        Assert.assertEquals(RefTag.recommended(POPULAR), DiscoveryParamsUtils.refTag(DiscoveryParams.builder().staffPicks(true).sort(POPULAR).build()));
        Assert.assertEquals(RefTag.social(), DiscoveryParamsUtils.refTag(DiscoveryParams.builder().social(1).build()));
        Assert.assertEquals(RefTag.search(), DiscoveryParamsUtils.refTag(DiscoveryParams.builder().term("art").build()));
        Assert.assertEquals(RefTag.discovery(), DiscoveryParamsUtils.refTag(DiscoveryParams.builder().build()));
    }
}

