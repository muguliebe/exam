# Spec
- JDK: 1.8
- Framework: Spring Boot
- DB: Postgres
- LANG: Kotlin

# 전체 흐름도
![all](/docs/images/flow.png)

# 개발환경
## 설치방법
### SDKMan 설치
1. 설치 파일 다운로드
  ```bash
  curl -s "https://get.sdkman.io" | bash
  ```

2. 설치
  ```bash
  source "$HOME/.sdkman/bin/sdkman-init.sh"
  sdk version
  ```

### JDK 1.8 설치
  ```bash
  sdk install java 8.0.265-amzn
  ```

## Swagger API 테스트
| /    | URL                                              |
|:-----|:-------------------------------------------------|
| 로컬  | http://localhost:5000/swagger-ui.html            |

# How To
- DB Port
    - 62501
    - 변경 필요 시 EmbeddedDataSource 소스에서 변경 합니다.
    - setPort 를 제거 시 랜덤 포트 이며, 부팅 시 로그에 표기가 됩니다.

# 소스 구조
```
src                       ---- 소스 Root
 └ kotlin                      └ Kotlin Source Root
 | └ com.exam                  |  └ Exam Main Package
 |   └ ap                      |     └- 대내 외부 AP 영역 ( 고객, 수신, 인증 .. )
 |   └ bank                    |     └- 뱅킹 영역
 |   |  └ controller           |     |   └--- HTTP 요청 진입점
 |   |  └ dto                  |     |   └--- Data Transfer Object
 |   |  └ entity               |     |   └--- 테이블과 매칭되는 엔티티 영역
 |   |  └ repo                 |     |   └--- JPA or MyBatis Repository
 |   |  └ service              |     |   └--- 비즈니스 로직
 |   |  └ task                 |     |   └--- 주기적인 작업
 |   └ fwk                     |     └- 프레임워크 영역
 |      └ core                 |     |   └--- 프레임웤 기본
 |      |  └ base              |     |   |    └--- controller, service 등에서 상속받아야 하는 상위 클래스 모임
 |      |  └ component         |     |   |
 |      |  └ error             |     |   |    └--- 에러 클래스 집합
 |      |  └ mybatis           |     |   |    └--- mybatis 위한 컨버트 클래스들
 |      └ custom               |     |   └--- 프로젝트별 특성 프레임웤 영역
 |         └ config            |     |   └--- 스프링 부트 설정 및 DB 설정
 |         └ filter            |     |   └--- AOP 영역
 |         └ pojo              |     |   |
 |         └ service           |     |   |
 |         └ util              |     |   └--- 유틸리티
 └ resources                   └  리소스 ( 스프링 부트 설정 + Web + MyBatis Query )
  └ db                           └  flyway 쿼리 영역 ( 현재 개발중 미사용 )
  └ mybatis                      └  mybatis 쿼리 및 설정
test                      ---- 테스트 Root
   └ http                      └ http 파일로 작성된 테스트
   └ kotlin                    └ 코틀린으로 작성된 테스트 소스
   | └ com.exam                  |  └ Exam Main Package
   |   |  └ base                 |     |   모든 테스트 소스에서 상속 받아야 하는 클래스
   |   |  └ controller           |     |   콘트롤러 테스트
```

