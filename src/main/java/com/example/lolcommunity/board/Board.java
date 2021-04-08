package com.example.lolcommunity.board;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

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
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String type; // 글분류 ex.질문, 자유, 전략 구분
	private String title;

	private String name;
	private String ip;
//	@JsonIgnore // 사용하려다 컨트롤러에서  sha256으로 변형시키려면 꺼내야하는데 getPassword()를 못써서
	private String password;

	@Column(columnDefinition = "TEXT") // TEXT
	private String content;

	private String createdTime;
	private String modifiedTime;
	private String deletedTime;

	private int hitCnt; // 조회수
	private int upCnt; // 추천수
	private int downCnt; // 비추천수
	private int replyCnt; // 댓글수
	@OneToMany
	@JoinColumn(name = "boardId") // nullable = true (default)
	private List<UpList> upList; // 추천수, ip로 추천수 구분
	@OneToMany
	@JoinColumn(name = "boardId")
	private List<DownList> downList; // 비추천수, ip로 추천수 구분
	@OneToMany
	@JoinColumn(name = "boardId")
	private List<Reply> reply; // 댓글수, replyCnt 뽑아내야함
	@OneToMany
	@JoinColumn(name = "boardId")
	private List<IpList> likeCnt; // 삭제 예정

	@OneToMany
	@JoinColumn(name = "boardId")
	private List<Attachment> attachment;

	// Java type -> MySQL type
	// String -> varchar(255)
	// long -> bigint
}
