/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.showcase.excel.service;


import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.test.BaseIT;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;


/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-7-3 ??3:43
 * <p>Version: 1.0
 */
@Ignore
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class ImportExcelDateServiceIT extends BaseIT {
    @Autowired
    private ExcelDataService excelDataService;

    private User user;

    /**
     * csv??
     */
    @Test
    public void testImportCsv() throws IOException {
        File file = new File("D:\\Backup\\test.csv");
        excelDataService.importCvs(user, new FileInputStream(file));
    }

    /**
     * ?? excel 2003 biff??
     * ???xml??? ????SAX?????
     */
    @Test
    public void testImportExcel2003() throws Exception {
        String fileName = "D:\\Backup\\test.xls";
        excelDataService.importExcel2003(user, new FileInputStream(fileName));
    }

    @Test
    public void testImportExcel2007() throws Exception {
        String fileName = "D:\\Backup\\test.xlsx";
        excelDataService.importExcel2007(user, new FileInputStream(fileName));
    }
}

