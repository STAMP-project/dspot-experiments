package com.vaadin.tests.design;


import com.vaadin.tests.design.UPPERCASE.InUpperCasePackage;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;


public class InnerClassDesignReadWriteTest {
    @Test
    public void testWritingAndReadingBackInnerClass() throws IOException {
        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(new InnerClassDesignReadWriteTest.StaticInner());
        vl.addComponent(new InnerClassDesignReadWriteTest.Foo.StaticInnerInner());
        vl.addComponent(new InUpperCasePackage());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Design.write(vl, baos);
        Design.read(new ByteArrayInputStream(baos.toByteArray()));
    }

    public static class StaticInner extends GridLayout {}

    public static class Foo {
        public static class StaticInnerInner extends HorizontalLayout {}
    }
}

