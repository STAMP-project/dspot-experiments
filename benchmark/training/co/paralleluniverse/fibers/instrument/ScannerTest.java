/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.paralleluniverse.fibers.instrument;


import co.paralleluniverse.common.reflection.ClassLoaderUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author eitan
 */
public class ScannerTest {
    @Test
    public void suspendablesMethodWithDescsTest() {
        String path = ClassLoader.getSystemClassLoader().getResource("suspendables.test").getPath();
        SimpleSuspendableClassifier ssc = new SimpleSuspendableClassifier(path);
        final String name = ClassLoaderUtil.classToSlashed("co.paralleluniverse.fibers.instrument.ScannerTest");
        Assert.assertEquals(true, ssc.isSuspendable(name, "foo", "V"));
        Assert.assertEquals(true, ssc.isSuspendable(name, "bar", "(I)V"));
        Assert.assertEquals(false, ssc.isSuspendable(name, "bar", "(D)V"));
    }

    @Test
    public void suspendablesClassesTest() {
        String path = ClassLoader.getSystemClassLoader().getResource("suspendables.test").getPath();
        SimpleSuspendableClassifier ssc = new SimpleSuspendableClassifier(path);
        final String name = ClassLoaderUtil.classToSlashed("co.paralleluniverse.fibers.instrument.ScannerTestB");
        Assert.assertEquals(true, ssc.isSuspendable(name, "anyMethod", "V"));
    }
}

