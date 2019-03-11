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
package org.sonar.ce.task.projectanalysis.source.linereader;


import DbFileSources.Data;
import DbFileSources.Data.Builder;
import java.util.Collections;
import org.junit.Test;
import org.sonar.ce.task.projectanalysis.duplication.TextBlock;


public class DuplicationLineReaderTest {
    Builder sourceData = Data.newBuilder();

    DbFileSources.Line.Builder line1 = sourceData.addLinesBuilder().setSource("line1").setLine(1);

    DbFileSources.Line.Builder line2 = sourceData.addLinesBuilder().setSource("line2").setLine(2);

    DbFileSources.Line.Builder line3 = sourceData.addLinesBuilder().setSource("line3").setLine(3);

    DbFileSources.Line.Builder line4 = sourceData.addLinesBuilder().setSource("line4").setLine(4);

    @Test
    public void read_nothing() {
        DuplicationLineReader reader = new DuplicationLineReader(Collections.emptySet());
        assertThat(reader.read(line1)).isEmpty();
        assertThat(line1.getDuplicationList()).isEmpty();
    }

    @Test
    public void read_duplication_with_duplicates_on_same_file() {
        DuplicationLineReader reader = DuplicationLineReaderTest.duplicationLineReader(DuplicationLineReaderTest.duplication(1, 2, DuplicationLineReaderTest.innerDuplicate(3, 4)));
        assertThat(reader.read(line1)).isEmpty();
        assertThat(reader.read(line2)).isEmpty();
        assertThat(reader.read(line3)).isEmpty();
        assertThat(reader.read(line4)).isEmpty();
        assertThat(line1.getDuplicationList()).containsExactly(1);
        assertThat(line2.getDuplicationList()).containsExactly(1);
        assertThat(line3.getDuplicationList()).containsExactly(2);
        assertThat(line4.getDuplicationList()).containsExactly(2);
    }

    @Test
    public void read_duplication_with_repeated_text_blocks() {
        DuplicationLineReader reader = DuplicationLineReaderTest.duplicationLineReader(DuplicationLineReaderTest.duplication(1, 2, DuplicationLineReaderTest.innerDuplicate(3, 4)), DuplicationLineReaderTest.duplication(3, 4, DuplicationLineReaderTest.innerDuplicate(1, 2)));
        assertThat(reader.read(line1)).isEmpty();
        assertThat(reader.read(line2)).isEmpty();
        assertThat(reader.read(line3)).isEmpty();
        assertThat(reader.read(line4)).isEmpty();
        assertThat(line1.getDuplicationList()).containsExactly(1);
        assertThat(line2.getDuplicationList()).containsExactly(1);
        assertThat(line3.getDuplicationList()).containsExactly(3);
        assertThat(line4.getDuplicationList()).containsExactly(3);
    }

    @Test
    public void read_duplication_with_duplicates_on_other_file() {
        DuplicationLineReader reader = DuplicationLineReaderTest.duplicationLineReader(DuplicationLineReaderTest.duplication(1, 2, new org.sonar.ce.task.projectanalysis.duplication.InProjectDuplicate(DuplicationLineReaderTest.fileComponent(1).build(), new TextBlock(3, 4))));
        assertThat(reader.read(line1)).isEmpty();
        assertThat(reader.read(line2)).isEmpty();
        assertThat(reader.read(line3)).isEmpty();
        assertThat(reader.read(line4)).isEmpty();
        assertThat(line1.getDuplicationList()).containsExactly(1);
        assertThat(line2.getDuplicationList()).containsExactly(1);
        assertThat(line3.getDuplicationList()).isEmpty();
        assertThat(line4.getDuplicationList()).isEmpty();
    }

    @Test
    public void read_duplication_with_duplicates_on_other_file_from_other_project() {
        DuplicationLineReader reader = DuplicationLineReaderTest.duplicationLineReader(DuplicationLineReaderTest.duplication(1, 2, new org.sonar.ce.task.projectanalysis.duplication.CrossProjectDuplicate("other-component-key-from-another-project", new TextBlock(3, 4))));
        assertThat(reader.read(line1)).isEmpty();
        assertThat(reader.read(line2)).isEmpty();
        assertThat(reader.read(line3)).isEmpty();
        assertThat(reader.read(line4)).isEmpty();
        assertThat(line1.getDuplicationList()).containsExactly(1);
        assertThat(line2.getDuplicationList()).containsExactly(1);
        assertThat(line3.getDuplicationList()).isEmpty();
        assertThat(line4.getDuplicationList()).isEmpty();
    }

    @Test
    public void read_many_duplications() {
        DuplicationLineReader reader = DuplicationLineReaderTest.duplicationLineReader(DuplicationLineReaderTest.duplication(1, 1, DuplicationLineReaderTest.innerDuplicate(2, 2)), DuplicationLineReaderTest.duplication(1, 2, DuplicationLineReaderTest.innerDuplicate(3, 4)));
        assertThat(reader.read(line1)).isEmpty();
        assertThat(reader.read(line2)).isEmpty();
        assertThat(reader.read(line3)).isEmpty();
        assertThat(reader.read(line4)).isEmpty();
        assertThat(line1.getDuplicationList()).containsExactly(1, 2);
        assertThat(line2.getDuplicationList()).containsExactly(2, 3);
        assertThat(line3.getDuplicationList()).containsExactly(4);
        assertThat(line4.getDuplicationList()).containsExactly(4);
    }

    @Test
    public void should_be_sorted_by_line_block() {
        DuplicationLineReader reader = DuplicationLineReaderTest.duplicationLineReader(DuplicationLineReaderTest.duplication(2, 2, DuplicationLineReaderTest.innerDuplicate(4, 4)), DuplicationLineReaderTest.duplication(1, 1, DuplicationLineReaderTest.innerDuplicate(3, 3)));
        assertThat(reader.read(line1)).isEmpty();
        assertThat(reader.read(line2)).isEmpty();
        assertThat(reader.read(line3)).isEmpty();
        assertThat(reader.read(line4)).isEmpty();
        assertThat(line1.getDuplicationList()).containsExactly(1);
        assertThat(line2.getDuplicationList()).containsExactly(2);
        assertThat(line3.getDuplicationList()).containsExactly(3);
        assertThat(line4.getDuplicationList()).containsExactly(4);
    }

    @Test
    public void should_be_sorted_by_line_length() {
        DuplicationLineReader reader = DuplicationLineReaderTest.duplicationLineReader(DuplicationLineReaderTest.duplication(1, 2, DuplicationLineReaderTest.innerDuplicate(3, 4)), DuplicationLineReaderTest.duplication(1, 1, DuplicationLineReaderTest.innerDuplicate(4, 4)));
        assertThat(reader.read(line1)).isEmpty();
        assertThat(reader.read(line2)).isEmpty();
        assertThat(reader.read(line3)).isEmpty();
        assertThat(reader.read(line4)).isEmpty();
        assertThat(line1.getDuplicationList()).containsExactly(1, 2);
        assertThat(line2.getDuplicationList()).containsExactly(2);
        assertThat(line3.getDuplicationList()).containsExactly(3);
        assertThat(line4.getDuplicationList()).containsExactly(3, 4);
    }
}

