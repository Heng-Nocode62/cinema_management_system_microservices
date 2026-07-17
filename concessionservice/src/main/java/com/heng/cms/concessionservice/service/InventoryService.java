package com.heng.cms.concessionservice.service;

import com.heng.cms.concessionservice.dto.request.CreateInventoryRequest;
import com.heng.cms.concessionservice.dto.request.UpdateStockRequest;
import com.heng.cms.concessionservice.dto.response.InventoryResponse;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface InventoryService {
    InventoryResponse createInventory(CreateInventoryRequest request);
    InventoryResponse getInventoryById(UUID inventoryId);
    InventoryResponse getInventoryByCinemaIdAndMenuItemId(UUID cinemaId, UUID menuItemId);
    List<InventoryResponse> getAllInventory(int page, int pageSize);
    List<InventoryResponse> getAllInventory(Map<String,Object> filterParams);
    void updateStock(UpdateStockRequest request);

    @Transactional
    void increaseStock(UpdateStockRequest request);

    @Transactional
    void decreaseStocks(List<UpdateStockRequest> requests);
}
