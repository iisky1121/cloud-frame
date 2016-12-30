/**
 * @Title: ExcelKit.java
 * 
 * @author liuyihang
 * @date 2014-7-21 下午5:03:57
 * @version V1.0
 */

package com.jfinal.ext.kit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * @ClassName: ExcelKit
 * @Description: TODO
 * @author liuyihang
 * @date 2014-7-21 下午5:03:57
 * 
 */

public class ExcelKit {
	private static final int maxRowCount = 60001;//不能够超过Excel的最大容量
	/**
	 * @throws IOException 
	 * @throws BiffException 
	 * excelToList(从前台文件流读取excel文件内容到List)
	 * 
	 * @Title: excelToList
	 * @author: liuyihang
	 * @date:2014-7-21
	 * @param @param fis 文件流
	 * @param @return
	 * @param @throws Exception
	 * @return List 返回类型
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes"})
	public static List readExcelToList(InputStream is) throws BiffException, IOException {
		return readExcelToList(is, 0);
	}
	@SuppressWarnings({ "rawtypes"})
	public static List readExcelToList(InputStream is, int sheetNum) throws BiffException, IOException {
		Workbook rwb = Workbook.getWorkbook(is);
		return toList(rwb, sheetNum);
	}
	
	@SuppressWarnings("rawtypes")
	public static List readExcelToList(File file) throws BiffException, IOException {
		return readExcelToList(file, 0);
	}
	@SuppressWarnings("rawtypes")
	public static List readExcelToList(File file, int sheetNum) throws BiffException, IOException {
		Workbook rwb = Workbook.getWorkbook(file);
		return toList(rwb, sheetNum);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List toList(Workbook rwb, int sheetNum){
		List list = new ArrayList();
		Sheet sheet = null;
		Cell cell = null;

		if(rwb != null){
			sheet = rwb.getSheet(sheetNum);
			// 行数(表头的目录不需要，从1开始)
			for (int i = 0; i < sheet.getRows(); i++) {
				// 创建一个数组 用来存储每一列的值
				String[] str = new String[sheet.getColumns()];
				// 列数
				for (int j = 0; j < sheet.getColumns(); j++) {
					// 获取第i行，第j列的值
					cell = sheet.getCell(j, i);
					str[j] = cell.getContents();
				}
				// 把刚获取的列存入list
				list.add(str);
			}
		}
		return list;
	}

	/**
	 * writeListToExcel(把List的内容写到一个Excel中)
	 * 
	 * @Title: writeListToExcel
	 * @author: liuyihang
	 * @date:2014-7-21
	 * @param
	 * @return void 返回类型
	 * @throws
	 */
	public static void writeListToExcel(String fileName,List<Object[]> list,HttpServletResponse response) throws Exception {
		// 设这输出的类型和文件格式
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		// 设置文件名和并且解决中文名不能下载
		response.addHeader("Content-Disposition", "attachment;   filename=\""+ new String(fileName.getBytes(), "iso8859-1") + "\"");
		// 创建输出流
		OutputStream os = response.getOutputStream();

		WritableWorkbook wbook = Workbook.createWorkbook(os);
		// 生成名为“sheet1”的工作表，参数0表示这是第一页
		int listSize = list.size();
		int sheetNum = 0;
		
		WritableSheet sheet = wbook.createSheet("sheet"+(sheetNum+1), sheetNum);
		
		for(int i=0;i<listSize;i++){
			int startIndex = i%maxRowCount;
			if(startIndex == 0){
				if(i != 0){
					sheetNum++;
					sheet = wbook.createSheet("sheet"+(sheetNum+1), sheetNum);
				}
				addSheetRow(sheet, list.get(0), 0);
			}
			if(i != 0){
				addSheetRow(sheet, list.get(i), startIndex+sheetNum);
			}
		}
		
		//写入数据并关闭文件   
		wbook.write();   
		wbook.close();
		os.flush();
		os.close();
	}
	
