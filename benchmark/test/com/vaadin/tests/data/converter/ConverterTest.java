package com.vaadin.tests.data.converter;


import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.server.SerializableFunction;
import org.junit.Assert;
import org.junit.Test;


public class ConverterTest {
    SerializableFunction<String, Result<String>> toModel = ( presentation) -> {
        if (presentation.startsWith("presentation-")) {
            return Result.ok(presentation.substring("presentation-".length()));
        }
        return Result.error(("invalid prefix: " + presentation));
    };

    SerializableFunction<String, String> toPresentation = ( model) -> "presentation-" + model;

    Converter<String, String> converter = Converter.from(toModel, toPresentation);

    @Test
    public void basicConversion() {
        Assert.assertEquals("presentation-123", converter.convertToPresentation("123", new ValueContext()));
        Assert.assertEquals("123", converter.convertToModel("presentation-123", new ValueContext()).getOrThrow(( msg) -> new AssertionError(msg)));
    }
}

