package com.eu.cloud.server.file.api.client;

import com.eu.cloud.core.wrapper.GlobalResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * FIle 服务 Feign 客户端
 *
 * @author jiangxd
 */
@Primary
@FeignClient(value = "eu-file", fallbackFactory = FileFallback.class)
public interface FileClient {

    /**
     * 图片上传
     *
     * @param file
     * @return
     */
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    GlobalResponseWrapper upload(@RequestPart("file") MultipartFile file);

}
