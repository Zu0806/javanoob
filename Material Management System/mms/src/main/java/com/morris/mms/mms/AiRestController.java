package com.morris.mms.mms;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiRestController {

    private final AiSuggestService ai;

    public AiRestController(AiSuggestService ai) {
        this.ai = ai;
    }

    public static class Req { public String name; }

    @PostMapping("/suggest")
    public AiSuggestResponse suggest(@RequestBody Req req) {
        return ai.suggest(req == null ? "" : req.name);
    }
}
