package com.heng.cms.concessionservice.dto.response;

import java.util.UUID;

public record ComboItemResponse(
        UUID id,
        UUID comboId,
        UUID menuItemId,
        Integer menuItemQuantity
) {
}
