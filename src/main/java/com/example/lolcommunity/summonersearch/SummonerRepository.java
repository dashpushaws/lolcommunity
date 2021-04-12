package com.example.lolcommunity.summonersearch;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.lolcommunity.summonersearch.entity.Summoner;

@Repository
public interface SummonerRepository extends JpaRepository<Summoner, Long> {
//	public List<Summoner> findByOrderMoreThan(Long order);
}
