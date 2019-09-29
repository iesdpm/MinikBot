package es.iesdpm.telegram.bot;

import java.io.IOException;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {

	public static void main(String[] args) throws TelegramApiException, SecurityException, IOException {		
		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		telegramBotsApi.registerBot(new Bot());
	}

}
