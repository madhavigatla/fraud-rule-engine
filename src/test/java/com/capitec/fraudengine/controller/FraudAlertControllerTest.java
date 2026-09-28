package com.capitec.fraudengine.controller;

import com.capitec.fraudengine.model.FraudAlert;
import com.capitec.fraudengine.repository.FraudAlertRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FraudAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FraudAlertRepository fraudAlertRepository;

    @Test
    @WithMockUser
    void testGetAllAlerts() throws Exception {
        Page<FraudAlert> page = new PageImpl<>(Collections.emptyList());
        when(fraudAlertRepository.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/fraud-alerts"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetAllAlertsWithSeverity() throws Exception {
        Page<FraudAlert> page = new PageImpl<>(Collections.emptyList());
        when(fraudAlertRepository.findBySeverity(eq("HIGH"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/fraud-alerts").param("severity", "HIGH"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetAlertsByCustomerId() throws Exception {
        Page<FraudAlert> page = new PageImpl<>(Collections.emptyList());
        when(fraudAlertRepository.findByCustomerId(eq("CUST001"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/fraud-alerts/customer/CUST001"))
                .andExpect(status().isOk());
    }
}
