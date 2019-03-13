/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.zylib.io;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the {@link FileUtils} class.
 *
 * @author cblichmann@google.com (Christian Blichmann)
 */
@RunWith(JUnit4.class)
public class FileUtilsTests {
    @Test
    public void testEnsureTrailingSlash() {
        // null String
        Assert.assertEquals("", FileUtils.ensureTrailingSlash(null));
        // Empty String
        Assert.assertEquals("", FileUtils.ensureTrailingSlash(""));
        // Normal case, append single slash
        Assert.assertEquals(String.format("%sPATH%1$s", File.separator), FileUtils.ensureTrailingSlash(String.format("%sPATH", File.separator)));
        // Double separator at end
        Assert.assertEquals(String.format("%sPATH%1$s", File.separator), FileUtils.ensureTrailingSlash(String.format("%sPATH%1$s%1$s", File.separator)));
    }
}

