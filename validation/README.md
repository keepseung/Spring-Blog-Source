# Validation - Spring MVC 기반 검증 기능

## 검증이 필요한 이유
웹 사이트 같은 클라이언트의 요청을 통제하기 위해서다.    
예를 들어 회원가입시 사용자가 입력한 데이터에 오류가 있다면 사용자가 입력한 데이터를 유지한 상태로 어떤 오류가 발생했는지 친절하게 알려주는데 검증이 사용된다.

## 검증 요구사항
- 타입 검증
  - 가격, 수량에 문자가 들어가면 검증 오류 처리
- 필드 검증
  - 상품명: 필수, 공백X
  - 가격: 1000원 이상, 1백만원 이하
  - 수량: 최대 9999
- 특정 필드의 범위를 넘어서는 검증
  - 가격 * 수량의 합은 10,000원 이상

## BindingResult과 FieldError, ObjectError
### BindingResult
- 스프링이 제공하는 검증 오류를 보관하는 객체이다. 검증 오류가 발생하면 여기에 보관하면 된다.
- BindingResult가 있으면 @ModelAttribute에 데이터 바인딩시 오류가 발생해도 컨트롤러가 호출된다!    
  예) @ModelAttribute에 바인딩 시 타입 오류가 발생하면?
  - BindingResult가 없으면 -> 400 오류가 발생하면서 컨트롤러가 호출되지 않고, 오류 페이지로 이동한다.
  - BindingResult가 있으면 -> 오류 정보( FieldError )를 BindingResult 에 담아서 컨트롤러를 정상 호출한다.

### FieldError - 필드 오류
- 필드에 오류가 있으면 FieldError 객체를 생성해서 bindingResult에 담아두면 된다.
~~~java
if (!StringUtils.hasText(item.getItemName())) {
    bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
}
~~~

#### FieldError의 두 가지 생성자 
~~~java
public FieldError(String objectName, String field, String defaultMessage);
public FieldError(String objectName, String field, @Nullable Object
rejectedValue, boolean bindingFailure, @Nullable String[] codes, @Nullable
Object[] arguments, @Nullable String defaultMessage)
~~~
파라미터 목록
- objectName : 오류가 발생한 객체 이름 = @ModelAttribute 이름
- field : 오류가 발생한 필드 이름
- rejectedValue : 사용자가 입력한 값(거절된 값)
- bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
- codes : 메시지 코드
- arguments : 메시지에서 사용하는 인자
- defaultMessage : 기본 오류 메시지

#### 오류 발생시 사용자 입력 값 유지
FieldError는 오류 발생시 사용자 입력 값을 저장하는 기능을 제공한다.    
여기서 rejectedValue 가 바로 오류 발생시 사용자 입력 값을 저장하는 필드다.   
보관한 사용자 입력 값을 검증 오류 발생시 화면에 다시 출력하면 된다.
~~~java
new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다.")
~~~

### ObjectError - 글로벌 오류 
특정 필드를 넘어서는 오류가 있으면 ObjectError 객체를 생성해서 bindingResult에 담아두면 된다.   
~~~java
bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
~~~

### ObjectError 생성자 요약
~~~java
public ObjectError(String objectName, String defaultMessage) {}
~~~
- objectName : @ModelAttribute 의 이름
- defaultMessage : 오류 기본 메시지

### 주의
- BindingResult는 검증할 대상 바로 다음에 와야한다. 순서가 중요하다. 예를 들어서 @ModelAttribute Item item, 바로 다음에 BindingResult가 와야 한다.
- BindingResult는 Model에 자동으로 포함된다.

## 오류 코드와 메시지 처리
### errors 메시지 파일 생성
messages.properties를 사용해도 되지만, 오류 메시지를 구분하기 쉽게 errors.properties라는 별도의 파일을 만든다.

### 스프링 부트 메시지 설정 추가
application.properties
~~~properties
spring.messages.basename=messages,errors
~~~
errors.properties 추가 (src/main/resources/errors.properties)
~~~properties
required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.
totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
~~~
> 참고: errors_en.properties 파일을 생성하면 오류 메시지도 국제화 처리를 할 수 있다.

### errors에 등록한 메시지를 사용하기
#### FieldError, ObjectError 직접 생성하는 방식
~~~java 
//특정 필드 예외인 경우
//range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000});

