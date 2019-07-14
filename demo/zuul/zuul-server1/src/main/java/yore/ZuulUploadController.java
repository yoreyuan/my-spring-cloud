package yore;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by yore on 2019/7/13 20:57
 */
//@Controller
@RestController
public class ZuulUploadController {

    @PostMapping("/upload")
    @ResponseBody
    public String uploadFile(@RequestParam(value = "file", required = true)MultipartFile file) throws IOException{
        byte[] bytes = file.getBytes();
        System.out.println(file.getOriginalFilename() + "\t" + file.getSize());
        File fileToSave = new File("/tmp/" + file.getOriginalFilename());
        FileCopyUtils.copy(bytes, fileToSave);
        return "{\"status\": \"success\", \"path\": \"" + fileToSave.getAbsolutePath() +"\"}";
    }

    @GetMapping("/test")
    public String test(String p){
        return "/test \t 参数为：" + p;
    }

}
