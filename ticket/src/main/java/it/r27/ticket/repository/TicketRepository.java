package it.r27.ticket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.r27.ticket.model.Ticket;
import it.r27.ticket.model.Ticket.TicketStatus;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    public List<Ticket> findByDescriptionContaining(String description);

    public List<Ticket> findByUserUsername(String user);

    public List<Ticket> findByUserUsernameAndStatusNot(String username, TicketStatus status);

    public List<Ticket> findByUserUsernameAndDescriptionContaining(String username, String description);

    public List<Ticket> findByUserIdAndStatusNot (Long id, TicketStatus status);

    public List<Ticket> findByCategoryName (String category);

    public List<Ticket> findByStatus (Ticket.TicketStatus status);
}