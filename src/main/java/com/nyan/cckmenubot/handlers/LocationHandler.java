package com.nyan.cckmenubot.handlers;

import java.util.ArrayList;
import java.util.List;

import com.nyan.cckmenubot.entities.Stall;
import com.nyan.cckmenubot.repositories.LocationRepository;
import com.nyan.cckmenubot.repositories.StallRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
@Component
public class LocationHandler {

	private String callData;
	private long chatId;
	private int messageId;

	@Autowired
	private StallRepository stallRepository;

	public EditMessageText handleUpdate(Update update) {
		
		// CallbackData is a String in the format "location;(location name)"
		String[] callDataArray = update.getCallbackQuery().getData().split(";");
		callData = callDataArray[1];
		chatId = update.getCallbackQuery().getMessage().getChatId();
		messageId = update.getCallbackQuery().getMessage().getMessageId();

		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		// Each list item corresponds to one keyboard row
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
		// Each list item corresponds to one keyboard button
		List<InlineKeyboardButton> rowInline = new ArrayList<InlineKeyboardButton>();
		
		if(update.getCallbackQuery().getFrom().getUserName() == null) {
			log.info("Displaying stalls from " + callData + " as requested by user " + update.getCallbackQuery().getFrom().getFirstName() + ".");
		} else {
			log.info("Displaying stalls from " + callData + " as requested by @" + update.getCallbackQuery().getFrom().getUserName() + ".");
		}
		
		StringBuilder messageText = new StringBuilder();
		messageText.append("Choose one of these stalls in " + callData + " to view its menu:");

		// Implement logic to show stalls once location is selected
		for (Stall stall : stallRepository.findByLocationNameOrderByStallName(callData)) {
			messageText.append("\n- " + stall.getStallName());
			InlineKeyboardButton button = new InlineKeyboardButton();
			button.setText(stall.getStallName());
			// CallbackData is a String in the format "location;(location name);stall;(stall name)"
			// ***CALLBACKDATA CAN ONLY HOLD A MAXIMUM OF 64 CHARACTERS***
			button.setCallbackData(update.getCallbackQuery().getData() + ";stall;" + stall.getStallName());
			
			// Splits the buttons into rows of two
			if(rowInline.size()>=2) {
				rowsInline.add(rowInline);
				rowInline = new ArrayList<InlineKeyboardButton>();
				rowInline.add(button);
			} else {
				rowInline.add(button);
			}			
		}
		
		rowsInline.add(rowInline);
		
		messageText.append("\n\n<i>Note that the prices reflected in the menus may not be up to date.</i>");

		markupInline.setKeyboard(rowsInline);

		EditMessageText message = new EditMessageText().builder()
													.chatId(chatId)
													.messageId(messageId)
													.text(messageText.toString())
													.replyMarkup(markupInline)
													.parseMode("HTML")
													.build();

		return message;
	}

}
