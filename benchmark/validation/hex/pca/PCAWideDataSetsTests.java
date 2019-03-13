package hex.pca;


import DataInfo.TransformType;
import PCAParameters.Method.GLRM;
import PCAParameters.Method.GramSVD;
import PCAParameters.Method.Power;
import PCAParameters.Method.Randomized;
import hex.pca.PCAModel.PCAParameters;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import water.TestUtil;


/**
 * Created by wendycwong on 2/27/17.
 */
@RunWith(Parameterized.class)
public class PCAWideDataSetsTests extends TestUtil {
    public static final double _TOLERANCE = 1.0E-6;

    public static String _smallDataset = "smalldata/pca_test/decathlon.csv";

    public static final String _prostateDataset = "smalldata/prostate/prostate_cat.csv";

    public static final TransformType[] _transformTypes = new TransformType[]{ TransformType.NONE, TransformType.STANDARDIZE, TransformType.DEMEAN, TransformType.DESCALE };

    public Random _rand = new Random();

    public PCAModel _golden = null;// 


    private PCAParameters pcaParameters;

    @Parameterized.Parameter
    public hex.pca.PCAImplementation PCAImplementation;

    /* This unit test will test that pca method GramSVD works with wide datasets.  It will first build a model
    using GramSVD under normal setting (_wideDataset is set to false).  Next, it builds a GramSVD model with
    _wideDataSet set to true.  The eigenvalues and eigenvectors from the two models are compared.  Test will fail
    if any difference exceeds 1e-6.

    Six test cases are used:
    case 1. we test with a small dataset with all numerical data columns and make sure it works.
    case 2. we add NA rows to the	small dataset with all numerical data columns.
    case 3. test with the same small dataset while preserving the categorical columns;
    case 4. test with the same small dataset with categorical columns and add NA rows;
    case 5. test with prostate dataset;
    case 6. test with prostate dataset with NA rows added.
     */
    @Test
    public void testWideDataSetGramSVD() throws InterruptedException, ExecutionException {
        ActualPCATests.testWideDataSets(GramSVD, GramSVD, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, false, true, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 1

        ActualPCATests.testWideDataSets(GramSVD, GramSVD, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, true, true, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 2

        ActualPCATests.testWideDataSets(GramSVD, GramSVD, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, false, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 3

        ActualPCATests.testWideDataSets(GramSVD, GramSVD, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, true, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 4

        ActualPCATests.testWideDataSets(GramSVD, GramSVD, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._prostateDataset, false, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 5

        ActualPCATests.testWideDataSets(GramSVD, GramSVD, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._prostateDataset, true, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 6

    }

    /* This unit test will test that pca method Power works with wide datasets.  It will first build a model
    using GramSVD under normal setting (_wideDataset is set to false).  Next, it builds a Power model with
    _wideDataSet set to true.  The eigenvalues and eigenvectors from the two models are compared.  Test will fail
    if any difference exceeds 1e-6.

    The same six test cases are used here.
     */
    @Test
    public void testWideDataSetPower() throws InterruptedException, ExecutionException {
        ActualPCATests.testWideDataSets(GramSVD, Power, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, false, true, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 1

        ActualPCATests.testWideDataSets(GramSVD, Power, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, true, true, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 2

        ActualPCATests.testWideDataSets(GramSVD, Power, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, false, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 3

        ActualPCATests.testWideDataSets(GramSVD, Power, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, true, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 4

        ActualPCATests.testWideDataSets(GramSVD, Power, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._prostateDataset, false, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 5

        ActualPCATests.testWideDataSets(GramSVD, Power, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._prostateDataset, true, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 6

    }

    /* This unit test will test that pca method Randomized works with wide datasets.  It will first build a model
    using GramSVD under normal setting (_wideDataset is set to false).  Next, it builds a Randomized model with
    _wideDataSet set to true.  The eigenvalues and eigenvectors from the two models are compared.  Test will fail
    if any difference exceeds 1e-6.

    The same six test cases are used here.
     */
    @Test
    public void testWideDataSetRandomized() throws InterruptedException, ExecutionException {
        ActualPCATests.testWideDataSets(GramSVD, Randomized, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, false, true, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 1

        ActualPCATests.testWideDataSets(GramSVD, Randomized, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, true, true, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 2

        ActualPCATests.testWideDataSets(GramSVD, Randomized, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, false, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 3

        ActualPCATests.testWideDataSets(GramSVD, Randomized, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, true, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 4

        ActualPCATests.testWideDataSets(GramSVD, Randomized, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._prostateDataset, false, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 5

        ActualPCATests.testWideDataSets(GramSVD, Randomized, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._prostateDataset, true, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 6

    }

    /* This unit test will test that pca method GLRM works with wide datasets.  It will first build a model
    using GramSVD under normal setting (_wideDataset is set to false).  Next, it builds a GLRM model with
    _wideDataSet set to true.  The eigenvalues and eigenvectors from the two models are compared.  Test will fail
    if any difference exceeds 1e-6 only for numerical dataset.  For categorical datasets, GLRM will not
    generate the same set of eigenvalues/vectors.  Hence for datasets with Enum columns, we will compare the two
    models built with normal and widedataset GLRM.
     */
    @Test
    public void testWideDataSetGLRM() throws InterruptedException, ExecutionException {
        ActualPCATests.testWideDataSets(GramSVD, GLRM, 1.0E-4, PCAWideDataSetsTests._smallDataset, false, true, PCAWideDataSetsTests._transformTypes[1], pcaParameters);// case 1 numerical

        ActualPCATests.testWideDataSets(GLRM, GLRM, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, true, true, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 2

        ActualPCATests.testWideDataSets(GLRM, GLRM, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, false, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 3

        ActualPCATests.testWideDataSets(GLRM, GLRM, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._smallDataset, true, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 4

        ActualPCATests.testWideDataSets(GLRM, GLRM, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._prostateDataset, false, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 5

        ActualPCATests.testWideDataSets(GLRM, GLRM, PCAWideDataSetsTests._TOLERANCE, PCAWideDataSetsTests._prostateDataset, true, false, PCAWideDataSetsTests._transformTypes[_rand.nextInt(PCAWideDataSetsTests._transformTypes.length)], pcaParameters);// case 6

    }
}

