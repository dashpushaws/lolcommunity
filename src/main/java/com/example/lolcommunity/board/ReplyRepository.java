package com.example.lolcommunity.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
//	public List<Reply> findByBoardId(String boadId);
	public List<Reply> findAllByBoardId(long boardId);

	public Reply findByBoardId(long boardId);

//	public Reply findById(long id);

}
