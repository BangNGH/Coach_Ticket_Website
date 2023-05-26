package dacs.nguyenhuubang.bookingwebsiteV1.RestController;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Contact;
import dacs.nguyenhuubang.bookingwebsiteV1.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactRestController {
    private final ContactService contactService;

    @GetMapping("/search")
    @ResponseBody
    public List<Contact> searchUsers(@RequestParam("q") String q) {
        List<Contact> cities = contactService.search(q);
        return cities;
    }

}
