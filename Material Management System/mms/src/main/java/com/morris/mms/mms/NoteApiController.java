package com.morris.mms.mms;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/notes")
@CrossOrigin(origins = "http://localhost:5173")
public class NoteApiController {

    private final NoteRepository noteRepo;

    public NoteApiController(NoteRepository noteRepo) {
        this.noteRepo = noteRepo;
    }

    @GetMapping
    public List<Note> list() {
        return noteRepo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @PostMapping
    public Note create(@RequestBody Map<String, Object> body) {
        String text = body.get("text") == null ? "" : String.valueOf(body.get("text")).trim();
        if (text.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "text is required");
        }

        Note n = new Note();
        n.setText(text);
        n.setDone(false);
        // ✅ 不要 setCreatedAt，Note.java 已經 createdAt = LocalDateTime.now()
        return noteRepo.save(n);
    }

    @PostMapping("/{id}/toggle")
    public Note toggle(@PathVariable Long id) {
        Note n = noteRepo.findById(id).orElseThrow();
        n.setDone(!n.isDone());
        return noteRepo.save(n);
    }
}
