package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.entity.Description;
import com.example.demo.entity.jdbc.Template;
import com.example.demo.entity.jparepository.DescriptionJpaRepository;

@RestController
public class Controller {

    @Autowired
    private Template template;
    
    @Autowired
    private DescriptionJpaRepository descriptionRepository;

    @RequestMapping("/")
    public String home() {
        return "Hello from Azure App Service! Let's start connecting to Azure SQL Server!";
    }

    @RequestMapping("posts")
    public ModelAndView getPosts(ModelMap model) {
		ModelAndView modelAndView = new ModelAndView();
		List<Description> descriptions = descriptionRepository.findAll();

		model.addAttribute("descriptions", descriptions);
		modelAndView.setViewName("posts");
		return modelAndView;
	}

    @RequestMapping("new-art")
	public ModelAndView addArtPage() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("addArt");
		return modelAndView;
	}


}
