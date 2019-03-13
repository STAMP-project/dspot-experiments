/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.showcase.excel.service;


import com.sishuok.es.common.entity.search.Searchable;
import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.test.BaseIT;
import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-7-3 ??3:43
 * <p>Version: 1.0
 */
/**
 * excel 2003 ??????biff?http://www.openoffice.org/sc/excelfileformat.pdf?
 */
@Ignore
public class ExportExcelDateServiceIT extends BaseIT {
    @Autowired
    private ExcelDataService excelDataService;

    private User user;

    @Test
    public void testExportCSV() throws IOException {
        excelDataService.exportCvs(user, "D:\\Backup", Searchable.newSearchable());
    }

    /**
     * ????????
     * excel 2007 ??sheet??1048576?
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testExportExcel2007() throws IOException {
        excelDataService.exportExcel2007(user, "D:\\Backup", Searchable.newSearchable());
    }

    /**
     * excel 2003
     * ??1 ???????
     * ??sheet??65536?(???usermodel?????????? ??flush?? ?????????)
     */
    @Test
    public void testExportExcel2003WithUsermodel() throws IOException {
        excelDataService.exportExcel2003WithUsermodel(user, "D:\\Backup", Searchable.newSearchable());
    }

    /**
     * excel 2003
     * ??2 ?xml????
     * 1??????????XML??
     * 2??????????????????excel???????????????????????????
     * 3????????testExportExcel2003_3
     * <p/>
     * ?????????
     * <p/>
     * ?????html???????sheet?
     */
    @Test
    public void testExportExcel2003WithXml() throws IOException {
        excelDataService.exportExcel2003WithXml(user, "D:\\Backup", Searchable.newSearchable());
    }

    /**
     * ??3  ???workbook
     * 1??????vbs ????
     * 2???????c#????
     * <p/>
     * ?????? ????????????????office 2007 ????
     */
    @Test
    public void testExportExcel2003WithOneSheetPerWorkBook() throws IOException {
        excelDataService.exportExcel2003WithOneSheetPerWorkBook(user, "D:\\Backup", Searchable.newSearchable());
    }
}

