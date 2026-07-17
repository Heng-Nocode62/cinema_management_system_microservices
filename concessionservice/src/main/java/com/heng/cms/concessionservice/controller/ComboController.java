package com.heng.cms.concessionservice.controller;

import com.heng.cms.concessionservice.dto.request.CreateComboRequest;
import com.heng.cms.concessionservice.dto.request.UpdateComboRequest;
import com.heng.cms.concessionservice.dto.response.ComboResponse;
import com.heng.cms.concessionservice.service.ComboService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/concessions/combos")
@RequiredArgsConstructor
public class ComboController {
    private final ComboService comboService;

    @PostMapping
    public ResponseEntity<ComboResponse> createCombo(@Valid @RequestBody CreateComboRequest request){
        return new ResponseEntity<>(comboService.createCombo(request), HttpStatus.CREATED);
    }

    @GetMapping("/{comboId}")
    public ResponseEntity<ComboResponse> getComboBy(
            @PathVariable("comboId")
            UUID comboId) {
        return new ResponseEntity<>(
                comboService.getComboById(comboId),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<List<ComboResponse>> getAllCombos() {
        return new ResponseEntity<>(comboService.getAllCombos(), HttpStatus.OK);
    }

    @PutMapping("/{comboId}")
    public ResponseEntity<ComboResponse> updateCombo(
            @PathVariable("comboId") UUID comboId,
            @Valid @RequestBody UpdateComboRequest request
            ){
        return  new ResponseEntity<>(comboService.updateCombo(comboId, request), HttpStatus.OK);
    }
    @PatchMapping("/{comboId}/delete")
    public void deleteCombo(@PathVariable("comboId")  UUID comboId){
        comboService.deleteCombo(comboId);
    }

}
