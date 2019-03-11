/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.paralleluniverse.fibers.instrument;


import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.TestsHelper;
import co.paralleluniverse.strands.SuspendableRunnable;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author mam
 */
public class InheritTest {
    @Test
    public void testInherit() {
        final InheritTest.C dut = new InheritTest.C();
        Fiber c = new Fiber(((String) (null)), null, new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution {
                dut.myMethod();
            }
        });
        for (int i = 0; i < 3; i++) {
            TestsHelper.exec(c);
        }
        Assert.assertEquals(5, dut.result.size());
        Assert.assertEquals("a", dut.result.get(0));
        Assert.assertEquals("o1", dut.result.get(1));
        Assert.assertEquals("o2", dut.result.get(2));
        Assert.assertEquals("b", dut.result.get(3));
        Assert.assertEquals("b", dut.result.get(4));
    }

    public static class A {
        public static void suspend() throws SuspendExecution {
            Fiber.park();
        }
    }

    public static class B extends InheritTest.A {
        final ArrayList<String> result = new ArrayList<String>();
    }

    public static class C extends InheritTest.B {
        public void otherMethod() throws SuspendExecution {
            result.add("o1");
            Fiber.park();
            result.add("o2");
        }

        public void myMethod() throws SuspendExecution {
            result.add("a");
            otherMethod();
            for (; ;) {
                result.add("b");
                if ((result.size()) > 10) {
                    otherMethod();
                    result.add("Ohh!");
                }
                InheritTest.A.suspend();
            }
        }
    }
}

