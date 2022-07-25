package com.nyan.cckmenubot.handlers;

import java.util.ArrayList;
import java.util.List;

import com.nyan.cckmenubot.entities.Location;
import com.nyan.cckmenubot.repositories.DevUpdateRepository;
import com.nyan.cckmenubot.repositories.LocationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
@Component
public class MenuHandler {
	
	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private DevUpdateRepository devUpdateRepository;
	
	public SendMessage handleUpdate(Update update) {
		
		String updateMessage = devUpdateRepository.findFirstByOrderByFeedbackIdDesc().getDevUpdate();
		
		SendMessage message = new SendMessage();
		message.setChatId(update.getMessage().getChatId());
		if(update.getMessage().getText().equals("/start")) {
			message.setText(updateMessage + "Hello! Thanks for trying out CCK Menu Bot. Choose one of the following locations to view the stalls:");
		} else if (update.getMessage().getText().equals("/menu")) {
			message.setText(updateMessage + "Welcome back! Choose one of the following locations to view the stalls:");
		}
		
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		// Each list item corresponds to one keyboard row
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
		// Each list item corresponds to one keyboard button
		List<InlineKeyboardButton> rowInline = new ArrayList<InlineKeyboardButton>();
		
		// Iterates over all locations in the locations database and
		// creates and adds a new keyboard button for each location to
		// the list of keyboard buttons
		for(Location location: locationRepository.findAllByOrderByLocationName()) {
			InlineKeyboardButton button = new InlineKeyboardButton();
			button.setText(location.getLocationName());
			// CallbackData is a String in the format "location;(locationId in locations)"
			button.setCallbackData("location;" + location.getLocationId());
			
			if(rowInline.size()>=2) {
				rowsInline.add(rowInline);
				rowInline = new ArrayList<InlineKeyboardButton>();
				rowInline.add(button);
			} else {
				rowInline.add(button);
			}
		}
		
		// Adding the row to the list of keyboard rows
		rowsInline.add(rowInline);
		
		markupInline.setKeyboard(rowsInline);
		message.setReplyMarkup(markupInline);
		message.setParseMode("HTML");
		
		return message;
	}
	
}
