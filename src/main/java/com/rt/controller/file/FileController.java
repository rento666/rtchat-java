package com.rt.controller.file;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rt.common.Result;
import com.rt.entity.files.Files;
import com.rt.mapper.files.FilesMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 文件 前端控制器
 * </p>
 *
 * @author TwoZiBro
 * @since 2023-12-03
 */
@RestController
@RequestMapping("/file")
    public class FileController {

    @Value("${rtchat.profile}")
    private String fileUploadPath;

    @Resource
    private FilesMapper filesMapper;

    /**
     * 文件上传接口
     * @param file 前端传递过来的文件
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {

        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();

        // 定义一个文件唯一的标识码
        String fileUUID = IdUtil.fastSimpleUUID() + StrUtil.DOT + type;

        File uploadFile = new File(fileUploadPath + fileUUID);
        // 判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        File parentFile = uploadFile.getParentFile();
        if(!parentFile.exists()) {
            parentFile.mkdirs();
        }

        String url;
        // 获取文件的md5
        String md5 = SecureUtil.md5(file.getInputStream());
        // 从数据库查询是否存在相同的记录
        Files dbFiles = getFileByMd5(md5);
        if (dbFiles != null) {
            // 能查到，直接获取url
            url = dbFiles.getUrl();
        } else {
            // 上传文件到磁盘
            file.transferTo(uploadFile);
            // 数据库若不存在重复文件，则不删除刚才上传的文件
            Files f = new Files();
            f.setName(originalFilename);
            f.setFType(type);
            f.setSize(size);
            f.setUrl(fileUUID);
            f.setIsDelete(false);
            f.setEnable(true);
            f.setMd5(md5);
            filesMapper.insert(f);

            url =  fileUUID;
        }

        return new Result().success("上传文件成功",url);
    }

    /**
     * 文件下载接口   /file/{fileUUID}
     */
    @GetMapping("/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        // 根据文件的唯一标识码获取文件
        File uploadFile = new File(fileUploadPath + fileUUID);
        // 设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");

        // 读取文件的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    // 直接预览文件，而不是下载！
    @GetMapping("/preview/{fileUUID}")
    public void preview(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        // 根据文件的唯一标识码获取文件
        File uploadFile = new File(fileUploadPath + fileUUID);

        // 文件后缀名
        String fileType  = fileUUID.substring(fileUUID.indexOf(".")+1).toLowerCase();

        ServletOutputStream os = response.getOutputStream();
        //设置文件ContentType类型
        if("jpg,jepg,gif,png".contains(fileType)){//图片类型
            response.setContentType("image/"+fileType);
        }else if("pdf".contains(fileType)){//pdf类型
            response.setContentType("application/pdf");
        }else{//自动判断下载文件类型
            response.setContentType("multipart/form-data");
        }
        // 读取文件的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }


    private Files getFileByMd5(String md5) {
        // 查询文件的md5是否存在
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<Files> filesList = filesMapper.selectList(queryWrapper);
        return filesList.isEmpty() ? null : filesList.get(0);
    }
}

