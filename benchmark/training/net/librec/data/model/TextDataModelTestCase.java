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


import Configured.CONF_DATA_COLUMN_FORMAT;
import Configured.CONF_DATA_INPUT_PATH;
import net.librec.BaseTestCase;
import net.librec.common.LibrecException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * TextDataMode TestCase {@link net.librec.data.model.TextDataModel}
 *
 * @author Liuxz and Sunyt
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TextDataModelTestCase extends BaseTestCase {
    /**
     * test the function of convertor part
     * {@link net.librec.data.convertor.TextDataConvertor} input data subject to
     * format UIR: userId itemId rating
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test01ColumnFormatUIR() throws LibrecException {
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIR");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/matrix4by4.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getDataSize(dataModel), 13);
    }

    /**
     * test the function of convertor part
     * {@link net.librec.data.convertor.TextDataConvertor} input data subject to
     * format UIRT: userId itemId rating date
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test02ColumnFormatUIRT() throws LibrecException {
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIRT");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/matrix4by4-date.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getDataSize(dataModel), 13);
    }

    /**
     * test the function of convertor part
     * {@link net.librec.data.convertor.TextDataConvertor} read all files in the
     * specified directory and its subdirectories
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test03SubDir() throws LibrecException {
        conf.set(CONF_DATA_INPUT_PATH, "test/test-convert-dir");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getDataSize(dataModel), 26);
    }

    /**
     * test the function of convertor part
     * {@link net.librec.data.convertor.TextDataConvertor} test three different
     * types of CSV : "," " " and "\t"
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test04CSV() throws LibrecException {
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/testCSV.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getDataSize(dataModel), 13);
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
    public void test05RatingRatio() throws LibrecException {
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.trainset.ratio", "0.8");
        conf.set("data.splitter.ratio", "rating");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        double actualRatio = getTrainRatio(dataModel);
        Assert.assertTrue(((Math.abs((actualRatio - 0.8))) <= 0.01));
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
    public void test06UserRatio() throws LibrecException {
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.trainset.ratio", "0.8");
        conf.set("data.splitter.ratio", "user");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        double actualRatio = getTrainRatio(dataModel);
        Assert.assertTrue(((Math.abs((actualRatio - 0.8))) <= 0.01));
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
    public void test07ItemRatio() throws LibrecException {
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.trainset.ratio", "0.8");
        conf.set("data.splitter.ratio", "item");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        double actualRatio = getTrainRatio(dataModel);
        Assert.assertTrue(((Math.abs((actualRatio - 0.8))) <= 0.01));
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
    public void test08ValidRatio() throws LibrecException {
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.ratio", "valid");
        conf.set("data.splitter.trainset.ratio", "0.5");
        conf.set("data.splitter.validset.ratio", "0.3");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        double actualTrainRatio = getTrainRatio(dataModel);
        Assert.assertTrue(((Math.abs((actualTrainRatio - 0.5))) <= 0.05));
        double actualValidRatio = getValidRatio(dataModel);
        Assert.assertTrue(((Math.abs((actualValidRatio - 0.3))) <= 0.05));
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.RatioDataSplitter} Sort all ratings by
     * date,and split the data by rating ratio.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test09RatingDateRatio() throws LibrecException {
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.trainset.ratio", "0.8");
        conf.set("data.splitter.ratio", "ratingdate");
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIRT");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/ratings-date.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        double actualRatio = getTrainRatio(dataModel);
        Assert.assertTrue(((Math.abs((actualRatio - 0.8))) <= 0.01));
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.RatioDataSplitter} Sort each user's
     * ratings by date, and split by user ratio.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test10UserDateRatio() throws LibrecException {
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.trainset.ratio", "0.8");
        conf.set("data.splitter.ratio", "userdate");
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIRT");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/ratings-date.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        double actualRatio = getTrainRatio(dataModel);
        Assert.assertTrue(((Math.abs((actualRatio - 0.8))) <= 0.02));
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.RatioDataSplitter} Sort each item's
     * ratings by date, and split by item ratio.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test11ItemDateRatio() throws LibrecException {
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.trainset.ratio", "0.8");
        conf.set("data.splitter.ratio", "itemdate");
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIRT");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/ratings-date.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        double actualRatio = getTrainRatio(dataModel);
        Assert.assertTrue(((Math.abs((actualRatio - 0.8))) <= 0.04));
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
    public void test12KCV() throws LibrecException {
        conf.set("data.model.splitter", "net.librec.data.splitter.KCVDataSplitter");
        conf.set("data.splitter.cv.number", "6");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/matrix4by4A-date.txt");
        conf.set("data.column.format", "UIRT");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        while (dataModel.hasNextFold()) {
            dataModel.nextFold();
            Assert.assertEquals(getTrainSize(dataModel), 10);
            Assert.assertEquals(getTestSize(dataModel), 2);
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
    public void test13LOOByUser() throws LibrecException {
        conf.set("data.model.splitter", "net.librec.data.splitter.LOOCVDataSplitter");
        conf.set("data.splitter.loocv", "user");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/matrix4by4.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getTrainSize(dataModel), 9);
        Assert.assertEquals(getTestSize(dataModel), 4);
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
    public void test14LOOByItem() throws LibrecException {
        conf.set("data.model.splitter", "net.librec.data.splitter.LOOCVDataSplitter");
        conf.set("data.splitter.loocv", "item");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/matrix4by4.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getTrainSize(dataModel), 9);
        Assert.assertEquals(getTestSize(dataModel), 4);
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.LOOCVDataSplitter} Each user splits out a
     * rating with biggest date value for test set,the rest for train set.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test15LOOByUserDate() throws LibrecException {
        conf.set("data.model.splitter", "net.librec.data.splitter.LOOCVDataSplitter");
        conf.set("data.splitter.loocv", "userdate");
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIRT");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/matrix4by4-date.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getTrainSize(dataModel), 9);
        Assert.assertEquals(getTestSize(dataModel), 4);
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.LOOCVDataSplitter} Each item splits out a
     * rating with biggest date value for test set,the rest for train set.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test16LOOByItemDate() throws LibrecException {
        conf.set("data.model.splitter", "net.librec.data.splitter.LOOCVDataSplitter");
        conf.set("data.splitter.loocv", "itemdate");
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIRT");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/matrix4by4-date.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getTrainSize(dataModel), 9);
        Assert.assertEquals(getTestSize(dataModel), 4);
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
    public void test17GivenNByUser() throws LibrecException {
        conf.set("data.model.splitter", "net.librec.data.splitter.GivenNDataSplitter");
        conf.set("data.splitter.givenn", "user");
        conf.set("data.splitter.givenn.n", "1");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/matrix4by4.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getTrainSize(dataModel), 4);
        Assert.assertEquals(getTestSize(dataModel), 9);
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
    public void test18GivenNByItem() throws LibrecException {
        conf.set("data.model.splitter", "net.librec.data.splitter.GivenNDataSplitter");
        conf.set("data.splitter.givenn", "item");
        conf.set("data.splitter.givenn.n", "1");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/matrix4by4.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getTrainSize(dataModel), 4);
        Assert.assertEquals(getTestSize(dataModel), 9);
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.GivenNDataSplitter} each user split out N
     * ratings with biggest value of date for test set,the rest for training
     * set.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test19GivenNByUserDate() throws LibrecException {
        conf.set("data.model.splitter", "net.librec.data.splitter.GivenNDataSplitter");
        conf.set("data.splitter.givenn", "userdate");
        conf.set("data.splitter.givenn.n", "1");
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIRT");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/matrix4by4-date.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getTrainSize(dataModel), 4);
        Assert.assertEquals(getTestSize(dataModel), 9);
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.GivenNDataSplitter} Each item split out N
     * ratings with biggest value of date for test set,the rest for training
     * set.
     *
     * @throws LibrecException
     * 		
     */
    @Test
    public void test20GivenNByItemDate() throws LibrecException {
        conf.set("data.model.splitter", "net.librec.data.splitter.GivenNDataSplitter");
        conf.set("data.splitter.givenn", "itemdate");
        conf.set("data.splitter.givenn.n", "1");
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIRT");
        conf.set(CONF_DATA_INPUT_PATH, "test/datamodeltest/matrix4by4-date.txt");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();
        Assert.assertEquals(getTrainSize(dataModel), 4);
        Assert.assertEquals(getTestSize(dataModel), 9);
    }
}

