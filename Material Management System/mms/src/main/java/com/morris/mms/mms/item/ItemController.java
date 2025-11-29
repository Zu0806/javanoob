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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.morris.mms.mms.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository repo;

    public ItemController(ItemRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String list(Model model,
                       @ModelAttribute("item") Item item,
                       HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("items", repo.findAll());
        model.addAttribute("loginUser", loginUser);
        return "items";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("item") Item item,
                         BindingResult br,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         HttpSession session) {

        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }

        if (br.hasErrors()) {
            model.addAttribute("items", repo.findAll());
            return "items";
        }

        repo.save(item);
        redirectAttributes.addFlashAttribute("successMessage", "物品新增成功！");
        return "redirect:/items";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes,
                         HttpSession session) {

        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }

        repo.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "已刪除物品 ID：" + id);
        return "redirect:/items";
    }

    @PostMapping("/{id}/adjust")
    public String adjust(@PathVariable Long id,
                         @RequestParam int delta,
                         RedirectAttributes redirectAttributes,
                         HttpSession session) {

        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }

        Item it = repo.findById(id).orElseThrow();
        it.setQuantity(Math.max(0, it.getQuantity() + delta));
        repo.save(it);

        redirectAttributes.addFlashAttribute("successMessage", "已更新物品 ID " + id + " 的數量");
        return "redirect:/items";
    }
}
