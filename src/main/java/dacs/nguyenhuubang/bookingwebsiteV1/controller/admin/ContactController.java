package dacs.nguyenhuubang.bookingwebsiteV1.controller.admin;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Contact;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.ContactRepo;
import dacs.nguyenhuubang.bookingwebsiteV1.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/contacts")
public class ContactController {
    private final ContactService contactService;
    private final ContactRepo contactRepo;

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        int pageSize = 6;
        Page<Contact> page = contactService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Contact> cities = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("cities", cities);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("reserseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "admin/pages/admin_crud_contacts";
    }

    @GetMapping("")
    public String getCities(Model model) {
        return findPaginated(1, model, "id", "asc");
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            contactService.delete(id);
            ra.addFlashAttribute("raMessage", "Liên hệ với (ID: " + id + ") đã bị xóa");
        } catch (ResourceNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (CannotDeleteException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/contacts";
    }


}
