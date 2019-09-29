package es.iesdpm.telegram.utils;

import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class EmailUtils {
	
	public static es.iesdpm.telegram.config.Email emailConfig; 
	
	public static void sendEmail(String message, List<String> emails) {
		sendEmail(message, emails.toArray(new String[emails.size()]));		
	}
	
	public static void sendEmail(String message, String ... emails) {
		new Thread(() -> {
			try {
				Email email = new SimpleEmail();
				email.setHostName(emailConfig.getSmtp().getHost());
				email.setSmtpPort(emailConfig.getSmtp().getPort());
				email.setAuthenticator(new DefaultAuthenticator(emailConfig.getUsername(), emailConfig.getPassword()));
				email.setSSLOnConnect(true);
				email.setFrom(emailConfig.getUsername());
				email.setSubject("Telegram del Departamento de Informática");
				email.setMsg(message);
				email.addBcc(emails);
				email.send();
			} catch (EmailException e) {
				e.printStackTrace();
			}
		}).start();
	}

}
