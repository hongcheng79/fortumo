package chc.fortumo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

@Controller
@RequestMapping("process")
public class ProcessController {
    private static final Logger logger = LoggerFactory.getLogger(ProcessController.class);

    @RequestMapping(value="compute", method = RequestMethod.POST)
    @ResponseBody
    public String compute(@RequestParam(value = "number", defaultValue="end") String number,
                          @SessionAttribute("counter") CounterSessionData counter) {
        logger.debug("Received request : "+number+" for session "+ RequestContextHolder.currentRequestAttributes().getSessionId());

        if ("end".equals(number)) {
            int value = counter.value();
            counter.reset();
            return Integer.toString(value);
        } else {
            try {
                int currentNumber = Integer.parseInt(number);
                counter.plus(currentNumber);
                return "";
            } catch (NumberFormatException numEx) {
                logger.error("Invalid integer string : "+number, numEx);
                return "error";
            }
        }
    }

}
