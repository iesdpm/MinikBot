package es.iesdpm.telegram.config;

public class Bot {
	private String token;
	private String botname;
	private Long chatId;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getBotname() {
		return botname;
	}

	public void setBotname(String botname) {
		this.botname = botname;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

}