//특정 필드 예외가 아닌 전체 예외인 경우
// 가격*개수 > 10000 이상인 조건
bindingResult.addError(new ObjectError("item", new String[] {"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
~~~
- codes : required.item.itemName 를 사용해서 메시지 코드를 지정한다. 메시지 코드는 하나가 아니라 배열로 여러 값을 전달할 수 있는데, 순서대로 매칭해서 처음 매칭되는 메시지가 사용된다.
- arguments : Object[]{1000, 1000000} 를 사용해서 코드의 {0} , {1} 로 치환할 값을 전달한다.

#### rejectValue() , reject()로 메세지 사용하기
BindingResult 가 제공하는 rejectValue() , reject() 를 사용하면 FieldError , ObjectError를 직접 생성하지 않고, 깔끔하게 검증 오류를 다룰 수 있다.
~~~java
//특정 필드 예외인 경우
//range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);

//특정 필드 예외가 아닌 전체 예외인 경우
// 가격*개수 > 10000 이상인 조건
bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
~~~

### 작동 방식
errors.properties에 있는 코드를 직접 입력하지 않았는데 에러 메세지가 입력이 된 이유    
> BindingResult는 어떤 객체를 대상으로 검증하는지 target을 이미 알고 있다. 따라서 target(item)에 대한 정보는 없어도 된다
rejectValue()
~~~java
void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);
~~~
- field : 오류 필드명
- errorCode : 오류 코드(이 오류 코드는 메시지에 등록된 코드가 아니다. 뒤에서 설명할 messageResolver를 위한 오류 코드이다.)
- errorArgs : 오류 메시지에서 {0} 을 치환하기 위한 값
- defaultMessage : 오류 메시지를 찾을 수 없을 때 사용하는 기본 메시지

reject()
~~~java
void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);
~~~


## MessageCodesResolver를 사용한 메세지 범용성 전략

### MessageCodesResolver
- 검증 오류 코드로 메시지 코드들을 생성한다.
- MessageCodesResolver 인터페이스이고 DefaultMessageCodesResolver는 기본 구현체이다.
- 주로 ObjectError , FieldError와 함께 사용함

### DefaultMessageCodesResolver의 기본 메시지 생성 규칙
#### 객체 오류
~~~ 
객체 오류의 경우 다음 순서로 2가지 생성
1.: code + "." + object name
2.: code

예) 오류 코드: required, object name: item
1.: required.item
2.: required
~~~

#### 필드 오류
~~~
필드 오류의 경우 다음 순서로 4가지 메시지 코드 생성
1.: code + "." + object name + "." + field
2.: code + "." + field
3.: code + "." + field type
4.: code

예) 오류 코드: typeMismatch, object name "user", field "age", field type: int
1. "typeMismatch.user.age"
2. "typeMismatch.age"
3. "typeMismatch.int"
4. "typeMismatch"
~~~

### 동작 방식
- rejectValue() , reject() 는 내부에서 MessageCodesResolver 를 사용한다. 여기에서 메시지 코드들을 생성한다.
- FieldError , ObjectError 의 생성자를 보면, 오류 코드를 하나가 아니라 여러 오류 코드를 가질 수 있다.    
MessageCodesResolver 를 통해서 생성된 순서대로 오류 코드를 보관한다.
- 이 부분을 BindingResult 의 로그를 통해서 확인해보자.
  codes [range.item.price, range.price, range.java.lang.Integer, range]
  
#### FieldError rejectValue("itemName", "required")
다음 4가지 오류 코드를 자동으로 생성
- required.item.itemName
- required.itemName
- required.java.lang.String
- required
#### ObjectError reject("totalPriceMin")
- 다음 2가지 오류 코드를 자동으로 생성
- totalPriceMin.item
- totalPriceMin

### 오류 코드 관리 전략
#### 핵심은 구체적인 것에서! 덜 구체적인 것으로!
MessageCodesResolver 는 required.item.itemName 처럼 구체적인 것을 먼저 만들어주고, required 처럼 덜 구체적인 것을 가장 나중에 만든다.    
이렇게 하면 앞서 말한 것처럼 메시지와 관련된 공통 전략을 편리하게 도입할 수 있다


#### 오류 코드 관리 전략 적용
~~~properties
required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.
totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}    

#==ObjectError==

#Level1
totalPriceMin.item=상품의 가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}    

#Level2 - 생략    
totalPriceMin=전체 가격은 {0}원 이상이어야 합니다. 현재 값 = {1}    

#==FieldError==    
#Level1    
required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.    

#Level2 - 생략    

#Level3    
required.java.lang.String = 필수 문자입니다.
required.java.lang.Integer = 필수 숫자입니다.
min.java.lang.String = {0} 이상의 문자를 입력해주세요.
min.java.lang.Integer = {0} 이상의 숫자를 입력해주세요.
range.java.lang.String = {0} ~ {1} 까지의 문자를 입력해주세요.
range.java.lang.Integer = {0} ~ {1} 까지의 숫자를 입력해주세요.
max.java.lang.String = {0} 까지의 숫자를 허용합니다.
max.java.lang.Integer = {0} 까지의 숫자를 허용합니다.    

