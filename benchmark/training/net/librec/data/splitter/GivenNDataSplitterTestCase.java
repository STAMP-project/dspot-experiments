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
package net.librec.data.splitter;


import Configured.CONF_DATA_COLUMN_FORMAT;
import net.librec.BaseTestCase;
import net.librec.data.convertor.TextDataConvertor;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * GivenNDataSplitter TestCase
 * {@link net.librec.data.splitter.GivenNDataSplitter}
 *
 * @author Liuxz and Sunyt
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GivenNDataSplitterTestCase extends BaseTestCase {
    private TextDataConvertor convertor;

    private TextDataConvertor convertorWithDate;

    /**
     * Test the methods splitData and getGivenNByUser
     * givennbyuser: Each user splits out N ratings for test set,the rest for training set.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test01GivenNByUser() throws Exception {
        conf.set("data.splitter.givenn", "user");
        conf.set("data.splitter.givenn.n", "1");
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIR");
        convertor.processData();
        GivenNDataSplitter splitter = new GivenNDataSplitter(convertor, conf);
        splitter.splitData();
        Assert.assertEquals(splitter.getTrainData().size(), 4);
        Assert.assertEquals(splitter.getTestData().size(), 9);
    }

    /**
     * Test the methods splitData and getGivenNByItem
     * givennbyitem: Each user splits out N ratings for test set,the rest for training set.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test02GivenNByItem() throws Exception {
        conf.set("data.splitter.givenn", "item");
        conf.set("data.splitter.givenn.n", "1");
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIR");
        convertor.processData();
        GivenNDataSplitter splitter = new GivenNDataSplitter(convertor, conf);
        splitter.splitData();
        Assert.assertEquals(splitter.getTrainData().size(), 4);
        Assert.assertEquals(splitter.getTestData().size(), 9);
    }

    /**
     * Test the methods splitData and getGivenNByUserDate
     * givennbyuserdate: Each user splits out N ratings for test set,the rest for training set.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test03GivenNByUserDate() throws Exception {
        conf.set("data.splitter.givenn", "userdate");
        conf.set("data.splitter.givenn.n", "1");
        convertorWithDate.processData();
        GivenNDataSplitter splitter = new GivenNDataSplitter(convertorWithDate, conf);
        splitter.splitData();
        Assert.assertEquals(splitter.getTrainData().size(), 4);
        Assert.assertEquals(splitter.getTestData().size(), 9);
    }

    /**
     * Test the methods splitData and getGivenNByItemDate
     * givennbyitemdate: Each item splits out N ratings for test set,the rest for training set.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test04GivenNByItemDate() throws Exception {
        conf.set("data.splitter.givenn", "itemdate");
        conf.set("data.splitter.givenn.n", "1");
        convertorWithDate.processData();
        GivenNDataSplitter splitter = new GivenNDataSplitter(convertorWithDate, conf);
        splitter.splitData();
        Assert.assertEquals(splitter.getTrainData().size(), 4);
        Assert.assertEquals(splitter.getTestData().size(), 9);
    }
}

