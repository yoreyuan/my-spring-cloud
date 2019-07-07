package yore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yore on 2019/7/6 17:56
 */
@RestController
public class FeignUploadController {

    @Autowired
//    FileClient client;

    @PostMapping(value = "/uploadFile/server", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String fileUploadServer(MultipartFile file ) throws Exception{
//        String url = "文件服务器地址";
//        try {
//            Map<String,Object> params = HttpContextUtil.getParamsMap();
//            InputStreamBody fileBody = new InputStreamBody(file1.getInputStream(),file1.getOriginalFilename());
//            InputStreamBody thumbBody = new InputStreamBody(file2.getInputStream(),file2.getOriginalFilename());
//            HttpEntity reqEntity = MultipartEntityBuilder.create()
//                    .addPart("file",fileBody)
//                    .addPart("thumb",thumbBody)
//                    .build();
//            String result = HttpUtils.doFormFile(url,reqEntity);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        return file.getOriginalFilename() + " 上传成功";
    }


    /**
     *由id下载存储的文件
     */
    @GetMapping(value = "/{id}/data")
    public void downloadFile(@PathVariable String id, HttpServletResponse servletResponse) throws IOException {
        /*HttpTrace.Response response = client.download(id);
        Response.Body body = response.body();
        for(Object key : response.headers().keySet()){
            List<String> kList = (List)response.headers().get(key);
            for(String val : kList){
                servletResponse.setHeader(StringUtils.toString(key), val);
            }
        }
        try(InputStream inputStream = body.asInputStream();
            OutputStream outputStream = servletResponse.getOutputStream()
        ){
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            outputStream.write(b);
            outputStream.flush();
        }catch (IOException e){
            throw new RestException("IO流异常", e);
        }*/
    }



}
