package org.apache.commons.io.output;


import java.io.IOException;
import java.io.OutputStream;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class AmplBrokenOutputStreamTest {
    private IOException exception;

    private OutputStream stream;

    @Before
    public void setUp() {
        exception = new IOException("test exception");
        stream = new BrokenOutputStream(exception);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg466_failAssert7() throws Exception {
        try {
            BrokenOutputStream bos = new BrokenOutputStream();
            try {
                bos.write(123);
            } catch (IOException e) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bos.close();
            org.junit.Assert.fail("testSelfSupressed_mg466 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken output stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg11_failAssert2() throws Exception {
        try {
            int __DSPOT_b_476 = -403121184;
            BrokenOutputStream bos = new BrokenOutputStream();
            try {
                bos.write(123);
            } catch (IOException e) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bos.write(__DSPOT_b_476);
            org.junit.Assert.fail("testSelfSupressed_mg11 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken output stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg10_failAssert1() throws Exception {
        try {
            BrokenOutputStream bos = new BrokenOutputStream();
            try {
                bos.write(123);
            } catch (IOException e) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bos.flush();
            org.junit.Assert.fail("testSelfSupressed_mg10 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken output stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg468_failAssert9() throws Exception {
        try {
            int __DSPOT_b_484 = 1860836987;
            BrokenOutputStream bos = new BrokenOutputStream();
            try {
                bos.write(123);
            } catch (IOException e) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bos.write(__DSPOT_b_484);
            org.junit.Assert.fail("testSelfSupressed_mg468 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken output stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg9_failAssert0() throws Exception {
        try {
            BrokenOutputStream bos = new BrokenOutputStream();
            try {
                bos.write(123);
            } catch (IOException e) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bos.close();
            org.junit.Assert.fail("testSelfSupressed_mg9 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken output stream", expected_2.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 10000)
    public void testSelfSupressed_mg467_failAssert8() throws Exception {
        try {
            BrokenOutputStream bos = new BrokenOutputStream();
            try {
                bos.write(123);
            } catch (IOException e) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.addSuppressed(e);
                }
            }
            bos.flush();
            org.junit.Assert.fail("testSelfSupressed_mg467 should have thrown IOException");
        } catch (IOException expected_2) {
            Assert.assertEquals("Broken output stream", expected_2.getMessage());
        }
    }
}

