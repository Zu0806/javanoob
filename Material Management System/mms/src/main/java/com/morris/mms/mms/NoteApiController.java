package com.morris.mms.mms;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/notes")
@CrossOrigin(origins = "http://localhost:5173")
public class NoteApiController {

    private final NoteRepository noteRepository;

    public NoteApiController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @GetMapping
    public List<Note> list() {
        return noteRepository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping
    public Note create(@RequestBody Map<String, String> body) {
        Note n = new Note();
        n.setText(body.getOrDefault("text", "").trim());
        n.setAuthorName(body.getOrDefault("authorName", "-"));
        // createdAt 在 Note 裡面預設就是 now() :contentReference[oaicite:3]{index=3}
        return noteRepository.save(n);
    }

    @PostMapping("/{id}/toggle")
    public Note toggle(@PathVariable Long id) {
        Note n = noteRepository.findById(id).orElseThrow();
        n.setDone(!n.isDone());
        return noteRepository.save(n);
    }
}
