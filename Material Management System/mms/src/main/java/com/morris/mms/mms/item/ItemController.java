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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;   // ★ 新增這行

import jakarta.validation.Valid;

@Controller
@RequestMapping("/items")
public class ItemController {
    private final ItemRepository repo;
    public ItemController(ItemRepository repo) { this.repo = repo; }

    @GetMapping
    public String list(Model model, @ModelAttribute("item") Item item) {
        model.addAttribute("items", repo.findAll());
        // ★ 不用特別處理 successMessage，Flash Attribute 會自動帶到 model 裡面
        return "items"; // 對應你那個 items.html（就是你貼的那份前端）
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("item") Item item,
                         BindingResult br,
                         Model model,
                         RedirectAttributes redirectAttributes) {  // ★ 加這個參數
        if (br.hasErrors()) {
            model.addAttribute("items", repo.findAll());
            return "items";
        }
        repo.save(item);

        // ★ 新增成功時放入 Flash Attribute
        redirectAttributes.addFlashAttribute("successMessage", "物品新增成功！");

        // redirect 回 /items，Thymeleaf 那段 th:if="${successMessage}" 就會顯示 Toast
        return "redirect:/items";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        repo.deleteById(id);
        // 如果你也想刪除時跳訊息，可以加這樣（可選）
        redirectAttributes.addFlashAttribute("successMessage", "已刪除物品 ID：" + id);
        return "redirect:/items";
    }

    @PostMapping("/{id}/adjust")
    public String adjust(@PathVariable Long id,
                         @RequestParam int delta,
                         RedirectAttributes redirectAttributes) {
        Item it = repo.findById(id).orElseThrow();
        it.setQuantity(Math.max(0, it.getQuantity() + delta));
        repo.save(it);

        // 若不想每次 +1 / -1 都跳訊息，可以拿掉這段（看你喜好）
        //redirectAttributes.addFlashAttribute("successMessage", "已更新物品 ID " + id + " 的數量");
        return "redirect:/items";
    }
}
