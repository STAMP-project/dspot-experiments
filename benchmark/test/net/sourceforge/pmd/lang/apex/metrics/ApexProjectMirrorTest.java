/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.metrics;


import apex.jorje.semantic.ast.compilation.Compilation;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.metrics.impl.AbstractApexClassMetric;
import net.sourceforge.pmd.lang.apex.metrics.impl.AbstractApexOperationMetric;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileVisitorTest;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKeyUtil;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Cl?ment Fournier
 */
public class ApexProjectMirrorTest {
    private static ApexNode<Compilation> acu;

    private MetricKey<ASTUserClassOrInterface<?>> classMetricKey = MetricKeyUtil.of(null, new ApexProjectMirrorTest.RandomClassMetric());

    private MetricKey<ASTMethod> opMetricKey = MetricKeyUtil.of(null, new ApexProjectMirrorTest.RandomOperationMetric());

    static {
        try {
            ApexProjectMirrorTest.acu = ApexProjectMirrorTest.parseAndVisitForString(IOUtils.toString(ApexMultifileVisitorTest.class.getResourceAsStream("MetadataDeployController.cls"), StandardCharsets.UTF_8));
        } catch (IOException ioe) {
            // Should definitely not happen
        }
    }

    @Test
    public void memoizationTest() {
        List<Integer> expected = visitWith(ApexProjectMirrorTest.acu, true);
        List<Integer> real = visitWith(ApexProjectMirrorTest.acu, false);
        Assert.assertEquals(expected, real);
    }

    @Test
    public void forceMemoizationTest() {
        List<Integer> reference = visitWith(ApexProjectMirrorTest.acu, true);
        List<Integer> real = visitWith(ApexProjectMirrorTest.acu, true);
        Assert.assertEquals(reference.size(), real.size());
        // we force recomputation so each result should be different
        for (int i = 0; i < (reference.size()); i++) {
            Assert.assertNotEquals(reference.get(i), real.get(i));
        }
    }

    private class RandomOperationMetric extends AbstractApexOperationMetric {
        private Random random = new Random();

        @Override
        public double computeFor(ASTMethod node, MetricOptions options) {
            return random.nextInt();
        }
    }

    private class RandomClassMetric extends AbstractApexClassMetric {
        private Random random = new Random();

        @Override
        public double computeFor(ASTUserClassOrInterface<?> node, MetricOptions options) {
            return random.nextInt();
        }
    }
}

