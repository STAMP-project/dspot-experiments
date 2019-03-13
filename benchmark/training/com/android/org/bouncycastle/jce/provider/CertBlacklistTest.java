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
package com.android.org.bouncycastle.jce.provider;


import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;


public class CertBlacklistTest extends TestCase {
    private File tmpFile;

    private Set<String> DEFAULT_PUBKEYS;

    private Set<String> DEFAULT_SERIALS;

    public static final String TEST_CERT = "" + (((((((((((((((("MIIDsjCCAxugAwIBAgIJAPLf2gS0zYGUMA0GCSqGSIb3DQEBBQUAMIGYMQswCQYDVQQGEwJVUzET" + "MBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNTW91bnRhaW4gVmlldzEPMA0GA1UEChMGR29v") + "Z2xlMRAwDgYDVQQLEwd0ZXN0aW5nMRYwFAYDVQQDEw1HZXJlbXkgQ29uZHJhMSEwHwYJKoZIhvcN") + "AQkBFhJnY29uZHJhQGdvb2dsZS5jb20wHhcNMTIwNzE0MTc1MjIxWhcNMTIwODEzMTc1MjIxWjCB") + "mDELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDU1vdW50YWluIFZp") + "ZXcxDzANBgNVBAoTBkdvb2dsZTEQMA4GA1UECxMHdGVzdGluZzEWMBQGA1UEAxMNR2VyZW15IENv") + "bmRyYTEhMB8GCSqGSIb3DQEJARYSZ2NvbmRyYUBnb29nbGUuY29tMIGfMA0GCSqGSIb3DQEBAQUA") + "A4GNADCBiQKBgQCjGGHATBYlmas+0sEECkno8LZ1KPglb/mfe6VpCT3GhSr+7br7NG/ZwGZnEhLq") + "E7YIH4fxltHmQC3Tz+jM1YN+kMaQgRRjo/LBCJdOKaMwUbkVynAH6OYsKevjrOPk8lfM5SFQzJMG") + "sA9+Tfopr5xg0BwZ1vA/+E3mE7Tr3M2UvwIDAQABo4IBADCB/TAdBgNVHQ4EFgQUhzkS9E6G+x8W") + "L4EsmRjDxu28tHUwgc0GA1UdIwSBxTCBwoAUhzkS9E6G+x8WL4EsmRjDxu28tHWhgZ6kgZswgZgx") + "CzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1Nb3VudGFpbiBWaWV3") + "MQ8wDQYDVQQKEwZHb29nbGUxEDAOBgNVBAsTB3Rlc3RpbmcxFjAUBgNVBAMTDUdlcmVteSBDb25k") + "cmExITAfBgkqhkiG9w0BCQEWEmdjb25kcmFAZ29vZ2xlLmNvbYIJAPLf2gS0zYGUMAwGA1UdEwQF") + "MAMBAf8wDQYJKoZIhvcNAQEFBQADgYEAYiugFDmbDOQ2U/+mqNt7o8ftlEo9SJrns6O8uTtK6AvR") + "orDrR1AXTXkuxwLSbmVfedMGOZy7Awh7iZa8hw5x9XmUudfNxvmrKVEwGQY2DZ9PXbrnta/dwbhK") + "mWfoepESVbo7CKIhJp8gRW0h1Z55ETXD57aGJRvQS4pxkP8ANhM=");

