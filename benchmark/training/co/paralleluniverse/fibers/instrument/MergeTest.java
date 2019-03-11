/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.paralleluniverse.fibers.instrument;


import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.TestsHelper;
import co.paralleluniverse.strands.SuspendableRunnable;
import org.junit.Test;


/**
 *
 *
 * @author mam
 */
public class MergeTest implements SuspendableRunnable {
    @Test
    public void testMerge() {
        Fiber c = new Fiber(((String) (null)), null, new MergeTest());
        TestsHelper.exec(c);
    }
}

