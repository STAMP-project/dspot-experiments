/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.paralleluniverse.fibers.instrument;


import co.paralleluniverse.strands.SuspendableRunnable;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Mann
 */
public class InitialSizeTest implements SuspendableRunnable {
    @Test
    public void test1() {
        testWithSize(1);
    }

    @Test
    public void test2() {
        testWithSize(2);
    }

    @Test
    public void test3() {
        testWithSize(3);
    }
}

