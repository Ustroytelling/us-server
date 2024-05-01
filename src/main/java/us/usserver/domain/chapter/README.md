# 📜 Chapter Domain을 개발하면서 했던 고민

---
## 점수 최신화

사용자(Author)들은 각 회차(Chapter)에 점수(Score)를 줄 수 있다. 점수들의 평균이 회차의 score가 되고, 모든 회차들의 평균 score가 Novel Entity의 score가 된다.

그렇다면 소설의 score는 어느 주기로 최신화가 되어야 할까?

### 💡 고려 사항

1. 소설의 score는 실시간성이 중요한 데이터가 아니다.
2. Chapter의 score가 필요하게 끔 API 스펙이 변경될 가능성이 높다.
3. Chapter 도메인에서 Novel Entity의 필드 값을 수정하는 로직을 작성하면 도메인 간 강한 결합이 생긴다.

### 💡 해결 방안
1. chapter에 score 컬럼을 생성한다. 
2. 
