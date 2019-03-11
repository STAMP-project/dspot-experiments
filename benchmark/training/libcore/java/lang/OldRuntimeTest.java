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
package libcore.java.lang;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Permission;
import java.util.Vector;
import junit.framework.TestCase;
import tests.support.resource.Support_Resources;


public class OldRuntimeTest extends TestCase {
    Runtime r = Runtime.getRuntime();

    InputStream is;

    public void test_freeMemory() {
        // Heap might grow or do GC at any time,
        // so we can't really test a lot. Hence
        // we are just doing some basic sanity
        // checks here.
        TestCase.assertTrue("must have some free memory", ((r.freeMemory()) > 0));
        TestCase.assertTrue("must not exceed total memory", ((r.freeMemory()) < (r.totalMemory())));
        long before = (r.totalMemory()) - (r.freeMemory());
        Vector<byte[]> v = new Vector<byte[]>();
        for (int i = 1; i < 10; i++) {
            v.addElement(new byte[10000]);
        }
        long after = (r.totalMemory()) - (r.freeMemory());
        TestCase.assertTrue("free memory must change with allocations", (after != before));
    }

    public void test_getRuntime() {
        // Test for method java.lang.Runtime java.lang.Runtime.getRuntime()
        TestCase.assertNotNull(Runtime.getRuntime());
    }

    public void test_addShutdownHook() {
        Thread thrException = new Thread() {
            public void run() {
                try {
                    Runtime.getRuntime().addShutdownHook(this);
                    TestCase.fail("IllegalStateException was not thrown.");
                } catch (IllegalStateException ise) {
                    // expected
                }
            }
        };
        try {
            Runtime.getRuntime().addShutdownHook(thrException);
        } catch (Throwable t) {
            TestCase.fail(t.getMessage());
        }
        try {
            Runtime.getRuntime().addShutdownHook(thrException);
            TestCase.fail("IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        SecurityManager sm = new SecurityManager() {
            public void checkPermission(Permission perm) {
                if (perm.getName().equals("shutdownHooks")) {
                    throw new SecurityException();
                }
            }
        };
        // remove previously added hook so we're not depending on the priority
        // of the Exceptions to be thrown.
        Runtime.getRuntime().removeShutdownHook(thrException);
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException ie) {
        }
    }

    public void test_availableProcessors() {
        TestCase.assertTrue(((Runtime.getRuntime().availableProcessors()) > 0));
    }

