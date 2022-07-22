package com.nyan.cckmenubot.repositories;

import java.util.List;

import com.nyan.cckmenubot.entities.Photo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer>{

	List<Photo> findByStallName(String stallName);

	
}
