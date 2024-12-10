package it.r27.ticket.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.r27.ticket.model.Payload;
import it.r27.ticket.model.Ticket;
import it.r27.ticket.model.Ticket.TicketStatus;
import it.r27.ticket.repository.TicketRepository;

@RestController
@CrossOrigin
@RequestMapping("/api/ticket")
public class TicketRestController {

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    public ResponseEntity<Payload<List<Ticket>>> index(@RequestParam(name = "category", required = false) String category, @RequestParam(name = "status", required = false) String status) {

        
        try {
            
            if (category != null && !category.isBlank()) {
    
                Payload<List<Ticket>>response = new Payload<>("Lista dei ticket filtrata per categoria", "200", ticketRepository.findByCategoryName(category));
                
                return new ResponseEntity<Payload<List<Ticket>>>(response, HttpStatus.OK);
    
            } if (status != null && !status.isBlank()) {
    
                Payload<List<Ticket>>response = new Payload<>("Lista dei ticket filtrata per categoria", "200", ticketRepository.findByStatus(TicketStatus.valueOf(status)));
                
                return new ResponseEntity<Payload<List<Ticket>>>(response, HttpStatus.OK);
    
            } else {
    
                Payload<List<Ticket>> response = new Payload<>("Lista dei ticket", "200", ticketRepository.findAll());
                
                return ResponseEntity.ok(response);
    
            }

        } catch (Exception e) {

            Payload<List<Ticket>> error = new Payload<>("Errore nella ricerca per: " + e.getMessage(), "400", null);
            return new ResponseEntity<Payload<List<Ticket>>>(error, HttpStatus.BAD_REQUEST);
            
        }
    }
    

}
