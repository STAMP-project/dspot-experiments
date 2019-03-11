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
package org.assertj.core.api;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.assertj.core.data.MapEntry;
import org.assertj.core.test.Maps;
import org.assertj.core.util.DateUtil;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;


public class AutoCloseableSoftAssertionsTest {
    @Test
    public void all_assertions_should_pass() {
        try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
            softly.assertThat(1).isEqualTo(1);
            softly.assertThat(Lists.list(1, 2)).containsOnly(1, 2);
        }
    }

    @Test
    public void should_be_able_to_catch_exceptions_thrown_by_all_proxied_methods() {
        Assertions.setRemoveAssertJRelatedElementsFromStackTrace(false);
        try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
            softly.assertThat(BigDecimal.ZERO).isEqualTo(BigDecimal.ONE);
            softly.assertThat(Boolean.FALSE).isTrue();
            softly.assertThat(false).isTrue();
            softly.assertThat(new boolean[]{ false }).isEqualTo(new boolean[]{ true });
            softly.assertThat(new Byte(((byte) (0)))).isEqualTo(((byte) (1)));
            softly.assertThat(((byte) (2))).inHexadecimal().isEqualTo(((byte) (3)));
            softly.assertThat(new byte[]{ 4 }).isEqualTo(new byte[]{ 5 });
            softly.assertThat(new Character(((char) (65)))).isEqualTo(new Character(((char) (66))));
            softly.assertThat(((char) (67))).isEqualTo(((char) (68)));
            softly.assertThat(new char[]{ 69 }).isEqualTo(new char[]{ 70 });
            softly.assertThat(new StringBuilder("a")).isEqualTo(new StringBuilder("b"));
            softly.assertThat(Object.class).isEqualTo(String.class);
            softly.assertThat(DateUtil.parseDatetime("1999-12-31T23:59:59")).isEqualTo(DateUtil.parseDatetime("2000-01-01T00:00:01"));
            softly.assertThat(new Double(6.0)).isEqualTo(new Double(7.0));
            softly.assertThat(8.0).isEqualTo(9.0);
            softly.assertThat(new double[]{ 10.0 }).isEqualTo(new double[]{ 11.0 });
            softly.assertThat(new File("a")).overridingErrorMessage(String.format("%nExpecting:%n <File(a)>%nto be equal to:%n <File(b)>%nbut was not.")).isEqualTo(new File("b"));
            softly.assertThat(new Float(12.0F)).isEqualTo(new Float(13.0F));
            softly.assertThat(14.0F).isEqualTo(15.0F);
            softly.assertThat(new float[]{ 16.0F }).isEqualTo(new float[]{ 17.0F });
            softly.assertThat(new ByteArrayInputStream(new byte[]{ ((byte) (65)) })).hasSameContentAs(new ByteArrayInputStream(new byte[]{ ((byte) (66)) }));
            softly.assertThat(new Integer(20)).isEqualTo(new Integer(21));
            softly.assertThat(22).isEqualTo(23);
            softly.assertThat(new int[]{ 24 }).isEqualTo(new int[]{ 25 });
            softly.assertThat(((Iterable<String>) (Lists.list("26")))).isEqualTo(Lists.list("27"));
            softly.assertThat(Lists.list("28").iterator()).isExhausted();
            softly.assertThat(Lists.list("30")).isEqualTo(Lists.list("31"));
            softly.assertThat(new Long(32L)).isEqualTo(new Long(33L));
            softly.assertThat(34L).isEqualTo(35L);
            softly.assertThat(new long[]{ 36L }).isEqualTo(new long[]{ 37L });
            softly.assertThat(Maps.mapOf(MapEntry.entry("38", "39"))).isEqualTo(Maps.mapOf(MapEntry.entry("40", "41")));
            softly.assertThat(new Short(((short) (42)))).isEqualTo(new Short(((short) (43))));
            softly.assertThat(((short) (44))).isEqualTo(((short) (45)));
            softly.assertThat(new short[]{ ((short) (46)) }).isEqualTo(new short[]{ ((short) (47)) });
            softly.assertThat("48").isEqualTo("49");
            softly.assertThat(new Object() {
                @Override
                public String toString() {
                    return "50";
                }
            }).isEqualTo(new Object() {
                @Override
                public String toString() {
                    return "51";
                }
            });
            softly.assertThat(new Object[]{ new Object() {
                @Override
                public String toString() {
                    return "52";
                }
            } }).isEqualTo(new Object[]{ new Object() {
                @Override
                public String toString() {
                    return "53";
                }
            } });
            final IllegalArgumentException illegalArgumentException = new IllegalArgumentException("IllegalArgumentException message");
            softly.assertThat(illegalArgumentException).hasMessage("NullPointerException message");
            softly.assertThatThrownBy(() -> {
                throw new Exception("something was wrong");
            }).hasMessage("something was good");
            softly.assertThat(Optional.of("bad option")).isEqualTo(Optional.of("good option"));
            softly.assertThat(LocalDate.of(2015, 1, 1)).isEqualTo(LocalDate.of(2015, 1, 2));
            softly.assertThat(LocalDateTime.of(2015, 1, 1, 23, 59, 59)).isEqualTo(LocalDateTime.of(2015, 1, 1, 23, 59, 0));
            softly.assertThat(ZonedDateTime.of(2015, 1, 1, 23, 59, 59, 0, ZoneOffset.UTC)).isEqualTo(ZonedDateTime.of(2015, 1, 1, 23, 59, 0, 0, ZoneOffset.UTC));
            softly.assertThat(OptionalInt.of(0)).isEqualTo(1);
            softly.assertThat(OptionalDouble.of(0.0)).isEqualTo(1.0);
            softly.assertThat(OptionalLong.of(0L)).isEqualTo(1L);
            softly.assertThat(LocalTime.of(12, 0)).isEqualTo(LocalTime.of(13, 0));
            softly.assertThat(OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)).isEqualTo(OffsetTime.of(13, 0, 0, 0, ZoneOffset.UTC));
            softly.assertThat(OffsetDateTime.MIN).isEqualTo(OffsetDateTime.MAX);
        } catch (MultipleFailuresError e) {
            List<String> errors = e.getFailures().stream().map(Object::toString).collect(Collectors.toList());
            Assertions.assertThat(errors).hasSize(49);
            Assertions.assertThat(errors.get(0)).contains(String.format("%nExpecting:%n <0>%nto be equal to:%n <1>%nbut was not."));
            Assertions.assertThat(errors.get(1)).contains(String.format("%nExpecting:%n <false>%nto be equal to:%n <true>%nbut was not."));
            Assertions.assertThat(errors.get(2)).contains(String.format("%nExpecting:%n <false>%nto be equal to:%n <true>%nbut was not."));
            Assertions.assertThat(errors.get(3)).contains(String.format("%nExpecting:%n <[false]>%nto be equal to:%n <[true]>%nbut was not."));
            Assertions.assertThat(errors.get(4)).contains(String.format("%nExpecting:%n <0>%nto be equal to:%n <1>%nbut was not."));
            Assertions.assertThat(errors.get(5)).contains(String.format("%nExpecting:%n <0x02>%nto be equal to:%n <0x03>%nbut was not."));
            Assertions.assertThat(errors.get(6)).contains(String.format("%nExpecting:%n <[4]>%nto be equal to:%n <[5]>%nbut was not."));
            Assertions.assertThat(errors.get(7)).contains(String.format("%nExpecting:%n <'A'>%nto be equal to:%n <'B'>%nbut was not."));
            Assertions.assertThat(errors.get(8)).contains(String.format("%nExpecting:%n <'C'>%nto be equal to:%n <'D'>%nbut was not."));
            Assertions.assertThat(errors.get(9)).contains(String.format("%nExpecting:%n <['E']>%nto be equal to:%n <['F']>%nbut was not."));
            Assertions.assertThat(errors.get(10)).contains(String.format("%nExpecting:%n <a>%nto be equal to:%n <b>%nbut was not."));
            Assertions.assertThat(errors.get(11)).contains(String.format("%nExpecting:%n <java.lang.Object>%nto be equal to:%n <java.lang.String>%nbut was not."));
            Assertions.assertThat(errors.get(12)).contains(String.format("%nExpecting:%n <1999-12-31T23:59:59.000>%nto be equal to:%n <2000-01-01T00:00:01.000>%nbut was not."));
            Assertions.assertThat(errors.get(13)).contains(String.format("%nExpecting:%n <6.0>%nto be equal to:%n <7.0>%nbut was not."));
            Assertions.assertThat(errors.get(14)).contains(String.format("%nExpecting:%n <8.0>%nto be equal to:%n <9.0>%nbut was not."));
            Assertions.assertThat(errors.get(15)).contains(String.format("%nExpecting:%n <[10.0]>%nto be equal to:%n <[11.0]>%nbut was not."));
            Assertions.assertThat(errors.get(16)).contains(String.format("%nExpecting:%n <File(a)>%nto be equal to:%n <File(b)>%nbut was not."));
            Assertions.assertThat(errors.get(17)).contains(String.format("%nExpecting:%n <12.0f>%nto be equal to:%n <13.0f>%nbut was not."));
            Assertions.assertThat(errors.get(18)).contains(String.format("%nExpecting:%n <14.0f>%nto be equal to:%n <15.0f>%nbut was not."));
            Assertions.assertThat(errors.get(19)).contains(String.format("%nExpecting:%n <[16.0f]>%nto be equal to:%n <[17.0f]>%nbut was not."));
            Assertions.assertThat(errors.get(20)).contains(String.format(("%nInputStreams do not have same content:%n%n" + (((("Changed content at line 1:%n" + "expecting:%n") + "  [\"B\"]%n") + "but was:%n") + "  [\"A\"]%n"))));
            Assertions.assertThat(errors.get(21)).contains(String.format("%nExpecting:%n <20>%nto be equal to:%n <21>%nbut was not."));
            Assertions.assertThat(errors.get(22)).contains(String.format("%nExpecting:%n <22>%nto be equal to:%n <23>%nbut was not."));
            Assertions.assertThat(errors.get(23)).contains(String.format("%nExpecting:%n <[24]>%nto be equal to:%n <[25]>%nbut was not."));
            Assertions.assertThat(errors.get(24)).contains(String.format("%nExpecting:%n <[\"26\"]>%nto be equal to:%n <[\"27\"]>%nbut was not."));
            Assertions.assertThat(errors.get(25)).contains(String.format("Expecting the iterator under test to be exhausted"));
            Assertions.assertThat(errors.get(26)).contains(String.format("%nExpecting:%n <[\"30\"]>%nto be equal to:%n <[\"31\"]>%nbut was not."));
            Assertions.assertThat(errors.get(27)).contains(String.format("%nExpecting:%n <32L>%nto be equal to:%n <33L>%nbut was not."));
            Assertions.assertThat(errors.get(28)).contains(String.format("%nExpecting:%n <34L>%nto be equal to:%n <35L>%nbut was not."));
            Assertions.assertThat(errors.get(29)).contains(String.format("%nExpecting:%n <[36L]>%nto be equal to:%n <[37L]>%nbut was not."));
            Assertions.assertThat(errors.get(30)).contains(String.format("%nExpecting:%n <{\"38\"=\"39\"}>%nto be equal to:%n <{\"40\"=\"41\"}>%nbut was not."));
            Assertions.assertThat(errors.get(31)).contains(String.format("%nExpecting:%n <42>%nto be equal to:%n <43>%nbut was not."));
            Assertions.assertThat(errors.get(32)).contains(String.format("%nExpecting:%n <44>%nto be equal to:%n <45>%nbut was not."));
            Assertions.assertThat(errors.get(33)).contains(String.format("%nExpecting:%n <[46]>%nto be equal to:%n <[47]>%nbut was not."));
            Assertions.assertThat(errors.get(34)).contains(String.format("%nExpecting:%n <\"48\">%nto be equal to:%n <\"49\">%nbut was not."));
            Assertions.assertThat(errors.get(35)).contains(String.format("%nExpecting:%n <50>%nto be equal to:%n <51>%nbut was not."));
            Assertions.assertThat(errors.get(36)).contains(String.format("%nExpecting:%n <[52]>%nto be equal to:%n <[53]>%nbut was not."));
            Assertions.assertThat(errors.get(37)).contains(String.format(("%nExpecting message:%n" + ((" <\"NullPointerException message\">%n" + "but was:%n") + " <\"IllegalArgumentException message\">"))));
            Assertions.assertThat(errors.get(38)).contains(String.format(("%nExpecting message:%n" + ((" <\"something was good\">%n" + "but was:%n") + " <\"something was wrong\">"))));
            Assertions.assertThat(errors.get(39)).contains(String.format("%nExpecting:%n <Optional[bad option]>%nto be equal to:%n <Optional[good option]>%nbut was not."));
            Assertions.assertThat(errors.get(40)).contains(String.format("%nExpecting:%n <2015-01-01>%nto be equal to:%n <2015-01-02>%nbut was not."));
            Assertions.assertThat(errors.get(41)).contains(String.format("%nExpecting:%n <2015-01-01T23:59:59>%nto be equal to:%n <2015-01-01T23:59>%nbut was not."));
            Assertions.assertThat(errors.get(42)).contains(String.format("%nExpecting:%n <2015-01-01T23:59:59Z>%nto be equal to:%n <2015-01-01T23:59Z>%nbut was not."));
            Assertions.assertThat(errors.get(43)).contains(String.format("%nExpecting:%n <OptionalInt[0]>%nto be equal to:%n <1>%nbut was not."));
            Assertions.assertThat(errors.get(44)).contains(String.format("%nExpecting:%n <OptionalDouble[0.0]>%nto be equal to:%n <1.0>%nbut was not."));
            Assertions.assertThat(errors.get(45)).contains(String.format("%nExpecting:%n <OptionalLong[0]>%nto be equal to:%n <1L>%nbut was not."));
            Assertions.assertThat(errors.get(46)).contains(String.format("%nExpecting:%n <12:00>%nto be equal to:%n <13:00>%nbut was not."));
            Assertions.assertThat(errors.get(47)).contains(String.format("%nExpecting:%n <12:00Z>%nto be equal to:%n <13:00Z>%nbut was not."));
            Assertions.assertThat(errors.get(48)).contains(String.format("%nExpecting:%n <-999999999-01-01T00:00+18:00>%nto be equal to:%n <+999999999-12-31T23:59:59.999999999-18:00>%nbut was not."));
            return;
        }
        Assertions.fail("Should not reach here");
    }
}