    public static final String TURKTRUST_1 = "" + ((((((((((((((((((((((("MIIFPTCCBCWgAwIBAgICCCcwDQYJKoZIhvcNAQEFBQAwgawxPTA7BgNVBAMMNFTDnFJLVFJVU1Qg" + "RWxla3Ryb25payBTdW51Y3UgU2VydGlmaWthc8SxIEhpem1ldGxlcmkxCzAJBgNVBAYTAlRSMV4w") + "XAYDVQQKDFVUw5xSS1RSVVNUIEJpbGdpIMSwbGV0acWfaW0gdmUgQmlsacWfaW0gR8O8dmVubGnE") + "n2kgSGl6bWV0bGVyaSBBLsWeLiAoYykgS2FzxLFtICAyMDA1MB4XDTExMDgwODA3MDc1MVoXDTIx") + "MDcwNjA3MDc1MVowbjELMAkGA1UEBhMCVFIxDzANBgNVBAgMBkFOS0FSQTEPMA0GA1UEBwwGQU5L") + "QVJBMQwwCgYDVQQKDANFR08xGDAWBgNVBAsMD0VHTyBCSUxHSSBJU0xFTTEVMBMGA1UEAwwMKi5F") + "R08uR09WLlRSMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv5zoj2Bpdl7R1M/zF6Qf") + "4su2F8vDqISKvuTuyJhNAHhFGHCsHjaixGMHspuz0l3V50kq/ECWbN8kKaeTrB112QOrWTU276iu") + "p1Gh+OlEOiR9vlQ4VAP00dWUjD6z9HQFCi8W3EsEtiiHiYOU9BcPpPkaUbECwP4nGVwR8aPwhB5P") + "GBJc98romdvciYkUpSOOwkuSRtooA7tRlLFu72QaNpXN1NueB36I3aajPk0YyiXy2w8XlgK7QI4P") + "SSBnSq+QblFocWVmLhF94je7py6lCnllrIFXpR3FWZLD5GcI6HKlBS78AQ+IMBLFHhsEVw5NQj90") + "chSZClfBWBZzIaV9RwIDAQABo4IBpDCCAaAwHwYDVR0jBBgwFoAUq042AzDS29UKaL6HpVBs/PZw") + "pSUwHQYDVR0OBBYEFGT7G4Y9uEryRIL5Vj3qJsD047M0MA4GA1UdDwEB/wQEAwIBBjBFBgNVHSAE") + "PjA8MDoGCWCGGAMAAwEBATAtMCsGCCsGAQUFBwIBFh9odHRwOi8vd3d3LnR1cmt0cnVzdC5jb20u") + "dHIvc3VlMA8GA1UdEwEB/wQFMAMBAf8wSQYDVR0fBEIwQDA+oDygOoY4aHR0cDovL3d3dy50dXJr") + "dHJ1c3QuY29tLnRyL3NpbC9UVVJLVFJVU1RfU1NMX1NJTF9zMi5jcmwwgaoGCCsGAQUFBwEBBIGd") + "MIGaMG4GCCsGAQUFBzAChmJodHRwOi8vd3d3LnR1cmt0cnVzdC5jb20udHIvc2VydGlmaWthbGFy") + "L1RVUktUUlVTVF9FbGVrdHJvbmlrX1N1bnVjdV9TZXJ0aWZpa2FzaV9IaXptZXRsZXJpX3MyLmNy") + "dDAoBggrBgEFBQcwAYYcaHR0cDovL29jc3AudHVya3RydXN0LmNvbS50cjANBgkqhkiG9w0BAQUF") + "AAOCAQEAj89QCCyoW0S20EcYDZAnvFLFmougK97Bt68iV1OM622+Cyeyf4Sz+1LBk1f9ni3fGT0Q") + "+RWZJYWq5YuSBiLVgk3NLcxnwe3wmnvErUgq1QDtAaNlBWMEMklOlWGfJ0eWaillUskJbDd4KwgZ") + "HDEj7g/jYEQqU1t0zoJdwM/zNsnLHkhwcWZ5PQnnbpff1Ct/1LH/8pdy2eRDmRmqniLUh8r2lZfJ") + "eudVZG6yIbxsqP3t2JCq5c2P1jDhAGF3g9DiskH0CzsRdbVpoWdr+PY1Xz/19G8XEpX9r+IBJhLd") + "bkpVo0Qh0A10mzFP/GUk5f/8nho2HvLaVMhWv1qKcF8IhQ==");

