package idevgame.meteor.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 閸栧懏澹傞幓蹇撴珤
 * @author moon
 *
 */
public class ClassPathScanner {

	private static final Logger logger = LoggerFactory
			.getLogger(ClassPathScanner.class);

	public static Set<Class> scan(String basePackage, boolean recursive, boolean excludeInner, boolean checkInOrEx,
			List<String> classFilterStrs) {
		Set<Class> classes = new LinkedHashSet<Class>();
		String packageName = basePackage;
		List<Pattern> classFilters = toClassFilters(classFilterStrs);
		
		if (packageName.endsWith(".")) {
			packageName = packageName
					.substring(0, packageName.lastIndexOf('.'));
		}
		String package2Path = packageName.replace('.', '/');

		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader()
					.getResources(package2Path);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					doScanPackageClassesByFile(classes, packageName, filePath,
							recursive, excludeInner, checkInOrEx, classFilters);
				} else if ("jar".equals(protocol)) {

					doScanPackageClassesByJar(packageName, url, recursive,
							classes, excludeInner, checkInOrEx, classFilters);
				}
			}
		} catch (IOException e) {
			logger.error("IOException error:", e);
		}

		return classes;
	}

	private static void doScanPackageClassesByJar(String basePackage, URL url,
			final boolean recursive, Set<Class> classes, boolean excludeInner, boolean checkInOrEx, List<Pattern> classFilters) {
		String packageName = basePackage;
		String package2Path = packageName.replace('.', '/');
		JarFile jar;
		try {
			jar = ((JarURLConnection) url.openConnection()).getJarFile();
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (!name.startsWith(package2Path) || entry.isDirectory()) {
					continue;
				}

				if (!recursive
						&& name.lastIndexOf('/') != package2Path.length()) {
					continue;
				}

				if (excludeInner && name.indexOf('$') != -1) {
					logger.debug("exclude inner class with name:" + name);
					continue;
				}
				String classSimpleName = name
						.substring(name.lastIndexOf('/') + 1);

				if (filterClassName(classSimpleName, checkInOrEx, classFilters)) {
					String className = name.replace('/', '.');
					className = className.substring(0, className.length() - 6);
					try {
						classes.add(Thread.currentThread()
								.getContextClassLoader().loadClass(className));
					} catch (ClassNotFoundException e) {
						logger.error("Class.forName error:", e);
					}
				}
			}
		} catch (IOException e) {
			logger.error("IOException error:", e);
		}
	}

	private static void doScanPackageClassesByFile(Set<Class> classes,
			String packageName, String packagePath, boolean recursive, final boolean excludeInner, final boolean checkInOrEx, final List<Pattern> classFilters) {
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		final boolean fileRecursive = recursive;
		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return fileRecursive;
				}
				String filename = file.getName();
				if (excludeInner && filename.indexOf('$') != -1) {
					logger.debug("exclude inner class with name:" + filename);
					return false;
				}
				return filterClassName(filename, checkInOrEx, classFilters);
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				doScanPackageClassesByFile(classes,
						packageName + "." + file.getName(),
						file.getAbsolutePath(), recursive, excludeInner, checkInOrEx, classFilters);
			} else {
				String className = file.getName().substring(0,
						file.getName().length() - 6);
				try {
					classes.add(Thread.currentThread().getContextClassLoader()
							.loadClass(packageName + '.' + className));

				} catch (ClassNotFoundException e) {
					logger.error("IOException error:", e);
				}
			}
		}
	}

	private static boolean filterClassName(String className, boolean checkInOrEx, List<Pattern> classFilters) {
		if (!className.endsWith(".class")) {
			return false;
		}
		if (null == classFilters || classFilters.isEmpty()) {
			return true;
		}
		String tmpName = className.substring(0, className.length() - 6);
		boolean flag = false;
		for (Pattern p : classFilters) {
			if (p.matcher(tmpName).find()) {
				flag = true;
				break;
			}
		}
		return (checkInOrEx && flag) || (!checkInOrEx && !flag);
	}

	/**
	 * @param pClassFilters the classFilters to set
	 */
	private static List<Pattern> toClassFilters(List<String> pClassFilters) {
		List<Pattern> classFilters = new ArrayList<Pattern>();
		if(pClassFilters!=null){
			
			for (String s : pClassFilters) {
				String reg = "^" + s.replace("*", ".*") + "$";
				Pattern p = Pattern.compile(reg);
				classFilters.add(p);
			}
		}
		return classFilters;
	}

}
