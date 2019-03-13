package com.vaadin.tests.server.component.datefield;


import DateTimeResolution.SECOND;
import com.vaadin.tests.server.component.abstractdatefield.AbstractLocalDateTimeFieldDeclarativeTest;
import com.vaadin.ui.InlineDateTimeField;
import com.vaadin.ui.declarative.Design;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import org.junit.Test;


/**
 * Tests the declarative support for implementations of
 * {@link AbstractDateField}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class InlineDateTimeFieldDeclarativeTest extends AbstractLocalDateTimeFieldDeclarativeTest<InlineDateTimeField> {
    @Test
    public void testInlineDateFieldToFromDesign() throws Exception {
        InlineDateTimeField field = new InlineDateTimeField("Day is", LocalDateTime.of(2003, 2, 27, 4, 13, 45));
        field.setShowISOWeekNumbers(true);
        field.setRangeStart(LocalDateTime.of(2001, 2, 27, 6, 12, 53));
        field.setRangeEnd(LocalDateTime.of(20011, 2, 27, 3, 43, 23));
        field.setResolution(SECOND);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Design.write(field, bos);
        InlineDateTimeField result = ((InlineDateTimeField) (Design.read(new ByteArrayInputStream(bos.toByteArray()))));
        assertEquals(field.getResolution(), result.getResolution());
        assertEquals(field.getCaption(), result.getCaption());
        assertEquals(field.getValue(), result.getValue());
        assertEquals(field.getRangeStart(), result.getRangeStart());
        assertEquals(field.getRangeEnd(), result.getRangeEnd());
    }
}

