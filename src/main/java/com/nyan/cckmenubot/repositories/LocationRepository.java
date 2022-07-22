package com.nyan.cckmenubot.repositories;

import java.util.List;

import com.nyan.cckmenubot.entities.Location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
		
	List<Location> findAllByOrderByLocationName();
	
}
