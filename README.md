# chatting 스터디 프로젝트

> 사용자들의 채팅서비스를 지원하는 프로그램을 개발한다.



## 요구사항

### 인증

* 이름, 아이디, 비밀번호 정보를 입력받아 회원가입 가능
* JWT을 사용한 로그인 인증방식을 구현


#### 사용자 목록

* 사용자목록은 전체 사용자목록 노출
* 1:1 채팅방 또는 그룹채팅방을 생성 가능


#### 채팅방 목록

* 현재 참여중인 채팅방 목록 실시간 조회
* 목록에서 채팅방 나가기 기능 제공.


#### 채팅

* 메시제에 대한 읽음상태를 표시.
* 이전 메시지 저장.
* TEXT와 이미지 파일형식 전송 가능.
* 채팅방을 나가면 퇴장 알림 노출.

---

.jar파일 생성

```
 .\gradlew.bat build
```
Dockerfile build

```
docker build --tag chatting .

docker run -p 8085:8085 chatting

```

compose 파일

```
docker-compose up --build
```




### - presentation

- [이효진0206](https://github.com/lhg0314/Chatting-BE/education/chatting-server/-/blob/main/presentation/hyojin0206.md)
- [오유민0206](http://git.openobject.net:8880/education/chatting-server/-/blob/main/presentation/yumin0206.md)
- [서상균0206](http://git.openobject.net:8880/education/chatting-server/-/blob/main/presentation/sangkyun0206.md)
