package it.r27.ticket.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.r27.ticket.model.Ticket;
import it.r27.ticket.model.User;
import it.r27.ticket.repository.TicketRepository;
import it.r27.ticket.repository.UserRepository;
import it.r27.ticket.security.DatabaseUserDetails;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping("/user")
    public String index(Model model, @AuthenticationPrincipal DatabaseUserDetails userDetails) {

        String username = userDetails.getUsername();
        Optional<User> currentUser = userRepository.findByUsername(username);

        model.addAttribute("user", currentUser.get());

        return "user/index";
    }

    @PostMapping("/user/update-status")
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
}
