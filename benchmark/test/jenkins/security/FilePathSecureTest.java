/**
 * The MIT License
 *
 * Copyright 2014 Jesse Glick.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.security;


import FilePath.TarCompression.NONE;
import hudson.FilePath;
import hudson.slaves.DumbSlave;
import hudson.util.DirScanner;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


public class FilePathSecureTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    private DumbSlave s;

    private FilePath root;

    private FilePath remote;

    @Test
    public void unzip() throws Exception {
        FilePath dir = root.child("dir");
        dir.mkdirs();
        dir.child("stuff").write("hello", null);
        FilePath zip = root.child("dir.zip");
        dir.zip(zip);
        zip.unzip(remote);
        Assert.assertEquals("hello", remote.child("dir/stuff").readToString());
    }

    @Test
    public void untar() throws Exception {
        FilePath dir = root.child("dir");
        dir.mkdirs();
        dir.child("stuff").write("hello", null);
        FilePath tar = root.child("dir.tar");
        OutputStream os = tar.write();
        try {
            dir.tar(os, new DirScanner.Full());
        } finally {
            os.close();
        }
        tar.untar(remote, NONE);
        Assert.assertEquals("hello", remote.child("dir/stuff").readToString());
    }

    @Test
    public void zip() throws Exception {
        FilePath dir = remote.child("dir");
        dir.mkdirs();
        dir.child("stuff").write("hello", null);
        FilePath zip = root.child("dir.zip");
        dir.zip(zip);
        zip.unzip(root);
        Assert.assertEquals("hello", remote.child("dir/stuff").readToString());
    }

    @Test
    public void tar() throws Exception {
        FilePath dir = remote.child("dir");
        dir.mkdirs();
        dir.child("stuff").write("hello", null);
        FilePath tar = root.child("dir.tar");
        OutputStream os = tar.write();
        try {
            dir.tar(os, new DirScanner.Full());
        } finally {
            os.close();
        }
        tar.untar(root, NONE);
        Assert.assertEquals("hello", remote.child("dir/stuff").readToString());
    }
}

