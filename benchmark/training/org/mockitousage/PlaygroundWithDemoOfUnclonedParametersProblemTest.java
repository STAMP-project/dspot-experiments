/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage;


import java.util.Date;
import java.util.GregorianCalendar;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


public class PlaygroundWithDemoOfUnclonedParametersProblemTest extends TestBase {
    PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportManager importManager;

    PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogDao importLogDao;

    PlaygroundWithDemoOfUnclonedParametersProblemTest.IImportHandler importHandler;

    @Test
    public void shouldIncludeInitialLog() {
        // given
        int importType = 0;
        Date currentDate = new GregorianCalendar(2009, 10, 12).getTime();
        PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean initialLog = new PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean(currentDate, importType);
        initialLog.setStatus(1);
        BDDMockito.given(importLogDao.anyImportRunningOrRunnedToday(importType, currentDate)).willReturn(false);
        BDDMockito.willAnswer(byCheckingLogEquals(initialLog)).given(importLogDao).include(ArgumentMatchers.any(PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean.class));
        // when
        importManager.startImportProcess(importType, currentDate);
        // then
        Mockito.verify(importLogDao).include(ArgumentMatchers.any(PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean.class));
    }

    @Test
    public void shouldAlterFinalLog() {
        // given
        int importType = 0;
        Date currentDate = new GregorianCalendar(2009, 10, 12).getTime();
        PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean finalLog = new PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean(currentDate, importType);
        finalLog.setStatus(9);
        BDDMockito.given(importLogDao.anyImportRunningOrRunnedToday(importType, currentDate)).willReturn(false);
        BDDMockito.willAnswer(byCheckingLogEquals(finalLog)).given(importLogDao).alter(ArgumentMatchers.any(PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean.class));
        // when
        importManager.startImportProcess(importType, currentDate);
        // then
        Mockito.verify(importLogDao).alter(ArgumentMatchers.any(PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean.class));
    }

    public class ImportManager {
        public ImportManager(PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogDao pImportLogDao) {
            super();
            importLogDao = pImportLogDao;
        }

        private PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogDao importLogDao = null;

        public void startImportProcess(int importType, Date date) {
            PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean importLogBean = null;
            try {
                importLogBean = createResume(importType, date);
                if (isOkToImport(importType, date)) {
                    // get the right handler
                    // importLogBean = ImportHandlerFactory.singleton().getImportHandler(importType).processImport(importLogBean);
                    // 2 = ok
                    importLogBean.setStatus(2);
                } else {
                    // 5 = failed - is there a running process
                    importLogBean.setStatus(9);
                }
            } catch (Exception e) {
                // 9 = failed - exception
                if (importLogBean != null)
                    importLogBean.setStatus(9);

            } finally {
                if (importLogBean != null)
                    finalizeResume(importLogBean);

            }
        }

        private boolean isOkToImport(int importType, Date date) {
            return importLogDao.anyImportRunningOrRunnedToday(importType, date);
        }

        private PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean createResume(int importType, Date date) {
            PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean importLogBean = new PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean(date, importType);
            // 1 = running
            importLogBean.setStatus(1);
            importLogDao.include(importLogBean);
            return importLogBean;
        }

        private void finalizeResume(PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean importLogBean) {
            importLogDao.alter(importLogBean);
        }
    }

    private interface ImportLogDao {
        boolean anyImportRunningOrRunnedToday(int importType, Date currentDate);

        void include(PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean importLogBean);

        void alter(PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean importLogBean);
    }

    private class IImportHandler {}

    private class ImportLogBean {
        private Date currentDate;

        private int importType;

        private int status;

        public ImportLogBean(Date currentDate, int importType) {
            this.currentDate = currentDate;
            this.importType = importType;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean))
                return false;

            PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean that = ((PlaygroundWithDemoOfUnclonedParametersProblemTest.ImportLogBean) (o));
            if ((importType) != (that.importType))
                return false;

            if ((status) != (that.status))
                return false;

            if ((currentDate) != null ? !(currentDate.equals(that.currentDate)) : (that.currentDate) != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = ((currentDate) != null) ? currentDate.hashCode() : 0;
            result = (31 * result) + (importType);
            result = (31 * result) + (status);
            return result;
        }
    }
}

