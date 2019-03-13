package com.querydsl.core.types;


import com.querydsl.core.types.dsl.Expressions;
import java.io.IOException;
import org.junit.Test;


public class ExpressionSerializationTest {
    @Test
    public void serialize() throws IOException, ClassNotFoundException {
        QTuple e = new QTuple(Expressions.stringPath("x"), Expressions.numberPath(Integer.class, "y"));
        serialize(e);
        serialize(e.newInstance("a", 1));
    }
}

