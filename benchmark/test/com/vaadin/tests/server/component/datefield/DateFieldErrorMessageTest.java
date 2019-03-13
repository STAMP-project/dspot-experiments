/**
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.server.component.datefield;


import com.vaadin.tests.server.component.abstractdatefield.AbstractLocalDateFieldDeclarativeTest;
import com.vaadin.ui.InlineDateField;
import java.time.LocalDate;
import org.junit.Test;


/**
 * Tests the resetting of component error after setting empty date string in
 * {@link AbstractDateField}.
 */
public class DateFieldErrorMessageTest extends AbstractLocalDateFieldDeclarativeTest<InlineDateField> {
    @Test
    public void testErrorMessageRemoved() throws Exception {
        InlineDateField field = new InlineDateField("Day is", LocalDate.of(2003, 2, 27));
        checkValueAndComponentError(field, "2003-02-27", LocalDate.of(2003, 2, 27), false);
        checkValueAndComponentError(field, "", null, false);
        checkValueAndComponentError(field, "2003-04-27", LocalDate.of(2003, 4, 27), false);
        checkValueAndComponentError(field, "foo", null, true);
        checkValueAndComponentError(field, "2013-07-03", LocalDate.of(2013, 7, 3), false);
        checkValueAndComponentError(field, "foo", null, true);
        checkValueAndComponentError(field, "", null, false);
    }
}

