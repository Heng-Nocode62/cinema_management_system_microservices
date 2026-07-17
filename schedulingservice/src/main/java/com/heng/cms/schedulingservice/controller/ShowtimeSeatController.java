package com.heng.cms.schedulingservice.controller;

import com.heng.cms.schedulingservice.dto.GetShowtimeSeatsRequest;
import com.heng.cms.schedulingservice.dto.SeatLockingRequest;
import com.heng.cms.schedulingservice.dto.ShowtimeSeatResponse;
import com.heng.cms.schedulingservice.dto.UpdateSeatStatusRequest;
import com.heng.cms.schedulingservice.dto.client.SeatResponse;
import com.heng.cms.schedulingservice.service.ShowtimeSeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/showtime-seat")
@RequiredArgsConstructor
public class ShowtimeSeatController {
    private final ShowtimeSeatService showtimeSeatService;


    @GetMapping("{id}")
    public ResponseEntity<ShowtimeSeatResponse> getById(
            @PathVariable("id") UUID showtimeSeatId
    ){
        return new ResponseEntity<>(showtimeSeatService.getById(showtimeSeatId),HttpStatus.OK);
    }

    @PostMapping("/showtime")
    //TODO handle lock
    public ResponseEntity<List<ShowtimeSeatResponse>> getShowTimeSeatsByIdsAndShowtimeId(
            @RequestBody GetShowtimeSeatsRequest request
            ){
        return ResponseEntity.ok().body(showtimeSeatService.getScreenIdAndSeatIds(request));
    }

    @PutMapping("update/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeatStatus(
            @Valid @RequestBody UpdateSeatStatusRequest request
    ){
        showtimeSeatService.updateSeatsStatus(request);
    }

    @PutMapping("lock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void lockSeats(
            @RequestBody SeatLockingRequest request
    ){
        showtimeSeatService.lockSeats(request);
    }
    @PutMapping("unlock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlockSeats(
            @RequestBody SeatLockingRequest request
            ){
        showtimeSeatService.unlockSeats(request);
    }


}
