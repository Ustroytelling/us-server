# 📜 Novel Domain을 개발하면서 했던 고민

---
## 메인 페이지 조회

메인 페이지로 이동할 때 회원과 비회원이 볼 수 있는 영역이 다르다.

이 기능을 어떻게 구현하면 좋을까?

### 💡 고려사항
1. 소설 메인 조회를 JWT Filter의 WhiteList에 추가하면 회원들의 id를 조회할 수 없어서 "내가 읽은 소설" 을 가져오지 못한다.
2. WhiteList에 추가하지 않으면 비회원은 accessToken과 refreshToken을 갖고 있지 않기 때문에 Filter를 통과하지 못한다.

### 💡 해결방안
1. 회원과 비회원의 API를 분리한다.
```java
public class NovelController {
  private final NovelService novelService;
  
  @GetMapping("/guest/main")
  public ApiCsResponse<MainPageRes> getNovelMainGuest() {
    MainPageRes homeNovelList = novelService.getMainPage(null);
    return ApiCsResponse.success(homeNovelList);
  }
  
  @GetMapping("/main")
  public ApiCsResponse<MainPageRes> getNovelMainUser(
          @AuthenticationPrincipal Long memberId
  ) {
    MainPageRes homeNovelList = novelService.getMainPage(memberId);
    return ApiCsResponse.success(homeNovelList);
  }
}
```
```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final String[] whitelist = {
            // ...
            "/novel/guest/**"
    };
}
```



---
## 메인 소설들 조회

메인 페이지로 이동할 때 소설들을 가져온다. 이때 실시간 업데이트, 신작, 인기 소설들을 조회한다.

더보기를 클릭하면 실시간 업데이트, 신작, 인기 소설을 더 조회한다.

이 기능을 어떻게 구현하면 좋을까?

### 💡 고려사항
1. 세 기능 전부 유사하거나 동일한 로직을 사용한다.
2. 메인 페이지 API 스펙은 언제든 변경될 수 있다.

### 💡 해결방안
1. 전략(Strategy) 패턴을 사용한다.
   - interface(NovelRepository)로 조회 로직 추상화
   - switch-case 문으로 조회 전략 설정
     ```java
      public class NovelServiceImpl implements NovelService {
        private final NovelRepository novelRepository;
     
        @Override
        @Transactional
        public MoreNovelRes getMoreNovels(Long memberId, MoreNovelReq moreNovelReq) {
          PageRequest pageRequest = switch (moreNovelReq.mainNovelType()) {
              case NEW -> getPageRequest(moreNovelReq.nextPage(), DEFAULT_PAGE_SIZE, Sort.Direction.DESC, SortColumn.createdAt);
              case UPDATE -> getPageRequest(moreNovelReq.nextPage(), DEFAULT_PAGE_SIZE, Sort.Direction.DESC, SortColumn.recentlyUpdated);
              case POPULAR -> getPageRequest(moreNovelReq.nextPage(), DEFAULT_PAGE_SIZE, Sort.Direction.DESC, SortColumn.hit);
          };

          Slice<Novel> novelSlice = novelRepository.findSliceBy(pageRequest);
          List<NovelInfo> novelInfos = novelSlice.map(NovelInfo::mapNovelToNovelInfo).toList();
          return new MoreNovelRes(novelInfos, novelSlice.getNumber() + 1, novelSlice.hasNext());
        }
      }
      ```
}
     