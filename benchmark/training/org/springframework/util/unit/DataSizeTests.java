/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.util.unit;


import DataUnit.BYTES;
import DataUnit.GIGABYTES;
import DataUnit.KILOBYTES;
import DataUnit.MEGABYTES;
import DataUnit.TERABYTES;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for {@link DataSize}.
 *
 * @author Stephane Nicoll
 */
public class DataSizeTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void ofBytesToBytes() {
        Assert.assertEquals(1024, DataSize.ofBytes(1024).toBytes());
    }

    @Test
    public void ofBytesToKilobytes() {
        Assert.assertEquals(1, DataSize.ofBytes(1024).toKilobytes());
    }

    @Test
    public void ofKilobytesToKilobytes() {
        Assert.assertEquals(1024, DataSize.ofKilobytes(1024).toKilobytes());
    }

    @Test
    public void ofKilobytesToMegabytes() {
        Assert.assertEquals(1, DataSize.ofKilobytes(1024).toMegabytes());
    }

    @Test
    public void ofMegabytesToMegabytes() {
        Assert.assertEquals(1024, DataSize.ofMegabytes(1024).toMegabytes());
    }

    @Test
    public void ofMegabytesToGigabytes() {
        Assert.assertEquals(2, DataSize.ofMegabytes(2048).toGigabytes());
    }

    @Test
    public void ofGigabytesToGigabytes() {
        Assert.assertEquals(4096, DataSize.ofGigabytes(4096).toGigabytes());
    }

    @Test
    public void ofGigabytesToTerabytes() {
        Assert.assertEquals(4, DataSize.ofGigabytes(4096).toTerabytes());
    }

    @Test
    public void ofTerabytesToGigabytes() {
        Assert.assertEquals(1024, DataSize.ofTerabytes(1).toGigabytes());
    }

    @Test
    public void ofWithBytesUnit() {
        Assert.assertEquals(DataSize.ofBytes(10), DataSize.of(10, BYTES));
    }

    @Test
    public void ofWithKilobytesUnit() {
        Assert.assertEquals(DataSize.ofKilobytes(20), DataSize.of(20, KILOBYTES));
    }

    @Test
    public void ofWithMegabytesUnit() {
        Assert.assertEquals(DataSize.ofMegabytes(30), DataSize.of(30, MEGABYTES));
    }

    @Test
    public void ofWithGigabytesUnit() {
        Assert.assertEquals(DataSize.ofGigabytes(40), DataSize.of(40, GIGABYTES));
    }

    @Test
    public void ofWithTerabytesUnit() {
        Assert.assertEquals(DataSize.ofTerabytes(50), DataSize.of(50, TERABYTES));
    }

    @Test
    public void parseWithDefaultUnitUsesBytes() {
        Assert.assertEquals(DataSize.ofKilobytes(1), DataSize.parse("1024"));
    }

    @Test
    public void parseNegativeNumberWithDefaultUnitUsesBytes() {
        Assert.assertEquals(DataSize.ofBytes((-1)), DataSize.parse("-1"));
    }

    @Test
    public void parseWithNullDefaultUnitUsesBytes() {
        Assert.assertEquals(DataSize.ofKilobytes(1), DataSize.parse("1024", null));
    }

    @Test
    public void parseNegativeNumberWithNullDefaultUnitUsesBytes() {
        Assert.assertEquals(DataSize.ofKilobytes((-1)), DataSize.parse("-1024", null));
    }

    @Test
    public void parseWithCustomDefaultUnit() {
        Assert.assertEquals(DataSize.ofKilobytes(1), DataSize.parse("1", KILOBYTES));
    }

    @Test
    public void parseNegativeNumberWithCustomDefaultUnit() {
        Assert.assertEquals(DataSize.ofKilobytes((-1)), DataSize.parse("-1", KILOBYTES));
    }

    @Test
    public void parseWithBytes() {
        Assert.assertEquals(DataSize.ofKilobytes(1), DataSize.parse("1024B"));
    }

    @Test
    public void parseWithNegativeBytes() {
        Assert.assertEquals(DataSize.ofKilobytes((-1)), DataSize.parse("-1024B"));
    }

    @Test
    public void parseWithPositiveBytes() {
        Assert.assertEquals(DataSize.ofKilobytes(1), DataSize.parse("+1024B"));
    }

    @Test
    public void parseWithKilobytes() {
        Assert.assertEquals(DataSize.ofBytes(1024), DataSize.parse("1KB"));
    }

    @Test
    public void parseWithNegativeKilobytes() {
        Assert.assertEquals(DataSize.ofBytes((-1024)), DataSize.parse("-1KB"));
    }

    @Test
    public void parseWithMegabytes() {
        Assert.assertEquals(DataSize.ofMegabytes(4), DataSize.parse("4MB"));
    }

    @Test
    public void parseWithNegativeMegabytes() {
        Assert.assertEquals(DataSize.ofMegabytes((-4)), DataSize.parse("-4MB"));
    }

    @Test
    public void parseWithGigabytes() {
        Assert.assertEquals(DataSize.ofMegabytes(1024), DataSize.parse("1GB"));
    }

    @Test
    public void parseWithNegativeGigabytes() {
        Assert.assertEquals(DataSize.ofMegabytes((-1024)), DataSize.parse("-1GB"));
    }

    @Test
    public void parseWithTerabytes() {
        Assert.assertEquals(DataSize.ofTerabytes(1), DataSize.parse("1TB"));
    }

    @Test
    public void parseWithNegativeTerabytes() {
        Assert.assertEquals(DataSize.ofTerabytes((-1)), DataSize.parse("-1TB"));
    }

    @Test
    public void isNegativeWithPositive() {
        Assert.assertFalse(DataSize.ofBytes(50).isNegative());
    }

    @Test
    public void isNegativeWithZero() {
        Assert.assertFalse(DataSize.ofBytes(0).isNegative());
    }

    @Test
    public void isNegativeWithNegative() {
        Assert.assertTrue(DataSize.ofBytes((-1)).isNegative());
    }

    @Test
    public void toStringUsesBytes() {
        Assert.assertEquals("1024B", DataSize.ofKilobytes(1).toString());
    }

    @Test
    public void toStringWithNegativeBytes() {
        Assert.assertEquals("-1024B", DataSize.ofKilobytes((-1)).toString());
    }

    @Test
    public void parseWithUnsupportedUnit() {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("3WB");
        this.thrown.expectMessage("is not a valid data size");
        DataSize.parse("3WB");
    }
}

