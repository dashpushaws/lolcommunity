package com.example.lolcommunity.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
//	public Board findById2(long id);
	// public Board findById(long id);를 선언하면
	// CRUD Repository가 영향받음

	// public List<Board> findById(String id);
//	public List<Board> findByName(String name);

	// 조회수 상위 5개 조회 - 동일 시, 최신순

	// 댓글수 상위 5개 조회 - 동일 시, 최신순
}
