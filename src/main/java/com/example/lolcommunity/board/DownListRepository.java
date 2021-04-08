package com.example.lolcommunity.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DownListRepository extends JpaRepository<DownList, Long> {
	public DownList findByIp(String ip);

	public List<DownList> findAllByBoardId(long boardId);

	public DownList findByIpAndBoardId(String ip, long boardId);
}
