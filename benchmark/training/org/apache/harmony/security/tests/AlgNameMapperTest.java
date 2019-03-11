/**
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.security.tests;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;
import junit.framework.TestCase;
import org.apache.harmony.security.utils.AlgNameMapper;


public class AlgNameMapperTest extends TestCase {
    private final String[][] HARDCODED_ALIASES = new String[][]{ new String[]{ "1.2.840.10040.4.1", "DSA" }, new String[]{ "1.2.840.10040.4.3", "SHA1withDSA" }, new String[]{ "1.2.840.113549.1.1.1", "RSA" }, new String[]{ "1.2.840.113549.1.1.4", "MD5withRSA" }, new String[]{ "1.2.840.113549.1.1.5", "SHA1withRSA" }, new String[]{ "1.2.840.113549.1.3.1", "DiffieHellman" }, new String[]{ "1.2.840.113549.1.5.3", "pbeWithMD5AndDES-CBC" }, new String[]{ "1.2.840.113549.1.12.1.3", "pbeWithSHAAnd3-KeyTripleDES-CBC" }, new String[]{ "1.2.840.113549.1.12.1.6", "pbeWithSHAAnd40BitRC2-CBC" }, new String[]{ "1.2.840.113549.3.2", "RC2-CBC" }, new String[]{ "1.2.840.113549.3.3", "RC2-EBC" }, new String[]{ "1.2.840.113549.3.4", "RC4" }, new String[]{ "1.2.840.113549.3.5", "RC4WithMAC" }, new String[]{ "1.2.840.113549.3.6", "DESx-CBC" }, new String[]{ "1.2.840.113549.3.7", "TripleDES-CBC" }, new String[]{ "1.2.840.113549.3.8", "rc5CBC" }, new String[]{ "1.2.840.113549.3.9", "RC5-CBC" }, new String[]{ "1.2.840.113549.3.10", "DESCDMF" }, new String[]{ "2.23.42.9.11.4.1", "ECDSA" } };

    public void testHardcodedAliases() throws Exception {
        final ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(errBuffer);
        for (int i = 0; i < (HARDCODED_ALIASES.length); i++) {
            try {
                TestCase.assertEquals(HARDCODED_ALIASES[i][1].toUpperCase(Locale.US), AlgNameMapper.map2AlgName(HARDCODED_ALIASES[i][0]).toUpperCase(Locale.US));
                TestCase.assertEquals(HARDCODED_ALIASES[i][0], AlgNameMapper.map2OID(HARDCODED_ALIASES[i][1]));
                TestCase.assertEquals(HARDCODED_ALIASES[i][1].toUpperCase(Locale.US), AlgNameMapper.getStandardName(HARDCODED_ALIASES[i][1].toUpperCase(Locale.US)).toUpperCase(Locale.US));
                TestCase.assertTrue(AlgNameMapper.isOID(HARDCODED_ALIASES[i][0]));
            } catch (Throwable e) {
                out.append((("Error encountered checking " + (HARDCODED_ALIASES[i][1])) + "\n"));
                e.printStackTrace(out);
            }
        }
        out.flush();
        if ((errBuffer.size()) > 0) {
            throw new Exception((("Errors encountered:\n\n" + (errBuffer.toString())) + "\n\n"));
        }
    }

    private final String[][] NON_HARDCODED_ALIASES = new String[][]{ new String[]{ "2.16.840.1.101.3.4.2.3", "SHA512" }// This isn't currently hardcoded in AlgNameMapper
    // This isn't currently hardcoded in AlgNameMapper
    // This isn't currently hardcoded in AlgNameMapper
    , new String[]{ "1.2.840.10045.3.1.7", "prime256v1" }// No provider provides EC curves
    // No provider provides EC curves
    // No provider provides EC curves
     };

    public void testNon_Hardcoded_Aliases_Exist() throws Exception {
        final ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(errBuffer);
        for (int i = 0; i < (NON_HARDCODED_ALIASES.length); i++) {
            try {
                String algName = AlgNameMapper.map2AlgName(NON_HARDCODED_ALIASES[i][0]);
                TestCase.assertNotNull(algName);
                TestCase.assertEquals(NON_HARDCODED_ALIASES[i][1].toUpperCase(Locale.US), algName.toUpperCase(Locale.US));
                String oid = AlgNameMapper.map2OID(algName);
                TestCase.assertNotNull(oid);
                TestCase.assertEquals(NON_HARDCODED_ALIASES[i][0], oid);
            } catch (Throwable e) {
                out.append((("Error encountered checking " + (HARDCODED_ALIASES[i][1])) + "\n"));
                e.printStackTrace(out);
            }
        }
        out.flush();
        if ((errBuffer.size()) > 0) {
            throw new Exception((("Errors encountered:\n\n" + (errBuffer.toString())) + "\n\n"));
        }
    }
}

