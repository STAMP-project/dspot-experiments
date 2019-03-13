/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2014, Parallel Universe Software Co. All rights reserved.
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


import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.strands.SuspendableCallable;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


/**
 * A test for issue #73: https://github.com/puniverse/quasar/issues/73
 *
 * @author circlespainter
 */
public class RTInitLocalArrayArgTest implements SuspendableCallable {
    @Test
    public void test() throws InterruptedException, ExecutionException {
        Assert.assertTrue(((new Fiber(this).start().get()) != null));
    }
}

