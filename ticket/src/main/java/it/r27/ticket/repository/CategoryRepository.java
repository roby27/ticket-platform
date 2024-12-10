package it.r27.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.r27.ticket.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
