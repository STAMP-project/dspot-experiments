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
 * @author Vladimir N. Molotkov
 * @version $Revision$
 */
package org.apache.harmony.security.tests.java.security;


import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.security.KeyRep;
import java.security.Security;
import java.util.Set;
import junit.framework.TestCase;

import static java.security.KeyRep.Type.PRIVATE;
import static java.security.KeyRep.Type.PUBLIC;
import static java.security.KeyRep.Type.SECRET;


public class KeyRepTest extends TestCase {
    private static final Set<String> keyFactoryAlgorithms = Security.getAlgorithms("KeyFactory");

    static {
        TestCase.assertFalse(KeyRepTest.keyFactoryAlgorithms.isEmpty());
    }

    public final void testKeyRep01() {
        TestCase.assertNotNull(new KeyRep(SECRET, "", "", new byte[]{  }));
        TestCase.assertNotNull(new KeyRep(PUBLIC, "", "", new byte[]{  }));
        TestCase.assertNotNull(new KeyRep(PRIVATE, "", "", new byte[]{  }));
    }

    public final void testKeyRep02() {
        try {
            new KeyRep(null, "", "", new byte[]{  });
            TestCase.fail("NullPointerException has not been thrown (type)");
        } catch (NullPointerException expected) {
        }
        try {
            new KeyRep(SECRET, null, "", new byte[]{  });
            TestCase.fail("NullPointerException has not been thrown (alg)");
        } catch (NullPointerException expected) {
        }
        try {
            new KeyRep(PRIVATE, "", null, new byte[]{  });
            TestCase.fail("NullPointerException has not been thrown (format)");
        } catch (NullPointerException expected) {
        }
        try {
            new KeyRep(PUBLIC, "", "", null);
            TestCase.fail("NullPointerException has not been thrown (encoding)");
        } catch (NullPointerException expected) {
        }
    }

    public final void testReadResolve01() throws Exception {
        KeyRepTest.KeyRepChild kr = new KeyRepTest.KeyRepChild(SECRET, "", "", new byte[]{  });
        try {
            kr.readResolve();
            TestCase.fail("NotSerializableException has not been thrown (no format)");
        } catch (NotSerializableException expected) {
        }
        kr = new KeyRepTest.KeyRepChild(SECRET, "", "X.509", new byte[]{  });
        try {
            kr.readResolve();
            TestCase.fail("NotSerializableException has not been thrown (unacceptable format)");
        } catch (NotSerializableException expected) {
        }
        kr = new KeyRepTest.KeyRepChild(SECRET, "", "RAW", new byte[]{  });
        try {
            kr.readResolve();
            TestCase.fail("NotSerializableException has not been thrown (empty key)");
        } catch (NotSerializableException expected) {
        }
    }

    public final void testReadResolve02() throws Exception {
        KeyRepTest.KeyRepChild kr = new KeyRepTest.KeyRepChild(PUBLIC, "", "", new byte[]{  });
        try {
            kr.readResolve();
            TestCase.fail("NotSerializableException has not been thrown (no format)");
        } catch (NotSerializableException expected) {
        }
        kr = new KeyRepTest.KeyRepChild(PUBLIC, "", "RAW", new byte[]{  });
        try {
            kr.readResolve();
            TestCase.fail("NotSerializableException has not been thrown (unacceptable format)");
        } catch (NotSerializableException expected) {
        }
        kr = new KeyRepTest.KeyRepChild(PUBLIC, "bla-bla", "X.509", new byte[]{  });
        try {
            kr.readResolve();
            TestCase.fail("NotSerializableException has not been thrown (unknown KeyFactory algorithm)");
        } catch (NotSerializableException expected) {
        }
    }

    public final void testReadResolve03() throws Exception {
        KeyRepTest.KeyRepChild kr = new KeyRepTest.KeyRepChild(PRIVATE, "", "", new byte[]{  });
        try {
            kr.readResolve();
            TestCase.fail("NotSerializableException has not been thrown (no format)");
        } catch (NotSerializableException expected) {
        }
        kr = new KeyRepTest.KeyRepChild(PRIVATE, "", "RAW", new byte[]{  });
        try {
            kr.readResolve();
            TestCase.fail("NotSerializableException has not been thrown (unacceptable format)");
        } catch (NotSerializableException expected) {
        }
        kr = new KeyRepTest.KeyRepChild(PRIVATE, "bla-bla", "PKCS#8", new byte[]{  });
        try {
            kr.readResolve();
            TestCase.fail("NotSerializableException has not been thrown (unknown KeyFactory algorithm)");
        } catch (NotSerializableException expected) {
        }
    }

    public final void testReadResolve04() throws Exception {
        for (String algorithm : KeyRepTest.keyFactoryAlgorithms) {
            KeyRepTest.KeyRepChild kr = new KeyRepTest.KeyRepChild(PUBLIC, algorithm, "X.509", new byte[]{ 1, 2, 3 });
            try {
                kr.readResolve();
                TestCase.fail(("NotSerializableException has not been thrown (no format) " + algorithm));
            } catch (NotSerializableException expected) {
            }
        }
    }

    public final void testReadResolve05() throws Exception {
        for (String algorithm : KeyRepTest.keyFactoryAlgorithms) {
            KeyRepTest.KeyRepChild kr = new KeyRepTest.KeyRepChild(PRIVATE, algorithm, "PKCS#8", new byte[]{ 1, 2, 3 });
            try {
                kr.readResolve();
                TestCase.fail(("NotSerializableException has not been thrown (no format) " + algorithm));
            } catch (NotSerializableException expected) {
            }
        }
    }

    class KeyRepChild extends KeyRep {
        public KeyRepChild(KeyRep.Type type, String algorithm, String format, byte[] encoded) {
            super(type, algorithm, format, encoded);
        }

        // Overriden to make public for testing
        @Override
        public Object readResolve() throws ObjectStreamException {
            return super.readResolve();
        }
    }
}

