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
 * LOOCVDataSplitter TestCase
 * {@link net.librec.data.splitter.LOOCVDataSplitter}
 *
 * @author Liuxz and Sunyt
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LOOCVDataSplitterTestCase extends BaseTestCase {
    private TextDataConvertor convertor;

    private TextDataConvertor convertorWithDate;

    /**
     * Test the methods splitData and getLOOByUser
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test01LOOByUser() throws Exception {
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIR");
        conf.set("data.splitter.loocv", "user");
        convertor.processData();
        LOOCVDataSplitter splitter = new LOOCVDataSplitter(convertor, conf);
        splitter.splitData();
        Assert.assertEquals(splitter.getTrainData().size(), 9);
        Assert.assertEquals(splitter.getTestData().size(), 4);
    }

    /**
     * Test the methods splitData and getLOOByItem
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test02LOOByItem() throws Exception {
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIR");
        conf.set("data.splitter.loocv", "item");
        convertor.processData();
        LOOCVDataSplitter splitter = new LOOCVDataSplitter(convertor, conf);
        splitter.splitData();
        Assert.assertEquals(splitter.getTrainData().size(), 9);
        Assert.assertEquals(splitter.getTestData().size(), 4);
    }

    /**
     * Test the methods splitData and getLOOByUserDate
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test03LOOByUserDate() throws Exception {
        conf.set("data.splitter.loocv", "userdate");
        convertorWithDate.processData();
        LOOCVDataSplitter splitter = new LOOCVDataSplitter(convertorWithDate, conf);
        splitter.splitData();
        Assert.assertEquals(splitter.getTrainData().size(), 9);
        Assert.assertEquals(splitter.getTestData().size(), 4);
    }

    /**
     * Test the methods splitData and getLOOByItemDate
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test04LOOByItemDate() throws Exception {
        conf.set("data.splitter.loocv", "itemdate");
        convertorWithDate.processData();
        LOOCVDataSplitter splitter = new LOOCVDataSplitter(convertorWithDate, conf);
        splitter.splitData();
        Assert.assertEquals(splitter.getTrainData().size(), 9);
        Assert.assertEquals(splitter.getTestData().size(), 4);
    }
}

