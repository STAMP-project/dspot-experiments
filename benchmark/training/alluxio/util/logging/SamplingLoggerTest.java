/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.util.logging;


import alluxio.Constants;
import alluxio.util.CommonUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;


/**
 * Unit tests for {@link SamplingLogger}.
 */
public class SamplingLoggerTest {
    private Logger mBaseLogger;

    private SamplingLogger mSamplingLogger;

    @Test
    public void sampleSingle() {
        setupLogger((10 * (Constants.SECOND_MS)));
        Mockito.doReturn(true).when(mBaseLogger).isWarnEnabled();
        for (int i = 0; i < 10; i++) {
            mSamplingLogger.warn("warning");
        }
        Mockito.verify(mBaseLogger, Mockito.times(1)).warn("warning");
    }

    @Test
    public void sampleMultiple() {
        setupLogger((10 * (Constants.SECOND_MS)));
        Mockito.doReturn(true).when(mBaseLogger).isWarnEnabled();
        for (int i = 0; i < 10; i++) {
            mSamplingLogger.warn("warning1");
            mSamplingLogger.warn("warning2");
        }
        Mockito.verify(mBaseLogger, Mockito.times(1)).warn("warning1");
        Mockito.verify(mBaseLogger, Mockito.times(1)).warn("warning2");
    }

    @Test
    public void sampleAfterCooldown() {
        setupLogger(1);
        Mockito.doReturn(true).when(mBaseLogger).isWarnEnabled();
        for (int i = 0; i < 10; i++) {
            mSamplingLogger.warn("warning1");
            mSamplingLogger.warn("warning2");
            CommonUtils.sleepMs(2);
        }
        Mockito.verify(mBaseLogger, Mockito.times(10)).warn("warning1");
        Mockito.verify(mBaseLogger, Mockito.times(10)).warn("warning2");
    }
}

