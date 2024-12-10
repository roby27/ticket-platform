package it.r27.ticket.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import it.r27.ticket.model.Note;
import it.r27.ticket.model.Ticket;
import it.r27.ticket.model.User;
import it.r27.ticket.repository.CategoryRepository;
import it.r27.ticket.repository.TicketRepository;
import it.r27.ticket.repository.UserRepository;
import it.r27.ticket.security.DatabaseUserDetails;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String index(Authentication authentication, Model model, @RequestParam(name = "search", required = false) String search, @AuthenticationPrincipal DatabaseUserDetails userDetails) {
        
        List<Ticket> allTickets;

        String user = userDetails.getUsername();

        List<Ticket> filteredTickets;
        
        if(search != null && !search.isBlank()) {
            allTickets = ticketRepository.findByDescriptionContaining(search);
            filteredTickets = ticketRepository.findByUserUsernameAndDescriptionContaining(user, search);
            model.addAttribute("search", search);
        } else {
            allTickets = ticketRepository.findAll();
            filteredTickets = ticketRepository.findByUserUsername(user);
        };

        model.addAttribute("tickets", allTickets);
        model.addAttribute("filteredTickets", filteredTickets);

        return "ticket/index";
    }

    @GetMapping("/new")
    public String newTicket (Model model) {

        List<User> availableUsers = userRepository.findByAvailable(true);

        model.addAttribute("ticket", new Ticket());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("availableUsers", availableUsers);

        return "ticket/new";
    }
    
    @PostMapping("/new")
    public String storeTicket (@Valid @ModelAttribute("ticket") Ticket ticket, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        
        if(bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("users", userRepository.findAll());
            List<User> availableUsers = userRepository.findByAvailable(true);
            model.addAttribute("availableUsers", availableUsers);
            return "ticket/new";
        }

        ticket.setData(LocalDateTime.now());
        ticket.setStatus(Ticket.TicketStatus.DA_FARE);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket creato con successo.");

        ticketRepository.save(ticket);
        
        return "redirect:/ticket";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable(name = "id") Long id, Model model) {

        Optional<Ticket> ticket = ticketRepository.findById(id);

        if (ticket.isPresent()) {
            model.addAttribute("ticket", ticket.get());
        } else {
            return "redirect:/ticket";
        }

        return "ticket/detail";

    }

    @GetMapping("/update/{id}")
	public String update(@PathVariable("id") Long id, Model model) {
		model.addAttribute("ticket", ticketRepository.findById(id).get());
		model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
		return "ticket/update";
	}
	
	@PostMapping("/update/{id}")
	public String update(@Valid @ModelAttribute("ticket") Ticket ticket, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		
		if(bindingResult.hasErrors()) {
		    model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("users", userRepository.findAll());
			return "/ticket/update";
		}

		ticketRepository.save(ticket);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket aggiornato con successo.");
		
		return "redirect:/ticket";
	}

    @PostMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {

		ticketRepository.deleteById(id);

		redirectAttributes.addFlashAttribute("deleteMessage", "Ticket eliminato con successo.");

		return "redirect:/ticket";
	}

    @GetMapping("/{id}/note")
    public String nota(@PathVariable Long id, Model model) {

        Ticket ticket = ticketRepository.findById(id).get();

        Note nota = new Note();
        nota.setTicket(ticket);

        model.addAttribute("note", nota);

        return "note/new";
    }
    

}
