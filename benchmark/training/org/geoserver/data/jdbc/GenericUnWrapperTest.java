/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.data.jdbc;


import GenericUnWrapper.CONNECTION_METHODS;
import java.sql.Connection;
import java.sql.SQLException;
import org.geotools.data.jdbc.datasource.DataSourceFinder;
import org.geotools.data.jdbc.datasource.UnWrapper;
import org.junit.Assert;
import org.junit.Test;


/**
 * Uses the known target org.apache.commons.dbcp.DelegatingStatement to test out GenericUnWrapper in
 * isolation.
 *
 * @author Jody Garnett (Boundless)
 */
public class GenericUnWrapperTest {
    GenericUnWrapper unwrapper = new GenericUnWrapper();

    @Test
    public void testUnwrapConnection() throws NoSuchMethodException, SecurityException, SQLException {
        Connection connection = new TestConnection();
        Connection wrapper = new WrapperConnection(connection);
        Assert.assertTrue(wrapper.isWrapperFor(Connection.class));
        Connection unwrap = wrapper.unwrap(Connection.class);
        Assert.assertSame(connection, unwrap);
        UnWrapper unwrapper = new GenericUnWrapper();
        Assert.assertFalse(unwrapper.canUnwrap(wrapper));
        try {
            Assert.assertNull(unwrapper.unwrap(wrapper));
            Assert.fail("Cannot unwrap yet");
        } catch (Exception expected) {
        }
        CONNECTION_METHODS.put(WrapperConnection.class, WrapperConnection.class.getMethod("getUnderlyingConnection", null));
        Assert.assertTrue(unwrapper.canUnwrap(wrapper));
        Assert.assertSame(connection, unwrapper.unwrap(wrapper));
    }

    @Test
    public void testSPIRegistration() throws Exception {
        Connection connection = new TestConnection();
        Connection wrapper = new WrapperConnection(connection);
        CONNECTION_METHODS.put(WrapperConnection.class, WrapperConnection.class.getMethod("getUnderlyingConnection", null));
        UnWrapper uw = DataSourceFinder.getUnWrapper(wrapper);
        Assert.assertNotNull("registed and canUnwrap", uw);
        if (uw instanceof GenericUnWrapper) {
            Assert.assertSame("Generic unwrapper is working", connection, uw.unwrap(wrapper));
        }
    }
}

