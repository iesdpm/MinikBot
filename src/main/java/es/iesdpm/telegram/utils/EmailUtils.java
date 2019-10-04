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
		if (emails.length == 0) return;
		new Thread(() -> {
			boolean send = false;
			while (!send) { 
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
					send = true;
				} catch (EmailException e) {
					e.printStackTrace();
					try {
						Log.warn("Waiting {0} seconds to retry", 5000L);
						Thread.sleep(5000L);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}

			}
		}).start();
	}

}
