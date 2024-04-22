package org.studytest.saving_account.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProcessController {
    @GetMapping(value = {"/Process"})
    public String showProcess() {
        return "maturing";
    }
}
