package com.example.lolcommunity.board;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class IpList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long boardId;

	private String ip;
}
