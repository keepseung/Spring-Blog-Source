## 블로그 글
1. [Springboot에 AWS S3 연동 (이미지, 동영상 업로드)](https://develop-writing.tistory.com/128)
2. [[AWS] Spring Boot 와 멀티파트 업로드를 사용해 S3에 대용량 파일 업로드하기](https://develop-writing.tistory.com/129)

### 주의사항
resource/aws.yml 파일을 추가해서 각자의 AWS 엑세스 키, 시크릿 키를 추가해야 합니다.    
형식은 다음과 같습니다.
~~~yml
cloud:
  aws:
    credentials:
      access-key: {엑세스 키}
      secret-key: {시크릿 키}
~~~
