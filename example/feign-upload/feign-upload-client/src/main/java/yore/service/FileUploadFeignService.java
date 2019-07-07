package yore.service;

import feign.Response;
import org.springframework.web.bind.annotation.RequestParam;
import yore.config.FeignMultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by yore on 2019/7/6 15:58
 */
@FeignClient(value = "feign-file-server", configuration = FeignMultipartSupportConfig.class)
public interface FileUploadFeignService {

    /***
     * 1.produces,consumes必填
     * 2.注意区分@RequestPart和RequestParam，不要将 @RequestPart(value = "file") 写成@RequestParam(value = "file")
     *
     * @param file file
     * @return String
     */
    @RequestMapping(method = RequestMethod.POST, value = "/uploadFile/server",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String fileUpload(@RequestPart(value = "file") MultipartFile file);

    /**
     * 生成图片验证码
     * @param imagekey
     * @return
     */
    Response createImageCode(@RequestParam("imagekey") String imagekey);

}
