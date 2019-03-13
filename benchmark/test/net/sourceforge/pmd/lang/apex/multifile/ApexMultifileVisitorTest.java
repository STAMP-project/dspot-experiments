/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.multifile;


import ApexProjectMirror.INSTANCE;
import apex.jorje.semantic.ast.compilation.Compilation;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTest;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;
import net.sourceforge.pmd.lang.apex.metrics.ApexSignatureMatcher;
import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSigMask;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import static ApexProjectMirror.INSTANCE;


/**
 *
 *
 * @author Cl?ment Fournier
 */
public class ApexMultifileVisitorTest extends ApexParserTest {
    @Test
    public void testProjectMirrorNotNull() {
        Assert.assertNotNull(INSTANCE);
    }

    @Test
    public void testOperationsAreThere() throws IOException {
        ApexNode<Compilation> acu = ApexMultifileVisitorTest.parseAndVisitForString(IOUtils.toString(ApexMultifileVisitorTest.class.getResourceAsStream("MetadataDeployController.cls"), StandardCharsets.UTF_8));
        final ApexSignatureMatcher toplevel = INSTANCE;
        final ApexOperationSigMask opMask = new ApexOperationSigMask();
        // We could parse qnames from string but probably simpler to do that
        acu.jjtAccept(new ApexParserVisitorAdapter() {
            @Override
            public Object visit(ASTMethod node, Object data) {
                if (!(node.getImage().matches("(<clinit>|<init>|clone)"))) {
                    Assert.assertTrue(toplevel.hasMatchingSig(node.getQualifiedName(), opMask));
                }
                return data;
            }
        }, null);
    }
}

