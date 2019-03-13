/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.base.extractors;


import org.drools.core.base.TestBean;
import org.drools.core.spi.InternalReadAccessor;
import org.junit.Assert;
import org.junit.Test;


public class ByteClassFieldExtractorTest extends BaseClassFieldExtractorsTest {
    InternalReadAccessor reader;

    TestBean bean = new TestBean();

    @Test
    public void testGetBooleanValue() {
        try {
            this.reader.getBooleanValue(null, this.bean);
            Assert.fail("Should have throw an exception");
        } catch (final Exception e) {
            // success
        }
    }

    @Test
    public void testGetByteValue() {
        Assert.assertEquals(1, this.reader.getByteValue(null, this.bean));
    }

    @Test
    public void testGetCharValue() {
        try {
            this.reader.getCharValue(null, this.bean);
            Assert.fail("Should have throw an exception");
        } catch (final Exception e) {
            // success
        }
    }

    @Test
    public void testGetDoubleValue() {
        Assert.assertEquals(1.0, this.reader.getDoubleValue(null, this.bean), 0.01);
    }

    @Test
    public void testGetFloatValue() {
        Assert.assertEquals(1.0F, this.reader.getFloatValue(null, this.bean), 0.01);
    }

    @Test
    public void testGetIntValue() {
        Assert.assertEquals(1, this.reader.getIntValue(null, this.bean));
    }

    @Test
    public void testGetLongValue() {
        Assert.assertEquals(1, this.reader.getLongValue(null, this.bean));
    }

    @Test
    public void testGetShortValue() {
        Assert.assertEquals(1, this.reader.getShortValue(null, this.bean));
    }

    @Test
    public void testGetValue() {
        Assert.assertEquals(1, ((Number) (this.reader.getValue(null, this.bean))).byteValue());
    }

    @Test
    public void testIsNullValue() {
        Assert.assertFalse(this.reader.isNullValue(null, this.bean));
    }
}

