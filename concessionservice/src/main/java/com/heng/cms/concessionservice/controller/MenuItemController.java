package com.heng.cms.concessionservice.controller;

import com.heng.cms.concessionservice.dto.request.MenuItemRequest;
import com.heng.cms.concessionservice.dto.response.MenuItemResponse;
import com.heng.cms.concessionservice.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/concessions/menuItems")
@RequiredArgsConstructor
public class MenuItemController {
    private final MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<MenuItemResponse> crateMenuItem(@Valid @RequestBody MenuItemRequest menuItemRequest) {
        return new ResponseEntity<>(
                menuItemService.createMenuItem(menuItemRequest),
                HttpStatus.CREATED
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> getMenuItem(@PathVariable UUID id) {
        return new ResponseEntity<>(menuItemService.getMenuItem(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService.getAllMenuItem());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable("id")
            UUID id,
            @Valid
            @RequestBody MenuItemRequest menuItemRequest
    ) {
        return  new ResponseEntity<>(menuItemService.updateMenuItem(id, menuItemRequest), HttpStatus.OK);
    }

    @PatchMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable UUID id) {

        menuItemService.deleteMenuItem(id);
    }
}
