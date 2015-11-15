package com.babydear.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.babydear.dto.UserDTO;
import com.babydear.model.Baby;
import com.babydear.model.Family;
import com.babydear.model.User;
import com.babydear.repository.BabyRepository;
import com.babydear.repository.CardRepository;
import com.babydear.repository.FamilyRepository;
import com.babydear.repository.UserRepository;
import com.babydear.service.ImgService;
import com.babydear.service.MailService;
import com.babydear.service.TagService;



@RestController
//@RequestMapping(value = "/api/card")
public class UserController {

	@Autowired
	TagService tagService;
	
	@Autowired
	ImgService imgService;
	
	@Autowired
	MailService mailService;
	
	@Autowired
	CardRepository cardRepo;
	@Autowired
	UserRepository userRepo;
	@Autowired
	BabyRepository babyRepo;
	@Autowired
	FamilyRepository familyRepo;
	
	@RequestMapping("/api/user/create")
	public User create(@Valid UserDTO userDTO) {
		Family family = null;
		if (userDTO.getFId() == null) {
			family = familyRepo.save(new Family());
		}else{
			family = familyRepo.findOne(userDTO.getFId());
		}
		User user = new User(userDTO, family);
		List<Baby> babies = userDTO.getBabies();
		user = userRepo.save(user);
		babyRepo.save(babies);
		
		mailService.sendSignUpMail();
		System.out.println(userDTO);
		return user;
	}
	
	@RequestMapping("/api/user/login")
	public UserDTO login(UserDTO userDTO) {
//		User user = userRepo.findByEmail(userDTO.getEmail());
//		System.out.println(userDTO);
		return userDTO;
	}
	@RequestMapping("/api/user/facebook")
	public String facebook(User user) {
		System.out.println(user);
		return "redirect:/";
	}

}