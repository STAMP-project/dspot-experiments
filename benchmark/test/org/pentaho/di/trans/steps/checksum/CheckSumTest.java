/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.checksum;


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBinary;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.RowAdapter;


public class CheckSumTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static Object previousKettleDefaultNumberFormat;

    private class MockRowListener extends RowAdapter {
        private List<Object[]> written;

        private List<Object[]> read;

        private List<Object[]> error;

        public MockRowListener() {
            written = new ArrayList<Object[]>();
            read = new ArrayList<Object[]>();
            error = new ArrayList<Object[]>();
        }

        public List<Object[]> getWritten() {
            return written;
        }

        @Override
        public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
            written.add(row);
        }

        @Override
        public void rowReadEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
            read.add(row);
        }

        @Override
        public void errorRowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
            error.add(row);
        }
    }

    @Test
    public void testHexOutput_md5() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(2, false, "xyz", new ValueMetaString("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("d16fb36f0911f878998c136191af705e", results.getWritten().get(0)[1]);
        results = executeHexTest(2, false, 10.8, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("372df98e33ac1bf6b26d225361ba7eb5", results.getWritten().get(0)[1]);
        results = executeHexTest(2, false, 10.82, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("68b142f87143c917f29d178aa1715957", results.getWritten().get(0)[1]);
        byte[] input = IOUtils.toByteArray(getFile("/org/pentaho/di/trans/steps/loadfileinput/files/pentaho_splash.png").getContent().getInputStream());
        results = executeHexTest(2, false, input, new ValueMetaBinary("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("8d808ff9051fdbfd8050f762daddf813", results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_md5_oldChecksumBehaviourMode() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(2, false, "xyz", new ValueMetaString("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("d16fb36f0911f878998c136191af705e", results.getWritten().get(0)[1]);
        results = executeHexTest(2, false, 10.8, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("372df98e33ac1bf6b26d225361ba7eb5", results.getWritten().get(0)[1]);
        results = executeHexTest(2, false, 10.82, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("68b142f87143c917f29d178aa1715957", results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_md5_compatibilityMode() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(2, true, "xyz", new ValueMetaString("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("FD6FFD6F0911FD78FDFD1361FDFD705E", results.getWritten().get(0)[1]);
        results = executeHexTest(2, true, 10.8, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("372DFDFD33FD1BFDFD6D225361FD7EFD", results.getWritten().get(0)[1]);
        results = executeHexTest(2, true, 10.82, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("68FD42FD7143FD17FD17FDFD715957", results.getWritten().get(0)[1]);
        byte[] input = IOUtils.toByteArray(getFile("/org/pentaho/di/trans/steps/loadfileinput/files/pentaho_splash.png").getContent().getInputStream());
        results = executeHexTest(2, true, input, new ValueMetaBinary("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("FDFDFDFD051FFDFDFD50FD62FDFDFD13", results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_md5_compatibilityMode_oldChecksumBehaviourMode() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(2, true, "xyz", new ValueMetaString("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("FD6FFD6F0911FD78FDFD1361FDFD705E", results.getWritten().get(0)[1]);
        results = executeHexTest(2, true, 10.8, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("372DFDFD33FD1BFDFD6D225361FD7EFD", results.getWritten().get(0)[1]);
        results = executeHexTest(2, true, 10.82, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("68FD42FD7143FD17FD17FDFD715957", results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_sha1() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(3, false, "xyz", new ValueMetaString("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("66b27417d37e024c46526c2f6d358a754fc552f3", results.getWritten().get(0)[1]);
        results = executeHexTest(3, false, 10.8, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("78aef53da0b8d7a80656c80aa35ad6d410b7f068", results.getWritten().get(0)[1]);
        results = executeHexTest(3, false, 10.82, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("749f3d4c2db67c9f3186563a72ef5da9461f0496", results.getWritten().get(0)[1]);
        byte[] input = IOUtils.toByteArray(getFile("/org/pentaho/di/trans/steps/loadfileinput/files/pentaho_splash.png").getContent().getInputStream());
        results = executeHexTest(3, false, input, new ValueMetaBinary("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("e67d0b5b60663b8a5e0df1d23b44de673738315a", results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_sha1_oldChecksumBehaviourMode() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(3, false, "xyz", new ValueMetaString("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("66b27417d37e024c46526c2f6d358a754fc552f3", results.getWritten().get(0)[1]);
        results = executeHexTest(3, false, 10.8, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("78aef53da0b8d7a80656c80aa35ad6d410b7f068", results.getWritten().get(0)[1]);
        results = executeHexTest(3, false, 10.82, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("749f3d4c2db67c9f3186563a72ef5da9461f0496", results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_sha1_compatibilityMode() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(3, true, "xyz", new ValueMetaString("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("66FD7417FD7E024C46526C2F6D35FD754FFD52FD", results.getWritten().get(0)[1]);
        results = executeHexTest(3, true, 10.8, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("78FDFD3DFDFDE80656FD0AFD5AFDFD10FDFD68", results.getWritten().get(0)[1]);
        results = executeHexTest(3, true, 10.82, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("74FD3D4C2DFD7CFD31FD563A72FD5DFD461F04FD", results.getWritten().get(0)[1]);
        byte[] input = IOUtils.toByteArray(getFile("/org/pentaho/di/trans/steps/loadfileinput/files/pentaho_splash.png").getContent().getInputStream());
        results = executeHexTest(3, true, input, new ValueMetaBinary("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("FD7D0B5B60663BFD5E0DFDFD3B44FD673738315A", results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_sha1_compatibilityMode_oldChecksumBehaviourMode() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(3, true, "xyz", new ValueMetaString("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("66FD7417FD7E024C46526C2F6D35FD754FFD52FD", results.getWritten().get(0)[1]);
        results = executeHexTest(3, true, 10.8, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("78FDFD3DFDFDE80656FD0AFD5AFDFD10FDFD68", results.getWritten().get(0)[1]);
        results = executeHexTest(3, true, 10.82, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("74FD3D4C2DFD7CFD31FD563A72FD5DFD461F04FD", results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_sha256() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(4, false, "xyz", new ValueMetaString("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("3608bca1e44ea6c4d268eb6db02260269892c0b42b86bbf1e77a6fa16c3c9282", results.getWritten().get(0)[1]);
        results = executeHexTest(4, false, 10.8, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("b52b603f9ec86c382a8483cad4f788f2f927535a76ad1388caedcef5e3c3c813", results.getWritten().get(0)[1]);
        results = executeHexTest(4, false, 10.82, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("45cbb96ff9625490cd675a7a39fecad6c167c1ed9b8957f53224fcb3e4a1e4a1", results.getWritten().get(0)[1]);
        byte[] input = IOUtils.toByteArray(getFile("/org/pentaho/di/trans/steps/loadfileinput/files/pentaho_splash.png").getContent().getInputStream());
        results = executeHexTest(4, false, input, new ValueMetaBinary("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("6914d0cb9296d658569570c23924ea4822be73f0ee3bc46d11651fb4041a43e1", results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_sha256_oldChecksumBehaviourMode() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(4, false, "xyz", new ValueMetaString("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("3608bca1e44ea6c4d268eb6db02260269892c0b42b86bbf1e77a6fa16c3c9282", results.getWritten().get(0)[1]);
        results = executeHexTest(4, false, 10.8, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("b52b603f9ec86c382a8483cad4f788f2f927535a76ad1388caedcef5e3c3c813", results.getWritten().get(0)[1]);
        results = executeHexTest(4, false, 10.82, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals("45cbb96ff9625490cd675a7a39fecad6c167c1ed9b8957f53224fcb3e4a1e4a1", results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_adler32() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(1, false, "xyz", new ValueMetaString("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("47645036"), results.getWritten().get(0)[1]);
        results = executeHexTest(1, false, 10.8, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("32243912"), results.getWritten().get(0)[1]);
        results = executeHexTest(1, false, 10.82, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("48627962"), results.getWritten().get(0)[1]);
        byte[] input = IOUtils.toByteArray(getFile("/org/pentaho/di/trans/steps/loadfileinput/files/pentaho_splash.png").getContent().getInputStream());
        results = executeHexTest(1, false, input, new ValueMetaBinary("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("1586189688"), results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_adler32_oldChecksumBehaviourMode() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(1, false, "xyz", new ValueMetaString("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("47645036"), results.getWritten().get(0)[1]);
        results = executeHexTest(1, false, 10.8, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("32243912"), results.getWritten().get(0)[1]);
        results = executeHexTest(1, false, 10.82, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("48627962"), results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_crc32() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(0, false, "xyz", new ValueMetaString("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("3951999591"), results.getWritten().get(0)[1]);
        results = executeHexTest(0, false, 10.8, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("1857885434"), results.getWritten().get(0)[1]);
        results = executeHexTest(0, false, 10.82, new ValueMetaNumber("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("1205016603"), results.getWritten().get(0)[1]);
        byte[] input = IOUtils.toByteArray(getFile("/org/pentaho/di/trans/steps/loadfileinput/files/pentaho_splash.png").getContent().getInputStream());
        results = executeHexTest(0, false, input, new ValueMetaBinary("test"), false);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("1508231614"), results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_crc32_oldChecksumBehaviourMode() throws Exception {
        CheckSumTest.MockRowListener results = executeHexTest(0, false, "xyz", new ValueMetaString("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("3951999591"), results.getWritten().get(0)[1]);
        results = executeHexTest(0, false, 10.8, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("1857885434"), results.getWritten().get(0)[1]);
        results = executeHexTest(0, false, 10.82, new ValueMetaNumber("test"), true);
        Assert.assertEquals(1, results.getWritten().size());
        Assert.assertEquals(Long.valueOf("1205016603"), results.getWritten().get(0)[1]);
    }

    @Test
    public void testHexOutput_sha256_compatibilityMode() throws Exception {
        try {
            executeHexTest(4, true, "xyz", new ValueMetaString("test"), false);
            Assert.fail();
        } catch (KettleException e) {
            // expected, SHA-256 is not supported for compatibility mode
        }
    }

    @Test
    public void testHexOutput_sha256_compatibilityMode_oldChecksumBehaviourMode() throws Exception {
        try {
            executeHexTest(4, true, "xyz", new ValueMetaString("test"), true);
            Assert.fail();
        } catch (KettleException e) {
            // expected, SHA-256 is not supported for compatibility mode
        }
    }
}

