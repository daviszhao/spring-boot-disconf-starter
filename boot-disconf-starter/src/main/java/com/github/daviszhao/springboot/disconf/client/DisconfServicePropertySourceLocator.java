package com.github.daviszhao.springboot.disconf.client;

import com.baidu.disconf.client.fetcher.FetcherMgr;
import com.baidu.disconf.client.fetcher.impl.FetcherMgrImpl;
import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;
import com.baidu.disconf.core.common.path.DisconfWebPathMgr;
import com.baidu.disconf.core.common.restful.RestfulFactory;
import com.baidu.disconf.core.common.restful.RestfulMgr;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

@Setter
@Order(0)
@Slf4j
public class DisconfServicePropertySourceLocator implements PropertySourceLocator {
    private final List<PropertySourceLoader> loaders;
    private FetcherMgr fetcherMgr;
    private DisconfProperties disconfProperties;

    public DisconfServicePropertySourceLocator(DisconfProperties disconfProperties) {

        boolean configError = false;

        if (disconfProperties.getHostList() == null || disconfProperties.getHostList().size() == 0) {
            log.error("must specify 'disconf.host_list'");
            configError = true;
        }
        if (StringUtils.isEmpty(disconfProperties.getProfile())) {
            log.error("must specify 'disconf.profile'");
            configError = true;
        }
        if (configError) System.exit(9);
        this.disconfProperties = disconfProperties;
        loaders = SpringFactoriesLoader.loadFactories(PropertySourceLoader.class,
                getClass().getClassLoader());
    }

    @Override
    @Retryable(interceptor = "configServerRetryInterceptor")
    public PropertySource<?> locate(
            Environment environment) {

        log.info("Fetching config from server at: [{}] " + this.disconfProperties.getHostList());
        try {

            return getRemoteEnvironment();
        } catch (Exception e) {
            log.error("read config file from disconf server failed.", e);
            return null;
        }


    }

    //    @Synchronized("fetcherMgr")
    private FetcherMgr getFetcherMgr() throws Exception {
        if (this.fetcherMgr == null) {
            // 获取一个默认的抓取器
            RestfulMgr restfulMgr = RestfulFactory.getRestfulMgrNomal();

            fetcherMgr = new FetcherMgrImpl(restfulMgr, disconfProperties.getConfServerUrlRetryTimes(),
                    disconfProperties.getConfServerUrlRetrySleepSeconds(),
                    disconfProperties.isEnableLocalDownloadDirInClassPath(),
                    disconfProperties.getUserDefineDownloadDir(),
                    disconfProperties.getLocalDownloadDir(),
                    disconfProperties.getHostList());
        }
        return fetcherMgr;
    }

    private PropertySource<?> getRemoteEnvironment() throws Exception {

        PropertySource<?> specific;
        // Remote URL
        String configName = this.disconfProperties.getConfigName();
        specific = getPropertySource(configName + ".properties");
        if (specific != null) {
            return specific;
        } else {
            specific = getPropertySource(configName + ".yml");
            if (specific != null) {
                return specific;
            } else {
                specific = getPropertySource(configName + ".yaml");
                if (specific != null) {
                    return specific;
                }
            }
        }
        return null;
    }

    private PropertySource<?> getPropertySource(String fileName) throws Exception {
        String url = DisconfWebPathMgr.getRemoteUrlParameter("/api/config",
                disconfProperties.getApp(),
                disconfProperties.getVersion(),
                disconfProperties.getProfile(),
                fileName,
                DisConfigTypeEnum.FILE);
        System.out.println("URL=" + url);

        String file = getFetcherMgr().downloadFileFromServer(url, fileName, System.getProperty("java.io.tmpdir") + "/" + disconfProperties.getApp());
        System.out.println("file downloaded:" + file);

        FileSystemResource resource = new FileSystemResource(file);
        for (PropertySourceLoader loader : this.loaders) {
            if (canLoadFileExtension(loader, resource)) {

                return loader.load("applicationConfig", resource,
                        null);
            }
        }
        return null;
    }

    private boolean canLoadFileExtension(PropertySourceLoader loader, Resource resource) {
        String filename = resource.getFilename().toLowerCase(Locale.ENGLISH);
        for (String extension : loader.getFileExtensions()) {
            if (filename.endsWith("." + extension.toLowerCase(Locale.ENGLISH))) {
                return true;
            }
        }
        return false;
    }
}
