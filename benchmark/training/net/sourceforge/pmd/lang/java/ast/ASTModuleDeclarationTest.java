/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import ASTModuleDirective.DirectiveType.EXPORTS;
import ASTModuleDirective.DirectiveType.PROVIDES;
import ASTModuleDirective.DirectiveType.REQUIRES;
import ASTModuleDirective.DirectiveType.USES;
import ASTModuleDirective.RequiresModifier.TRANSITIVE;
import java.util.List;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


public class ASTModuleDeclarationTest {
    @Test
    public final void jdk9ModuleInfo() {
        ASTCompilationUnit ast = ParserTstUtil.parseJava9(ASTModuleDeclarationTest.loadSource("jdk9_module_info.java"));
        List<ASTModuleDeclaration> modules = ast.findDescendantsOfType(ASTModuleDeclaration.class);
        Assert.assertEquals(1, modules.size());
        ASTModuleDeclaration module = modules.get(0);
        Assert.assertTrue(module.isOpen());
        Assert.assertEquals("com.example.foo", module.getImage());
        Assert.assertEquals(7, module.jjtGetNumChildren());
        List<ASTModuleDirective> directives = module.findChildrenOfType(ASTModuleDirective.class);
        Assert.assertEquals(7, directives.size());
        // requires com.example.foo.http;
        Assert.assertEquals(REQUIRES.name(), directives.get(0).getType());
        Assert.assertNull(directives.get(0).getRequiresModifier());
        Assert.assertEquals("com.example.foo.http", directives.get(0).getFirstChildOfType(ASTModuleName.class).getImage());
        // requires java.logging;
        Assert.assertEquals(REQUIRES.name(), directives.get(1).getType());
        Assert.assertNull(directives.get(1).getRequiresModifier());
        Assert.assertEquals("java.logging", directives.get(1).getFirstChildOfType(ASTModuleName.class).getImage());
        // requires transitive com.example.foo.network;
        Assert.assertEquals(REQUIRES.name(), directives.get(2).getType());
        Assert.assertEquals(TRANSITIVE.name(), directives.get(2).getRequiresModifier());
        Assert.assertEquals("com.example.foo.network", directives.get(2).getFirstChildOfType(ASTModuleName.class).getImage());
        // exports com.example.foo.bar;
        Assert.assertEquals(EXPORTS.name(), directives.get(3).getType());
        Assert.assertNull(directives.get(3).getRequiresModifier());
        Assert.assertEquals("com.example.foo.bar", directives.get(3).getFirstChildOfType(ASTName.class).getImage());
        // exports com.example.foo.internal to com.example.foo.probe;
        Assert.assertEquals(EXPORTS.name(), directives.get(4).getType());
        Assert.assertNull(directives.get(4).getRequiresModifier());
        Assert.assertEquals("com.example.foo.internal", directives.get(4).getFirstChildOfType(ASTName.class).getImage());
        Assert.assertEquals("com.example.foo.probe", directives.get(4).getFirstChildOfType(ASTModuleName.class).getImage());
        // uses com.example.foo.spi.Intf;
        Assert.assertEquals(USES.name(), directives.get(5).getType());
        Assert.assertNull(directives.get(5).getRequiresModifier());
        Assert.assertEquals("com.example.foo.spi.Intf", directives.get(5).getFirstChildOfType(ASTName.class).getImage());
        // provides com.example.foo.spi.Intf with com.example.foo.Impl;
        Assert.assertEquals(PROVIDES.name(), directives.get(6).getType());
        Assert.assertNull(directives.get(6).getRequiresModifier());
        Assert.assertEquals("com.example.foo.spi.Intf", directives.get(6).getFirstChildOfType(ASTName.class).getImage());
        Assert.assertEquals("com.example.foo.Impl", directives.get(6).findChildrenOfType(ASTName.class).get(1).getImage());
    }
}

