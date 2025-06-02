# WannaV : 영수증 기반 비건 식당 예약 및 리뷰, 쇼핑몰 통합 플랫폼

<p align="center">
  <img src="https://github.com/user-attachments/assets/410d78b9-1221-4577-a1d1-c8e50a8e1817" width="600" />
</p>

## 개발 팀(뷰리풀 팀)
<table>
  <thead>
    <tr align=center >
      <td><b>팀장</b></td>
      <td><b>nam3280</b></td>
      <td><b>팀원1</b></td>
      <td><b>팀원2</b></td>
    </tr>
  </thead>
  <tbody>
    <tr valign=top>
      <td>
        <ul>
          <li>상품 관리</li>
          <li>결제·챗봇</li>
          <li>대시보드·감정분석</li>
        </ul>
      </td>
      <td>
        <ul>
          <li>회원 관리</li>
          <li>식당 예약</li>
          <li>이벤트·쿠폰 관리</li>
        </ul>
      </td>
      <td>
        <ul>
          <li>OCR 인증·리뷰 관리</li>
          <li>마이페이지</li>
          <li>서버 관리</li>
        </ul>
      </td>
      <td>
        <ul>
          <li>메인 페이지 관리</li>
          <li>식당 관리</li>        
        </ul>
      </td>
    </tr>
  </tbody>
</table>

## 개발 환경
- **Language** : Java JDK 17, JavaScript
- **IDE**: IntelliJ IDEA Ultimate
- **Framework**: Spring Boot 3.1.1, Spring Security 6, JPA
- **Database**: MySQL 9.0.1,  Spring Data Redis
- **Library**: Thymeleaf, Bootstrap, JSON Web Tokens, Axios
- **Tools**: GitHub, Notion, Slack
- **Service :** NAVER CLOUD PLATFORM
- **Server** : NginX, Tomcat
- **API :** Kakao 주소, Kakao 로그인, TossPayments, Clova

## 프로젝트 기획

### 1. 배경

#### (1) 건강 적신호
해가 지날수록 암, 심장병, 당뇨병과 같은 만성질환이 급증하면서 건강 관리의 중요성이 커지고 있다.  
잘못된 식습관, 과도한 육류 소비 등이 몸에 해로운 영향을 끼치고 있다.  
이러한 만성질환은 단순히 한 개인의 문제가 아닌, 사회 전체의 의료 부담으로 이어지고 있으며, 많은 사람들이 자신의 건강 상태를 경고 받고 있는 실정이다.

<img alt="image" src="https://github.com/user-attachments/assets/65d3820b-6d1d-45ac-b398-9c7fef19e2cc" />

 #### (2) 비건 식당의 상승세
 국제채식인연맹(IVU)에 따르면 전 세계 채식 인구는 1억 8,000여 명에 다달았으며 국내 채식 인구는 100~150만 명으로 추정하고 있다. 또한, 한국리서치에 따르면 10명 중 7명은 비건을 시작하는 이유로 ‘건강’을 꼽았으며, 음식뿐만 아니라 비건 제품 구매 경험도 높아지고 있는 추세이다.

 <img width="436" alt="image" src="https://github.com/user-attachments/assets/33e89fd7-3bcf-4ebb-b453-d3f2c0db8eb4" />

 #### 건강을 위해 비건을 시작하는 사람들이 늘어남에 따라, 비건 플랫폼을 제작하기로 결정.

### 2. 설문조사

<img width="706" alt="image" src="https://github.com/user-attachments/assets/402b4c58-84c4-42bf-92b0-288fdb568cb1" />

### 3. 벤치마킹
<table>
  <thead>
    <tr>
      <th>여부</th>
      <th>구분</th>
      <th>내용 설명</th>
      <th>C사</th>
      <th>T사</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td></td>
      <td>영업 사원</td>
      <td>기업 고객만 전담하는 영업 사원의 방문 요청 가능</td>
      <td>○</td>
      <td>○</td>
    </tr>
    <tr>
      <td></td>
      <td>리뷰 서비스</td>
      <td>영수증 기반 리뷰 작성 기능 제공</td>
      <td>X</td>
      <td>X</td>
    </tr>
    <tr>
      <td>차별화 서비스</td>
      <td>리워드</td>
      <td>리뷰 작성 시 리워드 제공</td>
      <td>X</td>
      <td>○</td>
    </tr>
    <tr>
      <td></td>
      <td>회원 등급</td>
      <td>리뷰 수 및 베스트 리뷰에 따른 회원 등급 부여</td>
      <td>X</td>
      <td>X</td>
    </tr>
    <tr>
      <td></td>
      <td>추천 서비스</td>
      <td>고객 특성에 맞춘 상품 추천 기능 제공</td>
      <td>X</td>
      <td>○</td>
    </tr>
  </tbody>
</table>

### 4. 주요 기능

#### 설문조사와 벤치마킹을 통하여 다음 기능을 추가하기로 결정

<ul>
  <li>식당 추천 및 검색 조건 필터링 기능을 활용하여 사용자의 다양한 기호 반영</li>
  <li>식당 예약 후 영수증 기반의 리뷰로 고객의 신뢰성 확보</li>
  <li>리뷰 작성에 따른 리워드 시스템으로 리뷰 작성 유도 및 고객 유치</li>
  <li>이벤트 및 회원등급을 활용한 고객 관리</li>
  <li>비건 상품 판매를 통해 매출 발생</li>
</ul>

### 5. 정책

#### 매출 통계
<img width="1000" alt="image" src="https://github.com/user-attachments/assets/198b4dc8-d10e-42b1-957b-558624f73216" />

