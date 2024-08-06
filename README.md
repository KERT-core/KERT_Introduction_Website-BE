# KERT_Introduction_Website-BE

## 브랜치 설명

### main

- 배포되기 전 최종 브랜치입니다. 긴급한 사유없이 함부로 main 브랜치를 다루어선 안됩니다..!
- 웬만해선 pull request로 merge합니다. (dev -> main)

### dev

- main에 올리기전 이것저것 개발해둔 걸 merge시키는 곳입니다. 여기가 날아가도 main으로 땜빵할 수 있는 안전한 브랜치입니다.
- 모든 push/pull는 dev 또는 기타 브랜치로 해주세요.

```shell
git push origin dev
```

```shell
git pull origin dev
```

## Push 규칙

- 브랜치를 합치는 과정은 `main <- dev <- 기타 브랜치`로 진행될 예정입니다.
- 기타 브랜치는 `feat/기능이름` 또는 `hotfix/이름`등 으로 적어주시면 됩니다.

## 개발 시작

### 레포지토리를 처음 받을 때

1. 레포지토리 클론

```shell
git clone https://github.com/KERT-core/KERT_Introduction_Website-FE
```

2. 클론한 폴더로 이동

```shell
cd KERT_Introduction_Website-FE
```

3. dev 브랜치로 전환

```shell
git switch dev
```

4. 라이브러리/프레임워크 설치

```shell
npm install
```

5. 개발 서버 실행

```shell
npm run dev -- --open
```

### 최종 수정을 끝내고 레포지토리에 올릴 때

1. 변경 사항 추가

```shell
git add *
```

또는

```shell
git add 파일명 또는 폴더명
```

2. 커밋 메시지 추가

```shell
git commit -m "수정사항 같은거 적기"
```

3. 브랜치에 최종 업로드

```shell
git push origin dev
```

### 최종 수정을 끝내고 레포지토리에 올리기 전에 다른 사람이 수정한게 있다면

1. 다른 사람의 변경 내용 불러오기

```shell
git pull origin dev
```

2. 변경 사항 추가

```shell
git add *
```

또는

```shell
git add 파일명 또는 폴더명
```

3. 커밋 메시지 추가

```shell
git commit -m "수정사항 같은거 적기"
```

4. 브랜치에 최종 업로드

```shell
git push origin dev
```

## 사용하는 프레임워크/라이브러리

### SpringBoot + MySQL
