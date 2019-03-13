/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.plugin.dynamodule.scanner;


import java.net.URL;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Check some methods od the DynaModuleScanner
 *
 * @author Florent Benoit
 */
public class DynaModuleScannerTest {
    /**
     * First, check that a file is scanned and then check that if a file is excluded, it is not
     * scanned.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void checkSkipJars() throws Exception {
        DynaModuleScanner dynaModuleScanner = new DynaModuleScanner();
        dynaModuleScanner = Mockito.spy(dynaModuleScanner);
        Mockito.doNothing().when(dynaModuleScanner).performScan(ArgumentMatchers.any(URL.class));
        // first check that URL is scanned
        URL testUrl = new URL("file:///my-file.jar");
        dynaModuleScanner.scan(testUrl);
        Mockito.verify(dynaModuleScanner).performScan(testUrl);
        dynaModuleScanner.setAdditionalSkipResources(new String[]{ ".*I-want-to-exclude-this.jar" });
        URL testExcludedUrl = new URL("file:///my-file-I-want-to-exclude-this.jar");
        dynaModuleScanner.scan(testExcludedUrl);
        Mockito.verify(dynaModuleScanner, Mockito.never()).performScan(testExcludedUrl);
    }

    /**
     * First, check that a file is scanned and then check that if a file is excluded, it is not
     * scanned.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void checkSkipClass() throws Exception {
        DynaModuleScanner dynaModuleScanner = new DynaModuleScanner();
        dynaModuleScanner = Mockito.spy(dynaModuleScanner);
        Mockito.doNothing().when(dynaModuleScanner).performScan(ArgumentMatchers.any(URL.class));
        // first check that URL is scanned
        URL testUrl = new URL("file:///my-file.class");
        dynaModuleScanner.scan(testUrl);
        Mockito.verify(dynaModuleScanner).performScan(testUrl);
        dynaModuleScanner.setAdditionalSkipResources(new String[]{ ".*I-want-to-exclude-this.class" });
        URL testExcludedUrl = new URL("file:///my-file-I-want-to-exclude-this.class");
        dynaModuleScanner.scan(testExcludedUrl);
        Mockito.verify(dynaModuleScanner, Mockito.never()).performScan(testExcludedUrl);
    }
}

