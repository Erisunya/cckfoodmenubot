package com.nyan.cckmenubot.repositories;

import com.nyan.cckmenubot.entities.DevUpdate;
import com.nyan.cckmenubot.entities.Feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevUpdateRepository extends JpaRepository<DevUpdate, Integer> {
	
	DevUpdate findFirstByOrderByFeedbackIdDesc();
	
}
