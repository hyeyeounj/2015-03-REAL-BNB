package com.babydear.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.babydear.dto.CardDTO;
import com.babydear.model.Baby;
import com.babydear.model.Card;
import com.babydear.model.User;
import com.babydear.repository.CardRepository;
import com.babydear.service.ImgService;
import com.babydear.service.TagService;


@RestController
//@RequestMapping(value = "/api/card")
public class CardController {
	@Autowired
	CardRepository cardRepo;
	@Autowired
	TagService tagService;
	
	@Autowired
	ImgService imgService;
	
	@RequestMapping(value = "/api/card",  method = RequestMethod.GET)
	public String selectCards(){
		return "{ error: null, startCard: 10, count: 10, cards: [a,b,c,d]}";
	}
	@RequestMapping(value = "/api/card", method = RequestMethod.POST)
	public Card createCard(User user, CardDTO cardDTO){
		Assert.notNull(cardDTO, "card should be not null!");
		
		final Set<Baby> babies = tagService.processTags(cardDTO.getBabies());
		final String image = imgService.processImg(cardDTO.getImage());
		Card card = new Card(user.getFamily(), user, babies, image, cardDTO.getContent(), cardDTO.getModifiedDate());
		cardRepo.save(card);
//		return "{ create : true, error: null }";
		return card;
	}
	@RequestMapping(value = "/api/card", method = RequestMethod.PUT)
	public String updateCard(){
		return " { update : true, error: null }";
	}
	@RequestMapping(value = "/api/card", method = RequestMethod.DELETE)
	public String deleteCard(){
		return " { delete: true, error: null }";
	}

}