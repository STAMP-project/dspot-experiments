/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.function;


import IsInstanceOf.NAME;
import java.util.ArrayList;
import java.util.List;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.Predicates;
import org.geoserver.catalog.impl.LayerInfoImpl;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.FunctionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;


/**
 * Simple test class for testing the InstanceOf class.
 *
 * @author Nicola Lagomarsini geosolutions
 */
public class InstanceOfTest {
    @Test
    public void testFactory() {
        // Ensure the Function Factory behaves correctly
        FunctionFactory factory = new GeoServerFunctionFactory();
        List<FunctionName> functionNames = factory.getFunctionNames();
        // Ensure the function name is returned correctly
        Assert.assertNotNull(functionNames);
        Assert.assertTrue(((functionNames.size()) == 1));
        Assert.assertEquals(NAME, functionNames.get(0));
        // Get the filterFactory
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        // Ensure the function is returned correctly
        List<Expression> args = new ArrayList<Expression>();
        args.add(ff.literal(Object.class));
        Function f = factory.function(NAME.getFunctionName(), args, null);
        Assert.assertNotNull(f);
        f = factory.function(NAME.getName(), args, null);
        Assert.assertNotNull(f);
        // Check if the function throws an exception when the parameters number
        // is not correct
        boolean catchedException = false;
        try {
            // Add a new parameter, should trow an exception
            args.add(ff.literal(Object.class));
            f = factory.function(NAME.getName(), args, null);
        } catch (IllegalArgumentException e) {
            catchedException = true;
        }
        Assert.assertTrue(catchedException);
        // Check if the function throws an exception when no parameters
        // is present
        catchedException = false;
        try {
            // Add a new parameter, should trow an exception
            f = factory.function(NAME.getName(), null, null);
        } catch (NullPointerException e) {
            catchedException = true;
        }
        Assert.assertTrue(catchedException);
    }

    @Test
    public void testFunction() {
        Filter filter = Predicates.isInstanceOf(Object.class);
        // Ensure the filter exists
        Assert.assertNotNull(filter);
        // Ensure the filter returned is a PropertyIsEqual filter
        Assert.assertTrue((filter instanceof PropertyIsEqualTo));
    }

    @Test
    public void testInstanceOfObject() {
        // Ensuring that this function always return true when the input
        // class is Object
        Filter filter = Predicates.isInstanceOf(Object.class);
        Assert.assertTrue(filter.evaluate(new Object()));
        Assert.assertTrue(filter.evaluate("test"));
        Assert.assertTrue(filter.evaluate(1));
        Assert.assertTrue(filter.evaluate(true));
    }

    @Test
    public void testInstanceOfString() {
        // Ensuring that this function return true only when the object
        // class is String
        Filter filter = Predicates.isInstanceOf(String.class);
        Assert.assertTrue(filter.evaluate("test"));
        Assert.assertFalse(filter.evaluate(new Object()));
        Assert.assertFalse(filter.evaluate(1));
        Assert.assertFalse(filter.evaluate(true));
    }

    @Test
    public void testInstanceOfLayerInfo() {
        // Ensuring that this function return true only when the object
        // class is LayerInfo
        Filter filter = Predicates.isInstanceOf(LayerInfo.class);
        Assert.assertTrue(filter.evaluate(new LayerInfoImpl()));
        Assert.assertFalse(filter.evaluate("test"));
        Assert.assertFalse(filter.evaluate(new Object()));
        Assert.assertFalse(filter.evaluate(1));
        Assert.assertFalse(filter.evaluate(true));
    }
}

