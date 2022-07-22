package com.nyan.cckmenubot.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "photos")
public class Photo {
	
	@Id
	private int photoId;
	@Column(name = "stall_name")
	private String stallName;
	@Column(name = "file_id")
	private String fileId;
	@Column(name = "created_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdTime;
	@Column(name = "last_updated_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdatedTime;
	
}
