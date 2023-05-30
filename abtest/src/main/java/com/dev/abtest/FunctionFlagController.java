package com.dev.abtest;

import io.hackle.sdk.HackleClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/function-flag")
public class FunctionFlagController {
    private final HackleClient hackleClient;
    private final int functionFlagKey = 4;

    @GetMapping(value = "/hello")
    public String functionFlag(@RequestParam Long userId, Model model) {

        boolean featureOn = hackleClient.isFeatureOn(functionFlagKey, userId.toString());
        model.addAttribute("featureOn", featureOn);

        return "function-flag";
    }

}
