/**
 * Copyright (c) 2018. paascloud.net All Rights Reserved.
 * ?????paascloud???????????????
 * ????MailTest.java
 * ???????
 * ?????paascloud.net@gmail.com
 * ????: https://github.com/paascloud
 * ????: http://blog.paascloud.net
 * ????: http://paascloud.net
 */
package com.paascloud.provider.mail;


import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.paascloud.provider.PaasCloudOmcApplicationTests;
import com.paascloud.provider.service.OptFreeMarkerService;
import com.paascloud.provider.service.OptSendMailService;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;


@Slf4j
public class MailTest extends PaasCloudOmcApplicationTests {
    @Resource
    private OptSendMailService optSendMailService;

    @Resource
    private OptFreeMarkerService optVelocityService;

    @Test
    public void sendTemplateMailTest() throws Exception {
        Map<String, Object> map = Maps.newHashMap();
        map.put("username", "paascloud");
        map.put("url", "http://www.beian.gov.cn/uac/user/activeEmail?token=04fd7024ba324d6d841614a7d44507cd");
        map.put("dateTime", "2017-01-01 22:22:22");
        String templateLocation = "mail/sendRegisterSuccessTemplate.ftl";
        String text = optVelocityService.getTemplate(map, templateLocation);
        String subject = "??sendSimpleMail??";
        Set<String> to = Sets.newHashSet();
        to.add("xxxxx@163.com");
        int sendTemplateMail = optSendMailService.sendTemplateMail(subject, text, to);
        log.info("sendTemplateMailTest={}", sendTemplateMail);
    }

    @Test
    public void sendTemplateMailTest2() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("username", "paascloud-sendTemplateMailTest2");
        map.put("url", "http://www.beian.gov.cn/uac/user/activeEmail?token=04fd7024ba324d6d841614a7d44507cd");
        map.put("dateTime", "2017-01-01 22:22:22");
        String templateLocation = "mail/sendRegisterSuccessTemplate.ftl";
        String subject = "??sendSimpleMail??";
        Set<String> to = Sets.newHashSet();
        to.add("xxxx@qq.com");
        int sendTemplateMail = optSendMailService.sendTemplateMail(map, templateLocation, subject, to);
        log.info("sendTemplateMailTest2={}", sendTemplateMail);
    }
}

