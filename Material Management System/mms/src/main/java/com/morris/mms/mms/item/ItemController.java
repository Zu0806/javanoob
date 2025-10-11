package com.morris.mms.mms.item;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/items")
public class ItemController {
    private final ItemRepository repo;
    public ItemController(ItemRepository repo) { this.repo = repo; }

    @GetMapping
    public String list(Model model, @ModelAttribute("item") Item item) {
        model.addAttribute("items", repo.findAll());
        return "items";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("item") Item item, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("items", repo.findAll());
            return "items";
        }
        repo.save(item);
        return "redirect:/items";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/items";
    }

    @PostMapping("/{id}/adjust")
    public String adjust(@PathVariable Long id, @RequestParam int delta) {
        Item it = repo.findById(id).orElseThrow();
        it.setQuantity(Math.max(0, it.getQuantity() + delta));
        repo.save(it);
        return "redirect:/items";
    }
}
