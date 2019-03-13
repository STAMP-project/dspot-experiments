/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.job.entries.dostounix;


import java.io.File;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class JobEntryDosToUnix_ConversionIdempotency_Test {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private File tmpFile;

    private String tmpFilePath;

    private JobEntryDosToUnix entry;

    @Test
    public void oneSeparator_nix2dos() throws Exception {
        doTest("\n", false, "\r\n");
    }

    @Test
    public void oneSeparator_nix2nix() throws Exception {
        doTest("\n", true, "\n");
    }

    @Test
    public void oneSeparator_dos2nix() throws Exception {
        doTest("\r\n", true, "\n");
    }

    @Test
    public void oneSeparator_dos2dos() throws Exception {
        doTest("\r\n", false, "\r\n");
    }

    @Test
    public void charNewLineChar_nix2dos() throws Exception {
        doTest("a\nb", false, "a\r\nb");
    }

    @Test
    public void charNewLineChar_nix2nix() throws Exception {
        doTest("a\nb", true, "a\nb");
    }

    @Test
    public void charNewLineChar_dos2nix() throws Exception {
        doTest("a\r\nb", true, "a\nb");
    }

    @Test
    public void charNewLineChar_dos2dos() throws Exception {
        doTest("a\r\nb", false, "a\r\nb");
    }

    @Test
    public void twoCrOneLf_2nix() throws Exception {
        doTest("\r\r\n", true, "\r\n");
    }

    @Test
    public void twoCrOneLf_2dos() throws Exception {
        doTest("\r\r\n", false, "\r\r\n");
    }

    @Test
    public void crCharCrLf_2nix() throws Exception {
        doTest("\ra\r\n", true, "\ra\n");
    }

    @Test
    public void crCharCrLf_2dos() throws Exception {
        doTest("\ra\r\n", false, "\ra\r\n");
    }

    @Test
    public void oneSeparator_nix2dos_hugeInput() throws Exception {
        doTestForSignificantInput("\n", false, "\r\n");
    }

    @Test
    public void oneSeparator_nix2nix_hugeInput() throws Exception {
        doTestForSignificantInput("\n", true, "\n");
    }

    @Test
    public void oneSeparator_dos2nix_hugeInput() throws Exception {
        doTestForSignificantInput("\r\n", true, "\n");
    }

    @Test
    public void oneSeparator_dos2dos_hugeInput() throws Exception {
        doTestForSignificantInput("\r\n", false, "\r\n");
    }
}

