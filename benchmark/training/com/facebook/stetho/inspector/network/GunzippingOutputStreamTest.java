/**
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.stetho.inspector.network;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class GunzippingOutputStreamTest {
    @Test(timeout = 1000)
    public void testGunzip() throws IOException {
        byte[] data = "test123test123".getBytes();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStream unzippingStream = GunzippingOutputStream.create(out);
        OutputStream zippingStream = new GZIPOutputStream(unzippingStream);
        zippingStream.write(data);
        zippingStream.close();
        Assert.assertArrayEquals(data, out.toByteArray());
    }
}

