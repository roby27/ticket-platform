package it.r27.ticket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import it.r27.ticket.model.Ticket;
import it.r27.ticket.model.Ticket.TicketStatus;
import it.r27.ticket.repository.TicketRepository;
import it.r27.ticket.security.DatabaseUserDetails;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/")
public class IndexController {
    
    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    public String index(Authentication authentication, Model model, @AuthenticationPrincipal DatabaseUserDetails userDetails) {

        String user = userDetails.getUsername();
        List<Ticket> filteredTickets = ticketRepository.findByUserUsernameAndStatusNot(user, TicketStatus.COMPLETATO);

        model.addAttribute("filteredTickets", filteredTickets);

        return "index";
    }
    
}
