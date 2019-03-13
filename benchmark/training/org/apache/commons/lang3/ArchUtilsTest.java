/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import org.apache.commons.lang3.arch.Processor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.arch.Processor.Arch.BIT_32;
import static org.apache.commons.lang3.arch.Processor.Arch.BIT_64;
import static org.apache.commons.lang3.arch.Processor.Type.IA_64;
import static org.apache.commons.lang3.arch.Processor.Type.PPC;
import static org.apache.commons.lang3.arch.Processor.Type.X86;


/**
 * Test class for {@link ArchUtils}.
 *
 * @author Tomschi
 */
public class ArchUtilsTest {
    private static final String X86 = "x86";

    private static final String X86_64 = "x86_64";

    private static final String IA64 = "ia64";

    private static final String IA64_32 = "ia64_32";

    private static final String PPC = "ppc";

    private static final String PPC64 = "ppc64";

    @Test
    public void testIs32BitJVM() {
        Processor processor = ArchUtils.getProcessor(ArchUtilsTest.X86);
        assertEqualsArchNotNull(BIT_32, processor);
        Assertions.assertTrue(processor.is32Bit());
        processor = ArchUtils.getProcessor(ArchUtilsTest.IA64_32);
        assertEqualsArchNotNull(BIT_32, processor);
        Assertions.assertTrue(processor.is32Bit());
        processor = ArchUtils.getProcessor(ArchUtilsTest.PPC);
        assertEqualsArchNotNull(BIT_32, processor);
        processor.is32Bit();
        processor = ArchUtils.getProcessor(ArchUtilsTest.X86_64);
        assertNotEqualsArchNotNull(BIT_32, processor);
        Assertions.assertFalse(processor.is32Bit());
        processor = ArchUtils.getProcessor(ArchUtilsTest.PPC64);
        assertNotEqualsArchNotNull(BIT_32, processor);
        Assertions.assertFalse(processor.is32Bit());
        processor = ArchUtils.getProcessor(ArchUtilsTest.IA64);
        assertNotEqualsArchNotNull(BIT_32, processor);
        Assertions.assertFalse(processor.is32Bit());
    }

    @Test
    public void testIs64BitJVM() {
        Processor processor = ArchUtils.getProcessor(ArchUtilsTest.X86_64);
        assertEqualsArchNotNull(BIT_64, processor);
        Assertions.assertTrue(processor.is64Bit());
        processor = ArchUtils.getProcessor(ArchUtilsTest.PPC64);
        assertEqualsArchNotNull(BIT_64, processor);
        Assertions.assertTrue(processor.is64Bit());
        processor = ArchUtils.getProcessor(ArchUtilsTest.IA64);
        assertEqualsArchNotNull(BIT_64, processor);
        Assertions.assertTrue(processor.is64Bit());
        processor = ArchUtils.getProcessor(ArchUtilsTest.X86);
        assertNotEqualsArchNotNull(BIT_64, processor);
        Assertions.assertFalse(processor.is64Bit());
        processor = ArchUtils.getProcessor(ArchUtilsTest.PPC);
        assertNotEqualsArchNotNull(BIT_64, processor);
        Assertions.assertFalse(processor.is64Bit());
        processor = ArchUtils.getProcessor(ArchUtilsTest.IA64_32);
        assertNotEqualsArchNotNull(BIT_64, processor);
        Assertions.assertFalse(processor.is64Bit());
    }

    @Test
    public void testArch() {
        Processor processor = ArchUtils.getProcessor(ArchUtilsTest.X86);
        assertEqualsTypeNotNull(X86, processor);
        Assertions.assertTrue(processor.isX86());
        assertNotEqualsTypeNotNull(PPC, processor);
        Assertions.assertFalse(processor.isPPC());
        processor = ArchUtils.getProcessor(ArchUtilsTest.X86_64);
        assertEqualsTypeNotNull(X86, processor);
        Assertions.assertTrue(processor.isX86());
        processor = ArchUtils.getProcessor(ArchUtilsTest.IA64_32);
        assertEqualsTypeNotNull(IA_64, processor);
        Assertions.assertTrue(processor.isIA64());
        processor = ArchUtils.getProcessor(ArchUtilsTest.IA64);
        assertEqualsTypeNotNull(IA_64, processor);
        Assertions.assertTrue(processor.isIA64());
        assertNotEqualsTypeNotNull(X86, processor);
        Assertions.assertFalse(processor.isX86());
        processor = ArchUtils.getProcessor(ArchUtilsTest.PPC);
        assertEqualsTypeNotNull(PPC, processor);
        Assertions.assertTrue(processor.isPPC());
        assertNotEqualsTypeNotNull(IA_64, processor);
        Assertions.assertFalse(processor.isIA64());
        processor = ArchUtils.getProcessor(ArchUtilsTest.PPC64);
        assertEqualsTypeNotNull(PPC, processor);
        Assertions.assertTrue(processor.isPPC());
    }

    @Test
    public void testGetProcessor() {
        Assertions.assertNotNull(ArchUtils.getProcessor(ArchUtilsTest.X86));
        Assertions.assertNull(ArchUtils.getProcessor("NA"));
    }
}

