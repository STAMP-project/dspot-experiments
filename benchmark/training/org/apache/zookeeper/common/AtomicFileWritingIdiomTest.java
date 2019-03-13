/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.common;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.common.AtomicFileWritingIdiom.OutputStreamStatement;
import org.apache.zookeeper.common.AtomicFileWritingIdiom.WriterStatement;
import org.junit.Assert;
import org.junit.Test;


public class AtomicFileWritingIdiomTest extends ZKTestCase {
    private static File tmpdir;

    @Test
    public void testOutputStreamSuccess() throws IOException {
        File target = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt");
        final File tmp = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt.tmp");
        createFile(target, "before");
        Assert.assertEquals("before", getContent(target));
        new AtomicFileWritingIdiom(target, new OutputStreamStatement() {
            @Override
            public void write(OutputStream os) throws IOException {
                os.write("after".getBytes("ASCII"));
                Assert.assertTrue("implementation of AtomicFileOutputStream has changed, update the test", tmp.exists());
            }
        });
        Assert.assertFalse("tmp file should have been deleted", tmp.exists());
        // content changed
        Assert.assertEquals("after", getContent(target));
        target.delete();
    }

    @Test
    public void testWriterSuccess() throws IOException {
        File target = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt");
        final File tmp = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt.tmp");
        createFile(target, "before");
        Assert.assertEquals("before", getContent(target));
        new AtomicFileWritingIdiom(target, new WriterStatement() {
            @Override
            public void write(Writer os) throws IOException {
                os.write("after");
                Assert.assertTrue("implementation of AtomicFileOutputStream has changed, update the test", tmp.exists());
            }
        });
        Assert.assertFalse("tmp file should have been deleted", tmp.exists());
        // content changed
        Assert.assertEquals("after", getContent(target));
        target.delete();
    }

    @Test
    public void testOutputStreamFailure() throws IOException {
        File target = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt");
        final File tmp = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt.tmp");
        createFile(target, "before");
        Assert.assertEquals("before", getContent(target));
        boolean exception = false;
        try {
            new AtomicFileWritingIdiom(target, new OutputStreamStatement() {
                @Override
                public void write(OutputStream os) throws IOException {
                    os.write("after".getBytes("ASCII"));
                    os.flush();
                    Assert.assertTrue("implementation of AtomicFileOutputStream has changed, update the test", tmp.exists());
                    throw new RuntimeException();
                }
            });
        } catch (RuntimeException ex) {
            exception = true;
        }
        Assert.assertFalse("tmp file should have been deleted", tmp.exists());
        Assert.assertTrue("should have raised an exception", exception);
        // content preserved
        Assert.assertEquals("before", getContent(target));
        target.delete();
    }

    @Test
    public void testWriterFailure() throws IOException {
        File target = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt");
        final File tmp = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt.tmp");
        createFile(target, "before");
        Assert.assertEquals("before", getContent(target));
        boolean exception = false;
        try {
            new AtomicFileWritingIdiom(target, new WriterStatement() {
                @Override
                public void write(Writer os) throws IOException {
                    os.write("after");
                    os.flush();
                    Assert.assertTrue("implementation of AtomicFileOutputStream has changed, update the test", tmp.exists());
                    throw new RuntimeException();
                }
            });
        } catch (RuntimeException ex) {
            exception = true;
        }
        Assert.assertFalse("tmp file should have been deleted", tmp.exists());
        Assert.assertTrue("should have raised an exception", exception);
        // content preserved
        Assert.assertEquals("before", getContent(target));
        target.delete();
    }

    @Test
    public void testOutputStreamFailureIOException() throws IOException {
        File target = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt");
        final File tmp = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt.tmp");
        createFile(target, "before");
        Assert.assertEquals("before", getContent(target));
        boolean exception = false;
        try {
            new AtomicFileWritingIdiom(target, new OutputStreamStatement() {
                @Override
                public void write(OutputStream os) throws IOException {
                    os.write("after".getBytes("ASCII"));
                    os.flush();
                    Assert.assertTrue("implementation of AtomicFileOutputStream has changed, update the test", tmp.exists());
                    throw new IOException();
                }
            });
        } catch (IOException ex) {
            exception = true;
        }
        Assert.assertFalse("tmp file should have been deleted", tmp.exists());
        Assert.assertTrue("should have raised an exception", exception);
        // content preserved
        Assert.assertEquals("before", getContent(target));
        target.delete();
    }

    @Test
    public void testWriterFailureIOException() throws IOException {
        File target = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt");
        final File tmp = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt.tmp");
        createFile(target, "before");
        Assert.assertEquals("before", getContent(target));
        boolean exception = false;
        try {
            new AtomicFileWritingIdiom(target, new WriterStatement() {
                @Override
                public void write(Writer os) throws IOException {
                    os.write("after");
                    os.flush();
                    Assert.assertTrue("implementation of AtomicFileOutputStream has changed, update the test", tmp.exists());
                    throw new IOException();
                }
            });
        } catch (IOException ex) {
            exception = true;
        }
        Assert.assertFalse("tmp file should have been deleted", tmp.exists());
        Assert.assertTrue("should have raised an exception", exception);
        // content preserved
        Assert.assertEquals("before", getContent(target));
        target.delete();
    }

