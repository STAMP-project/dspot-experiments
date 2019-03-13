/**
 * Copyright (C) 2012 The Android Open Source Project
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
package org.conscrypt;


import java.io.File;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class CertPinManagerTest extends TestCase {
    private X509Certificate[] chain;

    private List<X509Certificate> shortChain;

    private List<X509Certificate> longChain;

    private String shortPin;

    private String longPin;

    private List<File> tmpFiles = new ArrayList<File>();

    public void testPinFileMaximumLookup() throws Exception {
        // write a pinfile with two entries, one longer than the other
        String shortEntry = "*.google.com=true|" + (shortPin);
        String longEntry = "*.clients.google.com=true|" + (longPin);
        // create the pinFile
        String path = writeTmpPinFile(((shortEntry + "\n") + longEntry));
        CertPinManager pf = new CertPinManager(path, new TrustedCertificateStore());
        // verify that the shorter chain doesn't work for a name matching the longer
        TestCase.assertTrue("short chain long uri failed", pf.chainIsNotPinned("android.clients.google.com", shortChain));
        // verify that the longer chain doesn't work for a name matching the shorter
        TestCase.assertTrue("long chain short uri failed", pf.chainIsNotPinned("android.google.com", longChain));
        // verify that the shorter chain works for the shorter domain
        TestCase.assertTrue("short chain short uri failed", (!(pf.chainIsNotPinned("android.google.com", shortChain))));
        // and the same for the longer
        TestCase.assertTrue("long chain long uri failed", (!(pf.chainIsNotPinned("android.clients.google.com", longChain))));
    }

    public void testPinEntryMalformedEntry() throws Exception {
        // set up the pinEntry with a bogus entry
        String entry = "*.google.com=";
        try {
            new PinListEntry(entry, new TrustedCertificateStore());
            TestCase.fail("Accepted an empty pin list entry.");
        } catch (PinEntryException expected) {
        }
    }

    public void testPinEntryNull() throws Exception {
        // set up the pinEntry with a bogus entry
        String entry = null;
        try {
            new PinListEntry(entry, new TrustedCertificateStore());
            TestCase.fail("Accepted a basically wholly bogus entry.");
        } catch (NullPointerException expected) {
        }
    }

    public void testPinEntryEmpty() throws Exception {
        // set up the pinEntry with a bogus entry
        try {
            new PinListEntry("", new TrustedCertificateStore());
            TestCase.fail("Accepted an empty entry.");
        } catch (PinEntryException expected) {
        }
    }

    public void testPinEntryPinFailure() throws Exception {
        // write a pinfile with two entries, one longer than the other
        String shortEntry = "*.google.com=true|" + (shortPin);
        // set up the pinEntry with a pinlist that doesn't match what we'll give it
        PinListEntry e = new PinListEntry(shortEntry, new TrustedCertificateStore());
        TestCase.assertTrue("Not enforcing!", e.getEnforcing());
        // verify that it doesn't accept
        boolean retval = e.chainIsNotPinned(longChain);
        TestCase.assertTrue("Accepted an incorrect pinning, this is very bad", retval);
    }

    public void testPinEntryPinSuccess() throws Exception {
        // write a pinfile with two entries, one longer than the other
        String shortEntry = "*.google.com=true|" + (shortPin);
        // set up the pinEntry with a pinlist that matches what we'll give it
        PinListEntry e = new PinListEntry(shortEntry, new TrustedCertificateStore());
        TestCase.assertTrue("Not enforcing!", e.getEnforcing());
        // verify that it accepts
        boolean retval = e.chainIsNotPinned(shortChain);
        TestCase.assertTrue("Failed on a correct pinning, this is very bad", (!retval));
    }

    public void testPinEntryNonEnforcing() throws Exception {
        // write a pinfile with two entries, one longer than the other
        String shortEntry = "*.google.com=false|" + (shortPin);
        // set up the pinEntry with a pinlist that matches what we'll give it
        PinListEntry e = new PinListEntry(shortEntry, new TrustedCertificateStore());
        TestCase.assertFalse("Enforcing!", e.getEnforcing());
        // verify that it accepts
        boolean retval = e.chainIsNotPinned(shortChain);
        TestCase.assertTrue("Failed on an unenforced pinning, this is bad-ish", (!retval));
    }
}

