package com.example.lolcommunity.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpListRepository extends JpaRepository<UpList, Long> {
	public UpList findByIp(String ip);

	public UpList findByBoardId(long boardId);

	public UpList findByIpAndBoardId(String ip, long boardId);

	public List<UpList> findAllByIp(String ip);

	public List<UpList> findAllByBoardId(long boardId);

	public List<UpList> findAllById(long id);

}
