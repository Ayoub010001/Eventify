package net.ayoub.eventify.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String error(HttpServletRequest request) {
        Object attribute = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if(attribute != null && Integer.valueOf(attribute.toString()) == HttpStatus.NOT_FOUND.value()) {
            return "notFound";
        }
        return "error";
    }
}
