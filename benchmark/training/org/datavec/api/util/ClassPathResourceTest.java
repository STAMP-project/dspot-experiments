/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.datavec.api.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class ClassPathResourceTest {
    private boolean isWindows = false;// File sizes are reported slightly different on Linux vs. Windows


    @Test
    public void testGetFile1() throws Exception {
        File intFile = new ClassPathResource("datavec-api/iris.dat").getFile();
        Assert.assertTrue(intFile.exists());
        if (isWindows) {
            MatcherAssert.assertThat(intFile.length(), AnyOf.anyOf(IsEqual.equalTo(2700L), IsEqual.equalTo(2850L)));
        } else {
            Assert.assertEquals(2700, intFile.length());
        }
    }

    @Test
    public void testGetFileSlash1() throws Exception {
        File intFile = new ClassPathResource("datavec-api/iris.dat").getFile();
        Assert.assertTrue(intFile.exists());
        if (isWindows) {
            MatcherAssert.assertThat(intFile.length(), AnyOf.anyOf(IsEqual.equalTo(2700L), IsEqual.equalTo(2850L)));
        } else {
            Assert.assertEquals(2700, intFile.length());
        }
    }

    @Test
    public void testGetFileWithSpace1() throws Exception {
        File intFile = new ClassPathResource("datavec-api/csvsequence test.txt").getFile();
        Assert.assertTrue(intFile.exists());
        if (isWindows) {
            MatcherAssert.assertThat(intFile.length(), AnyOf.anyOf(IsEqual.equalTo(60L), IsEqual.equalTo(64L)));
        } else {
            Assert.assertEquals(60, intFile.length());
        }
    }

    @Test
    public void testInputStream() throws Exception {
        ClassPathResource resource = new ClassPathResource("datavec-api/csvsequence_1.txt");
        File intFile = resource.getFile();
        if (isWindows) {
            MatcherAssert.assertThat(intFile.length(), AnyOf.anyOf(IsEqual.equalTo(60L), IsEqual.equalTo(64L)));
        } else {
            Assert.assertEquals(60, intFile.length());
        }
        InputStream stream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = "";
        int cnt = 0;
        while ((line = reader.readLine()) != null) {
            cnt++;
        } 
        Assert.assertEquals(5, cnt);
    }

    @Test
    public void testInputStreamSlash() throws Exception {
        ClassPathResource resource = new ClassPathResource("datavec-api/csvsequence_1.txt");
        File intFile = resource.getFile();
        if (isWindows) {
            MatcherAssert.assertThat(intFile.length(), AnyOf.anyOf(IsEqual.equalTo(60L), IsEqual.equalTo(64L)));
        } else {
            Assert.assertEquals(60, intFile.length());
        }
        InputStream stream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = "";
        int cnt = 0;
        while ((line = reader.readLine()) != null) {
            cnt++;
        } 
        Assert.assertEquals(5, cnt);
    }
}

