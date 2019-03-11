/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer;


import ImportContext.State.PENDING;
import ImportTask.State.COMPLETE;
import ImportTask.State.READY;
import junit.framework.TestCase;


public class ImportContextTest extends TestCase {
    public void testUpdateState() throws Exception {
        ImportContext context = new ImportContext(0);
        // verify this works correctly with no tasks
        context.updated();
        TestCase.assertEquals(PENDING, context.getState());
        ImportTask t1 = new ImportTask(null);
        ImportTask t2 = new ImportTask(null);
        ImportTask t3 = new ImportTask(null);
        context.addTask(t1);
        context.addTask(t2);
        context.addTask(t3);
        TestCase.assertEquals(PENDING, context.getState());
        t1.setState(READY);
        context.updated();
        TestCase.assertEquals(PENDING, context.getState());
        t2.setState(READY);
        t3.setState(READY);
        context.updated();
        TestCase.assertEquals(PENDING, context.getState());
        t1.setState(COMPLETE);
        t2.setState(COMPLETE);
        context.updated();
        TestCase.assertEquals(PENDING, context.getState());
        t3.setState(COMPLETE);
        context.updated();
        TestCase.assertEquals(ImportContext.State.COMPLETE, context.getState());
    }
}

