/**
 * Copyright 2016 the original author or authors.
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
package org.springsource.loaded.test;


import java.io.File;
import java.io.Serializable;
import org.junit.Test;
import org.springsource.loaded.ConstantPoolScanner;
import org.springsource.loaded.ConstantPoolScanner.References;


/**
 *
 *
 * @author Andy Clement
 */
public class ConstantPoolScannerTests {
    static class Helper {
        public void methodOne() {
            foo(null);
        }

        public String foo(Serializable s) {
            return new String("hello world");
        }
    }

    @Test
    public void testConstantPoolScanner() throws Exception {
        File fileToCheck = new File("bin/org/springsource/loaded/test/ConstantPoolScannerTests$Helper.class");
        References references = ConstantPoolScanner.getReferences(load(fileToCheck));
        System.out.println(references);
        System.out.println(references.referencedClasses);
        assertContains(references.referencedMethods, "java/lang/String.<init>");
    }
}

