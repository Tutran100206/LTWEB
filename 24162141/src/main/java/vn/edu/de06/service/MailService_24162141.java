package vn.edu.de06.service;
import vn.edu.de06.config.AppConfig_24162141;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
public class MailService_24162141 {
    public void sendOtp(String email,String otp) throws MessagingException {
        String username=AppConfig_24162141.get("mail.username","");
        String password=AppConfig_24162141.get("mail.password","");
        boolean auth=Boolean.parseBoolean(AppConfig_24162141.get("mail.auth","true"));
        String from=AppConfig_24162141.get("mail.from",username);
        if(from.isBlank() || (auth && (username.isBlank() || password.isBlank()))) throw new MessagingException("SMTP chưa được cấu hình.");
        Properties p=new Properties();
        p.setProperty("mail.smtp.host",AppConfig_24162141.get("mail.host","smtp.gmail.com"));
        p.setProperty("mail.smtp.port",AppConfig_24162141.get("mail.port","587"));
        p.setProperty("mail.smtp.auth",String.valueOf(auth));
        p.setProperty("mail.smtp.starttls.enable",AppConfig_24162141.get("mail.starttls","true"));
        p.setProperty("mail.smtp.starttls.required",AppConfig_24162141.get("mail.starttls","true"));
        p.setProperty("mail.smtp.connectiontimeout","10000");
        p.setProperty("mail.smtp.timeout","10000");
        p.setProperty("mail.smtp.writetimeout","10000");
        Session session=Session.getInstance(p);
        MimeMessage message=new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email,true));
        message.setSubject("Mã OTP kích hoạt tài khoản – Đề 06","UTF-8");
        message.setText("Mã kích hoạt của bạn: "+otp+"\nMã có hiệu lực 5 phút. Không chia sẻ mã này.","UTF-8");
        try(Transport transport=session.getTransport("smtp")) {
            if(auth) transport.connect(username,password); else transport.connect();
            transport.sendMessage(message,message.getAllRecipients());
        }
    }
}
