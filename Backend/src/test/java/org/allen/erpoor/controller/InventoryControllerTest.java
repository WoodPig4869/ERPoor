package org.allen.erpoor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.allen.erpoor.inventory.InventoryController;
import org.allen.erpoor.inventory.ProductInventoryStatusRepository;
import org.allen.erpoor.inventory.entity.ProductInventoryStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class InventoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductInventoryStatusRepository productInventoryStatusRepository;

    @InjectMocks
    private InventoryController inventoryController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();
        objectMapper = new ObjectMapper();
    }
    @Test
    @DisplayName("查询库存状态 - 成功返回数据")
    void findAll_WithData() throws Exception {
        // 測試數據
        ProductInventoryStatus status1 = new ProductInventoryStatus(
                8,
                "100%純烏魚鬆120g",
                "罐",
                "", // 空描述
                new BigDecimal("370.00"),
                true,
                4557,
                LocalDate.parse("2025-05-29"),
                LocalDate.parse("2025-07-02"),
                "凱亞良品"
        );
        ProductInventoryStatus status2 = new ProductInventoryStatus(
                9,
                "頂級烏魚子醬",
                "瓶",
                "", // 空描述
                new BigDecimal("750.00"),
                true,
                5266,
                LocalDate.parse("2025-05-29"),
                LocalDate.parse("2025-07-02"),
                "凱亞良品"
        );

        when(productInventoryStatusRepository.findAll())
                .thenReturn(Arrays.asList(status1, status2));

        // 执行并验证 - 修改断言以匹配实际响应结构
        mockMvc.perform(get("/inventory/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))  // 改为 code 而不是 status
                .andExpect(jsonPath("$.message").value("查詢成功"))
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(productInventoryStatusRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("查询库存状态 - 空数据")
    void findAll_EmptyData() throws Exception {
        when(productInventoryStatusRepository.findAll())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/inventory/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))  // 改为 code
                .andExpect(jsonPath("$.message").value("未找到庫存記錄"))
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(productInventoryStatusRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("查询库存状态 - 服务层异常")
    void findAll_ServiceException() throws Exception {
        when(productInventoryStatusRepository.findAll())
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/inventory/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))  // 改为 code
                .andExpect(jsonPath("$.message").value("查詢庫存狀態失敗"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(productInventoryStatusRepository, times(1)).findAll();
    }
}