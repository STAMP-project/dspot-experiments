/**
 * Copyright 2013 ZXing authors
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
package com.google.zxing.aztec.detector;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the Detector
 *
 * @author Frank Yellin
 */
public final class DetectorTest extends Assert {
    @Test
    public void testErrorInParameterLocatorZeroZero() throws Exception {
        // Layers=1, CodeWords=1.  So the parameter info and its Reed-Solomon info
        // will be completely zero!
        DetectorTest.testErrorInParameterLocator("X");
    }

    @Test
    public void testErrorInParameterLocatorCompact() throws Exception {
        DetectorTest.testErrorInParameterLocator("This is an example Aztec symbol for Wikipedia.");
    }

    @Test
    public void testErrorInParameterLocatorNotCompact() throws Exception {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYabcdefghijklmnopqrstuvwxyz";
        DetectorTest.testErrorInParameterLocator(((alphabet + alphabet) + alphabet));
    }
}

