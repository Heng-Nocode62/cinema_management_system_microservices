package com.heng.cms.concessionservice.service;

import com.heng.cms.concessionservice.dto.request.CreateComboRequest;
import com.heng.cms.concessionservice.dto.request.UpdateComboRequest;
import com.heng.cms.concessionservice.dto.response.ComboResponse;

import java.util.List;
import java.util.UUID;

public interface ComboService {
    ComboResponse createCombo(CreateComboRequest request);
    ComboResponse getComboById(UUID comboId);
    List<ComboResponse> getAllCombos();
    ComboResponse updateCombo(UUID comboId, UpdateComboRequest request);
    void deleteCombo(UUID comboId);
}
