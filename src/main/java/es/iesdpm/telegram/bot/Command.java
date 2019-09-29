package es.iesdpm.telegram.bot;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface Command {
	
	public void action(Long chatId, Message message);

}
