/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.util;


import org.junit.Assert;
import org.junit.Test;


public class ThrowablesTest {
    @Test
    public void typical() {
        Throwable a = new Throwable();
        Throwable b = new Throwable(a);
        Assert.assertSame(a, Throwables.getRootCause(b));
        Assert.assertSame(a, Throwables.getRootCause(a));
    }

    @Test
    public void circularRef() {
        // God forbidden
        Throwable a = new Throwable();
        Throwable b = new Throwable(a);
        a.initCause(b);
        Assert.assertSame(b, Throwables.getRootCause(b));
        Assert.assertSame(a, Throwables.getRootCause(a));
    }

    @Test
    public void nullCause() {
        Throwable a = new Throwable();
        Assert.assertSame(a, Throwables.getRootCause(a));
    }

    @Test
    public void simplyNull() {
        Assert.assertNull(Throwables.getRootCause(null));
    }
}

