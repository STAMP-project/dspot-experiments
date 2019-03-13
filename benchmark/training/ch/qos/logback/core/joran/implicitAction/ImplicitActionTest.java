/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.joran.implicitAction;


import ch.qos.logback.core.joran.SimpleConfigurator;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import ch.qos.logback.core.testUtil.StatusChecker;
import ch.qos.logback.core.util.StatusPrinter;
import org.junit.Test;


public class ImplicitActionTest {
    static final String IMPLCIT_DIR = (CoreTestConstants.TEST_SRC_PREFIX) + "input/joran/implicitAction/";

    FruitContext fruitContext = new FruitContext();

    SimpleConfigurator simpleConfigurator;

    StatusChecker checker = new StatusChecker(fruitContext);

    @Test
    public void nestedComplex() throws Exception {
        try {
            doConfigure(((ImplicitActionTest.IMPLCIT_DIR) + "nestedComplex.xml"));
            verifyFruit();
        } catch (Exception je) {
            StatusPrinter.print(fruitContext);
            throw je;
        }
    }

    @Test
    public void nestedComplexWithoutClassAtrribute() throws Exception {
        try {
            doConfigure(((ImplicitActionTest.IMPLCIT_DIR) + "nestedComplexWithoutClassAtrribute.xml"));
            verifyFruit();
        } catch (Exception je) {
            StatusPrinter.print(fruitContext);
            throw je;
        }
    }

    @Test
    public void nestedComplexCollection() throws Exception {
        try {
            doConfigure(((ImplicitActionTest.IMPLCIT_DIR) + "nestedComplexCollection.xml"));
            verifyFruitList();
        } catch (Exception je) {
            StatusPrinter.print(fruitContext);
            throw je;
        }
    }

    @Test
    public void nestedComplexCollectionWithoutClassAtrribute() throws Exception {
        try {
            doConfigure(((ImplicitActionTest.IMPLCIT_DIR) + "nestedComplexCollectionWithoutClassAtrribute.xml"));
            verifyFruitList();
        } catch (Exception je) {
            StatusPrinter.print(fruitContext);
            throw je;
        }
    }

    @Test
    public void statusListenerWithPrefix() throws Exception {
        try {
            doConfigure(((ImplicitActionTest.IMPLCIT_DIR) + "statusListenerWithPrefix.xml"));
            StatusPrinter.print(fruitContext);
            checker.assertIsErrorFree();
        } catch (Exception je) {
            StatusPrinter.print(fruitContext);
            throw je;
        }
    }
}

