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
package org.sonar.ce.task.projectanalysis.scm;


import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ScmInfoImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    static final Changeset CHANGESET_1 = Changeset.newChangesetBuilder().setAuthor("john").setDate(123456789L).setRevision("rev-1").build();

    static final Changeset CHANGESET_2 = Changeset.newChangesetBuilder().setAuthor("henry").setDate(1234567810L).setRevision("rev-2").build();

    @Test
    public void get_all_changesets() {
        ScmInfo scmInfo = ScmInfoImplTest.createScmInfoWithTwoChangestOnFourLines();
        assertThat(scmInfo.getAllChangesets()).containsOnly(entry(1, ScmInfoImplTest.CHANGESET_1), entry(2, ScmInfoImplTest.CHANGESET_2), entry(3, ScmInfoImplTest.CHANGESET_1), entry(4, ScmInfoImplTest.CHANGESET_1));
    }

    @Test
    public void get_latest_changeset() {
        ScmInfo scmInfo = ScmInfoImplTest.createScmInfoWithTwoChangestOnFourLines();
        assertThat(scmInfo.getLatestChangeset()).isEqualTo(ScmInfoImplTest.CHANGESET_2);
    }

    @Test
    public void get_changeset_for_given_line() {
        ScmInfo scmInfo = ScmInfoImplTest.createScmInfoWithTwoChangestOnFourLines();
        assertThat(scmInfo.getChangesetForLine(1)).isEqualTo(ScmInfoImplTest.CHANGESET_1);
        assertThat(scmInfo.getChangesetForLine(2)).isEqualTo(ScmInfoImplTest.CHANGESET_2);
        assertThat(scmInfo.getChangesetForLine(3)).isEqualTo(ScmInfoImplTest.CHANGESET_1);
        assertThat(scmInfo.getChangesetForLine(4)).isEqualTo(ScmInfoImplTest.CHANGESET_1);
    }

    @Test
    public void exists_for_given_line() {
        ScmInfo scmInfo = ScmInfoImplTest.createScmInfoWithTwoChangestOnFourLines();
        assertThat(scmInfo.hasChangesetForLine(1)).isTrue();
        assertThat(scmInfo.hasChangesetForLine(5)).isFalse();
    }

    @Test
    public void fail_with_ISE_on_empty_changeset() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("A ScmInfo must have at least one Changeset and does not support any null one");
        new ScmInfoImpl(Collections.emptyMap());
    }

    @Test
    public void fail_with_IAE_when_line_is_smaller_than_one() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("There's no changeset on line 0");
        ScmInfo scmInfo = ScmInfoImplTest.createScmInfoWithTwoChangestOnFourLines();
        scmInfo.getChangesetForLine(0);
    }

    @Test
    public void fail_with_IAE_when_line_is_bigger_than_changetset_size() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("There's no changeset on line 5");
        ScmInfo scmInfo = ScmInfoImplTest.createScmInfoWithTwoChangestOnFourLines();
        scmInfo.getChangesetForLine(5);
    }

    @Test
    public void test_to_string() {
        ScmInfo scmInfo = ScmInfoImplTest.createScmInfoWithTwoChangestOnFourLines();
        assertThat(scmInfo.toString()).isEqualTo(("ScmInfoImpl{" + (((((("latestChangeset=Changeset{revision='rev-2', author='henry', date=1234567810}, " + "lineChangesets={") + "1=Changeset{revision='rev-1', author='john', date=123456789}, ") + "2=Changeset{revision='rev-2', author='henry', date=1234567810}, ") + "3=Changeset{revision='rev-1', author='john', date=123456789}, ") + "4=Changeset{revision='rev-1', author='john', date=123456789}") + "}}")));
    }
}

