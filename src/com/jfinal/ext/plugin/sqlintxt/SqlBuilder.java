package com.jfinal.ext.plugin.sqlintxt;

import java.io.File;
import java.util.Vector;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Sqls;

class SqlBuilder {
	private static Vector<File> vecFiles = new Vector<File>();

	static void clearSqlMap() {
		Sqls.clear();
	}

	static void init(String fileAbsolutePath) {
		getTxtFileName(PathKit.getRootClassPath()+fileAbsolutePath);
		loadTxt();
	}

	static void getTxtFileName(String fileAbsolutePath) {
		File file = new File(fileAbsolutePath);
		File[] files = file.listFiles();
		if (files == null) {
			return;
		} else {
			for (File f : files) {
				if (f.isDirectory()) {
					getTxtFileName(f.getAbsolutePath());// 采用递归调用

				} else if (f.getName().endsWith("-sql.txt")) {
					vecFiles.add(f);
				}
			}
		}
	}
	
	static void loadTxt(){
		for(File file : vecFiles){
			Sqls.load(file.getAbsolutePath().replace(PathKit.getRootClassPath(), ""));
		}
	}
}