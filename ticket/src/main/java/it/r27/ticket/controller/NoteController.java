package it.r27.ticket.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.r27.ticket.model.Note;
import it.r27.ticket.repository.NoteRepository;
import it.r27.ticket.security.DatabaseUserDetails;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @PostMapping("/new")
    public String newNote (@Valid @ModelAttribute("note") Note note, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model, @AuthenticationPrincipal DatabaseUserDetails userDetails) {
        
        
        if (bindingResult.hasErrors()) {
            return "note/new";
        }


        note.setAuthor(userDetails.getName());
        note.setData(LocalDateTime.now());
        noteRepository.save(note);

        redirectAttributes.addFlashAttribute("message", "Nota creata con successo");
        
        return "redirect:/ticket/detail/" + note.getTicket().getId();
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        
        Note note = noteRepository.findById(id).get();

        model.addAttribute("note", note);

        return "note/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@Valid @ModelAttribute("note") Note note, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
       
        if (bindingResult.hasErrors()) {
            return "note/edit" + note.getId();
        }

        noteRepository.save(note);

        redirectAttributes.addFlashAttribute("message", "Nota aggiornata con successo");
        
        return "redirect:/ticket/detail/" + note.getTicket().getId();
    }

    @PostMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long id, @ModelAttribute("note") Note note, RedirectAttributes redirectAttributes, Model model) {

        note = noteRepository.findById(id).get();

		noteRepository.deleteById(id);

		redirectAttributes.addFlashAttribute("deleteMessage", "Nota eliminata correttamente.");

		return "redirect:/ticket/detail/" + note.getTicket().getId();
	}
}