	private static void addSheetRow(WritableSheet sheet, Object[] datas, int i) throws RowsExceededException, WriteException{
		for(int j=0;j<datas.length;j++){ 
			sheet.addCell(toFmt(j, i, datas[j]));
		}
	}
	
	/**
	 * 自动转换成对应格式
	 * @param c
	 * @param r
	 * @param object
	 * @return
	 */
	private static WritableCell toFmt(int c, int r, Object object){
		if(object == null || StrKit.isBlank(object.toString())){
			return new Label(c, r, "");
		}
		Class<?> classType = object.getClass();
		if(classType.equals(Integer.class)){
			return new Number(c, r, (Integer)object);
		}
		else if(classType.equals(Long.class)){
			return new Number(c, r, (Long)object);
		}
		else if(classType.equals(Double.class)){
			return new Number(c, r, (Double)object);
		}
		else if(classType.equals(BigDecimal.class)){
			return new Number(c, r, ((BigDecimal)object).doubleValue());
		}
		else if(classType.equals(Date.class) || classType.equals(Timestamp.class)){
			return new Label(c, r, DateKit.toDateTimeStr((Date)object));
		}
		return new Label(c, r, object.toString());
	}
	
	/**
	  * checkModel(检查excel文件模版)
	  * @Title: checkModel
	  * @author: liuyihang
	  * @date:2014-7-22
	  * @param @param mColumnName 模版表头
	  * @param @param eColumnName Excel文件表头
	  * @param @return
	  * @return boolean    返回类型
	  * @throws
	  */
	public static boolean checkModel(String[] mColumnName,String[] eColumnName ){
		int mlen=mColumnName.length;
		int elen=eColumnName.length;
		int len=mlen>elen?elen:mlen;//防止数组越界
		if(mlen!=elen){
			return false;
		}
		for(int i=0;i<len;i++){
			if(eColumnName != null && !mColumnName[i].equals(eColumnName[i])){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * renderExcel(把List的内容写到一个Excel中，并下载)
	 * 
	 * @Title: renderExcel
	 * @author: liuyihang
	 * @date:2016-06-27
	 * @param
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void renderExcel(HttpServletResponse response,String fileName, String[] titles, String[] keys, List list){
    	//String[] keys = new String[]{"refundCount","exceptionCount","refundBalanceSum","payBalanceCount","balanceCount","singleSum","refundBalanceCount","billDate","paySum","balanceSum","statisticId","createTime","payCount","offsetSum","singleCount","payBalanceSum","offsetCount","exceptionSum"};
        //String[] titles = new String[]{"字段1","字段2","字段3","字段4","字段5","字段6","字段7","字段8","字段9","字段10","字段11","字段12","字段13","字段14","字段15","字段16","字段17","字段18"};
        try {
        	List<Object[]> rows = new ArrayList<Object[]>();
        	rows.add(titles);

        	Map<String,Object> map = new HashMap<String, Object>();
        	for(Object object : list){
        		if(object instanceof Map){
        			map = (Map<String, Object>) object;
        		}
        		else if(object instanceof Model){
        			map = ModelKit.toMap((Model)object);
        		}
        		else if(object instanceof Record){
        			map = RecordKit.toMap((Record)object);
        		}
        		else{
        			throw new IllegalArgumentException(String.format("%s类型不支持，暂时只支持Map<String,Object>,Model和Record类型", object.getClass().getName()));
        		}
        		List<Object> row = new ArrayList<Object>();
        		for(String key : keys){
        			if(StrKit.isBlank(key)){
        				row.add("");
        			}
        			else{
        				row.add(map.get(key)==null?"":map.get(key));
        			}
        		}
        		rows.add(row.toArray());
        	}
        	if(fileName != null && fileName.indexOf(".xls") == -1){
        		fileName +=".xls";
        	}
			writeListToExcel(fileName, rows, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
