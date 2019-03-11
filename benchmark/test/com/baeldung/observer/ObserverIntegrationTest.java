package com.baeldung.observer;


import org.junit.Assert;
import org.junit.Test;


public class ObserverIntegrationTest {
    @Test
    public void whenChangingNewsAgencyState_thenNewsChannelNotified() {
        NewsAgency observable = new NewsAgency();
        NewsChannel observer = new NewsChannel();
        observable.addObserver(observer);
        observable.setNews("news");
        Assert.assertEquals(observer.getNews(), "news");
    }

    @Test
    public void whenChangingONewsAgencyState_thenONewsChannelNotified() {
        ONewsAgency observable = new ONewsAgency();
        ONewsChannel observer = new ONewsChannel();
        observable.addObserver(observer);
        observable.setNews("news");
        Assert.assertEquals(observer.getNews(), "news");
    }

    @Test
    public void whenChangingPCLNewsAgencyState_thenONewsChannelNotified() {
        PCLNewsAgency observable = new PCLNewsAgency();
        PCLNewsChannel observer = new PCLNewsChannel();
        observable.addPropertyChangeListener(observer);
        observable.setNews("news");
        Assert.assertEquals(observer.getNews(), "news");
    }
}

