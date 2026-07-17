package com.heng.cms.concessionservice.service;

import com.heng.cms.concessionservice.dto.ConcessionRequest;
import com.heng.cms.concessionservice.dto.ConcessionResponse;
import com.heng.cms.concessionservice.dto.ReserveConcessionRequest;
import jakarta.transaction.Transactional;

public interface ConcessionService {

    ConcessionResponse reserveConcession(ReserveConcessionRequest request);

    @Transactional
    void confirmReservedItem(ConcessionRequest request);

    @Transactional
    void cancelReservedItem(ConcessionRequest request);
}
