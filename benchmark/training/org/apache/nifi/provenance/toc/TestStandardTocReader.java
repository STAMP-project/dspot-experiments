/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.provenance.toc;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class TestStandardTocReader {
    @Test
    public void testDetectsCompression() throws IOException {
        final File file = new File(("target/" + (UUID.randomUUID().toString())));
        try (final OutputStream out = new FileOutputStream(file)) {
            out.write(0);
            out.write(0);
        }
        try {
            try (final StandardTocReader reader = new StandardTocReader(file)) {
                Assert.assertFalse(reader.isCompressed());
            }
        } finally {
            file.delete();
        }
        try (final OutputStream out = new FileOutputStream(file)) {
            out.write(0);
            out.write(1);
        }
        try {
            try (final StandardTocReader reader = new StandardTocReader(file)) {
                Assert.assertTrue(reader.isCompressed());
            }
        } finally {
            file.delete();
        }
    }

    @Test
    public void testGetBlockIndexV1() throws IOException {
        final File file = new File(("target/" + (UUID.randomUUID().toString())));
        try (final OutputStream out = new FileOutputStream(file);final DataOutputStream dos = new DataOutputStream(out)) {
            out.write(1);
            out.write(0);
            for (int i = 0; i < 1024; i++) {
                dos.writeLong((i * 1024L));
            }
        }
        try {
            try (final StandardTocReader reader = new StandardTocReader(file)) {
                Assert.assertFalse(reader.isCompressed());
                for (int i = 0; i < 1024; i++) {
                    Assert.assertEquals((i * 1024), reader.getBlockOffset(i));
                }
            }
        } finally {
            file.delete();
        }
    }

    @Test
    public void testGetBlockIndexV2() throws IOException {
        final File file = new File(("target/" + (UUID.randomUUID().toString())));
        try (final OutputStream out = new FileOutputStream(file);final DataOutputStream dos = new DataOutputStream(out)) {
            out.write(2);
            out.write(0);
            for (int i = 0; i < 1024; i++) {
                dos.writeLong((i * 1024L));
                dos.writeLong(0L);
            }
        }
        try {
            try (final StandardTocReader reader = new StandardTocReader(file)) {
                Assert.assertFalse(reader.isCompressed());
                for (int i = 0; i < 1024; i++) {
                    Assert.assertEquals((i * 1024), reader.getBlockOffset(i));
                }
            }
        } finally {
            file.delete();
        }
    }
}

