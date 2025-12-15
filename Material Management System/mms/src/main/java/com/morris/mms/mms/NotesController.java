package com.morris.mms.mms;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@Controller
public class NotesController {

    private final NoteRepository noteRepository;

    public NotesController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @PostMapping("/notes")
    public String addNote(HttpSession session,
                          @RequestParam("text") String text) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        if (text != null && !text.isBlank()) {
            Note note = new Note();
            note.setText(text.trim());
            noteRepository.save(note);
        }
        return "redirect:/items#section-notes";
    }

    @PostMapping("/notes/{id}/toggle")
    public String toggle(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        noteRepository.findById(id).ifPresent(note -> {
            note.setDone(!note.isDone());
            noteRepository.save(note);
        });
        return "redirect:/items#section-notes";
    }

    @GetMapping("/notes")
    public String allNotes(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("notes", noteRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("ym", YearMonth.now());
        return "notes"; // 你要新增 notes.html 或把它也塞回 items.html
    }
}