    public static final String TURKTRUST_2 = "" + ((((((((((((((((("MIID8DCCAtigAwIBAgICCGQwDQYJKoZIhvcNAQEFBQAwgawxPTA7BgNVBAMMNFTDnFJLVFJVU1Qg" + "RWxla3Ryb25payBTdW51Y3UgU2VydGlmaWthc8SxIEhpem1ldGxlcmkxCzAJBgNVBAYTAlRSMV4w") + "XAYDVQQKDFVUw5xSS1RSVVNUIEJpbGdpIMSwbGV0acWfaW0gdmUgQmlsacWfaW0gR8O8dmVubGnE") + "n2kgSGl6bWV0bGVyaSBBLsWeLiAoYykgS2FzxLFtICAyMDA1MB4XDTExMDgwODA3MDc1MVoXDTIx") + "MDgwNTA3MDc1MVowgaMxCzAJBgNVBAYTAlRSMRAwDgYDVQQIEwdMZWZrb3NhMRAwDgYDVQQHEwdM") + "ZWZrb3NhMRwwGgYDVQQKExNLS1RDIE1lcmtleiBCYW5rYXNpMSYwJAYDVQQDEx1lLWlzbGVtLmtr") + "dGNtZXJrZXpiYW5rYXNpLm9yZzEqMCgGCSqGSIb3DQEJARYbaWxldGlAa2t0Y21lcmtlemJhbmth") + "c2kub3JnMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw1hUpuRFY67NsZ6C9rzRAPCb") + "9RVpi4nZzJIA1TvIfr4hMPM0X5jseMf5GvgJQ+cBMZtooDd7BbZNy2z7O5A+8PYFaMDdokCENx2e") + "PIqAVuO6C5UAqM7J3n6RrhjOvqiw6dTQMbtXhjFao+YMuBVvRuuhGHBDK3Je64T/KLzcmAUlRJEu") + "y+ZMe7AatUaSDr/jy5DMA5xEYOdsnS5Zo30lRG+9vqbxb8CQi+E97sNjY+W4lEgJKQWMNh5rCxo4") + "Hinkm3CKyKX3PAS+DDVI3LQiCiIQUOMA2+1P5aTPTkpqlbjqhbWTWAPWOKCF9d83p3RMXOYt5Gah") + "S8rg5u6+toEC1QIDAQABoyMwITAOBgNVHQ8BAf8EBAMCAQYwDwYDVR0TAQH/BAUwAwEB/zANBgkq") + "hkiG9w0BAQUFAAOCAQEAwjWz5tsUvYORVW8KJSK/biHFrAnFotMtoTKEewRmnYaYjwXIr1IPaBqh") + "jkGGviLN2eOH/v97Uli6HC4lzhKHfMQUS9KF/f5nGcH8iQBy/gmFsfJQ1KDC6GNM4CfMGIzyxjYh") + "P0VzdUtKX3PAl5EqgMUcdqRDy6Ruz55+JkdvCL1nAC7xH+czJcZVwysTdGfLTCh6VtYPgIkeL6U8") + "3xQAyMuOHm72exJljYFqIsiNvGE0KufCqCuH1PD97IXMrLlwGmKKg5jP349lySBpJjm6RDqCTT+6") + "dUl2jkVbeNmco99Y7AOdtLsOdXBMCo5x8lK8zwQWFrzEms0joHXCpWfGWA==");

    public CertBlacklistTest() throws IOException {
        tmpFile = File.createTempFile("test", "");
        DEFAULT_PUBKEYS = getDefaultPubkeys();
        DEFAULT_SERIALS = getDefaultSerials();
        tmpFile.delete();
    }

    public void testPubkeyBlacklistLegit() throws Exception {
        // build the blacklist
        HashSet<String> bl = new HashSet<String>();
        bl.add("6ccabd7db47e94a5759901b6a7dfd45d1c091ccc");
        // write the blacklist
        writeBlacklist(bl);
        // add the default pubkeys into the bl
        bl.addAll(DEFAULT_PUBKEYS);
        // do the test
        TestCase.assertEquals(bl, getCurrentPubkeyBlacklist());
    }

