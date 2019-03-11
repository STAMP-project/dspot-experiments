/**
 * Copyright 2015 Radius Networks, Inc.
 * Copyright 2015 Andrew Reitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.altbeacon.beacon.logging;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Ensure correct instances are returned from factory methods.
 *
 * @author Andrew Reitz
 */
public class LoggersTest {
    @Test
    public void loggersReturnsVerboseInstance() {
        Logger logger = Loggers.verboseLogger();
        Assert.assertThat(logger, Matchers.instanceOf(VerboseAndroidLogger.class));
    }

    @Test
    public void verboseLoggerReturnsSameInstance() {
        Logger logger1 = Loggers.verboseLogger();
        Logger logger2 = Loggers.verboseLogger();
        Assert.assertThat(logger1, Matchers.sameInstance(logger2));
    }

    @Test
    public void loggersReturnsEmptyInstance() {
        Logger logger = Loggers.empty();
        Assert.assertThat(logger, Matchers.instanceOf(EmptyLogger.class));
    }

    @Test
    public void emptyLoggerReturnsSameInstance() {
        Logger logger1 = Loggers.empty();
        Logger logger2 = Loggers.empty();
        Assert.assertThat(logger1, Matchers.sameInstance(logger2));
    }

    @Test
    public void loggersReturnsWarningLoggerInstance() {
        Logger logger = Loggers.warningLogger();
        Assert.assertThat(logger, Matchers.instanceOf(WarningAndroidLogger.class));
    }

    @Test
    public void warningLoggerReturnsSameInstance() {
        Logger logger1 = Loggers.warningLogger();
        Logger logger2 = Loggers.warningLogger();
        Assert.assertThat(logger1, Matchers.sameInstance(logger2));
    }
}

