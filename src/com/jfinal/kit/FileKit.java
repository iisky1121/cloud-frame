/**
 * Copyright (c) 2011-2017, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfinal.kit;

import java.io.*;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * FileKit.
 */
public class FileKit {
	private static String rootPath;
	static{
		rootPath = PathKit.getWebRootPath() + File.separator + ".." + File.separator + ".." +File.separator + "upload"+ File.separator;
		initRooPath();
	}
	public static void setRootPath(String path){
		rootPath = path;
		initRooPath();
	}
	public static String getRootPath(){
		return rootPath;
	}
	
	private static void initRooPath(){
		File file = new File(rootPath);
		if(!file.exists()){
			file.mkdirs();
		}
	}
	
	public static void delete(File file) {
		if (file != null && file.exists()) {
			if (file.isFile()) {
				file.delete();
			}
			else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i=0; i<files.length; i++) {
					delete(files[i]);
				}
			}
			file.delete();
		}
	}
	
	/**
	 * 获取文件拓展名
	 */
	public static String getExtension(File file){
		return getExtension(file.getName());
	}
	
	/**
	 * 获取文件拓展名
	 */
	public static String getExtension(String fileName){
		return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
	}
	
	/**
	 * 判断是否图片
	 */
	public static boolean isImage(File file){
		return isImage(file.getName());
	}
	
	/**
	 * 判断是否图片
	 */
	public static boolean isImage(String fileName){
		if(StrKit.isBlank(fileName)){
			return false;
		}
		String type = "|.jpg|.jpeg|.gif|.png|.bmp";
		int index = fileName.lastIndexOf(".");
		if(index == -1){
			return false;
		}
		
		if(type.indexOf("|" + getExtension(fileName)) != -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 写入图片文件
	 */
	public static boolean createImage(File file, String newFileName){
		if(!isImage(file)){
			throw new IllegalArgumentException("图片格式不支持");
		}
		return create(file, newFileName);
	}
	
	/**
	 * 写入文件
	 */
	public static boolean create(File file, String newFileName){
		return create(file, rootPath, newFileName);
	}
	
	/**
	 * 写入文件
	 */
	public static boolean create(File file, String filePath, String newFileName){
		try {
			if(file == null){
				throw new RuntimeException("文件为空或者不存在");
			}
			String fileName = file.getName();
			String extension = fileName.substring(fileName.lastIndexOf("."));
        	
			FileInputStream fis = new FileInputStream(file);
			File targetDir = new File(filePath);
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}
			File target = new File(targetDir, newFileName + extension);
			if (!target.exists()) {
				target.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(target);
			byte[] bts = new byte[1024];
			while (fis.read(bts, 0, 1024) != -1) {
				fos.write(bts, 0, 1024);
			}
			fos.close();
			fis.close();
			
			file.delete();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 获取文件
	 */
	public static File get(String fileName){
		return get(rootPath ,fileName);
	}
	
	/**
	 * 获取文件
	 */
	public static File get(String filePath, String fileName){
		return new File(filePath + fileName);
	}
	
	/**
	 * 把内容写入到文件
	 * @param outputDir
	 * @param fileName
	 * @param content
	 * @param isOverWrite
	 */
	public static void writeToFile(String outputDir, String fileName, String content, boolean isOverWrite){
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filePath = outputDir + File.separator + fileName;
        File file = new File(filePath);
        if(!isOverWrite && file.exists()){
        	LogKit.info("文件：%s,已存在，不覆盖原文件");
		}
        FileWriter fw;
		try {
			fw = new FileWriter(filePath);
			fw.write(content);
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
	public static void writeToFile(String outputDir, String fileName, String content){
		writeToFile(outputDir, fileName, content, true);
	}
	
	/**
	 * 读取文件内容
	 */
	public static String read(File file){
        try {
        	BufferedReader reader = new BufferedReader(new FileReader(file));
            return read(reader);
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
	}
	
	public static String read(String filePath){
        try {
        	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return read(reader);
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
	}

	private static String read(BufferedReader reader){
		try {
			StringBuilder sb = new StringBuilder();
			String line = null;
			// 一次读入一行，直到读入null为文件结束
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			reader.close();

			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取文件目录指定后缀文件列表
	 */
	public static List<String> fileList(String filePath, String suffix) {
		if(filePath != null && suffix != null){
			List<String> list = new ArrayList<String>();  
			File file = new File(filePath);  
			File[] subFile = file.listFiles();  
			
			for(File f : subFile){
				if(!f.isDirectory() && f.getName().trim().toLowerCase().endsWith(suffix)){
					list.add(f.getName());
				}
			}
			return list;  
		}
		return null;
    } 

	/**
	 * 获取文件MD5值
	 */
	public static String getMd5(File file){
		String value = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}
}
