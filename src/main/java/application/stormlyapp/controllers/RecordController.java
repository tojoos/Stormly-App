package application.stormlyapp.controllers;

import application.stormlyapp.services.RecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.temporal.ChronoUnit;

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

    @GetMapping("/archive/{timeStamp}")
    public String getArchivePageFiltered(Model model, @PathVariable String timeStamp) {
        ChronoUnit unit;

        switch(timeStamp) {
            case "Hour":
                unit = ChronoUnit.HOURS;
                break;
            case "Day":
                unit = ChronoUnit.DAYS;
                break;
            case "Week":
                unit = ChronoUnit.WEEKS;
                break;
            case "Month":
                unit = ChronoUnit.MONTHS;
                break;
            default:
                return "404Page";
        }

        model.addAttribute("records", recordService.findAllBeforeDate(1L, unit));
        return "archivePage";
    }
}
