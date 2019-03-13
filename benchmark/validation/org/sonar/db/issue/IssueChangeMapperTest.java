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
package org.sonar.db.issue;


import IssueChangeDto.TYPE_COMMENT;
import IssueChangeDto.TYPE_FIELD_CHANGE;
import System2.INSTANCE;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.DbTester;


public class IssueChangeMapperTest {
    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Test
    public void insert_diff() {
        IssueChangeDto dto = new IssueChangeDto();
        /* no key on field changes */
        dto.setKey(null);
        dto.setUserUuid("user_uuid");
        dto.setIssueKey("ABCDE");
        dto.setChangeType(TYPE_FIELD_CHANGE);
        dto.setChangeData("severity=INFO|BLOCKER");
        dto.setCreatedAt(1500000000000L);
        dto.setUpdatedAt(1500000000000L);
        dto.setIssueChangeCreationDate(1500000000000L);
        dbTester.getSession().getMapper(IssueChangeMapper.class).insert(dto);
        dbTester.getSession().commit();
        assertDbUnit(getClass(), "insert_diff-result.xml", new String[]{ "id" }, "issue_changes");
    }

    @Test
    public void insert_comment() {
        IssueChangeDto dto = new IssueChangeDto();
        dto.setKey("COMMENT-1234");
        dto.setUserUuid("user_uuid");
        dto.setIssueKey("ABCDE");
        dto.setChangeType(TYPE_COMMENT);
        dto.setChangeData("the comment");
        dto.setCreatedAt(1500000000000L);
        dto.setUpdatedAt(1500000000000L);
        dto.setIssueChangeCreationDate(1500000000000L);
        dbTester.getSession().getMapper(IssueChangeMapper.class).insert(dto);
        dbTester.getSession().commit();
        assertDbUnit(getClass(), "insert_comment-result.xml", new String[]{ "id" }, "issue_changes");
    }
}

