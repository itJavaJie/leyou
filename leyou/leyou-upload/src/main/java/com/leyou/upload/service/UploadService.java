package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif");

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    @Autowired
    private FastFileStorageClient storageClient;

    public String uploadFile(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();

        // 校验文件类型
        String contentType = file.getContentType();

        if (!CONTENT_TYPES.contains(contentType)) {

            // 文件类型不合法，直接返回null
            LOGGER.info("文件类型不合法：{}", originalFilename);

            return null;
        }

        try {

            // 校验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

            if (bufferedImage == null) {

                LOGGER.info("文件内容不合法：{}", originalFilename);

                return null;
            }

            /* 使用原始方法上传图片 */
            //// 保存到服务器
            //file.transferTo(new File("E:\\image\\" + originalFilename));
            //
            //// 生成url地址返回
            //return "http://image.leyou.com/" + originalFilename;

            /* 使用FastDFS上传文件 */
            // 保存到FastDFS服务器
            String ext = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);

            // 返回图片路径
            return "http://image.leyou.com/" + storePath.getFullPath();

        } catch (IOException e) {
            LOGGER.info("服务器内部错误：{}", originalFilename);
            e.printStackTrace();
        }

        return null;

    }

}
