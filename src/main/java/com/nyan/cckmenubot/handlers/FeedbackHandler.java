package com.nyan.cckmenubot.handlers;

import com.nyan.cckmenubot.entities.Feedback;
import com.nyan.cckmenubot.repositories.FeedbackRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
@Component
public class FeedbackHandler {
	
	@Autowired
	private FeedbackRepository feedbackRepository;
	
	public SendMessage newFeedback(Update update) {
		
		SendMessage message = new SendMessage().builder()
											.text("Please enter your feedback and hit send. \n\nIf you ended up here by accident, fret not! Just press /menu to head back to the main page.")
											.chatId(update.getMessage().getChatId())
											.build();
		
		return message;
	}

	public SendMessage receivedFeedback(Update update) {
		
		log.info("User " + update.getMessage().getFrom().getFirstName() + " has provided some feedback.");;
		
		Feedback feedback = new Feedback();
		feedback.setFeedback(update.getMessage().getText());
		feedback.setUsername(update.getMessage().getFrom().getFirstName());
		feedbackRepository.save(feedback);
		
		SendMessage message = new SendMessage().builder()
											.text("Thank you for your feedback!\n\nPlease press /menu to head back to the main page.")
											.chatId(update.getMessage().getChatId())
											.build();
		
		return message;
	}
	
}
