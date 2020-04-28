package com.rookie.exception;


import com.rookie.utils.RookieJsonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class CustomExceptionHandler {

    // 上传问价超过 500 k， 捕获异常 MaxUploadSizeExceededException
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public RookieJsonResult handlerUploadFaceMax(MaxUploadSizeExceededException ex) {
        return RookieJsonResult.errorMsg("文件大小不能超过500kb，请压缩或降低质量后再上传！");
    }
}
