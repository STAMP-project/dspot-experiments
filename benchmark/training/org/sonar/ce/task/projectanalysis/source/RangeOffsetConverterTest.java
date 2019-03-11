/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.task.projectanalysis.source;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.ce.task.projectanalysis.source.linereader.RangeOffsetConverter;
import org.sonar.ce.task.projectanalysis.source.linereader.RangeOffsetConverter.RangeOffsetConverterException;


public class RangeOffsetConverterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    static final int LINE_1 = 1;

    static final int LINE_2 = 2;

    static final int LINE_3 = 3;

    static final int OFFSET_0 = 0;

    static final int OFFSET_2 = 2;

    static final int OFFSET_3 = 3;

    static final int OFFSET_4 = 4;

    static final int BIG_OFFSET = 10;

    static final int DEFAULT_LINE_LENGTH = 5;

    RangeOffsetConverter underTest = new RangeOffsetConverter();

    @Test
    public void return_range() {
        assertThat(underTest.offsetToString(RangeOffsetConverterTest.createTextRange(RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.OFFSET_2, RangeOffsetConverterTest.OFFSET_3), RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.DEFAULT_LINE_LENGTH)).isEqualTo((((RangeOffsetConverterTest.OFFSET_2) + ",") + (RangeOffsetConverterTest.OFFSET_3)));
    }

    @Test
    public void return_range_not_finishing_in_current_line() {
        assertThat(underTest.offsetToString(RangeOffsetConverterTest.createTextRange(RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.LINE_3, RangeOffsetConverterTest.OFFSET_2, RangeOffsetConverterTest.OFFSET_3), RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.DEFAULT_LINE_LENGTH)).isEqualTo((((RangeOffsetConverterTest.OFFSET_2) + ",") + (RangeOffsetConverterTest.DEFAULT_LINE_LENGTH)));
    }

    @Test
    public void return_range_that_began_in_previous_line_and_finish_in_current_line() {
        assertThat(underTest.offsetToString(RangeOffsetConverterTest.createTextRange(RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.LINE_3, RangeOffsetConverterTest.OFFSET_2, RangeOffsetConverterTest.OFFSET_3), RangeOffsetConverterTest.LINE_3, RangeOffsetConverterTest.DEFAULT_LINE_LENGTH)).isEqualTo((((RangeOffsetConverterTest.OFFSET_0) + ",") + (RangeOffsetConverterTest.OFFSET_3)));
    }

    @Test
    public void return_range_that_began_in_previous_line_and_not_finishing_in_current_line() {
        assertThat(underTest.offsetToString(RangeOffsetConverterTest.createTextRange(RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.OFFSET_2, RangeOffsetConverterTest.OFFSET_3), RangeOffsetConverterTest.LINE_2, RangeOffsetConverterTest.DEFAULT_LINE_LENGTH)).isEqualTo((((RangeOffsetConverterTest.OFFSET_0) + ",") + (RangeOffsetConverterTest.DEFAULT_LINE_LENGTH)));
    }

    @Test
    public void return_empty_string_when_offset_is_empty() {
        assertThat(underTest.offsetToString(RangeOffsetConverterTest.createTextRange(RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.OFFSET_0, RangeOffsetConverterTest.OFFSET_0), RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.DEFAULT_LINE_LENGTH)).isEmpty();
    }

    @Test
    public void return_whole_line_offset_when_range_begin_at_first_character_and_ends_at_first_character_of_next_line() {
        assertThat(underTest.offsetToString(RangeOffsetConverterTest.createTextRange(RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.LINE_2, RangeOffsetConverterTest.OFFSET_0, RangeOffsetConverterTest.OFFSET_0), RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.DEFAULT_LINE_LENGTH)).isEqualTo((((RangeOffsetConverterTest.OFFSET_0) + ",") + (RangeOffsetConverterTest.DEFAULT_LINE_LENGTH)));
    }

    @Test
    public void fail_when_end_offset_is_before_start_offset() {
        thrown.expect(RangeOffsetConverterException.class);
        thrown.expectMessage("End offset 2 cannot be defined before start offset 4 on line 1");
        underTest.offsetToString(RangeOffsetConverterTest.createTextRange(RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.OFFSET_4, RangeOffsetConverterTest.OFFSET_2), RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.DEFAULT_LINE_LENGTH);
    }

    @Test
    public void fail_when_end_offset_is_higher_than_line_length() {
        thrown.expect(RangeOffsetConverterException.class);
        thrown.expectMessage("End offset 10 is defined outside the length (5) of the line 1");
        underTest.offsetToString(RangeOffsetConverterTest.createTextRange(RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.OFFSET_4, RangeOffsetConverterTest.BIG_OFFSET), RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.DEFAULT_LINE_LENGTH);
    }

    @Test
    public void fail_when_start_offset_is_higher_than_line_length() {
        thrown.expect(RangeOffsetConverterException.class);
        thrown.expectMessage("Start offset 10 is defined outside the length (5) of the line 1");
        underTest.offsetToString(RangeOffsetConverterTest.createTextRange(RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.BIG_OFFSET, ((RangeOffsetConverterTest.BIG_OFFSET) + 1)), RangeOffsetConverterTest.LINE_1, RangeOffsetConverterTest.DEFAULT_LINE_LENGTH);
    }
}

