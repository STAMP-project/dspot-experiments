/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.io.impl;


import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ByteArrayResourceToStringTest {
    // using List<Byte> instead of directly byte[] to make sure the bytes are printed as part of the test name
    // see above ({index}: bytes[{0}], encoding[{1}]) -- Array.toString only return object id
    @Parameterized.Parameter(0)
    public List<Byte> bytes;

    @Parameterized.Parameter(1)
    public String encoding;

    @Parameterized.Parameter(2)
    public String expectedString;

    @Test
    public void testToString() {
        byte[] byteArray = ArrayUtils.toPrimitive(bytes.toArray(new Byte[0]));
        ByteArrayResource byteArrayResource = new ByteArrayResource(byteArray, encoding);
        Assert.assertEquals(expectedString, byteArrayResource.toString());
    }
}

