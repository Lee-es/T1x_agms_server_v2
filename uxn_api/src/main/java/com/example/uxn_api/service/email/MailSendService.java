package com.example.uxn_api.service.email;

import com.example.uxn_api.config.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import javax.mail.Session;
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
@PropertySource("classpath:real-application.yml")
@EnableAsync
@Slf4j
public class MailSendService { // 메인 메서드에서 실행했을 시에는 잘 작동 했는데 클래스로 따로 빼니까 @Value 값을 못 가져옴.

        // 1. 발신자의 메일 계정과 비밀번호 설정
        private static String user;
        private static String password;

        @Value("${spring.mail.username}")
        public void setUser(String ymlUserValue){
            user = ymlUserValue;
        }

        @Value("${spring.mail.password}")
        public void setPassword(String ymlPassword){
            password = ymlPassword;
        }


    public void verificationMailSend(String recipient, int num, int idx, String currentCode) throws MessagingException {
        verificationMailSend(recipient,num,idx,currentCode,true);
    }
    @Async
    public void verificationMailSend(String recipient, int num, int idx, String currentCode, boolean isUser) throws MessagingException {

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", 465);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // 3. SMTP 서버정보와 사용자 정보를 기반으로 Session 클래스의 인스턴스 생성
        Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        // 4. Message 클래스의 객체를 사용하여 수신자와 내용, 제목의 메시지를 작성한다.
        // 5. Transport 클래스를 사용하여 작성한 메세지를 전달한다.
        MimeMessage message = new MimeMessage(session);
        switch (num){
            case 1:
                message.setFrom(new InternetAddress(user));
                // 수신자 메일 주소
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                // Subject
                message.setSubject("AGMS 계정 인증 메일");
                // Text
                message.setText("AGMS 사용을 위한 이메일 인증 링크 입니다. 클릭하여 인증을 완료하여 주세요. " + CommonConstant.HOST_WITH_PORT +"/api/v1/mail/confirm/"+recipient+"/"+currentCode);
//                message.setText("이메일 인증 링크 입니다. 클릭하여 인증을 완료하여 주세요. " + "http://localhost:8080/api/v1/mail/confirm/"+recipient+"/"+code);
                log.debug("before transport");
                Transport.send(message);
                log.debug("이메일 인증  메일 전송 완료 ");
                break;// send message

            case 2:
                message.setFrom(new InternetAddress(user));
                // 수신자 메일 주소
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                // Subject
                message.setSubject("AGMS 계정 비밀번호 재 설정 메일");
                // Text
                message.setText("링크를 클릭하여 새로운 비밀번호를 설정해 주세요. "+CommonConstant.FRONT_HOST+ "/changepw/"+recipient + "/"+currentCode);
                log.debug("before transport");
                Transport.send(message);    // send message
                log.debug("비밀번호 메일 전송 완료 ");

                break;

            case 3:
                message.setFrom(new InternetAddress(user));

                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

                // Subject
                message.setSubject("환자 등록 요청");

                // Api 주소 보내서 클릭시 등록
//                message.setText(CommonConstant.HOST_WITH_PORT+ "/api/v1/user/staff-info/check-mail/"+idx+"/"+recipient);
                message.setText("환자 등록 요청이 왔습니다. 승인을 원하시면 클릭해주세요. "+CommonConstant.HOST_WITH_PORT+ "/api/v1/mail/check-mail/"+idx+"/"+recipient);

//                message.setText("http://localhost:8080/api/v1/user/staff-info/check-mail/"+idx+"/"+recipient);
                log.debug("before transport");
                Transport.send(message);    // send message
                log.debug("환자 등록 요청 전송 완료 ");
                break;
            case 4:
                message.setFrom(new InternetAddress(user));
                // 수신자 메일 주소
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                // Subject
                message.setSubject("AGMS 의사 초대 메일");
                // Text
                message.setText("링크를 클릭하면 의사 등록을 하게 됩니다. 로그인 후 링크를 클릭해주세요. "  + CommonConstant.HOST_WITH_PORT+ "/invited/"+currentCode);
                log.debug("before transport");
                Transport.send(message);
                log.debug("이메일 인증  메일 전송 완료 ");
                break;
            case 5:
                message.setFrom(new InternetAddress(user));
                // 수신자 메일 주소
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                // Subject
                message.setSubject("다른 IP에서 로그인을 시도하였습니다.");
                // Text
                message.setText("로그인 중 다른 IP에서 접속을 시도를 했습니다. 본인이 아닐경우 비밀번호 찾기를 통해 비밀번호를 변경 해주세요. "+CommonConstant.FRONT_HOST+ "/changepw/"+recipient + "/"+currentCode);
                log.debug("before transport");
                Transport.send(message);    // send message
                log.debug("비밀번호 메일 전송 완료 ");

                break;
        }

    }



    public static String createKey() {
        StringBuilder key = new StringBuilder();
        Random rnd = new Random(System.currentTimeMillis());

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }

        return key.toString();
    }



}
