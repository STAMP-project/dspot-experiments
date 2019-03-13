/**
 * Copyright (c) 2008, Matthias Mann
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Matthias Mann nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package co.paralleluniverse.fibers.instrument;


import Strand.UncaughtExceptionHandler;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.TestsHelper;
import co.paralleluniverse.strands.SuspendableRunnable;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the propagation of unhandled exceptions throw a suspendable call
 *
 * @author Matthias Mann
 */
public class ThrowTest implements SuspendableRunnable {
    private static UncaughtExceptionHandler previousUEH;

    private final ArrayList<String> results = new ArrayList<>();

    @Test
    public void testThrow() {
        results.clear();
        Fiber co = new Fiber(((String) (null)), null, this);
        try {
            TestsHelper.exec(co);
            results.add("B");
            TestsHelper.exec(co);
            results.add("D");
            TestsHelper.exec(co);
            Assert.assertTrue(false);
        } catch (IllegalStateException es) {
            Assert.assertEquals("bla", es.getMessage());
            // assertEquals(LightweightThread.State.FINISHED, co.getState());
        } finally {
            System.out.println(results);
        }
        Assert.assertEquals(5, results.size());
        Assert.assertEquals("A", results.get(0));
        Assert.assertEquals("B", results.get(1));
        Assert.assertEquals("C", results.get(2));
        Assert.assertEquals("D", results.get(3));
        Assert.assertEquals("F", results.get(4));
    }
}

