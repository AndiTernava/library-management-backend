package org.andi.librarymanagementbackend.unit;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.andi.librarymanagementbackend.controller.ReservationController;
import org.andi.librarymanagementbackend.dto.ReservationDto;
import org.andi.librarymanagementbackend.service.ReservationService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private ReservationService svc;
    @Autowired private ObjectMapper mapper;

    @Test
    @WithMockUser
    void postReservation_returnsDto() throws Exception {
        ReservationDto dto = new ReservationDto(1L,1L,1L,LocalDate.now(),LocalDate.now().plusDays(1),false,"PENDING");
        when(svc.create(any(), anyString())).thenReturn(dto);

        mvc.perform(post("/api/reservations")
                        .header("X-Tenant-ID","tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void listReservations_member_returnsList() throws Exception {
        when(svc.findByUser(anyLong(), anyString()))
                .thenReturn(List.of(new ReservationDto(1L,1L,1L,LocalDate.now(),LocalDate.now(),false,"PENDING")));

        mvc.perform(get("/api/reservations")
                        .header("X-Tenant-ID","tenant1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
}
