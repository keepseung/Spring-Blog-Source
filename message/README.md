# Message - 메세지, 국제화 기능

## 메세지
회사명, 상품명, 가격, 수량등 여러 웹 페이지에 공통적으로 전달해야 하는 텍스트들이 있습니다.
다양한 메시지를 한 곳에서 관리하도록 하는 기능을 메시지 기능이라 합니다.

보통 resource 디렉토리 안에 messages.properteis 파일에서 관리합니다.
```properties
company.name=킵고잉
item=상품
item.id=상품 ID
```

HTML들은 다음과 같이 해당 데이터를 key 값으로 불러서 사용합니다. (예시는 타임리프 사용함)
```HTML
<label for="itemName" th:text="#{company.name}">예시 회사명</label>
```

### 스프링 부트 메시지 소스 설정
스프링 부트를 사용하면 다음과 같이 메시지 소스를 설정할 수 있습니다.
```properties
spring.messages.basename=messages,config.i18n.messages
```
스프링 부트 메시지 소스 기본 값
```properties
spring.messages.basename=messages
```

## 국제화 
메시지에서 설명한 메시지 파일(messages.properteis)을 각 나라별로 별도로 관리하면 서비스를 국제화 할 수 있습니다.

예를 들어서 다음과 같이 2개의 파일을 만들어서 메세지를 분류할 수 있습니다.

messages.propertis
```properties
hello=안녕
hello.name=안녕 {0}
```
messages_en.propertis
```properties
hello=hello
hello.name=hello {0}
```

## 스프링 메시지 소스 사용하기
MessageSource 인터페이스
```java
public interface MessageSource {
    String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale);
    String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException;
}
```
MessgaeSource 인터페이스를 보면 코드를 포함한 일부 파라미터로 메시지를 읽어오는 기능을 제공합니다.   
스프링이 제공하는 메시지 소스를 어떻게 사용하는지 테스트 코드를 통해서 확인할 수 있습니다.   

-- MessageSourceTest 메세지 가져오기   
```java
@SpringBootTest
public class MessageSourceTest {

    @Autowired
    MessageSource ms;

    @Test
    void helloMessage() {
        String result = ms.getMessage("hello", null, null);
        assertThat(result).isEqualTo("안녕");
    }
```
- 메시지 코드로 hello를 입력하고 나머지 값은 null을 입력했습니다.  
- locale 정보가 없으면 basename에서 설정한 기본 이름 메시지 파일을 조회합니다. basename으로 messages를 지정했으므로 messages.properties 파일에서 데이터 조회합니다.    

--- MessageSourceTest 메시지가 없는 경우, 기본 메시지
```java
@Test
void notFoundMessageCode() {
    assertThatThrownBy(() -> ms.getMessage("no_code", null, null))
    .isInstanceOf(NoSuchMessageException.class);
}
@Test
void notFoundMessageCodeDefaultMessage() {
    String result = ms.getMessage("no_code", null, "기본 메시지", null);
    assertThat(result).isEqualTo("기본 메시지");
}
```
- 메시지가 없는 경우에는 NoSuchMessageException이 발생합니다. 
- 메시지가 없어도 기본 메시지(defaultMessage)를 사용하면 기본 메시지가 반환됩니다.

--- MessageSourceTest 매개변수 사용
```java
@Test
void argumentMessage() {
    String result = ms.getMessage("hello.name", new Object[]{"Spring"}, null);
    assertThat(result).isEqualTo("안녕 Spring");
}
```
- 메시지의 {0} 부분은 매개변수를 전달해서 치환할 수 있습니다.

### 국제화 파일 선택
locale 정보를 기반으로 국제화 파일을 선택합니다.
- Locale이 en_US 의 경우 messages_en_US -> messages_en -> messages 순서로 찾습니다.
- Locale 에 맞추어 구체적인 것이 있으면 구체적인 것을 찾고, 없으면 디폴트를 찾습니다.

--- MessageSourceTest 국제화 파일 선택
```java
@Test
void defaultLang() {
    assertThat(ms.getMessage("hello", null, null)).isEqualTo("안녕");
    assertThat(ms.getMessage("hello", null, Locale.KOREA)).isEqualTo("안녕");
}

@Test
void enLang() {
    assertThat(ms.getMessage("hello", null, Locale.ENGLISH)).isEqualTo("hello");
}
```
- ms.getMessage("hello", null, null) : locale 정보가 없으므로 messages를 사용
- ms.getMessage("hello", null, Locale.KOREA) : locale 정보가 있지만, message_ko가 없으므로 messages를 사용
- ms.getMessage("hello", null, Locale.ENGLISH) : locale 정보가 Locale.ENGLISH 이므로 messages_en을 찾아서 사용


## 웹으로 국제화 확인하기
크롬 브라우저 설정 언어를 검색하고, 우선 순위를 영어로 변경하고 테스트해보면 됩니다.    
웹 브라우저의 언어 설정 값을 변경하면 요청시 Accept-Language 의 값이 변경됩니다.    
Accept-Language 는 클라이언트가 서버에 기대하는 언어 정보를 담아서 요청하는 HTTP 요청 헤더입니다.    

## 스프링의 국제화 메시지 선택
앞서 MessageSource 테스트에서 보았듯이 메시지 기능은 Locale 정보를 알아야 언어를 선택할 수 있습니다.    
결국 스프링도 Locale 정보를 알아야 언어를 선택할 수 있는데, 스프링은 언어 선택시 기본으로 Accept-Language 헤더의 값을 사용합니다. 

## LocaleResolver
스프링은 Locale 선택 방식을 변경할 수 있도록 LocaleResolver 라는 인터페이스를 제공하는데, 스프링 부트는 기본으로 Accept-Language를 활용하는 AcceptHeaderLocaleResolver를 사용합니다.
```java
interface LocaleResolver {
    Locale resolveLocale(HttpServletRequest request);
    void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale);
}
```
## LocaleResolver 변경
만약 Locale 선택 방식을 변경하려면 LocaleResolver의 구현체를 변경해서 쿠키나 세션 기반의 Locale 선택 기능을 사용할 수 있습니다.
예를 들어서 고객이 직접 Locale 을 선택하도록 하는 방식입니다.
