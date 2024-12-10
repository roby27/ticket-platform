package it.r27.ticket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.r27.ticket.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);

    public List<User> findByAvailable(boolean available);

    Optional<User> findByEmail(String email);
}
