package com.example.lolcommunity.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
//	public List<Reply> findAllByBoardId(long boardId);
//
//	public Reply findByBoardId(long boardId);
	public Attachment findByBoardId(long boardId);
}
