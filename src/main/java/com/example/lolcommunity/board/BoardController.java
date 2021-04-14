package com.example.lolcommunity.board;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class BoardController {
	private AttachmentRepository attachmentRepo; // 리포지토리 선언하면 의존성 주입해라
	private BoardRepository boardRepo;
	private ReplyRepository replyRepo;
	private UpListRepository upListRepo;
	private DownListRepository downListRepo;
	private final Path FILE_PATH = Paths.get("attachment");

	// 생성자
	@Autowired
	public BoardController(AttachmentRepository attachmentRepo, BoardRepository boardRepo, ReplyRepository replyRepo,
			UpListRepository upListRepo, DownListRepository downListRepo) {
		this.attachmentRepo = attachmentRepo;
		this.boardRepo = boardRepo;
		this.upListRepo = upListRepo;
		this.downListRepo = downListRepo;
		this.replyRepo = replyRepo;

	}

	// 통신 테스트용
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String helloWorld(HttpServletRequest req) throws NoSuchAlgorithmException {
		System.out.println("hi");

//		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
//				.getRequest();
//		String ip = req.getHeader("X-FORWARDED-FOR");
//		if (ip == null) {
//			ip = req.getRemoteAddr();
//		}
//		System.out.println(ip);

		String ip = getIp(req);
		System.out.println(ip);

		return "Hello, World!!!!!!!!";
	}

	// 게시글 작성 // API이름: POST /boards
	@RequestMapping(value = "/boards", method = RequestMethod.POST)
	public Board addBoard(@RequestBody Board board, HttpServletRequest req) throws NoSuchAlgorithmException {
		board.setIp(getIp(req));
//		board.setCreatedTime(new Date().getTime()); // createdTime 타입 long이여야함
		board.setCreatedTime(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));

		// sha256 비밀번호 암호화
		String encryptedPassword = encryptPassword(board.getPassword());
		board.setPassword(encryptedPassword);

		boardRepo.save(board);
		board.setPassword("임시방편으로 변형시킴"); // @JsonIgnore 못써서 임시방편으로 변형시킴
		return board;
	}

	// 게시글 작성 시, 첨부파일이 있다면 실행됨
	@RequestMapping(value = "/boards/{boardId}/board-attachment", method = RequestMethod.POST)
	public Attachment addAttachment(@PathVariable long boardId, @RequestPart("data") MultipartFile file,
			HttpServletResponse res) throws IOException {
		if (boardRepo.findById(boardId).orElse(null) == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		System.out.println(file.getOriginalFilename());

		// 디렉토리가 없으면 생성
		if (!Files.exists(FILE_PATH)) {
			Files.createDirectories(FILE_PATH);
		}

		// 파일 저장
		FileCopyUtils.copy(file.getBytes(), new File(FILE_PATH.resolve(file.getOriginalFilename()).toString()));
		// 파일 메타 데이터 저장
		Attachment attachment = Attachment.builder().boardId(boardId).fileName(file.getOriginalFilename())
				.contentType(file.getContentType()).build();

		attachmentRepo.save(attachment);
		return attachment;
	}

	// 게시글 수정위한 비밀번호 검사
	@RequestMapping(value = "/boards-check/{id}", method = RequestMethod.DELETE)
	public boolean checkPasswordForModifyBoard(@PathVariable("id") long id, @RequestBody String password,
			HttpServletResponse res) throws NoSuchAlgorithmException {
		Board board = boardRepo.findById(id).orElse(null);
		if (board == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}
		String encryptedPassword = encryptPassword(password);
		if (encryptedPassword.equals(board.getPassword())) {
			return true;
		}
		return false;
	}

	// 게시글 수정
	@RequestMapping(value = "/boards/{id}", method = RequestMethod.PUT)
	public Board modifyBoard(@PathVariable("id") long id, @RequestBody Board modifiedBoard, HttpServletResponse res)
			throws NoSuchAlgorithmException {
		Board board = boardRepo.findById(id).orElse(null);
		if (board == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		System.out.println(board);
		board.setType(modifiedBoard.getType());
		board.setTitle(modifiedBoard.getTitle());
		board.setName(modifiedBoard.getName());
		// 비빌번호 sha256
		String encryptedPassword = encryptPassword(modifiedBoard.getPassword());
		board.setPassword(encryptedPassword);
		board.setContent(modifiedBoard.getContent());
		board.setModifiedTime(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));
		boardRepo.save(board);
		return board;
	}

	// 게시글 삭제 hard-delete
	@RequestMapping(value = "/boards/{id}", method = RequestMethod.DELETE)
	public boolean removeBoard(@PathVariable long id, @RequestBody String password, HttpServletResponse res)
			throws NoSuchAlgorithmException {
		Board board = boardRepo.findById(id).orElse(null);
		if (board == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}
		String encryptedPassword = encryptPassword(password);
		if (encryptedPassword.equals(board.getPassword())) {
//			upListRepo.de
//			UpList upList = upListRepo.findAllById(id);
			boardRepo.delete(board);
			return true;
		}

		return false;
	}

	// 댓글 달기
	@RequestMapping(value = "/boards/{boardId}/replies", method = RequestMethod.POST)
	public Reply addReply(@PathVariable("boardId") long boardId, @RequestBody Reply receiveReply,
			HttpServletResponse res, HttpServletRequest req) throws IOException, NoSuchAlgorithmException {

		if (boardRepo.findById(boardId).orElse(null) == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		String createdTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
		String ip = getIp(req);
		String encryptedPassword = encryptPassword(receiveReply.getPassword());

		Reply reply = Reply.builder().boardId(boardId).name(receiveReply.getName()).content(receiveReply.getContent())
				.password(encryptedPassword).ip(ip).createdTime(createdTime).build();

		replyRepo.save(reply);

		// 댓글 달면서 boardRepo의 댓글 개수 1증가 시켜야함 -> 댓글삭제는 반대로 적용시켜야함
		Board board = boardRepo.findById(boardId).orElse(null);
		board.setReplyCnt(replyRepo.findAllByBoardId(boardId).size());
		boardRepo.save(board);

		return reply;
	}

	// 댓글 조회
	@RequestMapping(value = "/boards/{boardId}/replies", method = RequestMethod.GET)
	public List<Reply> getReply(@PathVariable("boardId") long boardId, HttpServletResponse res) {
//		Reply checkReply = replyRepo.findByBoardId(boardId); // 댓글 중 boardId에 해당하는 댓글 존재하는지 검사
//		if (checkReply == null) {
//			if (replyRepo.findByBoardId(boardId).orElse(null) == null) { // orElse ? crud repository 아니라서 사용못함
//			res.setStatus(HttpServletResponse.SC_NOT_FOUND); // 이걸 사용하고싶은데, 오류발생. 어디가 문제인지 확인해야함
//			return null;
//		}
		List<Reply> reply = replyRepo.findAllByBoardId(boardId);
		return reply;
	}

	// 댓글 삭제 hard-delete // API이름: DELETE /boards/{boardId}/replies/{id}
	@RequestMapping(value = "/boards/{boardId}/replies/{id}", method = RequestMethod.DELETE)
	// 1.RequestBody가 원시타입 데이터가 올때, String으로 받아서 원하는 원시타입으로 변환해서 쓰면됨
	public boolean removeReply(@PathVariable long boardId, @PathVariable long id, @RequestBody String password,
			HttpServletResponse res) throws NoSuchAlgorithmException {
// 		2.RequestBody가 객체타입 데이터가 올때
//		public long removeReply(@PathVariable long id, @RequestBody Reply replyForDel, HttpServletResponse res) {
//			System.out.println(id);
//			System.out.println(replyForDel);
//		
		Reply reply = replyRepo.findById(id).orElse(null); // 없어도 되는 과정
		if (reply == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}
		String encryptedPassword = encryptPassword(password);

		if (encryptedPassword.equals(reply.getPassword())) {
			replyRepo.delete(reply);

			// 댓글삭제후 댓글개수 줄여야함
			Board board = boardRepo.findById(boardId).orElse(null);
			board.setReplyCnt(replyRepo.findAllByBoardId(boardId).size());
			boardRepo.save(board);

			return true;
		}

		return false;
	}

	// 전체 게시글 수 요청
	@RequestMapping(value = "/board-count", method = RequestMethod.GET)
	public long getBoardCount() {
//		boardRepo.count()
//		boardRepo.findAll().;
		return boardRepo.count();
	}

	// 페이징 하여 목록 조회
	@RequestMapping(value = "/boards/paging", method = RequestMethod.GET)
	public List<Board> getBoardPaging(@RequestParam("page") int page, @RequestParam("size") int size) {
		// Page<Board> 로 하면 페이징도하고 전체게시글 개수도 가져올수있다
//		return boardRepo.findAll(PageRequest.of(page, size)).toList();
		return boardRepo.findAll(PageRequest.of(page, size, Sort.by("id").descending())).toList();
	}

	// 조회수 상위 5개 조회 - 동일 시, 최신순(이건 따로 설정 안해줬는데 적용되서 리턴해줌)
	@RequestMapping(value = "/boards-hit", method = RequestMethod.GET)
	public List<Board> getBoardForHitCnt() {
		List<Board> board = boardRepo.findAll(PageRequest.of(0, 5, Sort.by("hitCnt").descending())).toList();
		return board;
	}

	// 댓글수 상위 5개 조회 - 동일 시, 최신순(이건 따로 설정 안해줬는데 적용되서 리턴해줌)
	@RequestMapping(value = "/boards-reply", method = RequestMethod.GET)
	public List<Board> getBoardForReplyCnt() {
		List<Board> board = boardRepo.findAll(PageRequest.of(0, 5, Sort.by("replyCnt").descending())).toList();
		return board;
	}

	// 1건 조회 // API이름: GET /boards/{id}
	@RequestMapping(value = "/boards/{id}", method = RequestMethod.GET)
	public Board getSingleBoard(@PathVariable long id, HttpServletResponse res) {
		Board board = boardRepo.findById(id).orElse(null);
		if (board == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		return board;
	}

	// 게시글의 첨부파일 다운로드 - 포기
	@RequestMapping(value = "/board-attachment/{id}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getBoardFile(@PathVariable("id") long id, HttpServletResponse res)
			throws IOException {
		Attachment attachment = attachmentRepo.findByBoardId(id);

		if (attachment == null) {
			return ResponseEntity.notFound().build();
		}

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", attachment.getContentType() + ";charset=UTF-8");
		// inline: 뷰어로, attachement: 내려받기
		responseHeaders.set("Content-Disposition",
				"inline; filename=" + URLEncoder.encode(attachment.getFileName(), "UTF-8"));
		System.out.println(ResponseEntity.ok().headers(responseHeaders)
				.body(Files.readAllBytes(FILE_PATH.resolve(attachment.getFileName()))));
		return ResponseEntity.ok().headers(responseHeaders)
				.body(Files.readAllBytes(FILE_PATH.resolve(attachment.getFileName())));
	}

	// 게시글 조회수 1 증가 // API이름: PATCH /boards/{id}
	@RequestMapping(value = "/boards/{id}", method = RequestMethod.PATCH)
	public Board increaseHinCnt(@PathVariable("id") long id, HttpServletResponse res) {

		Board board = boardRepo.findById(id).orElse(null);

		if (board == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} else {
			board.setHitCnt(board.getHitCnt() + 1);
			boardRepo.save(board);
			return board;
		}
	}

	// 추천하기
	@RequestMapping(value = "/boards/{boardId}/up", method = RequestMethod.PATCH)
	public boolean thumbUp(@PathVariable long boardId, HttpServletRequest req) {
		// ip 지금 127.0.0.1 하나밖에 없으니깐, db에 넣을땐 1활성화, db에서 지울땐 2활성화
		// 1 리포에 ip 넣기
		Board board = boardRepo.findById(boardId).orElse(null);
		String ip = getIp(req); // 추천하려는 ip

		UpList check = upListRepo.findByIpAndBoardId(ip, boardId);

		if (check == null) {
			UpList upList = UpList.builder().boardId(boardId).ip(ip).build();
			upListRepo.save(upList);

			board.setUpCnt(upListRepo.findAllByBoardId(boardId).size());
			boardRepo.save(board);
			return true;
		} else {
			return false;
		}
	}

	// 비추천하기
	@RequestMapping(value = "/boards/{boardId}/down", method = RequestMethod.PATCH)
	public boolean thumbDown(@PathVariable long boardId, HttpServletRequest req) {
		Board board = boardRepo.findById(boardId).orElse(null);
		String ip = getIp(req);
		DownList check = downListRepo.findByIpAndBoardId(ip, boardId);
		if (check == null) {
			DownList downList = DownList.builder().boardId(boardId).ip(ip).build();
			downListRepo.save(downList);

			board.setDownCnt(downListRepo.findAllByBoardId(boardId).size());
			boardRepo.save(board);
			return true;
		} else {
			return false;
		}
	}

	// 비빌먼호 sha256으로 변환
	private String encryptPassword(String rawPassword) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(rawPassword.getBytes());
		String encryptedPassword = String.format("%064x", new BigInteger(1, md.digest()));
		return encryptedPassword;
	}

	// ip 얻기
	private String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

}
