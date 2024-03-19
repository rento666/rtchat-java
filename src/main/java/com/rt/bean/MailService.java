package com.rt.bean;

import com.rt.component.SpringContextUtils;
import com.rt.component.value.MailValue;
import com.rt.utils.muban.TransportFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@DependsOn("springContextUtils")
public class MailService {

    private final GenericObjectPool<javax.mail.Transport> transportPool;

    public MailService() {
        GenericObjectPoolConfig<javax.mail.Transport> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(10); // 设置最大连接数

        this.transportPool = new GenericObjectPool<>(new TransportFactory(getSession()), poolConfig);

    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        /**
         * to: 这是邮件的接收者的电子邮件地址。
         *
         * subject: 这是邮件的主题。
         *
         * htmlContent: 这是邮件的 HTML 内容。
         */
        try (javax.mail.Transport transport = transportPool.borrowObject()) {
            Message message = new MimeMessage(getSession());

            MailValue mailValue = getMailValue();
            message.setFrom(new InternetAddress(mailValue.getUsername(),"蝶语"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html;charset=gbk");

            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Session getSession() {
        MailValue mailValue = getMailValue();
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", mailValue.getHost());
        props.put("mail.smtp.port", mailValue.getPort());

        String username = mailValue.getUsername();
        String password = mailValue.getPassword();

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private MailValue getMailValue() {
        return SpringContextUtils.getBean(MailValue.class);
    }
}

