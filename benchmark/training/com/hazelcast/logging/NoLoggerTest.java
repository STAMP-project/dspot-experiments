/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.logging;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.logging.Level;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NoLoggerTest extends AbstractLoggerTest {
    private ILogger logger;

    @Test
    public void finest_withMessage() {
        logger.finest(AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void finest_withThrowable() {
        logger.finest(AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void finest() {
        logger.finest(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void isFinestEnabled() {
        Assert.assertFalse(logger.isFinestEnabled());
    }

    @Test
    public void fine_withMessage() {
        logger.fine(AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void fine_withThrowable() {
        logger.fine(AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void fine() {
        logger.fine(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void isFineEnabled() {
        Assert.assertFalse(logger.isFineEnabled());
    }

    @Test
    public void info() {
        logger.info(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void info_withMessage() {
        logger.info(AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void info_withThrowable() {
        logger.info(AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void isInfoEnabled() {
        Assert.assertFalse(logger.isInfoEnabled());
    }

    @Test
    public void warning_withMessage() {
        logger.warning(AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void warning_withThrowable() {
        logger.warning(AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void warning() {
        logger.warning(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void isWarningEnabled() {
        Assert.assertFalse(logger.isWarningEnabled());
    }

    @Test
    public void severe_withMessage() {
        logger.severe(AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void severe_withThrowable() {
        logger.severe(AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void severe() {
        logger.severe(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void isSevereEnabled() {
        Assert.assertFalse(logger.isSevereEnabled());
    }

    @Test
    public void log_withMessage() {
        logger.log(Level.OFF, AbstractLoggerTest.MESSAGE);
        logger.log(Level.FINEST, AbstractLoggerTest.MESSAGE);
        logger.log(Level.FINER, AbstractLoggerTest.MESSAGE);
        logger.log(Level.FINE, AbstractLoggerTest.MESSAGE);
        logger.log(Level.INFO, AbstractLoggerTest.MESSAGE);
        logger.log(Level.WARNING, AbstractLoggerTest.MESSAGE);
        logger.log(Level.SEVERE, AbstractLoggerTest.MESSAGE);
        logger.log(Level.ALL, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void log() {
        logger.log(Level.OFF, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        logger.log(Level.FINEST, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        logger.log(Level.FINER, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        logger.log(Level.FINE, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        logger.log(Level.INFO, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        logger.log(Level.WARNING, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        logger.log(Level.SEVERE, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        logger.log(Level.ALL, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void logEvent() {
        logger.log(AbstractLoggerTest.LOG_EVENT);
    }

    @Test
    public void getLevel() {
        Assert.assertEquals(Level.OFF, logger.getLevel());
    }

    @Test
    public void isLoggable() {
        Assert.assertFalse(logger.isLoggable(Level.OFF));
        Assert.assertFalse(logger.isLoggable(Level.FINEST));
        Assert.assertFalse(logger.isLoggable(Level.FINER));
        Assert.assertFalse(logger.isLoggable(Level.FINE));
        Assert.assertFalse(logger.isLoggable(Level.INFO));
        Assert.assertFalse(logger.isLoggable(Level.WARNING));
        Assert.assertFalse(logger.isLoggable(Level.SEVERE));
        Assert.assertFalse(logger.isLoggable(Level.ALL));
    }
}

