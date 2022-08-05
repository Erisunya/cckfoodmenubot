package com.nyan.cckmenubot.handlers;

import java.util.ArrayList;
import java.util.List;

import com.nyan.cckmenubot.entities.Location;
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

	private String locationName;
	private long chatId;
	private int messageId;
	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private StallRepository stallRepository;

	public EditMessageText handleUpdate(Update update) {
		
		// CallbackData is a String in the format "location;(locationId in locations)"
		String[] callDataArray = update.getCallbackQuery().getData().split(";");
		locationName = locationRepository.findFirstByLocationId(Integer.parseInt(callDataArray[1])).getLocationName();
		chatId = update.getCallbackQuery().getMessage().getChatId();
		messageId = update.getCallbackQuery().getMessage().getMessageId();

		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		// Each list item corresponds to one keyboard row
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
		// Each list item corresponds to one keyboard button
		List<InlineKeyboardButton> rowInline = new ArrayList<InlineKeyboardButton>();
		
		if(update.getCallbackQuery().getFrom().getUserName() == null) {
			log.info("Displaying stalls from " + locationName + " as requested by user " + update.getCallbackQuery().getFrom().getFirstName() + ".");
		} else {
			log.info("Displaying stalls from " + locationName + " as requested by @" + update.getCallbackQuery().getFrom().getUserName() + ".");
		}
		
		StringBuilder messageText = new StringBuilder();
		messageText.append("Choose one of these stalls in " + locationName + " to view its menu:");

		// Implement logic to show stalls once location is selected
		for (Stall stall : stallRepository.findByLocationNameOrderByStallName(locationName)) {
			if(isStallHalal(stall).equals("Y")) {
				messageText.append("\n- " + stall.getStallName() + " (H)");
			} else {
				messageText.append("\n- " + stall.getStallName());
			}
			InlineKeyboardButton button = new InlineKeyboardButton();
			button.setText(stall.getStallName());
			// CallbackData is a String in the format "stall;(stallId in stalls)"
			button.setCallbackData("stall;" + stall.getStallId());
			
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
		
		messageText.append("\n\n<i>Note that the prices reflected in the menus may not be up to date.\n(H) denotes <b>known</b> halal options.</i>");

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

	private String isStallHalal(Stall stall) {
		
		return stall.getHalalStatus();
	}

}
