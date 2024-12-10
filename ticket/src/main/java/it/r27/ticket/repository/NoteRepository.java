package it.r27.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.r27.ticket.model.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {

}
