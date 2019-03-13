/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import java.io.ByteArrayOutputStream;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ParseCountingFileHttpBodyTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testWriteTo() throws Exception {
        final Semaphore didReportIntermediateProgress = new Semaphore(0);
        final Semaphore finish = new Semaphore(0);
        ParseCountingFileHttpBody body = new ParseCountingFileHttpBody(ParseCountingFileHttpBodyTest.makeTestFile(temporaryFolder.getRoot()), new ProgressCallback() {
            Integer maxProgressSoFar = 0;

            @Override
            public void done(Integer percentDone) {
                if (percentDone > (maxProgressSoFar)) {
                    maxProgressSoFar = percentDone;
                    Assert.assertTrue(((percentDone >= 0) && (percentDone <= 100)));
                    if ((percentDone < 100) && (percentDone > 0)) {
                        didReportIntermediateProgress.release();
                    } else
                        if (percentDone == 100) {
                            finish.release();
                        } else
                            if (percentDone == 0) {
                                // do nothing
                            } else {
                                Assert.fail("percentDone should be within 0 - 100");
                            }


                }
            }
        });
        // Check content
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        body.writeTo(output);
        Assert.assertArrayEquals(ParseCountingFileHttpBodyTest.getData().getBytes(), output.toByteArray());
        // Check progress callback
        Assert.assertTrue(didReportIntermediateProgress.tryAcquire(5, TimeUnit.SECONDS));
        Assert.assertTrue(finish.tryAcquire(5, TimeUnit.SECONDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteToWithNullOutput() throws Exception {
        ParseCountingFileHttpBody body = new ParseCountingFileHttpBody(ParseCountingFileHttpBodyTest.makeTestFile(temporaryFolder.getRoot()), null);
        body.writeTo(null);
    }
}

