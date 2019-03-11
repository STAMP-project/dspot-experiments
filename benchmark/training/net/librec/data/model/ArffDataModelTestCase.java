/**
 * Copyright (C) 2016 LibRec
 *
 * This file is part of LibRec.
 * LibRec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibRec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibRec. If not, see <http://www.gnu.org/licenses/>.
 */
package net.librec.data.model;


import Configured.CONF_DATA_INPUT_PATH;
import net.librec.BaseTestCase;
import net.librec.common.LibrecException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * ArffDataMode TestCase
 * {@link net.librec.data.model.ArffDataModel}
 *
 * @author SunYatong
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ArffDataModelTestCase extends BaseTestCase {
    /**
     * Test the function of reading a file.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test01ReadFile() throws LibrecException {
        conf.set(CONF_DATA_INPUT_PATH, "test/arfftest/data.arff");
        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(6, dataModel.getItemMappingData().size());
        Assert.assertEquals(5, dataModel.getUserMappingData().size());
    }

    /**
     * Test the function of reading files from directory and its sub-directories.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test02ReadDir() throws LibrecException {
        conf.set(CONF_DATA_INPUT_PATH, "test/arfftest");
        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(6, dataModel.getItemMappingData().size());
        Assert.assertEquals(5, dataModel.getUserMappingData().size());
    }

    /**
     * test the function of splitter part
     * {@link net.librec.data.splitter.RatioDataSplitter} split the data by
     * rating ratio
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test03RatingRatio() throws LibrecException {
        conf.set(CONF_DATA_INPUT_PATH, "test/hfttest/musical_instruments.arff");
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.trainset.ratio", "0.8");
        conf.set("data.splitter.ratio", "rating");
        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();
        double actualRatio = getTrainRatio(dataModel);
        System.out.println(actualRatio);
        Assert.assertTrue(((Math.abs((actualRatio - 0.8))) <= 0.02));
    }

    /**
     * test the function of splitter part
     * {@link net.librec.data.splitter.RatioDataSplitter} split the data by user
     * ratio
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test04UserRatio() throws LibrecException {
        conf.set(CONF_DATA_INPUT_PATH, "test/hfttest/musical_instruments.arff");
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.trainset.ratio", "0.8");
        conf.set("data.splitter.ratio", "user");
        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();
        double actualRatio = getTrainRatio(dataModel);
        Assert.assertTrue(((Math.abs((actualRatio - 0.8))) <= 0.02));
    }

    /**
     * test the function of splitter part
     * {@link net.librec.data.splitter.RatioDataSplitter} split the data by item
     * ratio
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test05ItemRatio() throws LibrecException {
        conf.set(CONF_DATA_INPUT_PATH, "test/hfttest/musical_instruments.arff");
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.trainset.ratio", "0.8");
        conf.set("data.splitter.ratio", "item");
        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();
        double actualRatio = getTrainRatio(dataModel);
        Assert.assertTrue(((Math.abs((actualRatio - 0.8))) <= 0.02));
    }

    /**
     * test the function of splitter part
     * {@link net.librec.data.splitter.RatioDataSplitter} split the data by
     * rating ratio into 3 sets: train,test,valid.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test06ValidRatio() throws LibrecException {
        conf.set(CONF_DATA_INPUT_PATH, "test/hfttest/musical_instruments.arff");
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.ratio", "valid");
        conf.set("data.splitter.trainset.ratio", "0.5");
        conf.set("data.splitter.validset.ratio", "0.3");
        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();
        double actualTrainRatio = getTrainRatio(dataModel);
        double actualValidRatio = getValidRatio(dataModel);
        Assert.assertTrue(((Math.abs((actualTrainRatio - 0.5))) <= 0.01));
        Assert.assertTrue(((Math.abs((actualValidRatio - 0.3))) <= 0.05));
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.KCVDataSplitter} Split all ratings for
     * k-fold cross validation.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test07KCV() throws LibrecException {
        conf.set(CONF_DATA_INPUT_PATH, "test/arfftest/data.arff");
        conf.set("data.model.splitter", "net.librec.data.splitter.KCVDataSplitter");
        conf.set("data.splitter.cv.number", "5");
        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();
        while (dataModel.hasNextFold()) {
            dataModel.nextFold();
            Assert.assertEquals(8, getTrainSize(dataModel));
            Assert.assertEquals(2, getTestSize(dataModel));
        } 
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.LOOCVDataSplitter} Each user splits out a
     * rating for test set,the rest for train set.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test08LOOByUser() throws LibrecException {
        conf.set(CONF_DATA_INPUT_PATH, "test/arfftest/data.arff");
        conf.set("data.model.splitter", "net.librec.data.splitter.LOOCVDataSplitter");
        conf.set("data.splitter.loocv", "user");
        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getTrainSize(dataModel), 5);
        Assert.assertEquals(getTestSize(dataModel), 5);
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.LOOCVDataSplitter} Each item splits out a
     * rating for test set,the rest for train set.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test09LOOByItem() throws LibrecException {
        conf.set(CONF_DATA_INPUT_PATH, "test/arfftest/data.arff");
        conf.set("data.model.splitter", "net.librec.data.splitter.LOOCVDataSplitter");
        conf.set("data.splitter.loocv", "item");
        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getTrainSize(dataModel), 4);
        Assert.assertEquals(getTestSize(dataModel), 6);
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.GivenNDataSplitter} Each user splits out
     * N ratings for test set,the rest for training set.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test10GivenNByUser() throws LibrecException {
        conf.set(CONF_DATA_INPUT_PATH, "test/arfftest/data.arff");
        conf.set("data.model.splitter", "net.librec.data.splitter.GivenNDataSplitter");
        conf.set("data.splitter.givenn", "user");
        conf.set("data.splitter.givenn.n", "1");
        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getTrainSize(dataModel), 5);
        Assert.assertEquals(getTestSize(dataModel), 5);
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.GivenNDataSplitter} Each item splits out
     * N ratings for test set,the rest for training set.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test11GivenNByItem() throws LibrecException {
        conf.set(CONF_DATA_INPUT_PATH, "test/arfftest/data.arff");
        conf.set("data.model.splitter", "net.librec.data.splitter.GivenNDataSplitter");
        conf.set("data.splitter.givenn", "item");
        conf.set("data.splitter.givenn.n", "1");
        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getTrainSize(dataModel), 6);
        Assert.assertEquals(getTestSize(dataModel), 4);
    }
}

