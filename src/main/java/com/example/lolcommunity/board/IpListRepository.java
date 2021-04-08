package com.example.lolcommunity.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpListRepository extends JpaRepository<IpList, Long> {

	public IpList findByIp(String ip);
}
