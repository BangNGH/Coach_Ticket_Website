package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class AdminController {


//init commit
    @RequestMapping(value = {"", "/", "/home"})
    public String homePage(){
        return "admin/pages/admin_landing_page";
    }


}