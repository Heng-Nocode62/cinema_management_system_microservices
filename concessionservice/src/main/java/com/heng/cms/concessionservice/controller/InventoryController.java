package com.heng.cms.concessionservice.controller;


import com.heng.cms.concessionservice.dto.request.CreateInventoryRequest;
import com.heng.cms.concessionservice.dto.request.UpdateStockRequest;
import com.heng.cms.concessionservice.dto.response.InventoryResponse;
import com.heng.cms.concessionservice.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/concessions/inventories")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody CreateInventoryRequest createInventoryRequest) {
        return new ResponseEntity<>(
                inventoryService.createInventory(createInventoryRequest),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getInventoryById(
            @PathVariable("id") UUID inventoryId) {
        return new ResponseEntity<>(
                inventoryService.getInventoryById(inventoryId),
                HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory(
            @RequestParam(name = "page", defaultValue = "0",required = false)
            int page,
            @RequestParam(name = "size",defaultValue = "10",required = false)
            int size
    ) {
        return new ResponseEntity<>(
                inventoryService.getAllInventory(page, size),
                HttpStatus.OK);
    }
    @PatchMapping("/increase")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void increaseStock(@Valid @RequestBody UpdateStockRequest updateStockRequest) {
        inventoryService.increaseStock(updateStockRequest);
    }

    @PatchMapping("/decrease")
    public void decreaseStock(@Valid @RequestBody  List<UpdateStockRequest> requests) {
        inventoryService.decreaseStocks(requests);
    }
}
