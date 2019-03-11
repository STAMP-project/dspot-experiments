/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link SimpleTypedNameDeclaration}
 */
public class SimpleTypedNameDeclarationTest {
    /**
     * Tests the equal method.
     */
    @Test
    public void testEquals() {
        Assert.assertEquals(SimpleTypedNameDeclarationTest.byClass(SimpleTypedNameDeclaration.class), SimpleTypedNameDeclarationTest.byClass(SimpleTypedNameDeclaration.class));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.byClass(List.class), SimpleTypedNameDeclarationTest.byClass(ArrayList.class));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.byClass(ArrayList.class), SimpleTypedNameDeclarationTest.byClass(List.class));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.byName("String"), SimpleTypedNameDeclarationTest.byName("String"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.byClass(String.class), SimpleTypedNameDeclarationTest.byName("String"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.byClass(JComponent.class), SimpleTypedNameDeclarationTest.byClass(JTextField.class));
        Assert.assertFalse(SimpleTypedNameDeclarationTest.byClass(Map.class).equals(SimpleTypedNameDeclarationTest.byClass(List.class)));
        Assert.assertFalse(SimpleTypedNameDeclarationTest.byName("A").equals(SimpleTypedNameDeclarationTest.byName("B")));
        Assert.assertFalse(SimpleTypedNameDeclarationTest.byClass(String.class).equals(SimpleTypedNameDeclarationTest.byName("A")));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Double.TYPE, "double"), SimpleTypedNameDeclarationTest.by(null, "double"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Double.class, "Double"), SimpleTypedNameDeclarationTest.by(null, "double"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Character.class, "Character"), SimpleTypedNameDeclarationTest.by(null, "char"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Double.TYPE, "double"), SimpleTypedNameDeclarationTest.by(null, "float"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Double.TYPE, "double"), SimpleTypedNameDeclarationTest.by(null, "int"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Double.TYPE, "double"), SimpleTypedNameDeclarationTest.by(Integer.class, "Integer"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Double.TYPE, "double"), SimpleTypedNameDeclarationTest.by(null, "long"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Double.TYPE, "double"), SimpleTypedNameDeclarationTest.by(Long.TYPE, "long"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Double.TYPE, "double"), SimpleTypedNameDeclarationTest.by(Long.class, "Long"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Float.TYPE, "float"), SimpleTypedNameDeclarationTest.by(null, "int"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Float.TYPE, "float"), SimpleTypedNameDeclarationTest.by(Integer.TYPE, "int"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Float.TYPE, "float"), SimpleTypedNameDeclarationTest.by(Integer.class, "Integer"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Float.TYPE, "float"), SimpleTypedNameDeclarationTest.by(null, "long"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Float.TYPE, "float"), SimpleTypedNameDeclarationTest.by(Long.TYPE, "long"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Float.TYPE, "float"), SimpleTypedNameDeclarationTest.by(Long.class, "Long"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Integer.TYPE, "int"), SimpleTypedNameDeclarationTest.by(null, "char"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Integer.TYPE, "int"), SimpleTypedNameDeclarationTest.by(Character.TYPE, "char"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Integer.TYPE, "int"), SimpleTypedNameDeclarationTest.by(Character.class, "Character"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Long.TYPE, "long"), SimpleTypedNameDeclarationTest.by(null, "int"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Long.TYPE, "long"), SimpleTypedNameDeclarationTest.by(Integer.TYPE, "int"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Long.TYPE, "long"), SimpleTypedNameDeclarationTest.by(Integer.class, "Integer"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Long.TYPE, "long"), SimpleTypedNameDeclarationTest.by(null, "char"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Long.TYPE, "long"), SimpleTypedNameDeclarationTest.by(Character.TYPE, "char"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Long.TYPE, "long"), SimpleTypedNameDeclarationTest.by(Character.class, "Character"));
        // should always equal to Object
        Assert.assertEquals(SimpleTypedNameDeclarationTest.by(Object.class, "Object"), SimpleTypedNameDeclarationTest.by(null, "Something"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.withNext(SimpleTypedNameDeclarationTest.byName("Foo.I"), "Foo.B"), SimpleTypedNameDeclarationTest.byName("Foo.I"));
        Assert.assertEquals(SimpleTypedNameDeclarationTest.byName("Foo.I"), SimpleTypedNameDeclarationTest.withNext(SimpleTypedNameDeclarationTest.byName("Foo.I"), "Foo.B"));
    }
}

