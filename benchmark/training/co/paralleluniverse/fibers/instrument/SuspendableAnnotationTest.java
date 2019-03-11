/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2015, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.fibers.instrument;


import co.paralleluniverse.common.util.SystemProperties;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.TestsHelper;
import co.paralleluniverse.strands.SuspendableCallable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Test instrumentation of methods marked with the @Suspendable annotation
 *
 * @author pron
 */
public class SuspendableAnnotationTest {
    private final List<String> results = new ArrayList<>();

    @Test
    public void testAnnotated() {
        try {
            Fiber co = new Fiber(((String) (null)), null, ((SuspendableCallable) (null))) {
                @Override
                protected Object run() throws SuspendExecution, InterruptedException {
                    suspendableMethod();
                    return null;
                }
            };
            TestsHelper.exec(co);
            TestsHelper.exec(co);
            TestsHelper.exec(co);
        } finally {
            System.out.println(results);
        }
        Assert.assertEquals(3, results.size());
        Assert.assertEquals(Arrays.asList("A", "B", "C"), results);
    }

    @Test
    public void testNonAnnotated() {
        Assume.assumeFalse(SystemProperties.isEmptyOrTrue("co.paralleluniverse.fibers.verifyInstrumentation"));
        try {
            Fiber co = new Fiber(((String) (null)), null, ((SuspendableCallable) (null))) {
                @Override
                protected Object run() throws SuspendExecution, InterruptedException {
                    nonsuspendableMethod();
                    return null;
                }
            };
            TestsHelper.exec(co);
            TestsHelper.exec(co);
            TestsHelper.exec(co);
        } finally {
            System.out.println(results);
        }
        Assert.assertEquals(3, results.size());
        Assert.assertEquals(Arrays.asList("A", "A", "A"), results);
    }
}

