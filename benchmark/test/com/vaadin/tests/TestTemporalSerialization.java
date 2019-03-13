package com.vaadin.tests;


import com.vaadin.server.SerializableSupplier;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.LocalDateRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Test;


public class TestTemporalSerialization {
    @Test
    public void smokeTestRendererSerialization() throws IOException, ClassNotFoundException {
        Grid<Object> grid = new Grid();
        grid.addColumn(( o) -> new Date(o.hashCode()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), new LocalDateRenderer());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(grid);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Grid readGrid = ((Grid) (new ObjectInputStream(inputStream).readObject()));
        TestCase.assertNotNull(readGrid.getColumns().get(0));
    }

    @Test
    public void testLocalDateRenderer() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        testSerialization(LocalDateRenderer.class);
    }

    @Test
    public void testLocalDateTimeRenderer() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        testSerialization(LocalDateTimeRenderer.class);
    }

    @Test(expected = AssertionError.class)
    public void testAssertionFail() {
        new LocalDateRenderer(new TestTemporalSerialization.NonSerializableThing());
    }

    private static class NonSerializableThing implements SerializableSupplier<DateTimeFormatter> {
        public NonSerializableThing() {
        }

        private DateTimeFormatter useless = DateTimeFormatter.ofPattern("Y");

        @Override
        public DateTimeFormatter get() {
            return useless;
        }
    }
}

