package com.morris.mms.mms;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai1")
public class AiSuggestController {

    private final AiSuggestService service;

    public AiSuggestController(AiSuggestService service) {
        this.service = service;
    }

    @PostMapping("/suggest")
    public AiSuggestResponse suggest(@RequestBody AiSuggestRequest req) {
        return service.suggest(req == null ? null : req.getName());
    }
}
