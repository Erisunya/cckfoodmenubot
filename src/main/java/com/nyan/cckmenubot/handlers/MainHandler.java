package com.nyan.cckmenubot.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Component
public class MainHandler {
	
	@Autowired
	private MenuHandler menuHandler;
	@Autowired
	private LocationHandler locationHandler;
	@Autowired
	private StallHandler stallHandler;
	@Autowired
	private FeedbackHandler feedbackHandler;
	private String messageText;
	private Boolean feedbackMode = false;

	public SendMessage handleTextUpdate(Update update) {
		
		messageText = update.getMessage().getText();
		
		// feedbackMode=true would mean that the user has provided some feedback as their text message]
		//
		// !Character.toString(messageText.charAt(0)).equals("/") allows users to escape feedback mode
		// if they pressed /feedback by accident
		if(feedbackMode && !Character.toString(messageText.charAt(0)).equals("/")) {
			feedbackMode = false;
			return feedbackHandler.receivedFeedback(update);
		} else {
			feedbackMode = false;
		}
		
		if(messageText.equals("/start") || messageText.equals("/menu")){
			return menuHandler.handleUpdate(update);
		} else if (messageText.equals("/feedback")) {
			feedbackMode = true;
			return feedbackHandler.newFeedback(update);
		}  else {
			log.info(update.getMessage().getFrom().getFirstName() + " sent a message of unknown type.");
			return new SendMessage().builder()
									.text("Sorry, but I don't recognise what you just sent :'(\n\nWhy not try pressing /menu instead?")
									.chatId(update.getMessage().getChatId())
									.build();
		}
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
