# Spring Cloud 연습 프로젝트
## MSA를 사용하기 위한 Spring Cloud 기술

### 1. Eureka Server
- Discovery Server
### 2. Eureka Client
- Discovery Client
### 3. Spring Cloud Gateway
- 라우팅(서비스 검색 및 통합)
- 캐싱
- 필터와 인터셉터로 인증, 권한 부여
- 부하 분산 LB
- 로깅
- IP 허용 목록에 추가
### 4. Spring Cloud Config
- 비밀 config 값을 외부에 저장
- 해당 값을 사용 중인 서비스들에게 값이 변경되면 한번에 적용하기 위해 사용

### feign client

## 연습 코드 repo
[Eureka 서버](https://github.com/emsthf/spring-cloud-eureka-sample)
- Eureka 서버는 서비스들의 정보를 가지고 있음
- 서비스들은 Eureka 서버에 자신의 정보를 등록
- 서비스들은 Eureka 서버에 등록된 다른 서비스들의 정보를 가져올 수 있음
- LB를 위해 랜덤 포트를 부여받은 서비스의 포트를 외부에 노출하지 않고 찾아갈 수 있게 해줌

[Gateway 서버](https://github.com/emsthf/spring-cloud-gateway-sample)
- MSA로 구성된 여러 서비스에서 Gateway 서버를 통해 접근하도록 설정
- Filter를 통해 인증, 권한 부여 구현
- Load Balancing 구현

[Config 서버](https://github.com/emsthf/spring-cloud-config)
- MSA로 구성된 여러 서비스에서 Config 서버를 통해 설정 값을 가져오도록 설정
- 해당 config를 사용하는 모든 서비스에 한번에 적용시킬 수 있다.
- 노출되면 안되는 config 값을 외부에 저장할 수 있다.