package org.allen.erpoor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.allen.erpoor.inventory.InventoryController;
import org.allen.erpoor.inventory.ProductInventoryViewRepository;
import org.allen.erpoor.inventory.ProductRepository;
import org.allen.erpoor.inventory.entity.Product;
import org.allen.erpoor.inventory.entity.ProductInventoryView;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class InventoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductInventoryViewRepository productInventoryViewRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryController inventoryController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("查询库存状态 - 空数据")
    void findAll_EmptyData() throws Exception {
        when(productInventoryViewRepository.findAll())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/inventory/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))  // 改为 code
                .andExpect(jsonPath("$.message").value("未找到庫存記錄"))
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(productInventoryViewRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("查询库存状态 - 服务层异常")
    void findAll_ServiceException() throws Exception {
        when(productInventoryViewRepository.findAll())
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/inventory/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))  // 改为 code
                .andExpect(jsonPath("$.message").value("查詢庫存狀態失敗"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(productInventoryViewRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("新增商品 - 成功")
    void addProduct_Success() throws Exception {
        // Arrange 測試輸入資料
        Product mockProduct = new Product();
        mockProduct.setName("測試商品");
        mockProduct.setCategory("飲料");
        mockProduct.setUnit("瓶");
        mockProduct.setDescription("這是一個測試商品");
        mockProduct.setEnabled(true);
        mockProduct.setPrice(new BigDecimal("25.50"));

        // 模擬儲存成功
        Product savedProduct = new Product();
        savedProduct.setName("測試商品");
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act & Assert 驗證只看狀態與 code 欄位
        mockMvc.perform(post("/inventory/addProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
