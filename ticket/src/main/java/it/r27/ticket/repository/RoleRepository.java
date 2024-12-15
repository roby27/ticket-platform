package it.r27.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.r27.ticket.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
