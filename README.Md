# Upload Free with yolov5
- 업로드 한 이미지에 있는 담배, 문신을 자동으로 모자이크 

## 1. How to use
- https://dashboard.ngrok.com/login 로 이동하여 로그인 후 authtoken 획득
-     
  본인의 구글드라이브에 Colaboratory 확장 후 yoloSetting.ipynb 다운로드/업로드
- yoloSetting.ipynb의 코드에 이전에 취득한 authtoken과 원하는 password 입력  
- http://www.mayf.shop 에서 를 원하는 이미지를 업로드한 후
  이전에 취득한 port, hostname, password


### 2. 구동원리
- spring-framwork의 web-server에 이미지 업로드
- 개인 google colab을 ngrok을 통해 외부에서 접근 허용
- JSch를 이용해 web-server와 colab 연결
- 학습된 담배&문신 데이터를 기반으로 담배&문식 탐지/모자이크
- 모자이크 된 이미지를 web-server에 임시 저장하여 사용자에게 전




### 3. 구동 방법
>1. install java 11
>2. git clone https://github.com/cjm2021401/spring-yolov5.git
>3. ./gradlew build
>4. cd build/libs
>5. java -jar spring-yolov5-0.0.1-SNAPSHOT.jar



### 4. 구동예시
1  get auth token
![EXAMPLE(완결)](https://user-images.githubusercontent.com/19799501/132450602-8e49b7ec-724c-44ac-b2ce-d15c68ebe233.png)

2  run yoloSetting.ipynb
   ![EXAMPLE(완결)](https://user-images.githubusercontent.com/19799501/132450711-4b373e51-c5db-40ef-a949-9494972916b6.png)

3 go to http://www.mayf.shop or localhost:8080
![EXAMPLE(완결)](https://user-images.githubusercontent.com/19799501/132450777-9fae7825-c81d-4348-b75c-3297d2c01c2c.png)

4 result
![EXAMPLE(완결)](https://user-images.githubusercontent.com/19799501/132450829-d36a2c33-a804-4783-9c59-3d346f09e8f9.png)

### 5. 사용 기술
- spring-framework
- yolov5
### 문의
- cjm2021401@khu.ac.kr