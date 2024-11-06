package com.team2.app.chat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team2.app.department.DepartmentVO;
import com.team2.app.employee.EmployeeVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatService {

	@Autowired
	ChatMapper chatMapper;

	public List<RoomVO> getList() throws Exception {
		return chatMapper.getList();
	}

	public int makeRoom(RoomVO roomVO) throws Exception {
		int result = chatMapper.makeRoom(roomVO);

		if (result > 0) {
			
			result = chatMapper.addMember(roomVO);
			
		}

		log.info("makeRoom Service vo : {}", roomVO);

		return result;
	}

	public RoomVO getRoomDetail(RoomVO roomVO) throws Exception {

		// 채팅방 정보 불러오기
		roomVO = chatMapper.getRoomDetail(roomVO);

		// 채팅방 인원 불러오기
		List<RoomMemberVO> roomMemberlist = chatMapper.getRoomMember(roomVO);
		roomVO.setRoomMember(roomMemberlist);
		
		List<ChatVO> chatList = chatMapper.getChat(roomMemberlist);
		
		// 이전 채팅 불러오기
		roomVO.setChatList(chatList);

		for(ChatVO chatVO: chatList) {
			chatVO.setReadCount(chatMapper.getReadCount(chatVO));
		}

		return roomVO;
	}

	public ChatVO addChat(ChatVO chatVO, RoomMemberVO roomMemberVO) throws Exception {
		
		chatMapper.addChat(chatVO);
		
		RoomVO roomVO = new RoomVO();
		roomVO.setRoomNum(roomMemberVO.getRoomNum());

		// 채팅방 인원 불러오기
		List<RoomMemberVO> list = chatMapper.getRoomMember(roomVO);

		// 채팅방 인원 마다 읽음 여부 생성
		for (RoomMemberVO vo : list) {
			ChatVO addChatVO = new ChatVO();
			addChatVO.setChatNum(chatVO.getChatNum());
			addChatVO.setMemberNum(vo.getMemberNum());
			chatMapper.addRead(addChatVO);
		}
		
		//나의 읽음상태 변경
		chatMapper.chReadStatus(chatVO);
		
		int read = chatMapper.getReadCount(chatVO);
		chatVO.setReadCount(read);
		
		return chatVO;
		
	}

	public EmployeeVO getEmpDetail(EmployeeVO employeeVO) throws Exception {
		return chatMapper.getEmpDetail(employeeVO);
	}
	
	public RoomMemberVO getMemberDetail(RoomMemberVO roomMemberVO) throws Exception {
		return chatMapper.getMemberDetail(roomMemberVO);
	}

	public List<EmployeeVO> empList(DepartmentVO departmentVO) throws Exception {
		return chatMapper.empList(departmentVO);
	}

}
