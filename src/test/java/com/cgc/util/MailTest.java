package com.cgc.util;

import com.cgc.CommunityApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void mailSendTest() {
        mailClient.sendMail("1906616510@qq.com", "TEST", "hello this a test");
    }

    @Test
    public void mailSendHtmlTest() {
        //Context 是thymeleaf提供的在后端封装数据用的对象，把数据以键值对的形式存在这里，传给模板引擎
        Context context = new Context();
        context.setVariable("email", "xingchen066@sina.com");
        context.setVariable("url", "www.baidu.com");

        //把context中的数据放入html模板
        String content = templateEngine.process("/mail/activation.html", context);
        mailClient.sendMail("1906616510@qq.com", "激活链接", content);
    }
}
