package com.ssafy.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ssafy.DTO.ConferenceDTO;
import com.ssafy.DTO.ConferenceHistoryDTO;
import com.ssafy.db.entity.Book;
import com.ssafy.db.entity.Conference;
import com.ssafy.db.entity.ConferenceHistory;
import com.ssafy.db.entity.User;
import com.ssafy.db.repository.BookRepository;
import com.ssafy.db.repository.ConferenceHistoryRepository;
import com.ssafy.db.repository.ConferenceRepository;
import com.ssafy.db.repository.UserConferenceRepository;
import com.ssafy.db.repository.UserRepository;

@Service
public class ConferenceService {
	
	private ConferenceRepository conferenceRepository;
	private UserRepository userRepository;
	private BookRepository bookRepository;
	
	private UserConferenceRepository userConferenceRepository;
	private ConferenceHistoryRepository conferenceHistoryRepository;
	private ModelMapper modelMapper;
	
	@Autowired
	public ConferenceService(ConferenceRepository conferenceRepository, UserRepository userRepository, BookRepository bookRepository, UserConferenceRepository userConferenceRepository, ConferenceHistoryRepository conferenceHistoryRepository) {
		this.conferenceRepository = conferenceRepository;
		this.userRepository = userRepository;
		this.bookRepository = bookRepository;
		this.userConferenceRepository = userConferenceRepository;
		this.conferenceHistoryRepository = conferenceHistoryRepository;
		this.modelMapper = new ModelMapper();
	}
	
	public List<ConferenceDTO> getConferences(){
		List<ConferenceDTO> list = new ArrayList<ConferenceDTO>();
		
		try {
			list = conferenceRepository.findAll().stream().map(source -> {
				ConferenceDTO res = modelMapper.map(source, ConferenceDTO.class);
			    return res;
			}).collect(Collectors.toList());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
	
	public Page<ConferenceDTO> getConferences(Pageable pageable){
		Page<ConferenceDTO> list = Page.empty();
		
		try {
			Page<Conference> data = conferenceRepository.findAll(pageable);
			list = data.map(source -> modelMapper.map(source, ConferenceDTO.class));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
	
	public boolean createConference(ConferenceDTO source) {
		try {
			conferenceRepository.save(modelMapper.map(source, Conference.class));
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public ConferenceDTO getConferenceById(int id) {
		try {
			Conference source = conferenceRepository.findById(id).get();
			return modelMapper.map(source, ConferenceDTO.class); 
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Page<ConferenceDTO> getConferencesByTitle(String title, Pageable pageable){
		Page<ConferenceDTO> list = Page.empty();
		try {
			Page<Conference> data = conferenceRepository.findByTitleContaining(title, pageable);
			list = data.map(source -> modelMapper.map(source, ConferenceDTO.class));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
	
	public Page<ConferenceDTO> getConferencesByBook(String bookname, Pageable pageable){
		Page<ConferenceDTO> list = Page.empty();
		try {
			Page<Book> data = bookRepository.findByBookNameContaining(bookname, pageable);
			list = data.map(source -> modelMapper.map(source, ConferenceDTO.class));

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
	
	public Page<ConferenceDTO> getConferencesByNickname(String nickname, Pageable pageable){
		Page<ConferenceDTO> list = Page.empty();
		try {
			Page<User> data = userRepository.findByNicknameContaining(nickname, pageable);
			list = data.map(source -> modelMapper.map(source, ConferenceDTO.class));

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
	
	public Page<ConferenceDTO> getConferencesByTags(String tags, Pageable pageable){
		Page<ConferenceDTO> list = Page.empty();
		try {
			Page<Conference> data = conferenceRepository.findByTagsContaining(tags, pageable);
			list = data.map(source -> modelMapper.map(source, ConferenceDTO.class));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
	
	public boolean updateConference(ConferenceDTO conferenceDTO) {
		try {
			Conference output = conferenceRepository.getById(conferenceDTO.getId());
			if(output == null) return false;
			Conference data = modelMapper.map(conferenceDTO, Conference.class);
			output = updateConferenceEntity(output, data);
			conferenceRepository.save(output);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteConference(int id) {
		try {
			Conference Conference = conferenceRepository.getById(id);
			conferenceRepository.delete(Conference);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean createSessionHistory(ConferenceHistoryDTO dto) {
		try {
			conferenceHistoryRepository.save(modelMapper.map(dto, ConferenceHistory.class));
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public Conference updateConferenceEntity(Conference target, Conference data) {
		target.setBook(data.getBook());
		target.setUser(data.getUser());
		target.setCallEndTime(data.getCallEndTime());
		target.setCallStartTime(data.getCallEndTime());
		target.setThumbnailUrl(data.getThumbnailUrl());
		target.setDescription(data.getDescription());
		target.setMaxMembers(data.getMaxMembers());
		target.setPassword(data.getPassword());
		target.setQuestion(data.getQuestion());
		target.setTags(data.getTags());
		target.setTitle(data.getTitle());
		
		return target;
	}
}
