package com.nyan.cckmenubot.repositories;

import java.util.List;

import com.nyan.cckmenubot.entities.Stall;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StallRepository extends JpaRepository<Stall, Integer>{
	
	List<Stall> findByLocationName(String locationName);

}