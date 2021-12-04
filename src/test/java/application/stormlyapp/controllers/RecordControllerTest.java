package application.stormlyapp.controllers;

import application.stormlyapp.exceptions.NotFoundException;
import application.stormlyapp.model.Record;
import application.stormlyapp.services.RecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RecordControllerTest {

    @Mock
    RecordService recordService;

    MockMvc mockMvc;

    RecordController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new RecordController(recordService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetIndexPage() throws Exception {
        when(recordService.findByDateTime(any())).thenReturn(Record.builder().build());
        when(recordService.findByDateHourly(any())).thenReturn(List.of(Record.builder().id(1L).build(),Record.builder().id(2L).build()));
        when(recordService.findByDateDaily(any())).thenReturn(List.of(Record.builder().id(1L).build(),Record.builder().id(2L).build()));
        when(recordService.getFormattedDate(any())).thenReturn("test string");

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
    }

    @Test
    void testGetArchivePage() throws Exception {
        mockMvc.perform(get("/archive"))
                .andExpect(status().isOk())
                .andExpect(view().name("archivePage"));

        verify(recordService,times(1)).findAll();
    }

    @Test
    void testGetArchivePageFiltered() throws Exception {
        mockMvc.perform(get("/archive/Month"))
                .andExpect(status().isOk())
                .andExpect(view().name("archivePage"))
                .andExpect(model().attributeExists("records"));

        verify(recordService, times(1)).findAllBeforeDate(anyLong(), any(ChronoUnit.class));
    }

    @Test
    void testGetArchivePageFilteredNotFound() throws Exception { //todo fix
        //mockMvc.perform(get("/archive/gregr"))
        //        .andExpect(status().isNotFound())
        //        .andExpect(view().name("404Page"));

       //verifyNoInteractions(recordService);
    }
}
