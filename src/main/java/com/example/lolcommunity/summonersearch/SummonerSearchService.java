package com.example.lolcommunity.summonersearch;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.lolcommunity.summonersearch.entity.Summoner;
import com.example.lolcommunity.summonersearch.event.SummonerList;

@Service
public class SummonerSearchService {
	@Autowired
	private SummonerRepository summonerRepo;

	@RabbitListener(queues = "lol.summoner")
	public void receiveMessage(SummonerList list) {
		System.out.println(list);
//		Summoner summoner = Summoner.builder().name(list.getName()).build();
//		System.out.println(summoner);
//		summonerRepo.save(summoner);
	}

//		if(account instanceof JSONObject){
//		System.out.println("222"+account.getClass());
//		}
//		Summoner summoner = Summoner.builder().userId(list.getUserId()).name(account.getName())
//				.address(account.getAddress()).build();

//		System.out.println("수신로그2: " + summoner); // Java Object type

}
