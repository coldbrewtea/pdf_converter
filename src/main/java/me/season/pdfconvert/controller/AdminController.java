package me.season.pdfconvert.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @RequestMapping(value = "/alive", method = RequestMethod.GET)
    public String alive() {
        return "I'm still alive...";
    }
}
