package com.jfinal.ext.kit;

import java.io.File;

import net.coobird.thumbnailator.Thumbnails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.FileKit;

/**
 * 图片处理类
 *
 */
public class ImageKit {
	private static final Logger logger = LoggerFactory.getLogger(ImageKit.class);
	//图片需要压缩等级(从大到小)
	private final static int[] compressLevel = new int[]{1080, 360};
	
	static{
		for(int compressLevel : compressLevel){
			File imageDir = new File(getCompressFileSavePath(compressLevel));
			if(!imageDir.exists()){
				imageDir.mkdirs();
			}
		}
	}
	
	/**
	 * 把图片压缩成对应大小的压缩图片
	 * @param imageName
	 */
	public static boolean createImage(File file, String imageName){
		boolean isSuccess = true;
		isSuccess = FileKit.createImage(file, imageName);
		if(isSuccess){
			imageName += FileKit.getExtension(file);
			createCompressImg(imageName);
		}
		return isSuccess;
	}
	
	public static void createCompressImg(String imageName){
		try{
			createCompressImg2(imageName);
		}catch(Exception e){
			logger.error(e.getMessage());
		}
	}
	
	private static void createCompressImg2(String imageName) throws Exception{
		File original = new File(FileKit.getRootPath()+imageName);
		if(!original.exists())
			return;
		
		String compressImageName = replaceExtension(imageName,".jpg");
		
		int height = new Image(original).getHeight();
		File lastCompressImage = null;
		for(int compressLevel : compressLevel){
			if(height < compressLevel){
				lastCompressImage = null;
				continue;
			}
			File currentCompressImage = new File(getCompressFileSavePath(compressLevel)+compressImageName);
			Thumbnails
				.of(lastCompressImage==null?original:lastCompressImage)
				.height(compressLevel)
				.outputFormat("jpg")
				.toFile(currentCompressImage);
			lastCompressImage = currentCompressImage;
			logger.info("图片压缩 {}", compressImageName);
		}
	}
	
	/**
	 * 根据原图片和压缩高度,获取对应压缩图片,没有则返回原图
	 * @param file
	 * @param compressLevel
	 * @return
	 */
	public static File getCompressImg(String imageName, int level){
		String compressImageName = replaceExtension(imageName, ".jpg");
		for(int i= compressLevel.length-1; i>=0; i--){
			if(compressLevel[i]==level){
				File compressImg = new File(getCompressFileSavePath(level)+compressImageName);
				return compressImg.exists()?compressImg:FileKit.get(imageName);
			}
		}
		logger.error("图片不存在 {}", imageName);
		return null;
	}
	
	/**
	 * 替换文件的后缀名(.xxx)
	 * @param filePath
	 * @param extension
	 * @return
	 */
	private static String replaceExtension(String filePath, String extension){
		return filePath.replace(FileKit.getExtension(filePath), extension);
	}
	
	/**
	 * 根据原图保存的目录,给出对应压缩大小的图片保存目录
	 * @param compressLevel
	 * @return
	 */
	private static String getCompressFileSavePath(int compressLevel){
		return FileKit.getRootPath()+compressLevel+"P"+File.separator;
	}
}
