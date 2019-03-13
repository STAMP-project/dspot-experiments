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


import org.junit.Assert;
import org.junit.Assume;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @since 4.12
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Category(Categories.Plain.class)
public class PlainJUnit4TestCaseWithFiveTestMethods {
    @Test
    public void abortedTest() {
        Assume.assumeFalse("this test should be aborted", true);
    }

    @Test
    @Category(Categories.Failing.class)
    public void failingTest() {
        Assert.fail("this test should fail");
    }

    @Test
    public void successfulTest() {
        Assert.assertEquals(3, (1 + 2));
    }
}

