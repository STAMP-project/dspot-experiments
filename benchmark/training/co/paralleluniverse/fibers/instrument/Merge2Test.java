/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.paralleluniverse.fibers.instrument;


import Strand.UncaughtExceptionHandler;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.TestsHelper;
import co.paralleluniverse.strands.SuspendableRunnable;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author mam
 */
public class Merge2Test implements SuspendableRunnable {
    private static UncaughtExceptionHandler previousUEH;

    public interface Interface {
        public void method();
    }

    @Test
    public void testMerge2() {
        try {
            Fiber c = new Fiber(((String) (null)), null, new Merge2Test());
            TestsHelper.exec(c);
            Assert.assertTrue("Should not reach here", false);
        } catch (NullPointerException ex) {
            // NPE expected
        }
    }
}

