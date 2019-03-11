/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.decorators;


import java.io.PrintWriter;
import java.io.StringWriter;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.impl.ModificationProxy;
import org.geoserver.security.CatalogMode;
import org.geoserver.security.SecureCatalogImpl;
import org.geoserver.security.WrapperPolicy;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


public abstract class SecuredResourceInfoTest<D extends ResourceInfo, S extends ResourceInfo> extends GeoServerSystemTestSupport {
    protected final WrapperPolicy policy = WrapperPolicy.readOnlyHide(new org.geoserver.security.AccessLimits(CatalogMode.HIDE));

    @Test
    public void testCanSecure() throws Exception {
        // get a delegate
        final D delegate = createDelegate();
        // secure it
        Object secure = SecuredObjects.secure(delegate, policy);
        Assert.assertTrue("Unable to secure ResourceInfo", getSecuredDecoratorClass().isAssignableFrom(secure.getClass()));
    }

    @Test
    public void testCanSecureProxied() throws Exception {
        // get a delegate
        final D delegate = createDelegate();
        // wrap the delegate in a ModificationProxy
        ResourceInfo proxy = ModificationProxy.create(delegate, getDelegateClass());
        // secure it
        Object secure = SecuredObjects.secure(proxy, policy);
        Assert.assertTrue("Unable to secure proxied Resourceinfo", getSecuredDecoratorClass().isAssignableFrom(secure.getClass()));
    }

    @Test
    public void testSecureWrapping() throws Exception {
        // get a delegate
        final D delegate = createDelegate();
        // assert the delegate is not secured
        Assert.assertFalse("ResourceInfo delegate should not be Secured", getSecuredDecoratorClass().isAssignableFrom(delegate.getClass()));
        // create a Secure wrapped instance
        S secured = createSecuredDecorator(delegate);
        Assert.assertTrue("ResourceInfo delegate should be Secured", getSecuredDecoratorClass().isAssignableFrom(secured.getClass()));
        // get the StoreInfo
        final StoreInfo securedStore = getStore();
        Assert.assertTrue("Secured ResourceInfo should return a Secured StoreInfo", getSecuredStoreInfoClass().isAssignableFrom(securedStore.getClass()));
        // copy non secured into secured
        Thread roundTripThread = getRoundTripThread(secured, secured);
        // catch Errors
        final StringWriter sw = new StringWriter();
        roundTripThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                // print the stack to the StringWriter
                e.printStackTrace(new PrintWriter(sw, true));
            }
        });
        // start the thread and wait for it to finish
        roundTripThread.start();
        roundTripThread.join();
        // If there was an Error in the thread, the StringWriter will have it
        StringBuffer buffer = sw.getBuffer();
        if ((buffer.length()) > 0) {
            Assert.fail(buffer.toString());
        }
        // just in case, unwrap the StoreInfo and ensure it doesn't throw a StackOverflow
        try {
            SecureCatalogImpl.unwrap(secured.getStore());
        } catch (Throwable t) {
            t.printStackTrace(new PrintWriter(sw, true));
        }
        buffer = sw.getBuffer();
        if ((buffer.length()) > 0) {
            Assert.fail(buffer.toString());
        }
    }
}

