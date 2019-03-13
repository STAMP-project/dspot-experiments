/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.util;


import Bytes.Unit;
import Bytes.Unit.GB;
import Bytes.Unit.GiB;
import Bytes.Unit.MB;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of the bytes converter
 *
 * @author Florent Benoit
 */
public class BytesTest {
    @Test
    public void convert0MiBValue() {
        String newSize = Bytes.toHumanSize("0MiB");
        Assert.assertEquals("0B", newSize);
    }

    @Test
    public void convert16MiBValue() {
        String newSize = Bytes.toHumanSize("16MiB");
        Assert.assertEquals("16MiB", newSize);
    }

    @Test
    public void convert100MBValue() {
        String newSize = Bytes.toHumanSize("100MB");
        Assert.assertEquals("100MB", newSize);
    }

    @Test
    public void convert200MBValue() {
        String newSize = Bytes.toHumanSize("200MB");
        Assert.assertEquals("200MB", newSize);
    }

    @Test
    public void convert500MBValue() {
        String newSize = Bytes.toHumanSize("500MB");
        Assert.assertEquals("500MB", newSize);
    }

    @Test
    public void convert1024MiBValue() {
        String newSize = Bytes.toHumanSize("1024MiB");
        Assert.assertEquals("1GiB", newSize);
    }

    @Test
    public void convert512MiBValue() {
        String newSize = Bytes.toHumanSize("512MiB");
        Assert.assertEquals("512MiB", newSize);
    }

    @Test
    public void convert256MiBValue() {
        String newSize = Bytes.toHumanSize("256MiB");
        Assert.assertEquals("256MiB", newSize);
    }

    @Test
    public void convert1000MBValue() {
        String newSize = Bytes.toHumanSize("1000MB");
        Assert.assertEquals("1GB", newSize);
    }

    @Test
    public void convert4096MiBValue() {
        String newSize = Bytes.toHumanSize("4096MiB");
        Assert.assertEquals("4GiB", newSize);
    }

    @Test
    public void convert1536MiBValue() {
        String newSize = Bytes.toHumanSize("1536MiB");
        Assert.assertEquals("1.5GiB", newSize);
    }

    @Test
    public void convert1500MBValue() {
        String newSize = Bytes.toHumanSize("1500MB");
        Assert.assertEquals("1.5GB", newSize);
    }

    @Test
    public void convert1500000MBValue() {
        String newSize = Bytes.toHumanSize("1500000MB");
        Assert.assertEquals("1.5TB", newSize);
    }

    @Test
    public void convert1572864MiBValue() {
        String newSize = Bytes.toHumanSize("1572864MiB");
        Assert.assertEquals("1.5TiB", newSize);
    }

    @Test
    public void convert1204GiBValue() {
        String newSize = Bytes.toHumanSize("1024GiB");
        Assert.assertEquals("1TiB", newSize);
    }

    @Test
    public void convert2TiBValue() {
        String newSize = Bytes.toHumanSize("2048GiB");
        Assert.assertEquals("2TiB", newSize);
    }

    @Test
    public void convert2TBValue() {
        String newSize = Bytes.toHumanSize("2000GB");
        Assert.assertEquals("2TB", newSize);
    }

    @Test
    public void convert1PiBValue() {
        String newSize = Bytes.toHumanSize("1125899906842624B");
        Assert.assertEquals("1PiB", newSize);
    }

    @Test
    public void checkSplitUnit() {
        Pair<Double, Bytes.Unit> value = Bytes.splitValueAndUnit("150MB");
        Assert.assertEquals(Double.valueOf(150), value.getFirst());
        Assert.assertEquals(MB, value.getSecond());
    }

    @Test
    public void checkSplitFloatUnit() {
        Pair<Double, Bytes.Unit> value = Bytes.splitValueAndUnit("1.5GB");
        Assert.assertEquals(Double.valueOf(1.5), value.getFirst());
        Assert.assertEquals(GB, value.getSecond());
    }

    @Test
    public void checkSplitGiBUnit() {
        Pair<Double, Bytes.Unit> value = Bytes.splitValueAndUnit("1500GiB");
        Assert.assertEquals(Double.valueOf(1500), value.getFirst());
        Assert.assertEquals(GiB, value.getSecond());
    }

    @Test
    public void checkSplitUnitSpace() {
        Pair<Double, Bytes.Unit> value = Bytes.splitValueAndUnit(" 150 MB");
        Assert.assertEquals(Double.valueOf(150), value.getFirst());
        Assert.assertEquals(MB, value.getSecond());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkSplitInvalidUnit() {
        Bytes.splitValueAndUnit("150");
        Assert.fail("Should have failed");
    }

    @Test
    public void convert1024MiBToBytes() {
        double result = Bytes.convertToBytes(new Pair<Double, Bytes.Unit>(1024.0, Unit.MiB));
        Assert.assertEquals(((1024 * 1024) * 1024), result, 0);
    }

    @Test
    public void convert1000MBToBytes() {
        double result = Bytes.convertToBytes(new Pair<Double, Bytes.Unit>(1000.0, Unit.MB));
        Assert.assertEquals(1.0E9, result, 0);
    }

    @Test
    public void convert4096MiBToBytes() {
        double result = Bytes.convertToBytes(new Pair<Double, Bytes.Unit>(4096.0, Unit.MiB));
        Assert.assertEquals((((4.0 * 1024) * 1024) * 1024), result, 0);
    }
}

