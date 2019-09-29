package fvarrui.telegram.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Config {
	private Bot bot = new Bot();
	private Email email = new Email();
	private List<String> subscribers = new ArrayList<>();
	private List<String> admins = new ArrayList<>();

	public Bot getBot() {
		return bot;
	}

	public void setBot(Bot bot) {
		this.bot = bot;
	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	public List<String> getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(List<String> subscribers) {
		this.subscribers = subscribers;
	}

	public List<String> getAdmins() {
		return admins;
	}

	public void setAdmins(List<String> admins) {
		this.admins = admins;
	}
	
	public static Config load(File file) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
		return gson.fromJson(json, Config.class);
	}

	public void save(File file) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(this);
		FileUtils.writeStringToFile(file, json, Charset.forName("UTF-8"));
	}

}
