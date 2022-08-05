package com.nyan.cckmenubot.handlers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nyan.cckmenubot.entities.Photo;
import com.nyan.cckmenubot.repositories.PhotoRepository;
import com.nyan.cckmenubot.repositories.StallRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
@Component
public class StallHandler {
	
	private long chatId;
	private int messageId;
	private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
	@Autowired
	private StallRepository stallRepository;
	@Autowired
	private PhotoRepository photoRepository;
	
	public SendPhoto handleUpdate(Update update) {
		
		// CallbackData is a String in the format "stall;(stallId)"
		String[] callDataArray = update.getCallbackQuery().getData().split(";");
		String locationName = stallRepository.findFirstByStallId(Integer.parseInt(callDataArray[1])).getLocationName();
		String stallName = stallRepository.findFirstByStallId(Integer.parseInt(callDataArray[1])).getStallName();
		chatId = update.getCallbackQuery().getMessage().getChatId();
		messageId = update.getCallbackQuery().getMessage().getMessageId();
		
		showDisplayMenuLogMessage(update, stallName);
				
		String fileId = null;
		String caption = null;
		for(Photo photo: photoRepository.findByStallNameAndLocationName(stallName, locationName)) {
			fileId = photo.getFileId();
			caption = createPhotoCaption(photo, stallName);
		}
		
		SendPhoto message = new SendPhoto().builder()
								.chatId(chatId)
								.photo(new InputFile(fileId))
								.caption(caption)
								.parseMode("HTML")
								.build();
										
		return message;
	}
	

	public SendMediaGroup handleUpdateMultiple(Update update) {
		
		// CallbackData is a String in the format "stall;(stallId)"
		String[] callDataArray = update.getCallbackQuery().getData().split(";");
		String locationName = stallRepository.findFirstByStallId(Integer.parseInt(callDataArray[1])).getLocationName();
		String stallName = stallRepository.findFirstByStallId(Integer.parseInt(callDataArray[1])).getStallName();
		chatId = update.getCallbackQuery().getMessage().getChatId();
		messageId = update.getCallbackQuery().getMessage().getMessageId();
		
		showDisplayMenuLogMessage(update, stallName);
				
		String fileId = null;
		List<InputMedia> photosList = new ArrayList<>();
		for(Photo photo: photoRepository.findByStallNameAndLocationName(stallName, locationName)) {
			fileId = photo.getFileId();
			InputMediaPhoto inputPhoto = new InputMediaPhoto().builder()
												.media(fileId)
												.parseMode("HTML")
												.build();
			if(photosList.isEmpty()) {
				inputPhoto.setCaption(createPhotoCaption(photo, stallName));
			}
			photosList.add(inputPhoto);
		}
		SendMediaGroup message = new SendMediaGroup().builder()
										.chatId(chatId)
										.medias(photosList)
										.build();
										
		return message;
	}
	
	private void showDisplayMenuLogMessage(Update update, String stallName) {
		if(update.getCallbackQuery().getFrom().getUserName() == null) {
			log.info("Displaying menu from " + stallName + " as requested by user " + update.getCallbackQuery().getFrom().getFirstName() + ".");
		} else {
			log.info("Displaying menu from " + stallName + " as requested by @" + update.getCallbackQuery().getFrom().getUserName() + ".");
		}
	}
	
	private String createPhotoCaption(Photo photo, String stallName) {
		String messageText = "<b>" + stallName + "</b>\n";
		messageText += "Last updated on: " + formatter.format(photo.getLastUpdatedTime()) + "\n";
		
		String halalStatus = stallRepository.findFirstByStallName(stallName).getHalalStatus();
		if(halalStatus.equals("N")) {
			messageText += "Halal: No\n";
		} else if (halalStatus.equals("Y")) {
			messageText += "Halal: Yes\n";
		}
		return messageText;
	}
}
