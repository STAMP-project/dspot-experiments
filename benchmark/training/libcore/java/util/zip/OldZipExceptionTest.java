/**
 * Copyright (C) 2008 The Android Open Source Project
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
package libcore.java.util.zip;


import java.util.zip.ZipException;
import junit.framework.TestCase;


public class OldZipExceptionTest extends TestCase {
    public void testZipException() {
        ZipException zz = new ZipException();
        TestCase.assertEquals(zz.getMessage(), null);
    }

    public void testZipExceptionLjava_lang_String() {
        ZipException zz = new ZipException("Test");
        TestCase.assertEquals(zz.getMessage(), "Test");
    }
}

