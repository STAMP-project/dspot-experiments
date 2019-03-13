/**
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.impl.cpu;


import java.io.File;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that we can read the number of CPUs from /proc/self/status files.
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class CpuCoreSensorTest {
    @Test
    public void readRegular() throws Exception {
        File file = new File("src/test/resources/cpus/status-1.txt");
        Assert.assertThat(CpuCoreSensor.readCPUMask(file), Is.is(1));
    }

    @Test
    public void readRegular2() throws Exception {
        File file = new File("src/test/resources/cpus/status-2.txt");
        Assert.assertThat(CpuCoreSensor.readCPUMask(file), Is.is(2));
    }

    @Test
    public void readMissingFile() throws Exception {
        File file = new File("src/test/resources/cpus/does-not-exist");
        Assert.assertThat(CpuCoreSensor.readCPUMask(file), Is.is((-1)));
    }

    @Test
    public void readMissingEntry() throws Exception {
        File file = new File("src/test/resources/cpus/missing.txt");
        Assert.assertThat(CpuCoreSensor.readCPUMask(file), Is.is((-1)));
    }

    @Test
    public void readCorruptedFile() throws Exception {
        File file = new File("src/test/resources/cpus/corrupted.txt");
        Assert.assertThat(CpuCoreSensor.readCPUMask(file), Is.is((-1)));
    }
}

