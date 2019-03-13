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
import org.junit.Test;


/**
 * KCVDataSplitter TestCase {@link net.librec.data.splitter.KCVDataSplitter}
 *
 * @author Liuxz and Sunyt
 */
public class KCVDataSplitterTestCase extends BaseTestCase {
    private TextDataConvertor convertor;

    private TextDataConvertor convertorWithDate;

    /**
     * Test method splitData with dateMatrix
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testKCVWithoutDate() throws Exception {
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIR");
        convertor.processData();
        KCVDataSplitter splitter = new KCVDataSplitter(convertor, conf);
        splitter.splitData();
        while (splitter.nextFold()) {
            Assert.assertEquals(splitter.getTrainData().size(), 10);
            Assert.assertEquals(splitter.getTestData().size(), 2);
        } 
    }

    /**
     * Test method splitData without dateMatrix
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testKCVWithDate() throws Exception {
        conf.set(CONF_DATA_COLUMN_FORMAT, "UIRT");
        convertorWithDate.processData();
        KCVDataSplitter splitter = new KCVDataSplitter(convertorWithDate, conf);
        splitter.splitData();
        while (splitter.nextFold()) {
            Assert.assertEquals(splitter.getTrainData().size(), 10);
            Assert.assertEquals(splitter.getTestData().size(), 2);
        } 
    }
}

