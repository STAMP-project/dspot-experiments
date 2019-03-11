/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics;


import java.util.List;
import java.util.Random;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.impl.AbstractJavaClassMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.AbstractJavaOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.testdata.MetricsVisitorTestData;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKeyUtil;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Cl?ment Fournier
 */
public class ProjectMemoizerTest {
    private MetricKey<ASTAnyTypeDeclaration> classMetricKey = MetricKeyUtil.of(null, new ProjectMemoizerTest.RandomClassMetric());

    private MetricKey<MethodLikeNode> opMetricKey = MetricKeyUtil.of(null, new ProjectMemoizerTest.RandomOperationMetric());

    @Test
    public void memoizationTest() {
        ASTCompilationUnit acu = ParserTstUtil.parseJavaDefaultVersion(MetricsVisitorTestData.class);
        List<Integer> expected = visitWith(acu, true);
        List<Integer> real = visitWith(acu, false);
        Assert.assertEquals(expected, real);
    }

    @Test
    public void forceMemoizationTest() {
        ASTCompilationUnit acu = ParserTstUtil.parseJavaDefaultVersion(MetricsVisitorTestData.class);
        List<Integer> reference = visitWith(acu, true);
        List<Integer> real = visitWith(acu, true);
        Assert.assertEquals(reference.size(), real.size());
        // we force recomputation so each result should be different
        for (int i = 0; i < (reference.size()); i++) {
            Assert.assertNotEquals(reference.get(i), real.get(i));
        }
    }

    private class RandomOperationMetric extends AbstractJavaOperationMetric {
        private Random random = new Random();

        @Override
        public double computeFor(MethodLikeNode node, MetricOptions options) {
            return random.nextInt();
        }
    }

    private class RandomClassMetric extends AbstractJavaClassMetric {
        private Random random = new Random();

        @Override
        public double computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
            return random.nextInt();
        }
    }
}

