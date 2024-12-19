package com.example.demo.controller;

import com.example.demo.dto.ReservationRequestDto;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Reservation;
import com.example.demo.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequestDto reservationRequestDto) {
        Reservation reservation = reservationService.createReservation(reservationRequestDto.getItemId(),
                reservationRequestDto.getUserId(),
                reservationRequestDto.getStartAt(),
                reservationRequestDto.getEndAt());

        return ResponseEntity.ok().body(reservation); // 7-2 응답 데이터 타입 변경
    }

    @PatchMapping("/{id}/update-status")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody String status) {
        Reservation reservation = reservationService.updateReservationStatus(id, status);

        return ResponseEntity.ok().body(reservation); // 7-2 응답 데이터 타입 변경
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> findAll() {
        List<ReservationResponseDto> reservations = reservationService.getReservations();

        return ResponseEntity.ok().body(reservations); // 7-2 응답 데이터 타입 변경
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponseDto>> searchAll(@RequestParam(required = false) Long userId,
                          @RequestParam(required = false) Long itemId) {
        List<ReservationResponseDto> reservationResponseDtos = reservationService.searchAndConvertReservations(userId, itemId);

        return ResponseEntity.ok().body(reservationResponseDtos); // 7-2 응답 데이터 타입 변경
    }
}
