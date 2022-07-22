package com.nyan.cckmenubot.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class MainHandler {
	
	@Autowired
	private MenuHandler menuHandler;
	@Autowired
	private LocationHandler locationHandler;
	@Autowired
	private StallHandler stallHandler;
	private String messageText;

	public SendMessage handleTextUpdate(Update update) {
		
		messageText = update.getMessage().getText();
		
		if(messageText.equals("/start") || messageText.equals("/menu")){
			return menuHandler.handleUpdate(update);
		}
		
		return null;
	}
	
	public EditMessageText handleLocationUpdate(Update update) {
		
		return locationHandler.handleUpdate(update);		
	}
	
	public SendPhoto handleStallUpdate(Update update) {
		
		return stallHandler.handleUpdate(update);
	}
	
	public SendMediaGroup handleStallUpdateMultiple(Update update) {
		
		return stallHandler.handleUpdateMultiple(update);
	}
	
}
