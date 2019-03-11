package net.librec.data.splitter;


import Configured.CONF_DATA_COLUMN_FORMAT;
import net.librec.BaseTestCase;
import net.librec.data.convertor.TextDataConvertor;
import net.librec.math.structure.SequentialAccessSparseMatrix;
import org.junit.Test;


/**
 * Created by Liuxz on 2018/4/6.
 */
public class ComplexSplitterTestCase extends BaseTestCase {
    private TextDataConvertor convertor;

    private TextDataConvertor convertorWithDate;

    /**
     * Test method splitData with dateMatrix
     *
     * @throws Exception
     * 		
     */
    @Test
    public void RatioPlusKCVTestCase() throws Exception {
        convertor.processData();
        SequentialAccessSparseMatrix allData = convertor.getPreferenceMatrix();
        RatioDataSplitter ratioSplitter = new RatioDataSplitter();
        ratioSplitter.setPreferenceMatrix(allData);
        ratioSplitter.getRatioByRating(0.8);
        SequentialAccessSparseMatrix train = ratioSplitter.getTrainData();
        SequentialAccessSparseMatrix test = ratioSplitter.getTestData();
        KCVDataSplitter kcvSplitter = new KCVDataSplitter();
        kcvSplitter.setPreferenceMatrix(train);
        kcvSplitter.splitData(5);
        SequentialAccessSparseMatrix[] trainSlice = ((SequentialAccessSparseMatrix[]) (kcvSplitter.getAssignMatrixList().toArray(new SequentialAccessSparseMatrix[5])));
        System.out.println("trainSlice cardinality: ");
        for (int i = 0; i < (trainSlice.length); i++) {
            System.out.println(trainSlice[i].size());
        }
        System.out.println(("test cardinality : " + (test.size())));
    }

    @Test
    public void TestSetPlusKCVTestCase() throws Exception {
        conf.set("inputDataPath", ((conf.get("dfs.data.dir")) + "/filmtrust/rating"));
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIR");
        String[] trainList = new String[]{ (conf.get("inputDataPath")) + "/ratings_0.txt", (conf.get("inputDataPath")) + "/ratings_1.txt", (conf.get("inputDataPath")) + "/ratings_2.txt" };
        TextDataConvertor convertorTrain = new TextDataConvertor(conf.get(CONF_DATA_COLUMN_FORMAT), trainList, " ");
        TextDataConvertor convertorTest = new TextDataConvertor(conf.get(CONF_DATA_COLUMN_FORMAT), ((conf.get("inputDataPath")) + "/ratings_3.txt"), " ");
        SequentialAccessSparseMatrix train = convertorTrain.getPreferenceMatrix();
        SequentialAccessSparseMatrix test = convertorTest.getPreferenceMatrix();
        KCVDataSplitter splitter = new KCVDataSplitter();
        splitter.setPreferenceMatrix(train);
        splitter.splitData(5);
        SequentialAccessSparseMatrix[] trainSlice = ((SequentialAccessSparseMatrix[]) (splitter.getAssignMatrixList().toArray(new SequentialAccessSparseMatrix[5])));
        System.out.println("trainSlice cardinality: ");
        for (int i = 0; i < (trainSlice.length); i++) {
            System.out.println(trainSlice[i].size());
        }
        System.out.println(("test cardinality : " + (test.size())));
    }
}