    public void test_execLjava_lang_StringLjava_lang_StringArray() {
        String[] envp = getEnv();
        checkExec(0, envp, null);
        checkExec(0, null, null);
        try {
            Runtime.getRuntime().exec(((String) (null)), null);
            TestCase.fail("NullPointerException should be thrown.");
        } catch (IOException ioe) {
            TestCase.fail("IOException was thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
        SecurityManager sm = new SecurityManager() {
            public void checkPermission(Permission perm) {
                if (perm.getName().equals("checkExec")) {
                    throw new SecurityException();
                }
            }

            public void checkExec(String cmd) {
                throw new SecurityException();
            }
        };
        try {
            Runtime.getRuntime().exec("", envp);
            TestCase.fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException iae) {
            // expected
        } catch (IOException e) {
            TestCase.fail("IOException was thrown.");
        }
    }

    public void test_execLjava_lang_StringArrayLjava_lang_StringArray() {
        String[] envp = getEnv();
        checkExec(4, envp, null);
        checkExec(4, null, null);
        try {
            Runtime.getRuntime().exec(((String[]) (null)), null);
            TestCase.fail("NullPointerException should be thrown.");
        } catch (IOException ioe) {
            TestCase.fail("IOException was thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            Runtime.getRuntime().exec(new String[]{ "ls", null }, null);
            TestCase.fail("NullPointerException should be thrown.");
        } catch (IOException ioe) {
            TestCase.fail("IOException was thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
        SecurityManager sm = new SecurityManager() {
            public void checkPermission(Permission perm) {
                if (perm.getName().equals("checkExec")) {
                    throw new SecurityException();
                }
            }

            public void checkExec(String cmd) {
                throw new SecurityException();
            }
        };
        try {
            Runtime.getRuntime().exec(new String[]{  }, envp);
            TestCase.fail("IndexOutOfBoundsException should be thrown.");
        } catch (IndexOutOfBoundsException ioob) {
            // expected
        } catch (IOException e) {
            TestCase.fail("IOException was thrown.");
        }
        try {
            Runtime.getRuntime().exec(new String[]{ "" }, envp);
            TestCase.fail("IOException should be thrown.");
        } catch (IOException e) {
            /* expected */
        }
    }

    public void test_execLjava_lang_StringLjava_lang_StringArrayLjava_io_File() {
        String[] envp = getEnv();
        File workFolder = Support_Resources.createTempFolder();
        checkExec(2, envp, workFolder);
        checkExec(2, null, null);
        try {
            Runtime.getRuntime().exec(((String) (null)), null, workFolder);
            TestCase.fail("NullPointerException should be thrown.");
        } catch (IOException ioe) {
            TestCase.fail("IOException was thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
        SecurityManager sm = new SecurityManager() {
            public void checkPermission(Permission perm) {
                if (perm.getName().equals("checkExec")) {
                    throw new SecurityException();
                }
            }

            public void checkExec(String cmd) {
                throw new SecurityException();
            }
        };
        try {
            Runtime.getRuntime().exec("", envp, workFolder);
            TestCase.fail("SecurityException should be thrown.");
        } catch (IllegalArgumentException iae) {
            // expected
        } catch (IOException e) {
            TestCase.fail("IOException was thrown.");
        }
    }

    public void test_execLjava_lang_StringArrayLjava_lang_StringArrayLjava_io_File() {
        String[] envp = getEnv();
        File workFolder = Support_Resources.createTempFolder();
        checkExec(5, envp, workFolder);
        checkExec(5, null, null);
        try {
            Runtime.getRuntime().exec(((String[]) (null)), null, workFolder);
            TestCase.fail("NullPointerException should be thrown.");
        } catch (IOException ioe) {
            TestCase.fail("IOException was thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            Runtime.getRuntime().exec(new String[]{ "ls", null }, null, workFolder);
            TestCase.fail("NullPointerException should be thrown.");
        } catch (IOException ioe) {
            TestCase.fail("IOException was thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
        SecurityManager sm = new SecurityManager() {
            public void checkPermission(Permission perm) {
                if (perm.getName().equals("checkExec")) {
                    throw new SecurityException();
                }
            }

            public void checkExec(String cmd) {
                throw new SecurityException();
            }
        };
        try {
            Runtime.getRuntime().exec(new String[]{ "" }, envp, workFolder);
            TestCase.fail("IOException should be thrown.");
        } catch (IOException e) {
            // expected
        }
    }

    public void test_execLjava_lang_String() {
        checkExec(1, null, null);
        try {
            Runtime.getRuntime().exec(((String) (null)));
            TestCase.fail("NullPointerException was not thrown.");
        } catch (NullPointerException npe) {
            // expected
        } catch (IOException e) {
            TestCase.fail("IOException was thrown.");
        }
        try {
            Runtime.getRuntime().exec("");
            TestCase.fail("IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException iae) {
            // expected
        } catch (IOException e) {
            TestCase.fail("IOException was thrown.");
        }
    }

    public void test_execLjava_lang_StringArray() {
        checkExec(3, null, null);
        try {
            Runtime.getRuntime().exec(((String[]) (null)));
            TestCase.fail("NullPointerException was not thrown.");
        } catch (NullPointerException npe) {
            // expected
        } catch (IOException e) {
            TestCase.fail("IOException was thrown.");
        }
        try {
            Runtime.getRuntime().exec(new String[]{ "ls", null });
            TestCase.fail("NullPointerException was not thrown.");
        } catch (NullPointerException npe) {
            // expected
        } catch (IOException e) {
            TestCase.fail("IOException was thrown.");
        }
        try {
            Runtime.getRuntime().exec(new String[]{  });
            TestCase.fail("IndexOutOfBoundsException was not thrown.");
        } catch (IndexOutOfBoundsException iobe) {
            // expected
        } catch (IOException e) {
            TestCase.fail("IOException was thrown.");
        }
        try {
            Runtime.getRuntime().exec(new String[]{ "" });
            TestCase.fail("IOException should be thrown.");
        } catch (IOException e) {
            // expected
        }
    }

    public void test_runFinalizersOnExit() {
        Runtime.getRuntime().runFinalizersOnExit(true);
    }

    public void test_removeShutdownHookLjava_lang_Thread() {
        Thread thr1 = new Thread() {
            public void run() {
                try {
                    Runtime.getRuntime().addShutdownHook(this);
                } catch (IllegalStateException ise) {
                    TestCase.fail("IllegalStateException shouldn't be thrown.");
                }
            }
        };
        try {
            Runtime.getRuntime().addShutdownHook(thr1);
            Runtime.getRuntime().removeShutdownHook(thr1);
        } catch (Throwable t) {
            TestCase.fail(t.getMessage());
        }
        Thread thr2 = new Thread() {
            public void run() {
                try {
                    Runtime.getRuntime().removeShutdownHook(this);
                    TestCase.fail("IllegalStateException wasn't thrown.");
                } catch (IllegalStateException ise) {
                    // expected
                }
            }
        };
        try {
            Runtime.getRuntime().addShutdownHook(thr2);
        } catch (Throwable t) {
            TestCase.fail(t.getMessage());
        }
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException ie) {
        }
    }

    public void test_maxMemory() {
        TestCase.assertTrue(((Runtime.getRuntime().maxMemory()) < (Long.MAX_VALUE)));
    }

    public void test_traceInstructions() {
        Runtime.getRuntime().traceInstructions(false);
        Runtime.getRuntime().traceInstructions(true);
        Runtime.getRuntime().traceInstructions(false);
    }

    public void test_traceMethodCalls() {
        try {
            Runtime.getRuntime().traceMethodCalls(false);
            Runtime.getRuntime().traceMethodCalls(true);
            Runtime.getRuntime().traceMethodCalls(false);
        } catch (RuntimeException ex) {
            // Slightly ugly: we default to the SD card, which may or may not
            // be there. So we also accept the error case as a success, since
            // it means we actually did enable tracing (or tried to).
            if (!("file open failed".equals(ex.getMessage()))) {
                throw ex;
            }
        }
    }

    public void test_load() {
        try {
            Runtime.getRuntime().load("nonExistentLibrary");
            TestCase.fail("UnsatisfiedLinkError was not thrown.");
        } catch (UnsatisfiedLinkError ule) {
            // expected
        }
        try {
            Runtime.getRuntime().load(null);
            TestCase.fail("NullPointerException was not thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    public void test_loadLibrary() {
        try {
            Runtime.getRuntime().loadLibrary("nonExistentLibrary");
            TestCase.fail("UnsatisfiedLinkError was not thrown.");
        } catch (UnsatisfiedLinkError ule) {
            // expected
        }
        try {
            Runtime.getRuntime().loadLibrary(null);
            TestCase.fail("NullPointerException was not thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
    }
}

