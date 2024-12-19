package com.example.demo.service;

import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.*;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalLogService rentalLogService;
    private final JPAQueryFactory queryFactory; // 추가된 부분, querydsl 사용
    private final QReservation reservation = QReservation.reservation; // 추가된 부분, q 클래스 만들기


    public ReservationService(ReservationRepository reservationRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              RentalLogService rentalLogService, EntityManager entityManager) {
        this.reservationRepository = reservationRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.rentalLogService = rentalLogService;
        this.queryFactory = new JPAQueryFactory(entityManager); // querydsl하려고 생성자 주입
    }

    // TODO: 1. 트랜잭션 이해
    @Transactional // 추가한 부분, transactional 어노테이션 추가
    public Reservation createReservation(Long itemId, Long userId, LocalDateTime startAt, LocalDateTime endAt) {
        // 쉽게 데이터를 생성하려면 아래 유효성검사 주석 처리
//        List<Reservation> haveReservations = reservationRepository.findConflictingReservations(itemId, startAt, endAt);
//        if(!haveReservations.isEmpty()) {
//            throw new ReservationConflictException("해당 물건은 이미 그 시간에 예약이 있습니다.");
//        }

        Item item = itemRepository.findByIdOrElseThrow(itemId);
        User user = userRepository.findByIdOrElseThrow(userId);
        Reservation reservation = new Reservation(item, user, Status.PENDING, startAt, endAt);
        Reservation savedReservation = reservationRepository.save(reservation);

        RentalLog rentalLog = new RentalLog(savedReservation, "로그 메세지", "CREATE");
        rentalLogService.save(rentalLog);

        return savedReservation;
    }

    // TODO: 3. N+1 문제
    public List<ReservationResponseDto> getReservations() {
        List<Reservation> reservations = reservationRepository.findAllReservationWithUserAndItem(); // 바뀐부분, join fetch 사용한 걸로 변경

        // N+1 문제 해결
        return reservations.stream().map(reservation -> new ReservationResponseDto(
                reservation.getId(),
                reservation.getUser().getNickname(), // 변경, 패치조인으로 받아서 리턴
                reservation.getItem().getName(), // 변경, 패치조인으로 받아서 리턴
                reservation.getStartAt(),
                reservation.getEndAt()
        )).toList();
    }

    // TODO: 5. QueryDSL 검색 개선
    public List<ReservationResponseDto> searchAndConvertReservations(Long userId, Long itemId) {

        List<Reservation> reservations = searchReservations(userId, itemId);

        return convertToDto(reservations);
    }

    // TODO: 5. QueryDSL 검색 개선 <- 이거 여기 있어야 할듯
    public List<Reservation> searchReservations(Long userId, Long itemId) {

        // 기존 코드
//        if (userId != null && itemId != null) {
//            return reservationRepository.findByUserIdAndItemId(userId, itemId);
//        } else if (userId != null) {
//            return reservationRepository.findByUserId(userId);
//        } else if (itemId != null) {
//            return reservationRepository.findByItemId(itemId);
//        } else {
//            return reservationRepository.findAll();
//        }

        // 변경된 코드, 동적쿼리 적용
        return queryFactory
                .selectFrom(reservation)
                .leftJoin(reservation.user).fetchJoin()
                .leftJoin(reservation.item).fetchJoin()
                .where(
                        userId != null ? reservation.user.id.eq(userId) : null,
                        itemId != null ? reservation.item.id.eq(itemId) : null
                )
                .fetch();
    }

    private List<ReservationResponseDto> convertToDto(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> new ReservationResponseDto(
                        reservation.getId(),
                        reservation.getUser().getNickname(),
                        reservation.getItem().getName(),
                        reservation.getStartAt(),
                        reservation.getEndAt()
                ))
                .toList();
    }

    // TODO: 7. 리팩토링
    @Transactional
    public Reservation updateReservationStatus(Long reservationId, String status) {
        Reservation reservation = reservationRepository.findByIdOrThrow(reservationId);

        // 기존코드
//        if ("APPROVED".equals(status)) {
//            if (!"PENDING".equals(reservation.getStatus())) {
//                throw new IllegalArgumentException("PENDING 상태만 APPROVED로 변경 가능합니다.");
//            }
//            reservation.updateStatus("APPROVED");
//        } else if ("CANCELED".equals(status)) {
//            if ("EXPIRED".equals(reservation.getStatus())) {
//                throw new IllegalArgumentException("EXPIRED 상태인 예약은 취소할 수 없습니다.");
//            }
//            reservation.updateStatus("CANCELED");
//        } else if ("EXPIRED".equals(status)) {
//            if (!"PENDING".equals(reservation.getStatus())) {
//                throw new IllegalArgumentException("PENDING 상태만 EXPIRED로 변경 가능합니다.");
//            }
//            reservation.updateStatus("EXPIRED");
//        } else {
//            throw new IllegalArgumentException("올바르지 않은 상태: " + status);
//        }

        // 리팩토링코드
        switch (Status.of(status)) {
            case Status.APPROVED:
                if (!Status.PENDING.equals(reservation.getStatus())) {
                    throw new IllegalArgumentException("PENDING 상태만 APPROVED로 변경 가능합니다.");
                }
                break;
            case Status.CANCELED:
                if (Status.EXPIRED.equals(reservation.getStatus())) {
                    throw new IllegalArgumentException("EXPIRED 상태인 예약은 취소할 수 없습니다.");
                }
                break;
            case Status.EXPIRED:
                if (!Status.PENDING.equals(reservation.getStatus())) {
                    throw new IllegalArgumentException("PENDING 상태만 EXPIRED로 변경 가능합니다.");
                }
                break;
            default:
                throw new IllegalArgumentException("올바르지 않은 상태: " + status);
        }

        return reservationRepository.save(reservation);

    }
}
