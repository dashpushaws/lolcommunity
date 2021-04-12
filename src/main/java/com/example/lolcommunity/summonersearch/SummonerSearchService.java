package com.example.lolcommunity.summonersearch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.lolcommunity.summonersearch.entity.Summoner;
import com.example.lolcommunity.summonersearch.event.SummonerList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class SummonerSearchService {
	@Autowired
	private RabbitTemplate rabbit;
//	@Autowired
//	private MessageConverter converter;
	@Autowired
	private SummonerRepository summonerRepo;

	// RabbitMQ 쌓여있는 큐 지연없이 바로 받아오기 + 매개변수가 존재하는 메소드에는 @Scheduled 적용불가능
//	@RabbitListener(queues = "lol.summoner")
//	public void receiveMessage(List<SummonerList> list) {
//		System.out.println(list);
//		for (SummonerList unit : list) {
//			Summoner summoner = Summoner.builder().name(unit.getName())
//					.createdTime(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date())).build();
//			summonerRepo.save(summoner);
//		}
//	}

//	@Scheduled(cron = "* 40/1 * * * *")
//	public void receiveMessage1() {
//		System.out.println(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));
//	}

//	@Scheduled(fixedRate = 1000 * 6)
//	@Scheduled(fixedDelay = 3000)
//	@Scheduled(cron = "초 분 시 일 월 요일 (년도)") // @Scheduled를 적용하는 메소드는 매개변수 사용불가
//	@Scheduled(cron = "* * 2 * * *") // 새벽 2시마다 실행
//	@Scheduled(cron = "* */5 * * * *") // 프로그램 실행 후, 5분마다 실행(빌드용)
	@Scheduled(cron = "0 */1 * * * *")
	public void receiveMessage2() {

		System.out.println(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));

//		List<SummonerList> aa = (List<SummonerList>) rabbit.receiveAndConvert("lol.summoner");
		try {
			Message msg = rabbit.receive("lol.summoner");
//			16진수로 byte stream으로 변경, 받은걸 바꾸고 <List<SummonerList>> 타입으로 바꿔서 받아서 저장
			List<SummonerList> response = new Gson().fromJson(new String(msg.getBody()),
					new TypeToken<List<SummonerList>>() {
					}.getType());
			summonerRepo.deleteAll();
			System.out.println(response);
			for (SummonerList unit : response) {
				Summoner summoner = Summoner.builder().name(unit.getName())
						.createdTime(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date())).build();
				summonerRepo.save(summoner);
			}

		} catch (Exception e) {
			e.getMessage();
		}

		// 하려다 실패한것
//		List<SummonerList> aa = rabbit.receiveAndConvert("lol.summoner", new ParameterizedTypeReference<List<SummonerList>>);
//				.receiveAndConvert("lol.summoner", new PrameterizedTypeReference<SummonerList>);

	}
}
