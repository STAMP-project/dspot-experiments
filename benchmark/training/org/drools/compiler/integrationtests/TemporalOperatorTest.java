/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.junit.Test;


public class TemporalOperatorTest {
    @Test
    public void testAfterWithLocalDateTime() {
        checkTemporalConstraint("localDateTime after $t1.localDateTime");
    }

    @Test
    public void testAfterWithZonedDateTime() {
        checkTemporalConstraint("zonedDateTime after $t1.zonedDateTime");
    }

    @Test
    public void testAfterWithDate() {
        checkTemporalConstraint("date after $t1.date");
    }

    @Test
    public void testAfterWithDateUsingOr() {
        checkTemporalConstraint("date == null || date after $t1.date");
    }

    @Test
    public void testAfterMixDateAndLocaldateTime() {
        checkTemporalConstraint("date after $t1.localDateTime");
    }

    public static class TimestampedObject {
        private final String name;

        private final LocalDateTime localDateTime;

        public TimestampedObject(String name, LocalDateTime time) {
            this.name = name;
            this.localDateTime = time;
        }

        public String getName() {
            return name;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public LocalDate getLocalDate() {
            return localDateTime.toLocalDate();
        }

        public ZonedDateTime getZonedDateTime() {
            return localDateTime.atZone(ZoneId.systemDefault());
        }

        public Date getDate() {
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    @Test
    public void testAfterWithConstant() {
        checkConstantTemporalConstraint("date after \"1-Jan-1970\"");
    }

    @Test
    public void testAfterWithConstantUsingOR() {
        // RHBRMS-2799
        checkConstantTemporalConstraint("date == null || date after \"1-Jan-1970\"");
    }

    @Test
    public void testAfterWithLocalDateTimeUsingOr() {
        // RHBRMS-2799
        checkConstantTemporalConstraint("localDateTime == null || localDateTime after \"1-Jan-1970\"");
    }

    @Test
    public void testAfterWithLocalDateTimeWithLiteral() {
        // RHBRMS-2799
        checkConstantTemporalConstraint("localDateTime after \"1-Jan-1970\"");
    }

    @Test
    public void testDateAfterWithLiteral() {
        checkConstantTemporalConstraint("date after \"1-Jan-1970\"");
    }

    @Test
    public void testAfterWithLocalDateWithLiteral() {
        checkConstantTemporalConstraint("localDate after \"1-Jan-1970\"");
    }

    @Test
    public void testComparisonWithLocalDateTimeAndLiteral() {
        checkConstantTemporalConstraint("localDate > \"1-Jan-1970\"");
    }

    @Test
    public void testComparisonWithLocalDate() {
        checkConstantTemporalConstraint("localDate > org.drools.compiler.integrationtests.TemporalOperatorTest.parseDateAsLocal(\"1-Jan-1970\")");
    }

    @Test
    public void testComparisonWithLocalDateAndLiteral() {
        checkConstantTemporalConstraint("localDateTime > \"1-Jan-1970\"");
    }

    @Test
    public void testComparisonWithLocalDateTime() {
        checkConstantTemporalConstraint("localDateTime > org.drools.compiler.integrationtests.TemporalOperatorTest.parseTimeAsLocal(\"1-Jan-1970\")");
    }
}

