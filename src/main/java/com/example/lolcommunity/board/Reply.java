package com.example.lolcommunity.board;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reply {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private Long boardId; // 게시판 삭제시 null 되도록

	private String ip;
	private String name;
//	@JsonIgnore
	private String password;

	@Column(columnDefinition = "TEXT")
	private String content;

	private String createdTime;
	private String deletedTime;

//	private String ip;
}
