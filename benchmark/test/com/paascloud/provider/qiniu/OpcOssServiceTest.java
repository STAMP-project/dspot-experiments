/**
 * Copyright (c) 2018. paascloud.net All Rights Reserved.
 * ?????paascloud???????????????
 * ????OpcOssServiceTest.java
 * ???????
 * ?????paascloud.net@gmail.com
 * ????: https://github.com/paascloud
 * ????: http://blog.paascloud.net
 * ????: http://paascloud.net
 */
package com.paascloud.provider.qiniu;


import com.paascloud.config.properties.PaascloudProperties;
import com.paascloud.provider.PaasCloudOmcApplicationTests;
import com.paascloud.provider.service.OpcOssService;
import com.qiniu.common.QiniuException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.annotation.Resource;
import org.junit.Test;


public class OpcOssServiceTest extends PaasCloudOmcApplicationTests {
    @Resource
    private OpcOssService optOssService;

    @Resource
    private PaascloudProperties paascloudProperties;

    @Test
    public void uploadFileTest() throws IOException, InterruptedException {
        File file = new File("C:\\Users\\Lenovo\\Pictures\\love\\love6.jpg");
        FileInputStream inputStream = new FileInputStream(file);
        // DefaultPutRet defaultPutRet = optOssService.uploadFile(inputStream, "love6.jpg", "picture", "paascloud-oss-bucket");
        // logger.info("?? ???? defaultPutRet={}", defaultPutRet);
    }

    @Test
    public void deleteFileTest() throws QiniuException {
        optOssService.deleteFile("picture/2017-08-03/2a55cf5b4fab42d0984c7fa4c3d1f95f.jpg", "paascloud-oss-bucket");
        logger.info("????");
    }

    @Test
    public void batchDeleteFileTest() throws QiniuException {
        String[] arr = new String[]{ "love1.jpg", "image/jpg/love3.jpg" };
        optOssService.batchDeleteFile(arr, "paascloud-oss-bucket");
        logger.info("????");
    }

    @Test
    public void getFileUrlTest() throws QiniuException {
        String fileUrl = optOssService.getFileUrl(paascloudProperties.getQiniu().getOss().getPrivateHost(), "alipay/QRCode14e5d60fb30d4f7ea83ed3ec936ec54d.png", 3600L);
        logger.info("??File url?? fileUrl={}", fileUrl);
    }

    @Test
    public void getFileUrlTest2() throws QiniuException {
        String fileUrl = optOssService.getFileUrl(paascloudProperties.getQiniu().getOss().getPrivateHost(), "alipay/QRCode/5be9c68b531f471bb85948a4954d5e6f.png");
        logger.info("??File url?? fileUrl={}", fileUrl);
    }
}

