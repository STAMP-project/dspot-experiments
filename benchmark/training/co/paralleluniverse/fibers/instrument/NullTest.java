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
public class NullTest implements SuspendableRunnable {
    Object result = "b";

    @Test
    public void testNull() {
        Fiber co = new Fiber(((String) (null)), null, this);
        int count = 1;
        while (!(TestsHelper.exec(co)))
            count++;

        Assert.assertEquals(2, count);
        Assert.assertEquals("a", result);
    }
}

