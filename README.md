# <img src="https://velog.velcdn.com/images/tty5799/post/ca07d3ee-2bdb-4825-b3f3-cb1f3582e818/image.png" align=left width=50 >모험:모두의 경험 소개

<img src="https://velog.velcdn.com/images/tty5799/post/3a6328e7-fdb6-4e46-a0a2-834d1edde501/image.png" width="80%">

### 같이 모험을 떠나볼까요?

- 모험:모두의 경험은 대학생 팀업 서비스 입니다. 

- 전공별 카테고리를 골라 필요한 전공을 구해 같이 새로운 경험을 할 수 있습니다.

🚢 **[mohum 서비스 바로가기](https://www.everymohum.com/)**

🎬 **[서비스 시연 영상](https://www.youtube.com/watch?v=T9QGqgg9Y40)**

🎤 **[발표 영상](https://www.youtube.com/watch?v=7CAoUo4Ma4c&t=3s)**

<br />

## ❓ 기획의도
`"👩 졸업작품을 만들라면 다른 전공이 필요한데 구하기가 힘들어요."`

`"👩‍🦱 지인에게 물어보거나 다른곳 에서 구할때 신뢰를 하기 어려워요!"`

`"👱‍♂️ 모든게 처음이라 협업 경험을 쌓고 싶어요!"`

**모험은 이러한 경험을 해소하기 위해 개발한 서비스입니다.**

<br />

## ✨ 핵심기능

 **`메인 페이지 - 카테고리 별 조회 & 검색 기능`** 
 
 
 <img src="https://velog.velcdn.com/images/tty5799/post/f23ab9a3-b126-40b9-b7ba-20b386f3e665/image.png" width="60%" >
 
 QueryDSL을 이용한 동적 쿼리 작성으로 전공 별/지역 별 조회 기능과 [제목/닉네임/내용] 별 검색 기능을 조합해서 사용할 수 있도록 했습니다.
 
 
 
 **`실시간 1:1 채팅 기능`** 
 
  <img src="https://velog.velcdn.com/images/tty5799/post/6d8c53af-300f-4aeb-bbac-655b90f74cb5/image.png" width="60%" >
 
 [WebSocket / Stomp pub/sub] 을 활용한 실시간 데이터 전송으로 유저간 1: 1 채팅 기능을 구현했습니다.
 
 
 
 **`실시간 알람 기능`**
 
  <img src="https://velog.velcdn.com/images/tty5799/post/5b5ef201-313d-47aa-a17f-50530ba6b6bb/image.png" width="60%" >
 
 사이트를 이용 중인 유저에게 SSE(Server Sent Event)를 이용하여 실시간 알람을 기능 제공하고 있습니다.
 
 
 
  **`이메일 알람 기능`**
 
  <img src="https://velog.velcdn.com/images/tty5799/post/01396e0b-f1e4-496f-b405-b051e95a540c/image.png" width="60%" >
 
 웹 사이트의 한계를 벗어나기 위해 JavaMailSender를 이용하여 사이트에 접속하지 않은 상황에서 발생한 이벤트를 이메일 알림으로 받을 수 있도록 했습니다.
 
 
 
 <br />

## 👥 백엔드 팀원 소개


#### `Backend`
<a href="https://github.com/kwongyumin" target="_blank"><img height="40"  src="https://img.shields.io/static/v1?label=Spring&message=권규민 (부팀장) &color=08CE5D&style=for-the-badge&>"/></a>
<a href="https://github.com/hyemco" target="_blank"><img height="40"  src="https://img.shields.io/static/v1?label=Spring&message=유혜민 &color=08CE5D&style=for-the-badge&>"/></a>

<br />


## 🗓 프로젝트 기간
* 2022년 4월 22일 ~ 2022년 6월 3일   
* 배포 : 2022년 5월 31일

<br />

## 📜 아키텍쳐
<img src="https://velog.velcdn.com/images/tty5799/post/444e86d8-5aa0-40d9-8926-731cf50f4b7b/image.png">   

<br />

## ERD 설계
<img src="https://s3.us-west-2.amazonaws.com/secure.notion-static.com/275e4e34-6c62-4d2c-b33e-80e82c5f305e/drawSQL-export-2022-06-01_23_39_1.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20220602%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20220602T065919Z&X-Amz-Expires=86400&X-Amz-Signature=101f7238d712175908f3003a2ce8d96663fc250c0b322bb1d83b6b49cb106242&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22drawSQL-export-2022-06-01_23_39%25201.png%22&x-id=GetObject">

<br />

## 🎈 LINK  
📔  **[노션 링크]( https://maroon-borogovia-266.notion.site/55e2db12612549328acae676de9c9a60)**

<br />

**Backend Tech Stack**  
<img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white"> 
<img src="https://img.shields.io/badge/Springboot-6DB33F?style=for-the-badge&logo=Springboot&logoColor=white">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=AmazonAWS&logoColor=white"> 
<img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=Amazon S3&logoColor=white">
<img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=GitHub Actions&logoColor=white"> 
<img src="https://img.shields.io/badge/codedeploy-6DB33F?style=for-the-badge&logo=codedeploy&logoColor=white">
<img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=JUnit5&logoColor=white">
<img src="https://img.shields.io/badge/Apache JMeter-D22128?style=for-the-badge&logo=Apache JMeter&logoColor=white">
<img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=NGINX&logoColor=white">
