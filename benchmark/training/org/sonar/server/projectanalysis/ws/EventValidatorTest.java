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
package org.sonar.server.projectanalysis.ws;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class EventValidatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fail_with_WS_categories() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Event of category 'QUALITY_GATE' cannot be modified. Authorized categories: VERSION, OTHER");
        EventValidator.checkModifiable().accept(newEvent().setCategory(EventCategory.QUALITY_GATE.getLabel()));
    }
}

