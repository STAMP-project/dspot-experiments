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
public class EnsembleWaterfallTestCase extends BaseTestCase {
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
        Ensemble ensembleJob = new EnsembleWaterfall("rec/ensemble/ensemble-linear.properties");
    }
}

