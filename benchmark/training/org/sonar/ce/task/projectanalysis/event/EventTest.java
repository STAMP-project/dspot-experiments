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
package org.sonar.ce.task.projectanalysis.event;


import Event.Category.ALERT;
import Event.Category.PROFILE;
import org.junit.Test;


public class EventTest {
    private static final String SOME_NAME = "someName";

    private static final String SOME_DATA = "some data";

    private static final String SOME_DESCRIPTION = "some description";

    @Test(expected = NullPointerException.class)
    public void createAlert_fail_fast_null_check_on_null_name() {
        Event.createAlert(null, EventTest.SOME_DATA, EventTest.SOME_DESCRIPTION);
    }

    @Test(expected = NullPointerException.class)
    public void createProfile_fail_fast_null_check_on_null_name() {
        Event.createProfile(null, EventTest.SOME_DATA, EventTest.SOME_DESCRIPTION);
    }

    @Test
    public void createAlert_verify_fields() {
        Event event = Event.createAlert(EventTest.SOME_NAME, EventTest.SOME_DATA, EventTest.SOME_DESCRIPTION);
        assertThat(event.getName()).isEqualTo(EventTest.SOME_NAME);
        assertThat(event.getCategory()).isEqualTo(ALERT);
        assertThat(event.getData()).isEqualTo(EventTest.SOME_DATA);
        assertThat(event.getDescription()).isEqualTo(EventTest.SOME_DESCRIPTION);
    }

    @Test
    public void createProfile_verify_fields() {
        Event event = Event.createProfile(EventTest.SOME_NAME, EventTest.SOME_DATA, EventTest.SOME_DESCRIPTION);
        assertThat(event.getName()).isEqualTo(EventTest.SOME_NAME);
        assertThat(event.getCategory()).isEqualTo(PROFILE);
        assertThat(event.getData()).isEqualTo(EventTest.SOME_DATA);
        assertThat(event.getDescription()).isEqualTo(EventTest.SOME_DESCRIPTION);
    }

    @Test
    public void same_name_and_category_make_equal_events() {
        Event source = Event.createAlert(EventTest.SOME_NAME, null, null);
        assertThat(source).isEqualTo(Event.createAlert(EventTest.SOME_NAME, null, null));
        assertThat(source).isEqualTo(source);
        assertThat(source).isNotEqualTo(null);
    }
}

