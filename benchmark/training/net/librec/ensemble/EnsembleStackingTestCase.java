package net.librec.ensemble;


import java.io.IOException;
import net.librec.BaseTestCase;
import net.librec.common.LibrecException;
import net.librec.math.algorithm.Randoms;
import org.junit.Test;


/**
 *
 *
 * @author logicxin
 */
public class EnsembleStackingTestCase extends BaseTestCase {
    /**
     *
     *
     * @throws ClassNotFoundException
     * 		
     * @throws LibrecException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testEnsemble() throws IOException, ClassNotFoundException, Exception, LibrecException {
        // Ensemble ensembleJob = new EnsembleStacking("rec/ensemble/ensemble-linear.properties");
    }

    @Test
    public void testKFold() throws IOException, ClassNotFoundException, Exception, LibrecException {
        // testCase  core code, resources, data
        System.out.println(Randoms.uniform());
        EnsembleStacking ensembleJob = new EnsembleStacking("rec/ensemble/ensemble-stacking.properties");
        ensembleJob.dataKFlodSpliter(5, 0.2);
        // System.out.println(Randoms.uniform());
    }
}

