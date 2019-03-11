package com.yiqiniu.easytrans.test;


import com.yiqiniu.easytrans.core.ConsistentGuardian;
import com.yiqiniu.easytrans.log.TransactionLogReader;
import com.yiqiniu.easytrans.log.impl.database.DataBaseTransactionLogConfiguration.DataBaseForLog;
import com.yiqiniu.easytrans.log.vo.LogCollection;
import com.yiqiniu.easytrans.rpc.EasyTransRpcConsumer;
import com.yiqiniu.easytrans.test.mockservice.accounting.AccountingService;
import com.yiqiniu.easytrans.test.mockservice.coupon.CouponService;
import com.yiqiniu.easytrans.test.mockservice.express.ExpressService;
import com.yiqiniu.easytrans.test.mockservice.order.OrderService;
import com.yiqiniu.easytrans.test.mockservice.point.PointService;
import com.yiqiniu.easytrans.test.mockservice.wallet.WalletService;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EasyTransTestConfiguration.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class FullTest {
    @Resource(name = "wholeJdbcTemplate")
    private JdbcTemplate wholeJdbcTemplate;

    @Resource
    OrderService orderService;

    @Resource
    private ConsistentGuardian guardian;

    @Resource
    private TransactionLogReader logReader;

    @Value("${spring.application.name}")
    private String applicationName;

    @Resource
    private EasyTransRpcConsumer consumer;

    @Resource
    private AccountingService accountingService;

    @Resource
    private ExpressService expressService;

    @Resource
    private PointService pointService;

    @Resource
    private WalletService walletService;

    @Autowired(required = false)
    private DataBaseForLog dbForLog;

    @Resource
    private CouponService couponService;

    private ExecutorService executor = Executors.newFixedThreadPool(4);

    // private ExecutorService executor = Executors.newFixedThreadPool(1);
    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    private int concurrentTestId = 100000;

    private boolean enableFescarTest = true;

    /**
     * ???????????????kafka ?trxtest??????
     * executed_trans?idempotent???????readme.md????????????????
     * ?????????????translog??????trans_log_unfinished?trans_log_detail???????readme.md?
     * ???KAFKA???????kafka??????????KAFKA?????????????,?????????????????,?????????KAFKA????kafka????????TOPIC?
     * trx-test-service_NotReliableOrderMsg
     * trx-test-service_reconsume_0
     * trx-test-service_reconsume_1
     * trx-test-service_reconsume_2
     * trx-test-service_ReliableOrderMsg
     * trx-test-service_ReliableOrderMsgCascade
     *
     *
     * ????????????????????????????????????????????
     * ??????????????????????????????????????????????
     *
     * ????????????????????????????????UT?????????????ut??????????assert
     *
     * UT??????????RPC???????????????????????????????????????
     */
    @Test
    public void test() {
        enableFescarTest = true;// fescar?????????????????commit??????????fescar???

        try {
            // synchronizer test
            // ??????????
            cleanAndSetUp();
            // ???????????????????
            commitedAndSubTransSuccess();
            sleepForFescar();
            // ?????COMMIT????????
            rollbackWithExceptionJustBeforeCommit();
            sleepForFescar();
            // ???????????????????????
            rollbackWithExceptionInMiddle();
            sleepForFescar();
            // ?????????????????????????
            rollbackWithExceptionJustAfterStartEasyTrans();
            // consistent guardian test
            // ?????????????????????????
            commitWithExceptionInMiddleOfConsistenGuardian();
            sleepForFescar();
            // ???????????????????????????
            rollbackWithExceptionInMiddleOfConsistenGuardian();
            sleepForFescar();
            // SAGA sucess test
            sagaSuccessTest();
            sleep(1000);// saga try is async, wait for try finished then execute rollbackWithExceptionInSagaTry

            rollbackWithExceptionInSagaTry();
            sleepForFescar();
            // idempotent test
            // ?????
            activateThreadPool();
            // ????TCC??
            sameMethodConcurrentTcc();
            // ?????????
            differentMethodConcurrentCompensable();
            // wallet 7000
            // account waste 2000
            // ExpressCount 2
            // point 3000
            // coupon 9998
            // ??????
            executeTccOnly();// wallet 6000

            executeCompensableOnly();// account waste 3000

            executeAfterTransMethodOnly();// ExpressCount 3

            executeReliableMsgOnly();// point 4000

            executeNotReliableMessageOnly();
            // ??????????
            testMessageQueueConsumeFailue();// point 6000

            // ??????????
            orderService.buySomethingCascading(1, 1000, enableFescarTest);// wallet 5000,account waste 4000,express count 4, point 7000,coupon 9997

            sleepForFescar();
            // ????????
            walletService.setExceptionOccurInCascadeTransactionBusinessEnd(true);
            try {
                orderService.buySomethingCascading(1, 1000, enableFescarTest);
            } catch (Exception e) {
            }
            walletService.setExceptionOccurInCascadeTransactionBusinessEnd(false);
            sleepForFescar();
            // ????????????????
            orderService.setCascadeTrxFinishedSleepMills(10000);
            orderService.buySomethingCascading(1, 1000, enableFescarTest);// wallet 4000,account waste 5000,express count 5, point 8000,coupon 9996

            orderService.setCascadeTrxFinishedSleepMills(0);
            // ????????x
            orderService.buySomethingWithAutoGenId(1, 1000);// wallet 3000,account waste 5000,express count 5, point 8000,coupon 9996

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        // // ?????????????????????????
        // // execute consistent guardian in case of timeout
        // List<LogCollection> unfinishedLogs = logReader.getUnfinishedLogs(null, 100, new Date());
        // for (LogCollection logCollection : unfinishedLogs) {
        // guardian.process(logCollection);
        // }
        // ??ConsistentDameon?????????????????????????5????????????30???????UT????????????
        sleep(30000);
        // execute consistent guardian in case of timeout
        List<LogCollection> unfinishedLogs = logReader.getUnfinishedLogs(null, 100, new Date());
        Assert.assertTrue("should be clean by daemon", unfinishedLogs.isEmpty());
        // ??????????????????
        executeFescarATOnly();// 9995

        // wallet 3000,account waste 5000,express count 5, point 7000,coupon 9995
        Assert.assertTrue(((walletService.getUserTotalAmount(1)) == 3000));
        Assert.assertTrue(((walletService.getUserFreezeAmount(1)) == 0));
        Assert.assertTrue(((accountingService.getTotalCost(1)) == 5000));
        Assert.assertTrue(((expressService.getUserExpressCount(1)) == 5));
        Assert.assertTrue(((pointService.getUserPoint(1)) == 8000));
        if (enableFescarTest) {
            Assert.assertTrue(((couponService.getUserCoupon(1)) == 9995));
        }
        System.out.println("Test Passed!!");
        testFescarLocalLock();
    }
}

