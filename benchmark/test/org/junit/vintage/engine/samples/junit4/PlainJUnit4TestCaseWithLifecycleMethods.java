/**
 * Copyright 2015-2019 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */
package org.junit.vintage.engine.samples.junit4;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlainJUnit4TestCaseWithLifecycleMethods {
    public static final List<String> EVENTS = new ArrayList<>();

    @Test
    public void failingTest() {
        PlainJUnit4TestCaseWithLifecycleMethods.EVENTS.add("failingTest");
        Assert.fail();
    }

    @Test
    public void succeedingTest() {
        PlainJUnit4TestCaseWithLifecycleMethods.EVENTS.add("succeedingTest");
    }
}

