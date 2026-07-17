package com.heng.cms.paymentservice.service;

import com.heng.cms.paymentservice.domain.PromoCode;
import com.heng.cms.paymentservice.exception.PaymentException;
import com.heng.cms.paymentservice.repository.PromoCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PromoCodeService {
    private final PromoCodeRepository  promoCodeRepository;

    public boolean validate(String code, BigDecimal amount) {
        return promoCodeRepository.findByCodeIgnoreCase(code)
                .map(p->isUsable(p,amount)).orElse(false);
    }



    private boolean isUsable(PromoCode p, BigDecimal amount) {
        if (!p.isActive()){
            return false;
        }
        if (p.getValidUntil() == null && p.getValidUntil().isBefore(LocalDate.now())){
            return false;
        }
        if(p.getMaxUses()!= null && p.getUsesCount()>p.getMaxUses()){
            return false;
        }
        return true;
    }

    public Map<String, Object> getDiscount(String code, BigDecimal amount) {
        PromoCode promoCode = promoCodeRepository.findByCodeIgnoreCase(code)
                .filter(p->isUsable(p,amount))
                .orElseThrow(()->new PaymentException("Invalid or expired promo code: " + code));
        return Map.of(
                "discountAmount", calculate(promoCode, amount),
                "valid",true
        );
    }

    public void consume(String code){
        promoCodeRepository.incrementUsesCount(code);
    }

    private BigDecimal calculate(PromoCode p, BigDecimal amount) {
        if("PERCENT".equalsIgnoreCase(p.getDiscountType())){
            return amount.multiply(p.getDiscountValue())
                    .divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP);
        }
        return p.getDiscountValue().min(amount);
    }

}
