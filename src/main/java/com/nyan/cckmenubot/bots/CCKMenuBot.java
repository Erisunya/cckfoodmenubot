package com.nyan.cckmenubot.bots;

import java.util.ArrayList;
import java.util.List;

import com.nyan.cckmenubot.config.BotConfig;
import com.nyan.cckmenubot.handlers.MainHandler;
import com.nyan.cckmenubot.repositories.PhotoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CCKMenuBot extends TelegramLongPollingBot{
	
	@Autowired
	private MainHandler handler;
	@Autowired
	private PhotoRepository photoRepository;
	private boolean previousMenu = false;
	private int previousMenuId;
	private List<Integer> previousMenusId;
	
	@Override
	public String getBotUsername() {
		return BotConfig.BOT_USERNAME;
	}

	@Override
	public String getBotToken() {
		return BotConfig.BOT_TOKEN;
	}
	
	@Override
	public void onUpdateReceived(Update update) {
		// SendMessage message = mainHandler.handleUpdate(update);
		System.out.println("Update received!");
				
		if (update.hasCallbackQuery()) {
			if(update.getCallbackQuery().getData().contains("location")) {
				EditMessageText message = handler.handleLocationUpdate(update);
				try {
					execute(message);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			} else if(update.getCallbackQuery().getData().contains("stall")) {
				String[] callDataArray = update.getCallbackQuery().getData().split(";");
				String callData = callDataArray[1];
				if(photoRepository.findByStallName(callData).size() < 2) {
					SendPhoto message = handler.handleStallUpdate(update);
					try {
						Message sentMessage = execute(message);
						deleteMenus(update);
						previousMenuId = sentMessage.getMessageId();
						previousMenu = true;
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				} else {
					SendMediaGroup message = handler.handleStallUpdateMultiple(update);
					try {
						List<Message> sentMessages = execute(message);
						deleteMenus(update);
						previousMenusId = new ArrayList<>();
						for(Message msg: sentMessages) {
							System.out.println(msg.getMessageId());
							previousMenusId.add(msg.getMessageId());
						}
						previousMenu = true;
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}
				
			}
			
		} else if(update.hasMessage() && update.getMessage().hasText()) {
			SendMessage message = handler.handleTextUpdate(update);
			try {
				execute(message);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			} 
		} else {
			log.info(update.getMessage().getFrom().getFirstName() + " sent a message of unknown type.");
			SendMessage  message = new SendMessage().builder()
												.text("Sorry, but I don't recognise what you just sent :'(\n\nWhy not try pressing /menu instead?")
												.chatId(update.getMessage().getChatId())
												.build();
			try {
				execute(message);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			} 
		}

		System.out.println("Update processed!");
	}

	public void deleteMenus(Update update) {
		if(previousMenu) {
			try {
				DeleteMessage deleteMessage = new DeleteMessage().builder()
																.chatId(update.getCallbackQuery().getMessage().getChatId())
																.messageId(previousMenuId)
																.build();
				execute(deleteMessage);
			} catch (Exception e) {
				
			}
			
			try {
				for(Integer messageId: previousMenusId) {
					DeleteMessage deleteMessage = new DeleteMessage().builder()
																.chatId(update.getCallbackQuery().getMessage().getChatId())
																.messageId(messageId)
																.build();
					
					execute(deleteMessage);
				}
			} catch (Exception e) {

			}
			
			previousMenu = false;
		}
	}

}
