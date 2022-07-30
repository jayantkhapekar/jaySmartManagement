package com.jayant.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jayant.dao.UserRepository;
import com.jayant.entity.City;
import com.jayant.entity.User;
import com.jayant.helper.Message;
import com.jayant.services.CityService;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private CityService cityService;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/home")
	public String home(Model model) {

		model.addAttribute("title", "this is home page");

		return "home";
	}

	@RequestMapping("/about")
	public String about(Model model) {

		model.addAttribute("title", "this is about page");

		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model model) {

		model.addAttribute("title", "this is about page");

		List<City> cityNameList = cityService.getAllCity();

		model.addAttribute("cityNameList", cityNameList);
		model.addAttribute("city", new City());
		model.addAttribute("user", new User());

		return "signup";
	}

	// Handler for register user
	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
			@RequestParam(value = "agreement", defaultValue = "false") Boolean agreement, Model model,
			HttpSession session) {

		try {

			if (!agreement) {
				System.out.print("your not agrred the team and conditon");
				throw new Exception("you have not agrred the team and condition");
			}

			if (bindingResult.hasErrors()) {

				System.out.print("ERROR" + bindingResult.toString());
				model.addAttribute("user", user);
				return "signup";
			}

			String password = user.getPassword();
			String confirmPassword = user.getRepeatPassword();

			if (!password.equals(confirmPassword)) {

				session.setAttribute("Not match", "alert-success");
				throw new Exception("password and confirm password not match");

			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agreement" + agreement);
			System.out.print("USER" + user);

			User result = userRepository.save(user);

			model.addAttribute("user", new User());

			session.setAttribute("Successfully register", "alert-success");
			return "about";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("" + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}

	// Handler for custom login

	@GetMapping("/signin")
	public String customLogin(Model model) {

		return "login";
	}
}
