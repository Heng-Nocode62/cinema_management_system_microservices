package com.heng.cms.concessionservice.mapper;

import com.heng.cms.concessionservice.domain.entity.Combo;
import com.heng.cms.concessionservice.domain.entity.ComboItem;
import com.heng.cms.concessionservice.dto.response.ComboResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ComboMapper {

    public ComboResponse toComboResponse(Combo combo, List<ComboItem> comboItems) {

        List<ComboResponse.ItemResponse> items = new ArrayList<>();
        for (ComboItem comboItem : comboItems) {
            items.add(new ComboResponse.ItemResponse(
                    comboItem.getId(),
                    comboItem.getMenuItem().getId(),
                    comboItem.getQuantity())
            );
        }
        return new ComboResponse(
                combo.getId(),
                combo.getName(),
                combo.getPrice(),
                combo.getSavingsAmount(),
                items
        );
    }

}
