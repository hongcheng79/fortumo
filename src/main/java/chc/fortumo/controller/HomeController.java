package chc.fortumo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping
public class HomeController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String index() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ssZ");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
