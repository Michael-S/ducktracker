package io.firebreathingduck.ducktracker.controllers;

//import io.firebreathingduck.ducktracker.controllers.model.Duck; // Removed, I don't need it (yet?)

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DucksController {

    @GetMapping("/duck")
    public String duckForm(Model model) {
        //model.addAttribute("duck", new Duck());
        return "duck";
    }

    @GetMapping("/pond")
    public String pondForm(Model model) {
        return "pond";
    }

    /*
    // I am replacing Spring-web's form handling with calls to the REST API
    // that I built.  I think that's simpler to work with, at least for now.
    @PostMapping("/duck")
    public String duckSubmit(@ModelAttribute Duck duck, Model model) {
        //model.addAttribute("duck", duck);
        return "result";
    }
    */

}