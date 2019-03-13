/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw.store.simple;


import org.geotools.filter.visitor.DuplicatingFilterVisitor;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.expression.Function;


public class RecordTextFunctionTest {
    @Test
    public void testDuplicate() {
        RecordTextFunction f = new RecordTextFunction();
        DuplicatingFilterVisitor visitor = new DuplicatingFilterVisitor();
        Function duplicate = ((Function) (f.accept(visitor, null)));
        Assert.assertThat(duplicate, CoreMatchers.instanceOf(RecordTextFunction.class));
    }
}

