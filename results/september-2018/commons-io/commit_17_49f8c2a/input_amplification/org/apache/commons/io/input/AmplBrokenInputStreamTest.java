package org.apache.commons.io.input;


import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class AmplBrokenInputStreamTest {
    private IOException exception;

    private InputStream stream;

    @Before
    public void setUp() {
        exception = new IOException("test exception");
        stream = new BrokenInputStream(exception);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg533_failAssert15() throws Exception {
        try {
            long __DSPOT_n_12 = -582027917L;
            BrokenInputStream bis = new BrokenInputStream();
            try {
                bis.read();
            } catch (IOException e) {
                try {
                    bis.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bis.skip(__DSPOT_n_12);
            org.junit.Assert.fail("testSelfSupressed_mg533 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken input stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg4_failAssert0() throws Exception {
        try {
            BrokenInputStream bis = new BrokenInputStream();
            try {
                bis.read();
            } catch (IOException e) {
                try {
                    bis.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bis.available();
            org.junit.Assert.fail("testSelfSupressed_mg4 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken input stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg531_failAssert13() throws Exception {
        try {
            BrokenInputStream bis = new BrokenInputStream();
            try {
                bis.read();
            } catch (IOException e) {
                try {
                    bis.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bis.read();
            org.junit.Assert.fail("testSelfSupressed_mg531 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken input stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg532_failAssert14() throws Exception {
        try {
            BrokenInputStream bis = new BrokenInputStream();
            try {
                bis.read();
            } catch (IOException e) {
                try {
                    bis.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bis.reset();
            org.junit.Assert.fail("testSelfSupressed_mg532 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken input stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg8_failAssert4() throws Exception {
        try {
            long __DSPOT_n_0 = -1150482841L;
            BrokenInputStream bis = new BrokenInputStream();
            try {
                bis.read();
            } catch (IOException e) {
                try {
                    bis.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bis.skip(__DSPOT_n_0);
            org.junit.Assert.fail("testSelfSupressed_mg8 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken input stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg7_failAssert3() throws Exception {
        try {
            BrokenInputStream bis = new BrokenInputStream();
            try {
                bis.read();
            } catch (IOException e) {
                try {
                    bis.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bis.reset();
            org.junit.Assert.fail("testSelfSupressed_mg7 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken input stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg6_failAssert2() throws Exception {
        try {
            BrokenInputStream bis = new BrokenInputStream();
            try {
                bis.read();
            } catch (IOException e) {
                try {
                    bis.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bis.read();
            org.junit.Assert.fail("testSelfSupressed_mg6 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken input stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg530_failAssert12() throws Exception {
        try {
            BrokenInputStream bis = new BrokenInputStream();
            try {
                bis.read();
            } catch (IOException e) {
                try {
                    bis.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bis.close();
            org.junit.Assert.fail("testSelfSupressed_mg530 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken input stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg529_failAssert11() throws Exception {
        try {
            BrokenInputStream bis = new BrokenInputStream();
            try {
                bis.read();
            } catch (IOException e) {
                try {
                    bis.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bis.available();
            org.junit.Assert.fail("testSelfSupressed_mg529 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken input stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg5_failAssert1() throws Exception {
        try {
            BrokenInputStream bis = new BrokenInputStream();
            try {
                bis.read();
            } catch (IOException e) {
                try {
                    bis.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bis.close();
            org.junit.Assert.fail("testSelfSupressed_mg5 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken input stream", expected_2.getMessage());
        }
    }
}