    @Test
    public void testOutputStreamFailureError() throws IOException {
        File target = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt");
        final File tmp = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt.tmp");
        createFile(target, "before");
        Assert.assertEquals("before", getContent(target));
        boolean exception = false;
        try {
            new AtomicFileWritingIdiom(target, new OutputStreamStatement() {
                @Override
                public void write(OutputStream os) throws IOException {
                    os.write("after".getBytes("ASCII"));
                    os.flush();
                    Assert.assertTrue("implementation of AtomicFileOutputStream has changed, update the test", tmp.exists());
                    throw new Error();
                }
            });
        } catch (Error ex) {
            exception = true;
        }
        Assert.assertFalse("tmp file should have been deleted", tmp.exists());
        Assert.assertTrue("should have raised an exception", exception);
        // content preserved
        Assert.assertEquals("before", getContent(target));
        target.delete();
    }

    @Test
    public void testWriterFailureError() throws IOException {
        File target = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt");
        final File tmp = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt.tmp");
        createFile(target, "before");
        Assert.assertEquals("before", getContent(target));
        boolean exception = false;
        try {
            new AtomicFileWritingIdiom(target, new WriterStatement() {
                @Override
                public void write(Writer os) throws IOException {
                    os.write("after");
                    os.flush();
                    Assert.assertTrue("implementation of AtomicFileOutputStream has changed, update the test", tmp.exists());
                    throw new Error();
                }
            });
        } catch (Error ex) {
            exception = true;
        }
        Assert.assertFalse("tmp file should have been deleted", tmp.exists());
        Assert.assertTrue("should have raised an exception", exception);
        // content preserved
        Assert.assertEquals("before", getContent(target));
        target.delete();
    }

    // ************** target file does not exist
    @Test
    public void testOutputStreamSuccessNE() throws IOException {
        File target = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt");
        final File tmp = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt.tmp");
        target.delete();
        Assert.assertFalse("file should not exist", target.exists());
        new AtomicFileWritingIdiom(target, new OutputStreamStatement() {
            @Override
            public void write(OutputStream os) throws IOException {
                os.write("after".getBytes("ASCII"));
                Assert.assertTrue("implementation of AtomicFileOutputStream has changed, update the test", tmp.exists());
            }
        });
        // content changed
        Assert.assertEquals("after", getContent(target));
        target.delete();
    }

    @Test
    public void testWriterSuccessNE() throws IOException {
        File target = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt");
        final File tmp = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt.tmp");
        target.delete();
        Assert.assertFalse("file should not exist", target.exists());
        new AtomicFileWritingIdiom(target, new WriterStatement() {
            @Override
            public void write(Writer os) throws IOException {
                os.write("after");
                Assert.assertTrue("implementation of AtomicFileOutputStream has changed, update the test", tmp.exists());
            }
        });
        Assert.assertFalse("tmp file should have been deleted", tmp.exists());
        // content changed
        Assert.assertEquals("after", getContent(target));
        target.delete();
    }

    @Test
    public void testOutputStreamFailureNE() throws IOException {
        File target = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt");
        final File tmp = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt.tmp");
        target.delete();
        Assert.assertFalse("file should not exist", target.exists());
        boolean exception = false;
        try {
            new AtomicFileWritingIdiom(target, new OutputStreamStatement() {
                @Override
                public void write(OutputStream os) throws IOException {
                    os.write("after".getBytes("ASCII"));
                    os.flush();
                    Assert.assertTrue("implementation of AtomicFileOutputStream has changed, update the test", tmp.exists());
                    throw new RuntimeException();
                }
            });
        } catch (RuntimeException ex) {
            exception = true;
        }
        Assert.assertFalse("tmp file should have been deleted", tmp.exists());
        Assert.assertTrue("should have raised an exception", exception);
        // file should not exist
        Assert.assertFalse("file should not exist", target.exists());
    }

    @Test
    public void testWriterFailureNE() throws IOException {
        File target = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt");
        final File tmp = new File(AtomicFileWritingIdiomTest.tmpdir, "target.txt.tmp");
        target.delete();
        Assert.assertFalse("file should not exist", target.exists());
        boolean exception = false;
        try {
            new AtomicFileWritingIdiom(target, new WriterStatement() {
                @Override
                public void write(Writer os) throws IOException {
                    os.write("after");
                    os.flush();
                    Assert.assertTrue("implementation of AtomicFileOutputStream has changed, update the test", tmp.exists());
                    throw new RuntimeException();
                }
            });
        } catch (RuntimeException ex) {
            exception = true;
        }
        Assert.assertFalse("tmp file should have been deleted", tmp.exists());
        Assert.assertTrue("should have raised an exception", exception);
        // file should not exist
        Assert.assertFalse("file should not exist", target.exists());
    }
}

