/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.adaptive.media.image.internal.util;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Adolfo P?rez
 */
public class TupleTest {
    @Test
    public void testElementAccess() {
        Tuple<Integer, Integer> tuple = Tuple.of(1, 2);
        Assert.assertEquals(1, ((int) (tuple.first)));
        Assert.assertEquals(2, ((int) (tuple.second)));
    }
}

