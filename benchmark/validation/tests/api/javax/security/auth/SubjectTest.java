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
package tests.api.javax.security.auth;


import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import javax.security.auth.Subject;
import junit.framework.TestCase;


/**
 * Tests for <code>Subject</code> class constructors and methods.
 */
public class SubjectTest extends TestCase {
    /**
     * javax.security.auth.Subject#Subject()
     */
    public void test_Constructor_01() {
        try {
            Subject s = new Subject();
            TestCase.assertNotNull("Null object returned", s);
            TestCase.assertTrue("Set of principal is not empty", s.getPrincipals().isEmpty());
            TestCase.assertTrue("Set of private credentials is not empty", s.getPrivateCredentials().isEmpty());
            TestCase.assertTrue("Set of public credentials is not empty", s.getPublicCredentials().isEmpty());
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
    }

    /**
     * javax.security.auth.Subject#doAs(Subject subject, PrivilegedAction action)
     */
    public void test_doAs_01() {
        Subject subj = new Subject();
        PrivilegedAction<Object> pa = new myPrivilegedAction();
        PrivilegedAction<Object> paNull = null;
        try {
            Object obj = Subject.doAs(null, pa);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            Object obj = Subject.doAs(subj, pa);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            Object obj = Subject.doAs(subj, paNull);
            TestCase.fail("NullPointerException wasn't thrown");
        } catch (NullPointerException npe) {
        }
    }

    /**
     * javax.security.auth.Subject#doAs(Subject subject, PrivilegedExceptionAction action)
     */
    public void test_doAs_02() {
        Subject subj = new Subject();
        PrivilegedExceptionAction<Object> pea = new myPrivilegedExceptionAction();
        PrivilegedExceptionAction<Object> peaNull = null;
        try {
            Object obj = Subject.doAs(null, pea);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            Object obj = Subject.doAs(subj, pea);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            Object obj = Subject.doAs(subj, peaNull);
            TestCase.fail("NullPointerException wasn't thrown");
        } catch (NullPointerException npe) {
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of NullPointerException"));
        }
        try {
            Subject.doAs(subj, new PrivilegedExceptionAction<Object>() {
                public Object run() throws PrivilegedActionException {
                    throw new PrivilegedActionException(null);
                }
            });
            TestCase.fail("PrivilegedActionException wasn't thrown");
        } catch (PrivilegedActionException e) {
        }
    }

    /**
     * javax.security.auth.Subject#doAsPrivileged(Subject subject,
     *                                                   PrivilegedAction action,
     *                                                   AccessControlContext acc)
     */
    public void test_doAsPrivileged_01() {
        Subject subj = new Subject();
        PrivilegedAction<Object> pa = new myPrivilegedAction();
        PrivilegedAction<Object> paNull = null;
        AccessControlContext acc = AccessController.getContext();
        try {
            Object obj = Subject.doAsPrivileged(null, pa, acc);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            Object obj = Subject.doAsPrivileged(subj, pa, acc);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            Object obj = Subject.doAsPrivileged(subj, paNull, acc);
            TestCase.fail("NullPointerException wasn't thrown");
        } catch (NullPointerException npe) {
        }
    }

    /**
     * javax.security.auth.Subject#doAsPrivileged(Subject subject,
     *                                                   PrivilegedExceptionAction action,
     *                                                   AccessControlContext acc)
     */
    public void test_doAsPrivileged_02() {
        Subject subj = new Subject();
        PrivilegedExceptionAction<Object> pea = new myPrivilegedExceptionAction();
        PrivilegedExceptionAction<Object> peaNull = null;
        AccessControlContext acc = AccessController.getContext();
        try {
            Object obj = Subject.doAsPrivileged(null, pea, acc);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            Object obj = Subject.doAsPrivileged(subj, pea, acc);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            Object obj = Subject.doAsPrivileged(subj, peaNull, acc);
            TestCase.fail("NullPointerException wasn't thrown");
        } catch (NullPointerException npe) {
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of NullPointerException"));
        }
        try {
            Subject.doAsPrivileged(subj, new PrivilegedExceptionAction<Object>() {
                public Object run() throws PrivilegedActionException {
                    throw new PrivilegedActionException(null);
                }
            }, acc);
            TestCase.fail("PrivilegedActionException wasn't thrown");
        } catch (PrivilegedActionException e) {
        }
    }

    /**
     * javax.security.auth.Subject#getSubject(AccessControlContext acc)
     */
    public void test_getSubject() {
        Subject subj = new Subject();
        AccessControlContext acc = new AccessControlContext(new ProtectionDomain[0]);
        try {
            TestCase.assertNull(Subject.getSubject(acc));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + e));
        }
    }

    /**
     * javax.security.auth.Subject#toString()
     */
    public void test_toString() {
        Subject subj = new Subject();
        try {
            TestCase.assertNotNull("Null returned", subj.toString());
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
    }

    /**
     * javax.security.auth.Subject#hashCode()
     */
    public void test_hashCode() {
        Subject subj = new Subject();
        try {
            TestCase.assertNotNull("Null returned", subj.hashCode());
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
    }
}

