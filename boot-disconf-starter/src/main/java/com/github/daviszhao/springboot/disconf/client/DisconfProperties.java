package com.github.daviszhao.springboot.disconf.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Dave Syer
 */
@SuppressWarnings("ConfigurationProperties")
@ConfigurationProperties(DisconfProperties.PREFIX)
@ToString
@Setter
@Getter
public class DisconfProperties {

    public static final String PREFIX = "spring.disconf";
    private boolean enableLocalDownloadDirInClassPath = true;
    private String userDefineDownloadDir = "./disconf/download";
    private String localDownloadDir = "./disconf/download";

    /**
     * Flag to say that remote configuration is enabled. Default true;
     */
    private boolean enabled = true;


    /**
     * # 版本, 请采用 X_X_X_X 格式
     */
    private String version = "1_0_0_0";
    private String profile;
    /**
     * # debug
     */
    private boolean debug = true;
    /**
     * # 忽略哪些分布式配置，用逗号分隔
     */
    private String ignore;
    /**
     * # 获取远程配置 重试次数，默认是3次
     */
    private int confServerUrlRetryTimes = 3;
    /**
     * # 获取远程配置 重试时休眠时间，默认是5秒
     */
    private int confServerUrlRetrySleepSeconds = 5;
    private List<String> hostList;
    private String configName = "application";
    @Value("${spring.application.name}")
    private String app;

    private DisconfProperties() {
    }

    public DisconfProperties(Environment environment) {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length == 0) {
            profiles = environment.getDefaultProfiles();
        }
        this.setProfile(StringUtils.arrayToCommaDelimitedString(profiles));
    }

}
