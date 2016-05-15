/**
 * 
 */
package com.sound.oil;


import java.util.regex.Pattern;

/**
 * @author Andy
 *
 */
public class OilFrom232 {
	/**
	 * 判断字符串是否为十进制数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	 } 
	
	public static double getOilBy232(short h, int oilBoxType) throws Exception {
		double volume = -1;
		double height = (double)h /1000;
		OilBox ob = OilBoxFactory.createOilBox(oilBoxType);
		volume = ob.getVolume(height);
		//System.out.printf("saving candata, oilBoxType into DB,  %.3f %.3f %d\n",height, volume, oilBoxType);
		return volume;
	}
	/**
	 * 通过传入串口上传数据和油箱类型获取油量
	 * @param rsdata
	 * @param oilBoxType
	 * @return
	 * @throws Exception
	 */
	public static double getOilBy232A(byte[] rsdata, int oilBoxType) throws Exception {
		if(rsdata.length != 37){
			throw new Exception("invalid array lengh " + rsdata.length);
		}
		int checksum = 0;
		for(int i=4;i<=31;i++)
		{
			checksum += rsdata[i];
		}
		String oilstring = new String(rsdata,"UTF-8");
		String [] oildata = oilstring.split(",");
		if(oildata.length != 8){
			throw new Exception("invalid array data " + oildata);
		}
		String total=oildata[7].substring(0, 4);
		if(!isNumeric(oildata[3])){
			throw new Exception("invalid oil data " + oildata[3]);
		}
		if(!isNumeric(total)){
			throw new Exception("invalid array data " + oildata);
		}
		else{
			if(checksum != Integer.parseInt(total)){
				throw new Exception("incorrect checksum " + total);
			}
		}
		
		int h= Integer.parseInt(oildata[3]);	
		double volume = -1;
		double height = (double)h /1000;
		OilBox ob = OilBoxFactory.createOilBox(oilBoxType);
		volume = ob.getVolume(height);
		System.out.printf("saving candata, oilBoxType into DB,  %.3f %.3f %d\n",height, volume, oilBoxType);
		return volume;
	}
	
	public static void testGetOilBy232() throws Exception {
//		byte[] b = new byte[]{'*', 'X','D',',', '0', '0', '0', '0', ',', '0', '1', ',', '1', '2', '7', '3', ',', '1', '2'
//				, '7', '5', ',', '1', '7', '2', '2', ',', '0', '2', '6', '8', ',', '1', '3', '7', '7', '#'};
//		byte[] b1 = new byte[]{'*', 'X','D',',', '0', '0', '0', '0', ',', '0', '1', ',', '1', '2', '7', '3', ',', '1', '2'
//				, '7', '6', ',', '1', '7', '2', '2', ',', '0', '2', '6', '8', ',', '1', '3', '7', '7', '#'};
//		byte[] b2 = new byte[]{'*', 'X','D',',', '0', '0', '0', '0', ',', '0', '1', ',', 'F', 'F', 'F', 'F', ',', '1', '2'
//				, '7', '5', ',', '1', '7', '2', '2', ',', '0', '2', '6', '8', ',', '1', '3', '7', '7', '#'};
//		double v = getOilBy232A(b, 1);
		
		double v;
		v = getOilBy232((short) 1000, 2);
		v = getOilBy232((short) 1000, 2);
		System.out.printf("saving candata, oilBoxType into DB,  %.3f %.3f %d\n",v, v, 1);
	}
}
