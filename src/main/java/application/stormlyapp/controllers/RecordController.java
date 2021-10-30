package application.stormlyapp.controllers;

import application.stormlyapp.model.Record;
import application.stormlyapp.services.RecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.temporal.ChronoUnit;
import java.util.Set;

@Controller
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping({"","/"})
    public String getIndexPage() {
        return "mainPage";
    }

    @GetMapping("/archive")
    public String getArchivePage(Model model) {
        model.addAttribute("records", recordService.findAll());
        return "archivePage";
    }
}
