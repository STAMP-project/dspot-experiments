/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.bugs;


import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.typeresolution.testdata.UsesJavaStreams;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class InterfaceMethodTest {
    @Test
    public void shouldNotFail() {
        ASTCompilationUnit acu = parseAndTypeResolveForClass(UsesJavaStreams.class);
    }
}

