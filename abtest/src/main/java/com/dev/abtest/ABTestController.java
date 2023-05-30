package com.dev.abtest;

import io.hackle.sdk.HackleClient;
import io.hackle.sdk.common.Event;
import io.hackle.sdk.common.Variation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/abtest")
public class ABTestController {
    public static final String ORDER_EVENT_NAME = "hello-click";
    private final HackleClient hackleClient;
    private final int EXPERIMENT_KEY = 5;

    @GetMapping(value = {"/hello", ""})
    public String abtest(@RequestParam Long userId, Model model) {

        Variation variation = hackleClient.variation(EXPERIMENT_KEY, userId.toString());
        log.info("ABTest group = {}, userId = {}", variation, userId);

        // 할당받은 그룹에 대한 로직
        if (variation == Variation.A) {
            // 그룹 A 로직
            model.addAttribute("data", "[A안] Hi Hackle ABTest");
        } else if (variation == Variation.B) {
            // 그룹 B 로직
            model.addAttribute("data", "[B안] Hello Hackle ABTest");
        }
        model.addAttribute("userId", userId);
        return "abtest";
    }

    @PostMapping("/hello")
    public String clickEvent(OrderEvent orderEvent, RedirectAttributes attributes) {
        /* 예시 1: 이벤트 키만 전송 */
        hackleClient.track(ORDER_EVENT_NAME, orderEvent.getOrderId().toString());

        /* 예시 2: 이벤트 키와 숫자 값을 함께 전송 */
        Event event = Event.builder(ORDER_EVENT_NAME)
                .value(orderEvent.getAmount().doubleValue()) // 이벤트 키와 함께 수집할 숫자 값을 value에 넣는다
                .build();

        hackleClient.track(event.toString(), orderEvent.getUserId().toString());
        log.info("Sent abtest event = {}", orderEvent);

        return "redirect:/abtest/click-result";
    }

    @GetMapping(value = "click-result")
    public String helloClickResult() {
        return "click-result";
    }
}
