package com.heng.cms.concessionservice.controller;


import com.heng.cms.concessionservice.dto.ConcessionRequest;
import com.heng.cms.concessionservice.dto.ConcessionResponse;
import com.heng.cms.concessionservice.dto.ReserveConcessionRequest;
import com.heng.cms.concessionservice.service.ConcessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/concessions")
@RequiredArgsConstructor
public class ConcessionController {
    private final ConcessionService concessionService;

    @PostMapping("/reserve")
    public ResponseEntity<ConcessionResponse> reserveItem(
            @Valid @RequestBody ReserveConcessionRequest request
    ){
        return new ResponseEntity<>(
                concessionService.reserveConcession(request),
                HttpStatus.OK
        );
    }

    @PostMapping("/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirmReservedItem(
            @Valid @RequestBody ConcessionRequest request
    ){
        concessionService.confirmReservedItem(request);
    }

    @PostMapping("/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservedItem(
            @Valid @RequestBody ConcessionRequest request
    ){
        concessionService.cancelReservedItem(request);
    }

}
