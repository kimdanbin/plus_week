# 플러스 주차 과제

1. Transactional에 대한 이해
 - @Transactional 추가

2. 인가에 대한 이해
 - AdminRoleInterceptor를 만들어서 WebConfig의 InterceptorRegistry에 추가

3. N+1에 대한 이해
 - 하나의 쿼리만으로도 select할 수 있는 데 n개를 더 추가로 가져오는 현상
 - join fetch를 이용하여 해결

4. DB 접근 최소화
 - user의 상태를 한꺼번에 변경한다음 saveAll로 저장

5. 동적 쿼리에 대한 이해
 - 쿼리dsl을 사용해 user와 item이 null인지 아닌지 구분해서 동적으로 쿼리 생성

6. 필요한 부분만 갱신하기
 - Item 엔티티에 @DynamicInsert 추가

7. 리팩토링
 - 필요하지 않은 else 구문을 걷어냅니다. -> switch 문으로 변경
 - 컨트롤러 응답 데이터 타입을 적절하게 변경합니다. -> ResponseEntity으로 변경
 - 재사용 비중이 높은 findById 함수들을 default 메소드로 선언합니다. -> findByIdOrElseThrow로 변경
 - 상태 값을 명확하게 enum으로 관리합니다. -> reservation의 status를 enum으로 변경

8. 테스트 코드
 - 비밀번호가 암호화가 되는지
 - 비밀번호 입력 받을 때 암호화된 비밀번호와 맞는지
 - item의 status가 null이어도 예외가 발생하지 않는지