package application.stormlyapp.controllers;

import application.stormlyapp.repositories.RecordRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecordController {

    private final RecordRepository recordRepository;

    public RecordController(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @GetMapping({"","/"})
    public String getIndexPage() {
        return "mainPage";
    }

    @GetMapping("/archive")
    public String getArchivePage(Model model) {
        model.addAttribute("records", recordRepository.findAll());
        return "archivePage";
    }
}
