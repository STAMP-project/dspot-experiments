package com.baeldung.hibernate.fetching;


import com.baeldung.hibernate.fetching.model.OrderDetail;
import com.baeldung.hibernate.fetching.view.FetchingAppView;
import java.util.Set;
import org.hibernate.Hibernate;
import org.junit.Assert;
import org.junit.Test;


public class HibernateFetchingIntegrationTest {
    // testLazyFetching() tests the lazy loading
    // Since it lazily loaded so orderDetalSetLazy won't
    // be initialized
    @Test
    public void testLazyFetching() {
        FetchingAppView fav = new FetchingAppView();
        Set<OrderDetail> orderDetalSetLazy = fav.lazyLoaded();
        Assert.assertFalse(Hibernate.isInitialized(orderDetalSetLazy));
    }

    // testEagerFetching() tests the eager loading
    // Since it eagerly loaded so orderDetalSetLazy would
    // be initialized
    @Test
    public void testEagerFetching() {
        FetchingAppView fav = new FetchingAppView();
        Set<OrderDetail> orderDetalSetEager = fav.eagerLoaded();
        Assert.assertTrue(Hibernate.isInitialized(orderDetalSetEager));
    }
}

