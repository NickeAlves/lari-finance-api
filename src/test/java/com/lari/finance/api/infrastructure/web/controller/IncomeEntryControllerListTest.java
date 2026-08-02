package com.lari.finance.api.infrastructure.web.controller;

import com.lari.finance.api.application.service.DateRange;
import com.lari.finance.api.domain.model.PaymentMethod;
import com.lari.finance.api.domain.model.UserRole;
import com.lari.finance.api.infrastructure.persistence.entity.IncomeEntryEntity;
import com.lari.finance.api.infrastructure.persistence.entity.UserAccountEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Regresión del bug de agosto de 2026: {@code GET /api/entries} sin {@code from}/{@code to}
 * devolvía solo el mes en curso, así que el calendario se vaciaba al cambiar de mes.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IncomeEntryControllerListTest {
    private static final String EMAIL = "lista-owner@example.com";

    // Dos meses atrás: fuera de la ventana "mes actual" cualquiera que sea el día de hoy.
    private static final LocalDate PAST_MONTH_DATE =
        LocalDate.now(DateRange.ZONE).minusMonths(2).withDayOfMonth(15);

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EntityManager entityManager;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        UUID userId = UUID.randomUUID();
        entityManager.persist(new UserAccountEntity(userId, "Owner", EMAIL, "hash", UserRole.OWNER, Instant.now()));
        Instant now = Instant.now();
        entityManager.persist(new IncomeEntryEntity(
            UUID.randomUUID(), userId, PAST_MONTH_DATE, "Clienta antigua", new BigDecimal("20.00"),
            PaymentMethod.EFECTIVO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
            BigDecimal.ZERO, BigDecimal.ZERO, null, false, null, null, now, now));
        entityManager.flush();
    }

    @Test
    @WithMockUser(username = EMAIL)
    void listWithoutDateParams_returnsEntriesOutsideTheCurrentMonth() throws Exception {
        mockMvc.perform(get("/api/entries"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.metadata.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].clientName").value("Clienta antigua"))
            .andExpect(jsonPath("$.content[0].date").value(PAST_MONTH_DATE.toString()));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void listStillHonoursExplicitDateFilters() throws Exception {
        mockMvc.perform(get("/api/entries")
                .param("from", PAST_MONTH_DATE.plusDays(1).toString())
                .param("to", PAST_MONTH_DATE.plusDays(5).toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.metadata.totalElements").value(0));
    }
}
