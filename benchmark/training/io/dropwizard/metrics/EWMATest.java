/**
 * Copyright 2010-2013 Coda Hale and Yammer, Inc., 2014-2017 Dropwizard Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dropwizard.metrics;


import java.util.concurrent.TimeUnit;
import org.junit.Test;


public class EWMATest {
    @Test
    public void aOneMinuteEWMAWithAValueOfThree() throws Exception {
        final EWMA ewma = EWMA.oneMinuteEWMA();
        ewma.update(3);
        ewma.tick();
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.6, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.22072766, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.08120117, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.02987224, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.01098938, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.00404277, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.00148725, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(5.4713E-4, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(2.0128E-4, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(7.405E-5, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(2.724E-5, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(1.002E-5, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(3.69E-6, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(1.36E-6, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(5.0E-7, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(1.8E-7, offset(1.0E-6));
    }

    @Test
    public void aFiveMinuteEWMAWithAValueOfThree() throws Exception {
        final EWMA ewma = EWMA.fiveMinuteEWMA();
        ewma.update(3);
        ewma.tick();
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.6, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.49123845, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.40219203, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.32928698, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.26959738, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.22072766, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.18071653, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.14795818, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.12113791, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.09917933, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.08120117, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.0664819, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.05443077, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.04456415, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.03648604, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.02987224, offset(1.0E-6));
    }

    @Test
    public void aFifteenMinuteEWMAWithAValueOfThree() throws Exception {
        final EWMA ewma = EWMA.fifteenMinuteEWMA();
        ewma.update(3);
        ewma.tick();
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.6, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.56130419, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.52510399, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.49123845, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.459557, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.42991879, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.40219203, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.37625345, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.35198773, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.32928698, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.30805027, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.28818318, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.26959738, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.25221023, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.23594443, offset(1.0E-6));
        elapseMinute(ewma);
        assertThat(ewma.getRate(TimeUnit.SECONDS)).isEqualTo(0.22072766, offset(1.0E-6));
    }
}

