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




### 3. 구동 예시
>####초기 데이터 베이스 설정
><pre><code>
>CREATE monday(
>    id    SERIAL NOT NULL
>    toon  VARCHAR(30),
>    image VARCHAR(150) DEFAULT 'https://image.chosun.com/sitedata/image/202006/24/2020062400801_0.png',
>    PRIMARY KEY(id)
>);
>
> 다른요일도 동일하게 설정
></code></pre>
>
>#### 데이터 입력
>- 초기 데이터입력은 주석처리된 코드를 풀어서 실행해주어야(cron.go)
><pre><code>
>func SetupCron(location string) (*cron.Cron, error) {
>//ReadWriteAllday()  //처음 세팅할때만 돌림
>loc, err := time.LoadLocation(location)
>if err != nil {
>return nil, err
>}
>c := cron.New(cron.WithLocation(loc))
>_,err=c.AddFunc("0 8 * * *",  DailydataCheck)//매일 8시에 확인
>if err != nil {
>return nil, err
>}
>
>	return c, nil
>}           
>
>           
></code></pre>
>#### CONFIG
> <pre><code>
> {
>"database": {
>"url": "데이터베이스 url : port",
>"user": "데이터베이스 User",
>"password": "데이터베이스  Password",
>"name": "naverwebtoon"
> },
>"naverwebtoon": {
>"url":"네이버 웹툰 메인 홈페이지"
> },
>"slackwebhook" : {
>"url" : "YOUR SLACK WEB HOOK"
> },
>"linetoken" : {
>"token" : "YOUR Notify personal access token"
> }
> }
> </code></pre>

### 5. 구동
                docker build -t <TAG> .
                dockr run -p 8080:8080 <TAG>

### 6. 예시
![EXAMPLE(완결)](./img/IMG_0082.PNG)

### 문의
- cjm2021401@khu.ac.kr