    public void testLegitPubkeyIsntBlacklisted() throws Exception {
        // build the public key
        PublicKey pk = CertBlacklistTest.createPublicKey(CertBlacklistTest.TEST_CERT);
        // write that to the test blacklist
        writeBlacklist(new HashSet<String>());
        // set our blacklist path
        CertBlacklist bl = new CertBlacklist(tmpFile.getCanonicalPath(), CertBlacklist.DEFAULT_SERIAL_BLACKLIST_PATH);
        // check to make sure it isn't blacklisted
        TestCase.assertEquals(bl.isPublicKeyBlackListed(pk), false);
    }

    public void testPubkeyIsBlacklisted() throws Exception {
        // build the public key
        PublicKey pk = CertBlacklistTest.createPublicKey(CertBlacklistTest.TEST_CERT);
        // get its hash
        String hash = CertBlacklistTest.getHash(pk);
        // write that to the test blacklist
        HashSet<String> testBlackList = new HashSet<String>();
        testBlackList.add(hash);
        writeBlacklist(testBlackList);
        // set our blacklist path
        CertBlacklist bl = new CertBlacklist(tmpFile.getCanonicalPath(), CertBlacklist.DEFAULT_SERIAL_BLACKLIST_PATH);
        // check to make sure it isn't blacklited
        TestCase.assertTrue(bl.isPublicKeyBlackListed(pk));
    }

    public void testSerialBlacklistLegit() throws IOException {
        // build the blacklist
        HashSet<String> bl = new HashSet<String>();
        bl.add("22e514121e61c643b1e9b06bd4b9f7d0");
        // write the blacklist
        writeBlacklist(bl);
        // add the default serials into the bl
        bl.addAll(DEFAULT_SERIALS);
        // do the test
        TestCase.assertEquals(bl, getCurrentSerialBlacklist());
    }

    public void testPubkeyBlacklistMultipleLegit() throws IOException {
        // build the blacklist
        HashSet<String> bl = new HashSet<String>();
        bl.add("6ccabd7db47e94a5759901b6a7dfd45d1c091ccc");
        bl.add("6ccabd7db47e94a5759901b6a7dfd45d1c091ccd");
        // write the blacklist
        writeBlacklist(bl);
        // add the default pubkeys into the bl
        bl.addAll(DEFAULT_PUBKEYS);
        // do the test
        TestCase.assertEquals(bl, getCurrentPubkeyBlacklist());
    }

    public void testSerialBlacklistMultipleLegit() throws IOException {
        // build the blacklist
        HashSet<String> bl = new HashSet<String>();
        bl.add("22e514121e61c643b1e9b06bd4b9f7d0");
        bl.add("22e514121e61c643b1e9b06bd4b9f7d1");
        // write the blacklist
        writeBlacklist(bl);
        // add the default serials into the bl
        bl.addAll(DEFAULT_SERIALS);
        // do the test
        TestCase.assertEquals(bl, getCurrentSerialBlacklist());
    }

    public void testPubkeyBlacklistMultipleBad() throws IOException {
        // build the blacklist
        HashSet<String> bl = new HashSet<String>();
        bl.add("6ccabd7db47e94a5759901b6a7dfd45d1c091ccc");
        bl.add("");
        bl.add("6ccabd7db47e94a5759901b6a7dfd45d1c091ccd");
        // write the blacklist
        writeBlacklist(bl);
        // add the default pubkeys into the bl
        bl.addAll(DEFAULT_PUBKEYS);
        // remove the bad one
        bl.remove("");
        // do the test- results should be all but the bad one are handled
        TestCase.assertEquals(bl, getCurrentPubkeyBlacklist());
    }

    public void testSerialBlacklistMultipleBad() throws IOException {
        // build the blacklist
        HashSet<String> bl = new HashSet<String>();
        bl.add("22e514121e61c643b1e9b06bd4b9f7d0");
        bl.add("");
        bl.add("22e514121e61c643b1e9b06bd4b9f7d1");
        // write the blacklist
        writeBlacklist(bl);
        // add the default serials into the bl
        bl.addAll(DEFAULT_SERIALS);
        // remove the bad one
        bl.remove("");
        // do the test- results should be all but the bad one are handled
        TestCase.assertEquals(bl, getCurrentSerialBlacklist());
    }

