/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 *
 *
 * @author Alexander Y. Kleymenov
 * @version $Revision$
 */
package tests.security.cert;


import java.math.BigInteger;
import java.security.cert.CRLException;
import java.security.cert.X509CRLEntry;
import java.util.Date;
import java.util.Set;
import junit.framework.TestCase;


/**
 *
 */
public class X509CRLEntryTest extends TestCase {
    X509CRLEntry tbt_crlentry;

    /**
     * The stub class used for testing of non abstract methods.
     */
    private class TBTCRLEntry extends X509CRLEntry {
        public TBTCRLEntry() {
            super();
        }

        public Set<String> getNonCriticalExtensionOIDs() {
            return null;
        }

        public Set<String> getCriticalExtensionOIDs() {
            return null;
        }

        public byte[] getExtensionValue(String oid) {
            return null;
        }

        public boolean hasUnsupportedCriticalExtension() {
            return false;
        }

        public byte[] getEncoded() throws CRLException {
            return null;
        }

        public BigInteger getSerialNumber() {
            return null;
        }

        public Date getRevocationDate() {
            return null;
        }

        public boolean hasExtensions() {
            return false;
        }

        public String toString() {
            return null;
        }
    }

    public X509CRLEntryTest() {
        tbt_crlentry = new X509CRLEntryTest.TBTCRLEntry() {
            public byte[] getEncoded() throws CRLException {
                return new byte[]{ 1, 2, 3 };
            }
        };
    }

    /**
     * X509CRLEntry() method testing. Tests for creating object.
     */
    public void testX509CRLEntry() {
        X509CRLEntryTest.TBTCRLEntry tbt_crlentry = new X509CRLEntryTest.TBTCRLEntry();
        TestCase.assertNull(tbt_crlentry.getCertificateIssuer());
        TestCase.assertNull(tbt_crlentry.getCriticalExtensionOIDs());
        try {
            TestCase.assertNull(tbt_crlentry.getEncoded());
        } catch (CRLException e) {
            TestCase.fail(("Unexpected exception " + (e.getMessage())));
        }
        TestCase.assertNull(tbt_crlentry.getNonCriticalExtensionOIDs());
        TestCase.assertNull(tbt_crlentry.getRevocationDate());
    }

    /**
     * equals(Object other) method testing. Tests the correctness of equal
     * operation: it should be reflexive, symmetric, transitive, consistent
     * and should be false on null object.
     */
    public void testEquals() {
        X509CRLEntryTest.TBTCRLEntry tbt_crlentry_1 = new X509CRLEntryTest.TBTCRLEntry() {
            public byte[] getEncoded() {
                return new byte[]{ 1, 2, 3 };
            }
        };
        X509CRLEntryTest.TBTCRLEntry tbt_crlentry_2 = new X509CRLEntryTest.TBTCRLEntry() {
            public byte[] getEncoded() {
                return new byte[]{ 1, 2, 3 };
            }
        };
        X509CRLEntryTest.TBTCRLEntry tbt_crlentry_3 = new X509CRLEntryTest.TBTCRLEntry() {
            public byte[] getEncoded() {
                return new byte[]{ 3, 2, 1 };
            }
        };
        // checking for reflexive law:
        TestCase.assertTrue("The equivalence relation should be reflexive.", tbt_crlentry.equals(tbt_crlentry));
        TestCase.assertEquals("The CRL Entries with equals encoded form should be equal", tbt_crlentry, tbt_crlentry_1);
        // checking for symmetric law:
        TestCase.assertTrue("The equivalence relation should be symmetric.", tbt_crlentry_1.equals(tbt_crlentry));
        TestCase.assertEquals("The CRL Entries with equals encoded form should be equal", tbt_crlentry_1, tbt_crlentry_2);
        // checking for transitive law:
        TestCase.assertTrue("The equivalence relation should be transitive.", tbt_crlentry.equals(tbt_crlentry_2));
        TestCase.assertFalse("Should not be equal to null object.", tbt_crlentry.equals(null));
        TestCase.assertFalse(("The CRL Entries with differing encoded form " + "should not be equal."), tbt_crlentry.equals(tbt_crlentry_3));
        TestCase.assertFalse(("The CRL Entries should not be equals to the object " + "which is not an instance of X509CRLEntry."), tbt_crlentry.equals(new Object()));
    }

    /**
     * hashCode() method testing. Tests that for equal objects hash codes
     * are equal.
     */
    public void testHashCode() {
        X509CRLEntryTest.TBTCRLEntry tbt_crlentry_1 = new X509CRLEntryTest.TBTCRLEntry() {
            public byte[] getEncoded() {
                return new byte[]{ 1, 2, 3 };
            }
        };
        TestCase.assertTrue("Equal objects should have the same hash codes.", ((tbt_crlentry.hashCode()) == (tbt_crlentry_1.hashCode())));
    }

    /**
     * getCertificateIssuer() method testing. Tests if the method throws
     * appropriate exception.
     */
    public void testGetCertificateIssuer() {
        TestCase.assertNull("The default implementation should return null.", tbt_crlentry.getCertificateIssuer());
    }

    public void testAbstractMethods() {
        X509CRLEntryTest.TBTCRLEntry tbt = new X509CRLEntryTest.TBTCRLEntry() {
            public byte[] getEncoded() {
                return new byte[]{ 1, 2, 3 };
            }
        };
        try {
            tbt.getEncoded();
            tbt.getRevocationDate();
            tbt.getSerialNumber();
            tbt.hasExtensions();
            tbt.toString();
        } catch (Exception e) {
            TestCase.fail("Unexpected exception");
        }
    }
}

