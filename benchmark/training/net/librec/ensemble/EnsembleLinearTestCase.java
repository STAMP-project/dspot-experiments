package net.librec.ensemble;


import java.io.IOException;
import net.librec.BaseTestCase;
import net.librec.common.LibrecException;
import org.junit.Test;


/**
 *
 *
 * @author logicxin
 */
public class EnsembleLinearTestCase extends BaseTestCase {
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
        // Creat an ensemble object
        String configFilePath = "rec/ensemble/ensemble-linear.properties";
        EnsembleLinear ensembleJob = new EnsembleLinear(configFilePath);
        ensembleJob.trainModel();
        // Get ensemble weight
        // List<Double> weightList = ensembleJob.ensembelWeight();
        // Get ensemble result
        // List ensenbleResult = ensembleJob.recommendedResult();
        // Save ensembel result
        // Boolean ensenbleResultSave = ensembleJob.saveRecommendResult("rec/ensemble/ensemble-linear.properties");
    }
}