    public void testPubkeyBlacklistDoesntExist() throws IOException {
        TestCase.assertEquals(DEFAULT_PUBKEYS, getCurrentPubkeyBlacklist());
    }

    public void testSerialBlacklistDoesntExist() throws IOException {
        TestCase.assertEquals(DEFAULT_SERIALS, getCurrentSerialBlacklist());
    }

    public void testPubkeyBlacklistNotHexValues() throws IOException {
        // build the blacklist
        HashSet<String> bl = new HashSet<String>();
        bl.add("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
        // write the blacklist
        writeBlacklist(bl);
        // do the test
        TestCase.assertEquals(DEFAULT_PUBKEYS, getCurrentPubkeyBlacklist());
    }

    public void testSerialBlacklistNotHexValues() throws IOException {
        // build the blacklist
        HashSet<String> bl = new HashSet<String>();
        bl.add("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
        // write the blacklist
        writeBlacklist(bl);
        // do the test
        TestCase.assertEquals(DEFAULT_SERIALS, getCurrentSerialBlacklist());
    }

    public void testPubkeyBlacklistIncorrectLength() throws IOException {
        // build the blacklist
        HashSet<String> bl = new HashSet<String>();
        bl.add("6ccabd7db47e94a5759901b6a7dfd45d1c091cc");
        // write the blacklist
        writeBlacklist(bl);
        // do the test
        TestCase.assertEquals(DEFAULT_PUBKEYS, getCurrentPubkeyBlacklist());
    }

    public void testSerialBlacklistZero() throws IOException {
        // build the blacklist
        HashSet<String> bl = new HashSet<String>();
        bl.add("0");
        // write the blacklist
        writeBlacklist(bl);
        // add the default serials
        bl.addAll(DEFAULT_SERIALS);
        // do the test
        TestCase.assertEquals(bl, getCurrentSerialBlacklist());
    }

    public void testSerialBlacklistNegative() throws IOException {
        // build the blacklist
        HashSet<String> bl = new HashSet<String>();
        bl.add("-1");
        // write the blacklist
        writeBlacklist(bl);
        // add the default serials
        bl.addAll(DEFAULT_SERIALS);
        // do the test
        TestCase.assertEquals(bl, getCurrentSerialBlacklist());
    }

    public void testTurkTrustIntermediate1SerialBlacklist() throws Exception {
        CertBlacklist bl = new CertBlacklist();
        TestCase.assertEquals(bl.isSerialNumberBlackListed(CertBlacklistTest.createSerialNumber(CertBlacklistTest.TURKTRUST_1)), true);
    }

    public void testTurkTrustIntermediate1PubkeyBlacklist() throws Exception {
        // build the public key
        PublicKey pk = CertBlacklistTest.createPublicKey(CertBlacklistTest.TURKTRUST_1);
        // write that to the test blacklist
        writeBlacklist(new HashSet<String>());
        // set our blacklist path
        CertBlacklist bl = new CertBlacklist();
        // check to make sure it isn't blacklisted
        TestCase.assertEquals(bl.isPublicKeyBlackListed(pk), true);
    }

    public void testTurkTrustIntermediate2SerialBlacklist() throws Exception {
        CertBlacklist bl = new CertBlacklist();
        TestCase.assertEquals(bl.isSerialNumberBlackListed(CertBlacklistTest.createSerialNumber(CertBlacklistTest.TURKTRUST_2)), true);
    }

    public void testTurkTrustIntermediate2PubkeyBlacklist() throws Exception {
        // build the public key
        PublicKey pk = CertBlacklistTest.createPublicKey(CertBlacklistTest.TURKTRUST_2);
        // set our blacklist path
        CertBlacklist bl = new CertBlacklist();
        // check to make sure it isn't blacklisted
        TestCase.assertEquals(bl.isPublicKeyBlackListed(pk), true);
    }
}

