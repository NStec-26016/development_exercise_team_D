package com.example.fullness.stationary.controller;

import com.example.fullness.stationary.form.AccountRegisterForm;
import com.example.fullness.stationary.service.AccountRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/admin/account")
@SessionAttributes("form")
public class AccountRegisterController {

	@Autowired
	private AccountRegisterService accountRegisterService;

	@ModelAttribute("form")
	public AccountRegisterForm setUpForm() {
		return new AccountRegisterForm();
	}

	@GetMapping("/form")
	public String showForm(Model model, @ModelAttribute("form") AccountRegisterForm form) {
		model.addAttribute("employees", accountRegisterService.getUnregisteredEmployees());
		return "admin/account/form";
	}

	@PostMapping("/form")
	public String validateForm(@ModelAttribute("form") AccountRegisterForm form) {
		form.setEmployeeName(accountRegisterService.getEmployeeNameById(form.getEmployeeId()));
		return "redirect:/admin/account/confirm";
	}

	@GetMapping("/confirm")
	public String showConfirm() {
		return "admin/account/confirm";
	}

	@PostMapping("/confirm")
	public String handleConfirmAction(@RequestParam("action") String action) {
		if ("back".equals(action)) {
			return "redirect:/admin/account/form";
		}
		return "redirect:/admin/account/complete";
	}

	@GetMapping("/complete")
	public String showComplete(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "admin/account/complete";
	}
}
