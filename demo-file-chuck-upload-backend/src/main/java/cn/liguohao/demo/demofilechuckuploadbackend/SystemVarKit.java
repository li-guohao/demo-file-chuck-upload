package cn.liguohao.demo.demofilechuckuploadbackend;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author guohao
 * @date 2022/10/17
 */
public class SystemVarKit {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemVarKit.class);

    public static String getCurrentAppDirPath() {
        return System.getProperty("user.dir");
    }

    public static String getCurrentUserName() {
        return System.getProperty("user.name");
    }

    public static String getCurrentUserDirPath() {
        return System.getProperty("user.home");
    }

    public static String getOsCacheDirPath() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String getIPAddress() {
        InetAddress localHost = null;
        try {
            localHost = Inet4Address.getLocalHost();
        } catch (UnknownHostException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return localHost.getHostAddress();
    }
}
