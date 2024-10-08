package com.iteng.startup.controller;

import com.iteng.startup.common.ErrorCode;
import com.iteng.startup.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author iteng
 * @date 2024-02-02 17:10
 */
@Slf4j
public class CommonController {

    /**
     * 下载
     *
     * @param filePath 文件实际目录
     * @param fileName 文件名
     */
    public void download(HttpServletResponse response, String filePath, String fileName) {
        if (StringUtils.isBlank(filePath)) {
            return;
        }
        /* Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
        attachment表示以附件方式下载 inline表示在线打开 "Content-Disposition: inline; filename=文件名.mp3"
        filename表示文件的默认名称，因为网络传输只支持URL编码的相关格式，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称 */
        try {
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("urlEncode错误", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "下载失败！");
        }
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        readFile(response, filePath + fileName);
    }

    /**
     * 上传
     *
     * @param filePath    文件实际目录
     * @param fileName    文件名
     * @param inputStream 文件流
     */
    public void upload(String filePath, String fileName, InputStream inputStream) {

        if (StringUtils.isBlank(filePath) || StringUtils.isBlank(fileName)) {
            return;
        }

        // 创建要存储的磁盘路径
        File uploadFilePath = new File(filePath);

        // 判断磁盘路径是否存在，存在则不创建
        if (!uploadFilePath.exists()) {
            uploadFilePath.mkdirs();
        }
        OutputStream os = null;

        try {
            // 读取文件输出流
            os = Files.newOutputStream(Paths.get(filePath + fileName));
            // 创建一个字节数组作为桶
            byte[] buffer = new byte[1024];
            // 从输入流管道读取数据，写到输出流管道
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                // 读取多少就倒出多少
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            log.error("IO异常，", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        } finally {
            // 关闭资源
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                log.error("IO异常，", e);
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
        }
    }

    /**
     * 将文件流写入到浏览器
     *
     * @param filePath 文件实际存储路径
     */
    public void readFile(HttpServletResponse response, String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return;
        }
        if (filePath.contains("../") || filePath.contains("..\\")) {
            return;
        }
        OutputStream out = null;
        FileInputStream in = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            in = new FileInputStream(file);
            byte[] byteData = new byte[1024];
            out = response.getOutputStream();
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            log.error("读取文件异常", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("io异常", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("io异常", e);
                }
            }
        }
    }
}