#### 리워드
<img width="1000" alt="image" src="https://github.com/user-attachments/assets/3da1b74a-90a7-448d-8605-63f688987831" />

#### 예약
<img width="1044" alt="image" src="https://github.com/user-attachments/assets/1baafa1f-c1e4-4876-9a21-bb65f825161e" />

## 6. 프로젝트 설계

<details>
<summary><h3>1. ERD</h3></summary>

![image](https://github.com/user-attachments/assets/41cdda05-871b-411a-8aca-84ac03b9d722)

</details>

<details>
<summary><h3>2. Figma</h3></summary>

![image](https://github.com/user-attachments/assets/5b2778fa-a913-4629-9360-c715500c75d5)


![image](https://github.com/user-attachments/assets/1aed3406-8f94-4760-81c5-defd6af3756f)


![image](https://github.com/user-attachments/assets/f4ded521-05b5-44e1-8f0d-f42f3fc1fd4f)

</details>




<details>
  <summary><h3>3. convention</h2></summary>

  ## (1) Naming
  <table border="1">
  <thead>
    <tr>
      <th>구분</th>
      <th>Convention</th>
      <th>Example</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Table Naming</td>
      <td>PascalCase</td>
      <td><code>User</code>, <code>OrderHistory</code></td>
    </tr>
    <tr>
      <td>Column Naming</td>
      <td>snake_case</td>
      <td><code>user_id</code>, <code>created_at</code></td>
    </tr>
    <tr>
      <td>Class Naming</td>
      <td>PascalCase</td>
      <td><code>UserController</code>, <code>UserServiceImpl</code></td>
    </tr>
    <tr>
      <td>Method Naming</td>
      <td>camelCase</td>
      <td><code>createUser()</code>, <code>findById()</code></td>
    </tr>
    <tr>
      <td>Field Naming</td>
      <td>camelCase</td>
      <td><code>userId</code>, <code>orderDate</code></td>
    </tr>
  </tbody>
</table>



  ## (2) Github
  
  ### 🎉 Init (Initialization)
  - **설명**: 프로젝트의 초기 설정이나 기본적인 구조 설계  
  - **예시**: `🎉 Init: set up initial project structure`

  ### ✨ Feat (Feature)
  - **설명**: 새로운 기능을 추가하는 커밋  
  - **예시**: `✨ Feat: add user login functionality`

  ### 🐛 Fix (Bug Fix)
  - **설명**: 버그를 수정하는 커밋  
  - **예시**: `🐛 Fix: correct calculation error in tax module`

  ### 🎨 Style
  - **설명**: 기능적 변경이 없으며, 코드의 포맷이나 스타일, 주석 등을 수정  
  - **예시**: `🎨 Style: format code according to ESLint rules`

  ### ♻️ Refactor
  - **설명**: 코드의 구조를 변경하지만 기능은 변경하지 않는 커밋  
  - **예시**: `♻️ Refactor: reorganize project structure`

  ### ✅ Test
  - **설명**: 테스트 관련 변경 (테스트 추가, 수정, 제거 등)  
  - **예시**: `✅ Test: add unit tests for new user service`

  ### 📝 Docs (Documentation)
  - **설명**: 문서화 관련 변경  
  - **예시**: `📝 Docs: update README with setup instructions`

  ### 🔒 Security
  - **설명**: 보안 관련 수정  
  - **예시**: `🔒 Security: fix XSS vulnerability`

  ### 🚀 Chore
  - **설명**: 기타 잡다한 작업이나 설정 변경  
  - **예시**: `🚀 Chore: upgrade npm packages`

  ## (3) branch
  **Branch type**

  - **`main`** : 운영환경에 배포하여 **제품으로 출시될 수 있는 브랜치**
  - **`develop`** : **다음 출시 버전을 개발하는 브랜치** (main 브랜치로 merge)
  - **`feature`: 기능을 개발하는 브랜치** (develop 브랜치로 merge)
  
  **Branch Naming**
  
  ### ❗️🚨🚨 develop 브랜치 내에서 기능(feature) 브랜치 생성 🚨🚨❗️
  
  - **Convention**: **`feature/{feature-name}`**
  - **Example**: **`feature/board-create`**

</details>

<details>
  <summary><h3>4. System Architecture</h2></summary>
  
![image](https://github.com/user-attachments/assets/37da60c0-5767-4b83-80ef-205850bc64ae)

</details>

## 구현
<table style="width: 100%; table-layout: fixed;">
  <tr>
    <td style="width: 33%; text-align:center; padding: 10px;">
      <img src="https://github.com/user-attachments/assets/9e981613-99f2-4247-ac9f-6e5bfca74457"
           style="width: 100%; height: 400px; object-fit: cover;" />
      <p>(1) 소셜 로그인</p>
    </td>
    <td style="width: 33%; text-align:center; padding: 10px;">
      <img src="https://github.com/user-attachments/assets/eac7afaa-3fdb-4660-a2fb-9ebf029aba0b"
           style="width: 100%; height: 400px; object-fit: cover;" />
      <p>(2) 예약</p>
    </td>
    <td style="width: 33%; text-align:center; padding: 10px;">
      <img src="https://github.com/user-attachments/assets/24c44f85-d8d4-4efc-86a7-368e7f1afb52"
           style="width: 100%; height: 400px; object-fit: cover;" />
      <p>(3) 결제</p>
    </td>
    <td style="width: 33%; text-align:center; padding: 10px;">
      <img src="https://github.com/user-attachments/assets/b560f442-7004-4320-be67-e001469e3c12"
           style="width: 100%; height: 400px; object-fit: cover;" />
      <p>(4) 이벤트</p>
    </td>
  </tr>
</table>
