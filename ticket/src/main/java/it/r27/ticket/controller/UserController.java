package it.r27.ticket.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.r27.ticket.model.Role;
import it.r27.ticket.model.Ticket;
import it.r27.ticket.model.Ticket.TicketStatus;
import it.r27.ticket.model.User;
import it.r27.ticket.repository.RoleRepository;
import it.r27.ticket.repository.TicketRepository;
import it.r27.ticket.repository.UserRepository;
import it.r27.ticket.security.DatabaseUserDetails;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal DatabaseUserDetails userDetails) {

        String username = userDetails.getUsername();
        Optional<User> currentUser = userRepository.findByUsername(username);

        model.addAttribute("user", currentUser.get());

        return "user/index";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("status") boolean status, @AuthenticationPrincipal DatabaseUserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        
        Long userId = userDetails.getId();
        User user = userRepository.findById(userId).get();

        List<Ticket> ticketsIncomplete = ticketRepository.findByUserIdAndStatusNot(userId, Ticket.TicketStatus.COMPLETATO);

        if (!status && !ticketsIncomplete.isEmpty()) {
            model.addAttribute("user", user);
            model.addAttribute("statusError", "Non puoi impostare lo stato 'Non disponibile' finché non hai completato tutti i ticket.");
            return "user/index";
        }
        
        user.setAvailable(status);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Stato aggiornato.");

        return "redirect:/user";
    }

    @GetMapping("/edit")
    public String edit(Model model, @AuthenticationPrincipal DatabaseUserDetails userDetails) {

        String username = userDetails.getUsername();
        Optional<User> currentUser = userRepository.findByUsername(username);
        List<Role> roles = roleRepository.findAll();

        model.addAttribute("user", currentUser.get());
        model.addAttribute("roles", roles);

        return "user/edit";
    }

    @PostMapping("/edit")
	public String update(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, @AuthenticationPrincipal DatabaseUserDetails userDetails) {
		
		if(bindingResult.hasErrors()) {
            String username = userDetails.getUsername();
            Optional<User> currentUser = userRepository.findByUsername(username);
            List<Role> roles = roleRepository.findAll();
		    model.addAttribute("user", currentUser.get());
            model.addAttribute("roles", roles);
			return "user/edit";
		}

        String newUsername = null;

        if(!user.getUsername().equals(userDetails.getUsername())) {

            newUsername = user.getUsername();
            
        }

		userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Utente aggiornato con successo.");

        if(newUsername != null) {
            return "redirect:/logout";
        }
		
		return "redirect:/user";
	}

    @GetMapping("/list")
    public String userList (Model model, @RequestParam(name = "search", required = false) String search) {
        
        List<User> users;
        List<Role> roles = roleRepository.findAll();
        Map<Long, Integer> tickets = new HashMap<>();
        
        if(search != null && !search.isBlank()) {
            users = userRepository.findByUsernameContaining(search);
            model.addAttribute("search", search);
        } else {
            users = userRepository.findAll();
        }

        for (User user : users) {
            int ticketCount = ticketRepository.findByUserIdAndStatusNot(user.getId(), TicketStatus.COMPLETATO).size();
            tickets.put(user.getId(), ticketCount);
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("users", users);
        model.addAttribute("roles", roles);

        return "user/list";
    }
    

    @GetMapping("/new")
    public String newUser (Model model) {
        
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("roles", roles);
        model.addAttribute("user", new User());
        
        return "user/new";
    }

    @PostMapping("/new")
    public String newUserSave(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            List<Role> roles = roleRepository.findAll();
            model.addAttribute("roles", roles);
            return "user/new";
        }

        String password = user.getPassword();

        user.setAvailable(false);
        user.setPassword("{noop}" + password);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Utente aggiunto con successo.");
        
        return "redirect:/user/list";
    }
    
    
}
