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
package ch.qos.logback.classic.corpusTest;


import ch.qos.logback.classic.corpus.RandomUtil;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class RandomUtilTest {
    long now = System.currentTimeMillis();

    @Test
    public void smoke() {
        int EXPECTED_AVERAGE = 6;
        int EXPECTED_STD_DEVIATION = 3;
        System.out.println();
        Random r = new Random(now);
        int len = 3000;
        int[] valArray = new int[len];
        for (int i = 0; i < len; i++) {
            valArray[i] = RandomUtil.gaussianAsPositiveInt(r, EXPECTED_AVERAGE, EXPECTED_STD_DEVIATION);
        }
        double avg = average(valArray);
        Assert.assertEquals(EXPECTED_AVERAGE, avg, 0.3);
    }
}

