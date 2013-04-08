package com.ruyicai.msgcenter.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.msgcenter.exception.RuyicaiException;

public class FileUtil {

	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static List<String> read(File file) {
		List<String> list = new ArrayList<String>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			String data = null;
			while (null != (data = in.readLine())) {
				list.add(data);
			}
		} catch (Exception e) {
			logger.error("读取文件出错, fileName:　" + file.getName() + ", error: "
					+ e.getMessage());
			throw new RuyicaiException("读取文件出错, fileName:　" + file.getName()
					+ ", error: " + e.getMessage());
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
		return list;
	}

	public static void write(String filepath, String filename, String content) {
		try {
			File parent = new File(filepath);
			if (!parent.exists()) {
				parent.mkdirs();
			}
			File file = new File(filepath, filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file)));
			bufferedWriter.write(content);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (Exception e) {
			logger.error("创建文件出错", e);
			throw new RuyicaiException("创建文件出错, filename:　" + filename
					+ ", error: " + e.getMessage());
		}
	}

	public static Properties loadProps(String resourceLocation) {
		Properties props = new Properties();
		try {
			props.load(FileUtil.class.getClassLoader().getResourceAsStream(
					resourceLocation));
		} catch (IOException e) {
			logger.error("load properties error", e);
		}
		return props;
	}
	
	
	
	/**
     * 根据相对路径获得 File
     * 
     * @param relativePath
     *            工程下的以"./"开头，web下的以"/"开头，ClassPath下的直接相对路径
     * @return File
     */
    public static File getFileByRelativePath(String relativePath) {
        return new File(getAbsolutePath(relativePath));
    }

    /**
     * 根据相对路径获得绝对路径
     * 
     * @param relativePath
     *            工程下的以"./"开头，web下的以"/"开头，ClassPath下的直接相对路径
     * @return 绝对路径字符串
     */
    public static String getAbsolutePath(String relativePath) {
        String result = null;
        if (null != relativePath) {
            if (relativePath.indexOf("./") == 0) {
                String workspacePath = new File("").getAbsolutePath();
                relativePath = relativePath.substring(2);
                if (relativePath.length() > 0) {
                    relativePath = relativePath
                            .replace('/', File.separatorChar);
                    result = workspacePath + String.valueOf(File.separatorChar)
                            + relativePath;
                } else {
                    result = workspacePath;
                }
            } else if (relativePath.indexOf("/") == 0) {
                String webRootPath = getAbsolutePathOfWebRoot();
                if (relativePath.length() > 0) {
                    relativePath = relativePath
                            .replace('/', File.separatorChar);
                    result = webRootPath + relativePath;
                } else {
                    result = webRootPath;
                }
            } else {
                String classPath = getAbsolutePathOfClassPath();
                if (relativePath.length() > 0) {
                    relativePath = relativePath
                            .replace('/', File.separatorChar);
                    result = classPath + File.separatorChar + relativePath;
                } else {
                    result = classPath;
                }
            }
        }
        return result;
    }

    // 得到WebRoot目录的绝对地址
    public static String getAbsolutePathOfWebRoot() {
        String result = null;
        result = getAbsolutePathOfClassPath();
        result = result.replace(File.separatorChar + "WEB-INF"
                + File.separatorChar + "classes", "");
        return result;
    }

    // 得到WEB-INF目录的绝对地址
    public static String getAbsolutePathOfWEBINF() {
        return getAbsolutePathOfClassPath().replace("classes", "");
    }
    
    // 得到ClassPath的绝对路径
    private static String getAbsolutePathOfClassPath() {
        String result = null;
        try {
            File file = new File(getURLOfClassPath().toURI());
            result = file.getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 得到ClassPath的URL
    private static URL getURLOfClassPath() {
        return getClassLoader().getResource("");
    }

    // 得到类加载器
    private static ClassLoader getClassLoader() {
        return FileUtil.class.getClassLoader();
    }

}
