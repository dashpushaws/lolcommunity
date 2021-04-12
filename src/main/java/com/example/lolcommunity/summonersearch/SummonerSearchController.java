package com.example.lolcommunity.summonersearch;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.lolcommunity.board.Board;
import com.example.lolcommunity.summonersearch.entity.Summoner;

@RestController
public class SummonerSearchController {

	@Autowired
	private SummonerRepository summonerRepo;

	// 소환사 목록 조회
	@RequestMapping(value = "/summoners", method = RequestMethod.GET)
	public List<Summoner> getSummonerList() {
		List<Summoner> summoner = summonerRepo.findAll();
		return summoner;
	}
}
