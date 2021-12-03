package application.stormlyapp.controllers;

import application.stormlyapp.exceptions.NotFoundException;
import application.stormlyapp.model.Record;
import application.stormlyapp.services.RecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

@Controller
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping({"","/"})
    public String getIndexPage(Model model) {
        List<Record> recordsHourly = recordService.findByDateHourly(LocalDateTime.now());
        List<Record> recordsDaily = recordService.findByDateDaily(LocalDateTime.now());
        model.addAttribute("recordsHourly", recordsHourly);
        model.addAttribute("recordsDaily", recordsDaily);
        model.addAttribute("recordNow", recordService.findByDateTime(LocalDateTime.now()));
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
                throw new NotFoundException("Given time stamp: '" + timeStamp + "' is not available.");
        }

        model.addAttribute("records", recordService.findAllBeforeDate(1L, unit));
        return "archivePage";
    }
}
