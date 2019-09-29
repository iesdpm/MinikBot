package es.iesdpm.telegram.bot;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.EmailValidator;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import es.iesdpm.telegram.config.Config;
import es.iesdpm.telegram.utils.EmailUtils;
import es.iesdpm.telegram.utils.Log;

public class Bot extends TelegramLongPollingBot {

	private static final Pattern COMMAND_PATTERN = Pattern.compile("^/(\\w+)\\b+(.*)$");
	private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

	private File configFile = new File("config.json"); 
	private Config config;
	
	private Map<String, Command> commands;
	private Command defaultCommand;
	
	public Bot() throws IOException {
		super();
		
		config = Config.load(configFile);
		EmailUtils.emailConfig = config.getEmail();
		
		Log.info("Bot running ... botname: {0}, token: {1}", config.getBot().getBotname(), config.getBot().getToken());
		Log.info("Authorized group chat id: {0}", config.getBot().getChatId());
		Log.info("Admins: {0}", config.getAdmins());
			
		commands = new HashMap<>();
		commands.put("ayuda", 		(id,mesg) -> help(id, mesg));
		commands.put("help", 		(id,mesg) -> help(id, mesg));
		commands.put("start", 		(id,mesg) -> help(id, mesg));
		commands.put("hola", 		(id,mesg) -> hello(id, mesg));
		commands.put("adios", 		(id,mesg) -> bye(id, mesg)); 
		commands.put("ciao", 		(id,mesg) -> bye(id, mesg)); 
		commands.put("suscribir", 	(id,mesg) -> subscribe(id, mesg)); 
		commands.put("anular", 		(id,mesg) -> cancel(id, mesg)); 
		commands.put("listar", 		(id,mesg) -> list(id, mesg));
		
		defaultCommand = (id,mesg) -> sendEmail(id, mesg);
		
	}
	
	private String extractBody(Message mesg) {
		String text = mesg.getText();
		int i = text.indexOf(" ");
		if (i == -1) return "";
		return text.substring(i);
	}
	
	private boolean isAdmin(String username) {
		return config.getAdmins().contains(username);
	}

	private void cancel(Long id, Message mesg) {
		String email = extractBody(mesg).trim();
		String username = mesg.getFrom().getUserName();
		
		if (!isAdmin(username)) {
			send(id, "¡Eeeeepp @" + username + "! No estás autorizado para cancelar la suscripción de nadie");
			return;
		}
		
		if (!EMAIL_VALIDATOR.isValid(email)) {
			send(id, "¿¡¡¿Qué dirección de email es esa, Maikel Nai?!!?!?!");
			return;
		}
		
		if (config.getSubscribers().remove(email)) {
			send(id, "¡Una pena! Ya no podré spamear a " + email);
			try {
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			send(id, "No sé qué email es ese");
	
	}

	private void subscribe(Long id, Message mesg) {
		String email = extractBody(mesg).trim();
		String username = mesg.getFrom().getUserName();
		
		if (!isAdmin(username)) {
			send(id, "¡Eeeeepp @" + username + "! No estás autorizado para suscribir a nadie");
			return;
		}
		
		if (!EMAIL_VALIDATOR.isValid(email)) {
			send(id, "¿¡¡¿Qué dirección de email es esa, Maikel Nai?!!?!?!");
			return;
		}
		
		config.getSubscribers().add(email);
		send(id, "¡Muy bien! A partir de ahora enviaré todas las tonterías que se dicen en este grupo al email " + email);
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void list(Long id, Message mesg) {
		if (config.getSubscribers().isEmpty())
			send(id, "No hay nadie suscrito, de momento.");
		else
			send(id, "Estos son los emails suscritos: " + config.getSubscribers());
	}

	private void sendEmail(Long id, Message mesg) {

		
		LocalDateTime time = LocalDateTime.ofEpochSecond(mesg.getDate(), 0, ZoneOffset.UTC);
		
		String fullname = (mesg.getFrom().getFirstName() + " " + mesg.getFrom().getLastName()).trim();
		
		String message = "[" + time + "] @" + mesg.getFrom().getUserName() + (fullname.isEmpty() ? "" : " (" + fullname + ")") + ": " + mesg.getText(); 

		Log.info("Enviando email: {0}", message);
		
		EmailUtils.sendEmail(message, config.getSubscribers());

	}

	private void bye(Long id, Message mesg) {
		send(id, "¡Ciao @" + mesg.getFrom().getUserName() + "!");
	}

	private void hello(Long id, Message mesg) {
		send(id, "¡Hola @" + mesg.getFrom().getUserName() + "!");
	}

	private void help(Long id, Message mesg) {
		send(id, 
				"¡Hola, soy " + getBotUsername() + "!\n" +
				"Me encargo de enviar los mensajes de este grupo a una lista de distribución." + "\n" +
				"Si me quieres dar alguna orden, puedes probar con las siguientes (no olvides la '/' del principio):" + "\n" + 
				"- /listar = Te digo los emails que hay suscritos" + "\n" + 
				"- /suscribir <email> = Añado el email indicado a la lista de distribución" + "\n" + 
				"- /cancelar <email> = Elimino el email indicado de la lista de distribución" + "\n" + 
				"- /ayuda = Muestro esta ayuda" + "\n" +
				"¡Aviso! Cualquier mensaje enviado a este grupo se enviará a los emails suscritos, excepto los mensajes que envío yo y las órdenes que me dan."
			);
	}

	@Override
	public String getBotUsername() {
		return config.getBot().getBotname();
	}

	@Override
	public String getBotToken() {
		return config.getBot().getToken();
	}

	@Override
	public void onUpdateReceived(Update update) {		
		
		Log.info("{0}", update.getMessage());
		
		Long chatId = update.getMessage().getChatId();
		String text = update.getMessage().getText();
		String command = null;
		
		if (text == null || text.isEmpty()) return;
		
		if (!chatId.equals(config.getBot().getChatId())) {
			send(chatId, "Lo siento pringao, no estoy autorizado a participar en este chat");
			return;
		}
		
        Matcher matcher = COMMAND_PATTERN.matcher(text);
        if (matcher.find()) {
            command = matcher.group(1).trim();
            if (command != null && commands.containsKey(command)) {
            	commands.get(command).action(chatId, update.getMessage());
            } else {
            	send(chatId, "El comando " + command + " es desconocido para mi. RTFM!");
            }
        } else {
        	defaultCommand.action(chatId, update.getMessage());
        }
		
	}

	private void send(Long chatId, String text) {
		Log.info("chatId: {0}, text: {1}", chatId, text);
	    try {
	        SendMessage echoMessage = new SendMessage();
	        echoMessage.setChatId(chatId);
	        echoMessage.setText(text);
	        execute(echoMessage);
	    } catch (TelegramApiException e) {
			Log.error(e.getMessage());
	    	e.printStackTrace();
	    }
	}

}