#Level4    
required = 필수 값 입니다.
min= {0} 이상이어야 합니다.
range= {0} ~ {1} 범위를 허용합니다.
max= {0} 까지 허용합니다.
~~~
크게 객체 오류와 필드 오류를 나누었다. 그리고 범용성에 따라 레벨을 나누어두었다.    
itemName 의 경우 required 검증 오류 메시지가 발생하면 다음 코드 순서대로 메시지가 생성된다.    
1. required.item.itemName
2. required.itemName
3. required.java.lang.String
4. required


### 스프링이 직접 만든 오류 메시지 처리
price(int) 필드에 문자 "A"를 입력해보자.    
로그를 확인해보면 BindingResult 에 FieldError가 담겨있고, 다음과 같은 메시지 코드들이 생성된 것을 확인할 수 있다.
codes[typeMismatch.item.price,typeMismatch.price,typeMismatch.java.lang.Integer,typeMismatch].   

다음과 같이 4가지 메시지 코드가 입력되어 있다.    
- typeMismatch.item.price
- typeMismatch.price
- typeMismatch.java.lang.Integer
- typeMismatch
> 스프링은 타입 오류가 발생하면 typeMismatch 라는 오류 코드를 사용한다. 이 오류 코드가 MessageCodesResolver 를 통하면서 4가지 메시지 코드가 생성된 것이다.

error.properties 에 다음 내용을 추가하자
~~~properties
typeMismatch.java.lang.Integer=숫자를 입력해주세요.
typeMismatch=타입 오류입니다.
~~~
결과적으로 소스코드를 하나도 건들지 않고, 원하는 메시지를 단계별로 설정할 수 있다.    

## Validator 분리
컨트롤러에서 검증 로직이 차지하는 부분은 매우 크다. 이런 경우 별도의 클래스로 역할을 분리하는 것이 좋다. 그리고 이렇게 분리한 검증 로직을 재사용 할 수도 있다.   
스프링은 검증을 체계적으로 제공하기 위해 다음 인터페이스를 제공한다.
~~~java
public interface Validator {
  boolean supports(Class<?> clazz);
  void validate(Object target, Errors errors);
}
~~~
- supports() {} : 해당 검증기를 지원하는 여부 확인
- validate(Object target, Errors errors) : 검증 대상 객체와 BindingResult

### 1. Validator를 구현한 ItemValidator 클래스를 직접 호출하기
~~~java
private final ItemValidator itemValidator;
@PostMapping("/add")
public String addItem(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

  itemValidator.validate(item, bindingResult);
  
  if (bindingResult.hasErrors()) {
     log.info("errors={}", bindingResult);
     return "validation/v1/addForm";
  }
----
}
~~~
ItemValidator 를 스프링 빈으로 주입 받아서 직접 호출했다.

### 2. WebDataBinder를 통해서 사용하기
WebDataBinder는 스프링의 파라미터 바인딩의 역할을 해주고 검증 기능도 내부에 포함한다.
~~~java
@InitBinder
public void init(WebDataBinder dataBinder) {
    log.info("init binder {}", dataBinder);
    dataBinder.addValidators(itemValidator);
}
~~~
이렇게 WebDataBinder 에 검증기를 추가하면 해당 컨트롤러에서는 검증기를 자동으로 적용할 수 있다.    
@InitBinder 해당 컨트롤러에만 영향을 준다. 글로벌 설정은 별도로 해야한다.

#### @Validated 적용
~~~java
private final ItemValidator itemValidator;
@PostMapping("/add")
public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes){
  
  if (bindingResult.hasErrors()) {
     log.info("errors={}", bindingResult);
     return "validation/v1/addForm";
  }
----
}
~~~
validator를 직접 호출하는 부분이 사라지고, 대신에 검증 대상 앞에 @Validated가 붙었다.

#### 동작방식
@Validated는 검증기를 실행하라는 애노테이션이다.   
이 애노테이션이 붙으면 앞서 WebDataBinder에 등록한 검증기를 찾아서 실행한다.    
그런데 여러 검증기를 등록한다면 그 중에 어떤 검증기가 실행되어야 할지 구분이 필요하다. 이때 supports()가 사용된다.    
여기서는 supports(Item.class) 호출되고, 결과가 true 이므로 ItemValidator의 validate()가 호출된다.
~~~java
@Component
public class ItemValidator implements Validator {
  @Override
  public boolean supports(Class<?> clazz) {
    return Item.class.isAssignableFrom(clazz);
  }
  @Override
  public void validate(Object target, Errors errors) {...}
}
~~~

> 참고 : 검증시 @Validated @Valid 둘다 사용가능하다.    
> javax.validation.@Valid 를 사용하려면 build.gradle 의존관계 추가가 필요하다.    
> implementation 'org.springframework.boot:spring-boot-starter-validation'    
> @Validated는 스프링 전용 검증 애노테이션이고, @Valid는 자바 표준 검증 애노테이션이다.





