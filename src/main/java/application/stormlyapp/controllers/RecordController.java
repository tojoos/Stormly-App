package application.stormlyapp.controllers;

import application.stormlyapp.repositories.RecordRepository;
import org.springframework.stereotype.Controller;
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
}
