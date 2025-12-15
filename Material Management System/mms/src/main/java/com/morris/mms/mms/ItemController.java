package com.morris.mms.mms;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Controller
public class ItemController {

    private final ItemRepository itemRepo;
    private final ItemQueryService queryService;
    private final NoteRepository noteRepo;

    public ItemController(ItemRepository itemRepo, ItemQueryService queryService, NoteRepository noteRepo) {
        this.itemRepo = itemRepo;
        this.queryService = queryService;
        this.noteRepo = noteRepo;
    }

    // === 共用：塞滿你的 items.html 需要的 model ===
    private void fillModel(Model model, List<Item> items) {
        model.addAttribute("items", items);
        model.addAttribute("categories", itemRepo.distinctCategories());
        model.addAttribute("rooms", itemRepo.distinctRooms());
        model.addAttribute("locations", itemRepo.distinctLocations());
        model.addAttribute("openNotes", noteRepo.findByDoneFalseOrderByCreatedAtDesc());

        // items.html 的 th:object="${item}" 需要它
        if (!model.containsAttribute("item")) {
            model.addAttribute("item", new Item());
        }

        // ====== ✅ 追加：過期/即將到期/低庫存清單 ======
        LocalDate today = LocalDate.now();

        List<Item> expiredItems = items.stream()
                .filter(i -> i.getExpireDate() != null && i.getExpireDate().isBefore(today))
                .sorted(Comparator.comparing(Item::getExpireDate))
                .toList();

        List<Item> exp7Items = items.stream()
                .filter(i -> i.getExpireDate() != null)
                .filter(i -> {
                    Long d = i.getDaysToExpire();
                    return d != null && d >= 0 && d <= 7;
                })
                .sorted(Comparator.comparing(Item::getExpireDate))
                .toList();

        List<Item> exp14Items = items.stream()
                .filter(i -> i.getExpireDate() != null)
                .filter(i -> {
                    Long d = i.getDaysToExpire();
                    return d != null && d > 7 && d <= 14;
                })
                .sorted(Comparator.comparing(Item::getExpireDate))
                .toList();

        List<Item> lowStockItems = items.stream()
                .filter(Item::isLowStock)
                .sorted(Comparator.comparing(i -> i.getQuantity() == null ? 0 : i.getQuantity()))
                .toList();

        model.addAttribute("expiredItems", expiredItems);
        model.addAttribute("exp7Items", exp7Items);
        model.addAttribute("exp14Items", exp14Items);
        model.addAttribute("lowStockItems", lowStockItems);
    }

    private boolean ensureLogin(HttpSession session) {
        return session.getAttribute("loginUser") != null;
    }

    // ====== 進入主頁 /items ======
    @GetMapping("/items")
    public String itemsPage(HttpSession session, Model model,
                            @ModelAttribute("successMessage") String successMessage) {
        if (!ensureLogin(session)) return "redirect:/login";

        fillModel(model, itemRepo.findAll());

        if (successMessage != null && !successMessage.isBlank()) {
            model.addAttribute("successMessage", successMessage);
        }
        return "items";
    }

    // ====== 新增 ======
    @PostMapping("/items")
    public String create(HttpSession session,
                         @Valid @ModelAttribute("item") Item item,
                         BindingResult br,
                         RedirectAttributes ra,
                         Model model) {
        if (!ensureLogin(session)) return "redirect:/login";

        if (item.getLocation() == null || item.getLocation().trim().isEmpty()) {
            br.rejectValue("location", "required", "新增時請選擇或輸入儲位");
        }

        if (br.hasErrors()) {
            fillModel(model, itemRepo.findAll());
            return "items";
        }

        if (item.getQuantity() == null) item.setQuantity(0);
        itemRepo.save(item);

        ra.addFlashAttribute("successMessage", "新增成功！");
        return "redirect:/items#section-list";
    }

    // ====== 查詢 ======
    @GetMapping("/items/search")
    public String search(HttpSession session,
                         @ModelAttribute("item") Item item,
                         Model model) {
        if (!ensureLogin(session)) return "redirect:/login";

        if (item.getName() == null || item.getName().trim().isEmpty()) {
            fillModel(model, itemRepo.findAll());
            model.addAttribute("successMessage", "請輸入名稱再查詢");
            return "items";
        }

        List<Item> result = queryService.search(item);
        fillModel(model, result);
        model.addAttribute("item", item);
        return "items";
    }

    // ====== 依儲位瀏覽 ======
    @GetMapping("/items/location")
    public String byLocation(HttpSession session, @RequestParam("name") String name, Model model) {
        if (!ensureLogin(session)) return "redirect:/login";

        List<Item> items = itemRepo.findByLocation(name);
        fillModel(model, items);

        Item probe = new Item();
        probe.setLocation(name);
        model.addAttribute("item", probe);

        return "items";
    }

    // ====== 依類別瀏覽 ======
    @GetMapping("/items/category")
    public String byCategory(HttpSession session, @RequestParam("name") String name, Model model) {
        if (!ensureLogin(session)) return "redirect:/login";

        List<Item> items = itemRepo.findByCategory(name);
        fillModel(model, items);

        Item probe = new Item();
        probe.setCategory(name);
        model.addAttribute("item", probe);

        return "items";
    }

    // ====== +1 / -1 ======
    @PostMapping("/items/{id}/adjust")
    public String adjust(HttpSession session, @PathVariable Long id, @RequestParam("delta") int delta) {
        if (!ensureLogin(session)) return "redirect:/login";

        itemRepo.findById(id).ifPresent(it -> {
            int q = (it.getQuantity() == null) ? 0 : it.getQuantity();
            q += delta;
            if (q < 0) q = 0;
            it.setQuantity(q);
            itemRepo.save(it);
        });
        return "redirect:/items#section-list";
    }

    // ====== 小視窗更改數量 ======
    @PostMapping("/items/{id}/setQuantity")
    public String setQuantity(HttpSession session, @PathVariable Long id, @RequestParam("quantity") int quantity) {
        if (!ensureLogin(session)) return "redirect:/login";

        if (quantity < 0) quantity = 0;
        int finalQuantity = quantity;

        itemRepo.findById(id).ifPresent(it -> {
            it.setQuantity(finalQuantity);
            itemRepo.save(it);
        });

        return "redirect:/items#section-list";
    }

    // ====== 刪除 ======
    @PostMapping("/items/{id}/delete")
    public String delete(HttpSession session, @PathVariable Long id) {
        if (!ensureLogin(session)) return "redirect:/login";
        itemRepo.deleteById(id);
        return "redirect:/items#section-list";
    }
}
