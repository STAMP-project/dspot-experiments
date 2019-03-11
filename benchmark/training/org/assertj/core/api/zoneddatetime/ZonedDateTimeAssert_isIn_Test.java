/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api.zoneddatetime;


import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests specific to {@link org.assertj.core.api.ZonedDateTimeAssert#isIn(ZonedDateTime...)} that can't be done
 * in {@link org.assertj.core.api.AbstractAssert#isIn(Object...)} tests.
 *
 * @author Joel Costigliola
 * @author Marcin Zaj?czkowski
 */
public class ZonedDateTimeAssert_isIn_Test extends ZonedDateTimeAssertBaseTest {
    @Test
    public void isIn_should_compare_datetimes_in_actual_timezone() {
        ZonedDateTime utcDateTime = ZonedDateTime.of(2013, 6, 10, 0, 0, 0, 0, ZoneOffset.UTC);
        ZoneId cestTimeZone = ZoneId.of("Europe/Berlin");
        ZonedDateTime cestDateTime = ZonedDateTime.of(2013, 6, 10, 2, 0, 0, 0, cestTimeZone);
        // cestDateTime and utcDateTime are equals in same timezone
        Assertions.assertThat(utcDateTime).isIn(cestDateTime, ZonedDateTime.now());
    }
}

