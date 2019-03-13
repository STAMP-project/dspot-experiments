package org.robobinding.codegen.presentationmodel;


import com.google.testing.compile.CompilationRule;
import org.junit.ClassRule;
import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
public class PresentationModelObjectClassGenTest {
    @ClassRule
    public static final CompilationRule compilation = new CompilationRule();

    @Test
    public void shouldDefineConstructor() {
        PresentationModelInfo presentationModelInfo = createPresentationModelInfoFor(DefineConstructor.class);
        PresentationModelObjectClassGen gen = new PresentationModelObjectClassGen(presentationModelInfo);
        gen.defineFields();
        gen.defineConstructor();
        assertOutputSameTextFile(gen, "DefineConstructor_PM.java.txt");
    }

    @Test
    public void shouldDefinePropertyNames() {
        PresentationModelInfo presentationModelInfo = createPresentationModelInfoFor(DefinePropertyNames.class);
        PresentationModelObjectClassGen gen = new PresentationModelObjectClassGen(presentationModelInfo);
        gen.definePropertyNames();
        assertOutputSameTextFile(gen, "DefinePropertyNames_PM.java.txt");
    }

    @Test
    public void shouldDefineDataSetPropertyNames() {
        PresentationModelInfo presentationModelInfo = createPresentationModelInfoFor(DefineDataSetPropertyNames.class);
        PresentationModelObjectClassGen gen = new PresentationModelObjectClassGen(presentationModelInfo);
        gen.defineDataSetPropertyNames();
        assertOutputSameTextFile(gen, "DefineDataSetPropertyNames_PM.java.txt");
    }

    @Test
    public void shouldDefinePropertyDependencies() {
        PresentationModelInfo presentationModelInfo = createPresentationModelInfoFor(DefinePropertyDependencies.class);
        PresentationModelObjectClassGen gen = new PresentationModelObjectClassGen(presentationModelInfo);
        gen.definePropertyDependencies();
        assertOutputSameTextFile(gen, "DefinePropertyDependencies_PM.java.txt");
    }

    @Test
    public void shouldDefineEventMethods() {
        PresentationModelInfo presentationModelInfo = createPresentationModelInfoFor(DefineEventMethods.class);
        PresentationModelObjectClassGen gen = new PresentationModelObjectClassGen(presentationModelInfo);
        gen.defineEventMethods();
        assertOutputSameTextFile(gen, "DefineEventMethods_PM.java.txt");
    }

    @Test
    public void shouldDefineTryToCreateProperty() {
        PresentationModelInfo presentationModelInfo = createPresentationModelInfoFor(DefineTryToCreateProperty.class);
        PresentationModelObjectClassGen gen = new PresentationModelObjectClassGen(presentationModelInfo);
        gen.defineFields();
        gen.defineTryToCreateProperty();
        assertOutputSameTextFile(gen, "DefineTryToCreateProperty_PM.java.txt");
    }

    @Test
    public void shouldDefineTryToCreateDataSetProperty() {
        PresentationModelInfo presentationModelInfo = createPresentationModelInfoFor(DefineTryToCreateDataSetProperty.class);
        PresentationModelObjectClassGen gen = new PresentationModelObjectClassGen(presentationModelInfo);
        gen.defineFields();
        gen.defineTryToCreateDataSetProperty();
        assertOutputSameTextFile(gen, "DefineTryToCreateDataSetProperty_PM.java.txt");
    }

    @Test
    public void shouldDefineTryToCreateFunction() {
        PresentationModelInfo presentationModelInfo = createPresentationModelInfoFor(DefineTryToCreateFunction.class);
        PresentationModelObjectClassGen gen = new PresentationModelObjectClassGen(presentationModelInfo);
        gen.defineFields();
        gen.defineTryToCreateFunction();
        assertOutputSameTextFile(gen, "DefineTryToCreateFunction_PM.java.txt");
    }
}

