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
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
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


public class AutoCloseableBDDSoftAssertionsTest {
    @Test
    public void all_assertions_should_pass() {
        try (AutoCloseableBDDSoftAssertions softly = new AutoCloseableBDDSoftAssertions()) {
            softly.then(1).isEqualTo(1);
            softly.then(Lists.newArrayList(1, 2)).containsOnly(1, 2);
        }
    }

    @Test
    public void should_be_able_to_catch_exceptions_thrown_by_all_proxied_methods() {
        try (AutoCloseableBDDSoftAssertions softly = new AutoCloseableBDDSoftAssertions()) {
            softly.then(BigDecimal.ZERO).isEqualTo(BigDecimal.ONE);
            softly.then(Boolean.FALSE).isTrue();
            softly.then(false).isTrue();
            softly.then(new boolean[]{ false }).isEqualTo(new boolean[]{ true });
            softly.then(new Byte(((byte) (0)))).isEqualTo(((byte) (1)));
            softly.then(((byte) (2))).inHexadecimal().isEqualTo(((byte) (3)));
            softly.then(new byte[]{ 4 }).isEqualTo(new byte[]{ 5 });
            softly.then(new Character(((char) (65)))).isEqualTo(new Character(((char) (66))));
            softly.then(((char) (67))).isEqualTo(((char) (68)));
            softly.then(new char[]{ 69 }).isEqualTo(new char[]{ 70 });
            softly.then(new StringBuilder("a")).isEqualTo(new StringBuilder("b"));
            softly.then(Object.class).isEqualTo(String.class);
            softly.then(DateUtil.parseDatetime("1999-12-31T23:59:59")).isEqualTo(DateUtil.parseDatetime("2000-01-01T00:00:01"));
            softly.then(new Double(6.0)).isEqualTo(new Double(7.0));
            softly.then(8.0).isEqualTo(9.0);
            softly.then(new double[]{ 10.0 }).isEqualTo(new double[]{ 11.0 });
            softly.then(new File("a")).overridingErrorMessage(String.format("%nExpecting:%n <File(a)>%nto be equal to:%n <File(b)>%nbut was not.")).isEqualTo(new File("b"));
            softly.then(new Float(12.0F)).isEqualTo(new Float(13.0F));
            softly.then(14.0F).isEqualTo(15.0F);
            softly.then(new float[]{ 16.0F }).isEqualTo(new float[]{ 17.0F });
            softly.then(new ByteArrayInputStream(new byte[]{ ((byte) (65)) })).hasSameContentAs(new ByteArrayInputStream(new byte[]{ ((byte) (66)) }));
            softly.then(new Integer(20)).isEqualTo(new Integer(21));
            softly.then(22).isEqualTo(23);
            softly.then(new int[]{ 24 }).isEqualTo(new int[]{ 25 });
            softly.then(((Iterable<String>) (Lists.newArrayList("26")))).isEqualTo(Lists.newArrayList("27"));
            softly.then(Lists.list("28").iterator()).isExhausted();
            softly.then(Lists.list("30")).isEqualTo(Lists.newArrayList("31"));
            softly.then(new Long(32L)).isEqualTo(new Long(33L));
            softly.then(34L).isEqualTo(35L);
            softly.then(new long[]{ 36L }).isEqualTo(new long[]{ 37L });
            softly.then(Maps.mapOf(MapEntry.entry("38", "39"))).isEqualTo(Maps.mapOf(MapEntry.entry("40", "41")));
            softly.then(new Short(((short) (42)))).isEqualTo(new Short(((short) (43))));
            softly.then(((short) (44))).isEqualTo(((short) (45)));
            softly.then(new short[]{ ((short) (46)) }).isEqualTo(new short[]{ ((short) (47)) });
            softly.then("48").isEqualTo("49");
            softly.then(new Object() {
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
            softly.then(new Object[]{ new Object() {
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
            softly.then(illegalArgumentException).hasMessage("NullPointerException message");
            softly.then(Optional.of("not empty")).isEqualTo("empty");
            softly.then(OptionalInt.of(0)).isEqualTo(1);
            softly.then(OptionalDouble.of(0.0)).isEqualTo(1.0);
            softly.then(OptionalLong.of(0L)).isEqualTo(1L);
            softly.then(LocalTime.of(12, 0)).isEqualTo(LocalTime.of(13, 0));
            softly.then(OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)).isEqualTo(OffsetTime.of(13, 0, 0, 0, ZoneOffset.UTC));
            softly.then(OffsetDateTime.MIN).isEqualTo(OffsetDateTime.MAX);
        } catch (MultipleFailuresError e) {
            List<String> errors = e.getFailures().stream().map(Object::toString).collect(Collectors.toList());
            Assertions.assertThat(errors).hasSize(45);
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
            Assertions.assertThat(errors.get(38)).contains(String.format("%nExpecting:%n <Optional[not empty]>%nto be equal to:%n <\"empty\">%nbut was not."));
            Assertions.assertThat(errors.get(39)).contains(String.format("%nExpecting:%n <OptionalInt[0]>%nto be equal to:%n <1>%nbut was not."));
            Assertions.assertThat(errors.get(40)).contains(String.format("%nExpecting:%n <OptionalDouble[0.0]>%nto be equal to:%n <1.0>%nbut was not."));
            Assertions.assertThat(errors.get(41)).contains(String.format("%nExpecting:%n <OptionalLong[0]>%nto be equal to:%n <1L>%nbut was not."));
            Assertions.assertThat(errors.get(42)).contains(String.format("%nExpecting:%n <12:00>%nto be equal to:%n <13:00>%nbut was not."));
            Assertions.assertThat(errors.get(43)).contains(String.format("%nExpecting:%n <12:00Z>%nto be equal to:%n <13:00Z>%nbut was not."));
            Assertions.assertThat(errors.get(44)).contains(String.format("%nExpecting:%n <-999999999-01-01T00:00+18:00>%nto be equal to:%n <+999999999-12-31T23:59:59.999999999-18:00>%nbut was not."));
            return;
        }
        Assertions.fail("Should not reach here");
    }
}

