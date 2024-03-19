package com.rt.utils;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

public class IpUtil {

    private static Searcher searcher;

    public static String getClientIP(HttpServletRequest request){
        // 获取客户端真实IP地址
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        //多级 方向代理
        if (ip.indexOf(",")>0){
            ip = ip.substring(0,ip.indexOf(",")).trim();
        }
        if (ip.equals("0:0:0:0:0:0:0:1")) {
            ip = "本地内网";
        }
        return ip;
    }

    public static String getIpAddrProv(String ip) {
        return changeRegionOnlyProvince(getIpAddress(ip));
    }

    // 获取ip地址：市区
    public static String getIpAddrCity(String ip){
        return changeRegionOnlyCity(getIpAddress(ip));
    }

    // 获取ip地址：省份
    private static String getIpAddress(String ip) {
        if ("127.0.0.1".equals(ip) || ip.startsWith("192.168") || "本地内网".equals(ip)) {
            return "局域网 ip";
        }
        if (searcher == null) {
            try {
                Resource resource = new ClassPathResource("ipdb/ip2region.xdb");
                InputStream inputStream = resource.getInputStream();
                byte[] dbBinStr = FileCopyUtils.copyToByteArray(inputStream);
                searcher = Searcher.newWithBuffer(dbBinStr);
            } catch (IOException e) {
                return "searcher错误~ " + e.getMessage();
            }
        }
        String region;
        String errorMessage;
        try {
            region = searcher.search(ip);
        } catch (Exception e) {
            errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.length() > 256) {
                errorMessage = errorMessage.substring(0,256);
            }
            return "region搜索不到~ " + errorMessage;
        }
        // 输出 region

        // 中国|0|上海|上海市|电信
        // 美国|0|俄勒冈|0|谷歌

        return region;
    }

    // 仅仅输出到省份、国外则直接国家，不输出运营商
    private static String changeRegionOnlyProvince(String region) {
        if(region.equals("局域网 ip")){
            return "内网";
        }
        String[] rs = region.split("\\|");
        if(rs.length > 1) {
            if ("0".equals(rs[0])) {
                // 国家都没有，直接未知！
                return "未知";
            } else if (!"中国".equals(rs[0])) {
                // 外国ip
                return rs[0];
            } else if (!"0".equals(rs[2])) {
                // 省份可以查到
                return rs[2].replaceAll("省$", "");
            } else if (!"0".equals(rs[3])) {
                // 省份查不到，市区可以查到
                return rs[3].replaceAll("市$", "");
            } else {
                // 省份、市区都查不到，那就查他是哪个国家的，由于第一个if判断的是国家是否为0，所以这里国家必定有值
                return rs[0];
            }
        }else {
            return "未知";
        }
    }

    // 不查省份、直接查市区（国内）
    private static String changeRegionOnlyCity(String region) {
        if(region.equals("局域网 ip")){
            return "内网";
        }
        String[] rs = region.split("\\|");
        if(rs.length > 1) {
            if ("0".equals(rs[0])) {
                // 国家都没有，直接未知！
                return "未知";
            } else if (!"中国".equals(rs[0])) {
                // 外国ip
                return rs[0];
            } else if (!"0".equals(rs[3])) {
                // 市区可以查到
                return rs[3].replaceAll("市$", "");
            } else {
                // 市区都查不到，那就查他是哪个国家的，由于第一个if判断的是国家是否为0，所以这里国家必定有值
                return rs[0];
            }
        }else{
            return "未知";
        }
    }

}
