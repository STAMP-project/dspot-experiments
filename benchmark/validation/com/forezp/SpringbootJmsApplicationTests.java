package com.forezp;


import java.io.File;
import javax.mail.internet.MimeMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootJmsApplicationTests {
    @Autowired
    private JavaMailSenderImpl mailSender;

    /**
     * ???????????
     */
    @Test
    public void sendTxtMail() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        // ?????????
        simpleMailMessage.setTo(new String[]{ "miles02@163.com" });
        simpleMailMessage.setFrom("miles02@163.com");
        simpleMailMessage.setSubject("Spring Boot Mail ????????");
        simpleMailMessage.setText("??????????");
        // ????
        mailSender.send(simpleMailMessage);
        System.out.println("?????");
    }

    /**
     * ????HTML?????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void sendHtmlMail() throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo("miles02@163.com");
        mimeMessageHelper.setFrom("miles02@163.com");
        mimeMessageHelper.setSubject("Spring Boot Mail ?????HTML?");
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head></head>");
        sb.append("<body><h1>spring ????</h1><p>hello!this is spring mail test?</p></body>");
        sb.append("</html>");
        // ??html
        mimeMessageHelper.setText(sb.toString(), true);
        // ????
        mailSender.send(mimeMessage);
        System.out.println("?????");
    }

    /**
     * ???????????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void sendAttachedImageMail() throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // multipart??
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo("miles02@163.com");
        mimeMessageHelper.setFrom("miles02@163.com");
        mimeMessageHelper.setSubject("Spring Boot Mail ????????");
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head></head>");
        sb.append("<body><h1>spring ????</h1><p>hello!this is spring mail test?</p>");
        // cid??????imageId??????
        sb.append("<img src=\"cid:imageId\"/></body>");
        sb.append("</html>");
        // ??html
        mimeMessageHelper.setText(sb.toString(), true);
        // ??imageId
        FileSystemResource img = new FileSystemResource(new File("E:/1.jpg"));
        mimeMessageHelper.addInline("imageId", img);
        // ????
        mailSender.send(mimeMessage);
        System.out.println("?????");
    }

    /**
     * ?????????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void sendAttendedFileMail() throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // multipart??
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
        mimeMessageHelper.setTo("miles02@163.com");
        mimeMessageHelper.setFrom("miles02@163.com");
        mimeMessageHelper.setSubject("Spring Boot Mail ????????");
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head></head>");
        sb.append("<body><h1>spring ????</h1><p>hello!this is spring mail test?</p></body>");
        sb.append("</html>");
        // ??html
        mimeMessageHelper.setText(sb.toString(), true);
        // ????
        FileSystemResource img = new FileSystemResource(new File("E:/1.jpg"));
        mimeMessageHelper.addAttachment("image.jpg", img);
        // ????
        mailSender.send(mimeMessage);
        System.out.println("?????");
    }
}

