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
package org.assertj.core.util;


import java.util.Date;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link org.assertj.core.util.DateUtil#timeDifference(java.util.Date, java.util.Date)}</code>.
 *
 * @author Joel Costigliola
 */
public class DateUtil_timeDifference_Test {
    @Test
    public void should_return_dates_time_difference() {
        final Date date1 = DateUtil.parseDatetimeWithMs("2003-04-26T03:01:02.999");
        final Date date2 = DateUtil.parseDatetimeWithMs("2003-04-26T03:01:02.888");
        Assertions.assertThat(DateUtil.timeDifference(date1, date2)).isEqualTo(111);
        Assertions.assertThat(DateUtil.timeDifference(date2, date1)).isEqualTo(DateUtil.timeDifference(date1, date2));
    }

    @Test
    public void should_throws_IllegalArgumentException_if_first_date_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> timeDifference(new Date(), null)).withMessage("Expecting date parameter not to be null");
    }

    @Test
    public void should_throws_IllegalArgumentException_if_second_date_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> timeDifference(null, new Date())).withMessage("Expecting date parameter not to be null");
    }
}

