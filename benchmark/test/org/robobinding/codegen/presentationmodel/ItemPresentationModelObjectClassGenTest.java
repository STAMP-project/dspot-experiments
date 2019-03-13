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
public class ItemPresentationModelObjectClassGenTest {
    @ClassRule
    public static final CompilationRule compilation = new CompilationRule();

    @Test
    public void shouldDefineConstructorWithChangeSupport() {
        PresentationModelInfo presentationModelInfo = createPresentationModelInfoFor(DefineConstructor.class);
        ItemPresentationModelObjectClassGen gen = new ItemPresentationModelObjectClassGen(presentationModelInfo);
        gen.defineFields();
        gen.defineConstructor();
        assertOutputSameTextFile(gen, "DefineConstructor_IPM.java.txt");
    }
}

