package com.yc.controller;

import com.yc.service.HdfsService;
import com.yc.vo.JsonModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-16 10:51
 */
@RestController
public class HdfsController {

    @Autowired
    private HdfsService hdfsService;

    /**
     * 列出文件信息
     *
     * @param jm
     * @param path
     * @return
     */
    @RequestMapping("/getFiles")
    public JsonModel getFiles(JsonModel jm, String path) {
        try {
            List<Map<String, String>> maps = hdfsService.liatStatus(path);
            if (maps != null && maps.size() > 0) {
                jm.setCode(1);
                jm.setObj(maps);
            } else {
                jm.setCode(0);
                jm.setMsg("没有文件。。。");
            }
        } catch (Exception e) {
            jm.setCode(-1);
            jm.setMsg(e.getMessage());
        }
        return jm;
    }

    @RequestMapping("/mkdir")
    public JsonModel mkdir(JsonModel jm, String path) {
        try {
            boolean flag = hdfsService.mkdir(path);
            if (flag) {
                jm.setCode(1);
                jm.setObj("创建成功！");
            } else {
                jm.setCode(0);
                jm.setMsg("创建失败。");
            }
        } catch (Exception e) {
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        return jm;
    }

    @RequestMapping("/delete")
    public JsonModel delete(JsonModel jm, String path) {
        try {
            boolean flag = hdfsService.delete(path);
            if (flag) {
                jm.setCode(1);
                jm.setObj("删除成功！");
            } else {
                jm.setCode(0);
                jm.setMsg("删除失败。");
            }
        } catch (Exception e) {
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        return jm;
    }

    @RequestMapping("/rename")
    public JsonModel rename(JsonModel jm, String path,@RequestParam("newName") String newName) {
        if (StringUtils.isEmpty(path) || StringUtils.isEmpty(newName)) {
            jm.setCode(0);
            jm.setMsg("请求参数为空。");
        }
        String newPath = path.substring(0, path.lastIndexOf("/") + 1) + newName;
        try {
            boolean flag = hdfsService.rename(path, newPath);
            if (flag) {
                jm.setCode(1);
                jm.setObj("修改成功！");
            } else {
                jm.setCode(0);
                jm.setMsg("修改失败。");
            }
        } catch (Exception e) {
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        return jm;
    }

    @RequestMapping("/moveTo")
    public JsonModel moveTo(JsonModel jm, String path,@RequestParam("newPath") String newPath) {
        if (StringUtils.isEmpty(path) || StringUtils.isEmpty(newPath)) {
            jm.setCode(0);
            jm.setMsg("请求参数为空。");
        }
        try {
            hdfsService.copyFile(path,newPath);
            boolean flag = hdfsService.delete(path);
            if (flag) {
                jm.setCode(1);
                jm.setObj("修改成功！");
            } else {
                jm.setCode(0);
                jm.setMsg("修改失败。");
            }
        } catch (Exception e) {
            jm.setCode(0);
            jm.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return jm;
    }
}
