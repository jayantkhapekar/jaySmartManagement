package com.jayant.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.jayant.dao.ContactRepository;
import com.jayant.dao.UserRepository;
import com.jayant.entity.Contact;
import com.jayant.entity.User;
import com.jayant.helper.Message;


@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String username = principal.getName();
		System.out.print("USERNAME" + username);

		// Get user using username (Email)

		User user = userRepository.getUserByUserName(username);

		System.out.print("USER " + user);

		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String dashbord(Model model, Principal principal) {

		return "normal/user_dashbord";
	}

	// open add form from handler

	@RequestMapping("/add-contact")
	public String openAddContactForm(Model model) {

		model.addAttribute("title", "add contact form");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";

	}

	// processing add contact

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {
			System.out.print("DATA" + contact);

			String name = principal.getName();

			User user = userRepository.getUserByUserName(name);

			// processing and uploading file

			if (file.isEmpty()) {

				System.out.print("File is empty ");
				contact.setImage("contact.png");

			} else {
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.print("Image is uploaded");

			}

			user.getContacts().add(contact);

			contact.setUser(user);

			this.userRepository.save(user);

			System.out.println("Added to database");

			// Message add

			session.setAttribute("message", new Message("contact added successfully", "success"));

		} catch (Exception e) {

			System.out.println("ERROR" + e.getMessage());

			session.setAttribute("message", new Message("something went wrong", "danger"));
		}

		return "normal/add_contact_form";
	}

	// show contact handler
	// per page 5[n]
	// current page = 0[page]

	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {

		model.addAttribute("title", "show contacts");

		String username = principal.getName();

		User user = this.userRepository.getUserByUserName(username);

		// current page = page
		// contact per page =

		Pageable pageable = PageRequest.of(page, 7);

		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentpage", page);
		model.addAttribute("totalpage", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	// showing particular contact details

	@GetMapping("/{cId}/contact/")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		System.out.println("CID" + cId);

		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);

		Optional<Contact> contOptional = contactRepository.findById(cId);
		Contact contact = contOptional.get();

		if (user.getId() == contact.getUser().getId()) {

			model.addAttribute("contact", contact);
		}

		return "normal/contact_detail"; 

	}

	// Delete contact Handler
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId , Model model , HttpSession session , Principal principal ) {

		Contact contact = this.contactRepository.findById(cId).get();
		
		User user = this.userRepository.getUserByUserName(principal.getName());

		user.getContacts().remove(contact);

		this.userRepository.save(user);
		
		
		session.setAttribute("message", new Message("Contact Deleted Successfully", "success"));

		return "redirect:/user/show-contacts/0";
	}

}
