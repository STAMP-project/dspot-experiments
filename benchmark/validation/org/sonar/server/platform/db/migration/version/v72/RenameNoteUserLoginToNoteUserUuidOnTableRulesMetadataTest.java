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
package org.sonar.server.platform.db.migration.version.v72;


import java.sql.SQLException;
import java.sql.Types;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class RenameNoteUserLoginToNoteUserUuidOnTableRulesMetadataTest {
    @Rule
    public final CoreDbTester db = CoreDbTester.createForSchema(RenameNoteUserLoginToNoteUserUuidOnTableRulesMetadataTest.class, "rules_metadata.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private RenameNoteUserLoginToNoteUserUuidOnTableRulesMetadata underTest = new RenameNoteUserLoginToNoteUserUuidOnTableRulesMetadata(db.database());

    @Test
    public void rename_column() throws SQLException {
        underTest.execute();
        db.assertColumnDefinition("rules_metadata", "note_user_uuid", Types.VARCHAR, 255, true);
        db.assertColumnDoesNotExist("rules_metadata", "note_user_login");
    }
}

