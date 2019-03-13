/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.paralleluniverse.fibers.instrument;


import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.TestsHelper;
import co.paralleluniverse.strands.SuspendableRunnable;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Mann
 */
public class DoubleTest implements SuspendableRunnable {
    double result;

    @Test
    public void testDouble() {
        Fiber co = new Fiber(((String) (null)), null, this);
        TestsHelper.exec(co);
        Assert.assertEquals(0, result, 1.0E-8);
        boolean res = TestsHelper.exec(co);
        Assert.assertEquals(1, result, 1.0E-8);
        Assert.assertEquals(res, true);
    }
}

