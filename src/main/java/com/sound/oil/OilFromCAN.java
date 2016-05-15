package com.sound.oil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;



public class OilFromCAN {
	byte[] buffer= new byte[45];
	
	int index = 0;
	{
	     Arrays.fill(buffer, (byte) 0 );
	};

	/**
	 * Get oil volume by CAN data frame of CAN frame   received 
	 * @param candata
	 * @param oilBoxType
	 * @return
	 * @throws Exception
	 */
	public static double getOilByCANData3(byte[] candata, int oilBoxType) throws Exception {
		if((candata.length > 8)||(candata.length <4)){
			throw new Exception("invalid array lengh " + candata.length);
		}
		ByteBuffer bb = ByteBuffer.wrap(candata);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		double volume = -1;
		short h = bb.getShort();
		double height = (double)h /1000;
		OilBox ob = OilBoxFactory.createOilBox(oilBoxType);
		volume = ob.getVolume(height);
		System.out.printf("saving candata, oilBoxType into DB,  %.3f %.3f %d\n",height, volume, oilBoxType);
		return volume;
	}

	public static double getOilByCANData(short h, int oilBoxType) throws Exception {
		double volume = -1;
		double height = (double)h /1000;
		OilBox ob = OilBoxFactory.createOilBox(oilBoxType);
		volume = ob.getVolume(height);
		//System.out.printf("saving candata, oilBoxType into DB,  %.3f %.3f %d\n",height, volume, oilBoxType);
		return volume;
	}
	/**
	 * Get oil volume by CAN data frame of Dianyingpu  DS1309B received 
	 * @param candata
	 * @param oilBoxType
	 * @return
	 * @throws Exception
	 */
	public  double getOilByCANData1(byte[] candata, int oilBoxType) throws Exception {
		if(candata.length > 8){
			throw new Exception("invalid array lengh " + candata.length);
		}
		int startIndex = -1;
		int endIndex = -1;
		double volume = -1;
		byte [] oilbuf= new byte[37];
		if(index >=37)
		{
			index = 0;
		}
		System.arraycopy(candata, 0, buffer, index, candata.length);
		index += candata.length;
		if(index < 37){
			return -1;
		}
		String stringbuf=new String(buffer,0,index,"UTF-8");
		startIndex = stringbuf.indexOf("*XD");
		if(-1 == startIndex){
			index = 0;
			return -1;
		}
		else if(startIndex > 0){
			index = index - startIndex;
			System.arraycopy(buffer, startIndex, buffer, 0, index );
			
			stringbuf=new String(buffer,0, index,"UTF-8");
			startIndex = 0;
		}
//		startIndex = stringbuf.indexOf("*XD");	
		endIndex = stringbuf.indexOf("#");
		if(-1 == endIndex) {
			return -1;
		}
		if((endIndex - startIndex) == 36){
			System.arraycopy(buffer, 0, oilbuf, 0, 37);
			int sum = 0;
			for(int i = 4; i < 32;i++)
			{
				sum += oilbuf[i];
			}
			stringbuf= new String(oilbuf,"UTF-8");
			String [] oilStrings = stringbuf.split(",");
			if(oilStrings.length != 8) {
				System.arraycopy(buffer, endIndex+1, buffer, 0, index - endIndex-1);
				index = index - endIndex - 1;
				throw new IOException("invalid can data: " + stringbuf);
			}
			String checksumstr = oilStrings[7].substring(0,4);
			int checksum = Integer.parseInt(checksumstr,10);
			if(checksum !=sum){
				System.arraycopy(buffer, endIndex+1, buffer, 0, index - endIndex-1);
				index = index - endIndex - 1;
				throw new Exception("check sum not right " + checksum);
				
			}
			
			int h;
			if(oilStrings[3].equals("FFFF")){
				h = 0;
			}
			else
			{
			 h = Integer.parseInt(oilStrings[3],10);
			}
			double height = (double)h/1000;
			OilBox ob = OilBoxFactory.createOilBox(oilBoxType);
			volume = ob.getVolume(height);
			System.arraycopy(buffer, endIndex+1, buffer, 0, index - endIndex-1);
			index = index - endIndex - 1;
		}
		else
		{
			System.arraycopy(buffer, endIndex+1, buffer, 0, index - endIndex-1);
			index = index - endIndex - 1;
			throw new Exception("invalid length of oil string  " + (endIndex - startIndex));
		}

		return volume;
	}
	
	/**
	 * Get oil volume by CAN data frame of Dianyingpu  DS1309B received 
	 * @param candata
	 * @param oilBoxType
	 * @return
	 * @throws Exception
	 */
	public  double getOilByCANData2(byte[] candata, int oilBoxType) throws Exception {
		if(candata.length > 8){
			throw new Exception("invalid array lengh " + candata.length);
		}

		double volume = -1;
		int begin=-1, end=0;
		int status = 0;
		byte [] oilbuf= new byte[37];

		if(index <=37){
			System.arraycopy(candata, 0, buffer, index, candata.length);
			index += candata.length;
		}
		if(index < 37){
			return -1;
		}
		for(int i= 0; i<index; i++)
		{
			if(buffer[i]==(byte)'*') {
				status = 1;
				begin = i;
			}
			else if(status==1) {
				if(buffer[i]==(byte)'X') {
					status = 2;
				}
				else {
					status = 0;
				}
			}
			else if(status==2) {
				if(buffer[i]==(byte)'D') {
					status = 3;
				}
				else {
					status = 0;
				}
			}
			else if(status==3) {
				if (buffer[i]==(byte)'#') {
					status = 4;
					end = i+1; // end 指向消息结束的下一个字节， 消息长度= end-begin
					String stringbuf;
					if((end - begin)<= 37){
						System.arraycopy(buffer, begin, oilbuf, 0, end-begin);
						stringbuf=new String(oilbuf,0,end-begin,"UTF-8");
						System.out.println(" oil can data: ...." + stringbuf);
					}
					
					System.arraycopy(buffer, end, buffer, 0, index - end);
					index = index - end;
					
					if((end - begin)==37){
						int sum = 0;
						for(int j = 4; j < 32;j++)
						{
							sum += oilbuf[j];
						}

						stringbuf= new String(oilbuf,"UTF-8");
						String [] oilStrings = stringbuf.split(",");
						if(oilStrings.length != 8) {
							
							throw new IOException("invalid can data: " + stringbuf);
						}
						String checksumstr = oilStrings[7].substring(0,4);

						int checksum = Integer.parseInt(checksumstr,10);
						if(checksum !=sum){
							throw new Exception("check sum not right " + checksum);
						}

						
						
						int h;
						if(oilStrings[3].equals("FFFF")){
							h = 0;
						}
						else
						{
						 h = Integer.parseInt(oilStrings[3],10);
						}
						double height = (double)h/1000;
						OilBox ob = OilBoxFactory.createOilBox(oilBoxType);
						volume = ob.getVolume(height);
						return volume;
					}
					else
					{
						throw new Exception("invalid length of oil string  " + (end - begin));
					}

				}
				else {
					// nop
				}
			}
		
		}
		if(status==0) { // 没有找到*
			index = 0;
			return -1;
		}
		else if(status>=1 && status<=3 ) { // 把*号开始前的都删除
			System.arraycopy(buffer, begin, buffer, 0, index -begin);
			index = index - begin;
		}
		else if(status!=4) {
			throw new RuntimeException("invalid status");
		}
			
	
		return volume;
	}
	
	public static void testGetOilByCANData() throws Exception {
		
		double v = getOilByCANData((short) 1000,OilBoxFactory.DFL1160BX4 );
		System.out.println(v);
		v = getOilByCANData((short) 1000, 2);
		System.out.println(v);

	}
	
	public  void testGetOilByCANData1() throws Exception {
		byte[][] b = new byte[][]{
				{(byte)'1', (byte)'*', (byte)'X', (byte)'D', (byte)'1', (byte)'0'},
				{(byte)'#'},
				{(byte)'*', (byte)'X', (byte)'D', (byte)',', (byte)'0', (byte)'0', (byte)'0', (byte)'0' },
				{(byte)',', (byte)'0', (byte)'1', (byte)',', (byte)'1', (byte)'2', (byte)'7', (byte)'3' },
				{(byte)',', (byte)'1', (byte)'2', (byte)'7', (byte)'5', (byte)',', (byte)'1', (byte)'7' },
				{(byte)'2', (byte)'2', (byte)',', (byte)'0', (byte)'2', (byte)'6', (byte)'8', (byte)',' },
				{(byte)'1', (byte)'3', (byte)'7', (byte)'7', (byte)'#',(byte)'*', (byte)'X', (byte)'D'},
				{(byte)'*', (byte)'X', (byte)'D',(byte)',', (byte)'0', (byte)'0', (byte)'0', (byte)'0' },
				{(byte)',', (byte)'0', (byte)'1', (byte)',', (byte)'1', (byte)'2', (byte)'7', (byte)'3' },
				{(byte)',', (byte)'1', (byte)'2', (byte)'7', (byte)'5', (byte)',', (byte)'1', (byte)'7' },
				{(byte)'2', (byte)'2', (byte)',', (byte)'0', (byte)'2', (byte)'6', (byte)'8', (byte)',' },
				{(byte)'1', (byte)'3', (byte)'7', (byte)'7', (byte)'#', (byte)'0'},
				{(byte)'*', (byte)'X', (byte)'D', (byte)',', (byte)'0', (byte)'0', (byte)'0', (byte)'0' },
				{(byte)',', (byte)'0', (byte)'1', (byte)',', (byte)'1', (byte)'2', (byte)'7', (byte)'3' },
				{(byte)',', (byte)'1', (byte)',', (byte)'7', (byte)'5', (byte)',', (byte)'1', (byte)'7' },
				{(byte)'2', (byte)'2', (byte)',', (byte)'0', (byte)'2', (byte)'6', (byte)'8', (byte)',' },
				{(byte)'1', (byte)'3', (byte)'7', (byte)'7', (byte)'#', (byte)'0', (byte)'0', (byte)'0' },
				{(byte)'*', (byte)'X', (byte)'D', (byte)',', (byte)'0', (byte)'0', (byte)'0', (byte)'0' },
				{(byte)',', (byte)'0', (byte)'1', (byte)',', (byte)'1', (byte)'2', (byte)'7', (byte)'3' },
				{(byte)',', (byte)'1', (byte)',', (byte)'7', (byte)'5', (byte)',', (byte)'1', (byte)'7' },
				{(byte)'2', (byte)'2', (byte)',', (byte)'0', (byte)'2', (byte)'6', (byte)'8', (byte)',' },
				{(byte)'1', (byte)'#', (byte)'7', (byte)'7', (byte)'#', (byte)'0', (byte)'0', (byte)'0' },
				{(byte)',', (byte)'*', (byte)'X', (byte)'D',  (byte)'1', (byte)'2', (byte)'#', (byte)'3' },
				{(byte)',', (byte)'1', (byte)'3', (byte)'7', (byte)'5', (byte)',', (byte)'1', (byte)'7' },
				{(byte)'2', (byte)'2', (byte)',', (byte)'0', (byte)'2', (byte)'6', (byte)'8', (byte)',' },
				{(byte)'*', (byte)'*', (byte)'X', (byte)'D',  (byte)'1', (byte)'2', (byte)'#', (byte)'*' },
				{(byte)'X', (byte)'D', (byte)'#', (byte)'7', (byte)'5', (byte)',', (byte)'1', (byte)'7' },
				{(byte)'2', (byte)'2', (byte)',', (byte)'*', (byte)'X', (byte)'D', (byte)'8', (byte)'#' },
				{ (byte)'*', (byte)'X', (byte)'D', (byte)'8', (byte)'#', (byte)'2', (byte)'7', (byte)'3' },
				{(byte)',', (byte)'1', (byte)'3', (byte)'7', (byte)'5', (byte)',', (byte)'1', (byte)'7' },
				{(byte)'2', (byte)'2', (byte)',', (byte)'0', (byte)'2', (byte)'6', (byte)'8', (byte)',' },
				{(byte)',', (byte)'0', (byte)'1', (byte)',', (byte)'1', (byte)'2', (byte)'7', (byte)'3' },
				{(byte)'*', (byte)'X', (byte)'D',  (byte)'1', (byte)'2', (byte)'#', (byte)'1', (byte)'7' },
				{(byte)'2', (byte)'2', (byte)',', (byte)'0', (byte)'2', (byte)'6', (byte)'8', (byte)',' },
				{(byte)'1', (byte)'X', (byte)'D', (byte)',', (byte)'0', (byte)'0', (byte)'0', (byte)'0' },
				{(byte)'1', (byte)'X', (byte)'D', (byte)',', (byte)'0', (byte)'0', (byte)'0', (byte)'0' },
				{(byte)'1', (byte)'X', (byte)'D', (byte)',', (byte)'0', (byte)'0', (byte)'0', (byte)'0' },
				};
		byte[][] b1 = new byte[][]{
		{0x2a, 0x58, 0x44 ,0x2c, 0x30 ,0x30, 0x30, 0x30}, 
		{0x2c ,0x30 ,0x31, 0x2c, 0x46 ,0x46, 0x46, 0x46}, 
		{0x2c, 0x30, 0x30,0x30,0x30, 0x2c, 0x30, 0x31}, 
		{0x33 , 0x36 , 0x2c , 0x30 , 0x31 , 0x39 , 0x38 , 0x2c}, 
		{0x31 ,0x34 ,0x33 ,0x37 ,0x23 },
		{0x2a ,0x58,0x44 ,0x2c ,0x30 ,0x30 ,0x30 ,0x30}, 
		{0x2c ,0x30 ,0x31 ,0x2c ,0x31 ,0x39 ,0x37 ,0x32}, 
		{0x2c ,0x31 ,0x39 ,0x37 ,0x32 ,0x2c ,0x31 ,0x35}, 
		{0x32 ,0x34 ,0x2c ,0x30 ,0x31 ,0x39 ,0x38 ,0x2c}, 
		{0x31 ,0x33 ,0x38 ,0x39 ,0x23}, 
		{0x2a ,0x58 ,0x44 ,0x2c ,0x30 ,0x30 ,0x30 ,0x30}, 
		{0x2c ,0x30 ,0x31 ,0x2c ,0x31 ,0x39 ,0x37 ,0x32}, 
		{0x2c ,0x31 ,0x39 ,0x37 ,0x39 ,0x2c ,0x30 ,0x36}, 
		{0x37 ,0x32,0x2c ,0x30 ,0x31 ,0x39 ,0x39 ,0x2c}, 
		{0x31 ,0x34 ,0x30 ,0x30 ,0x23}
		};
		for(int i =0; i< 15;i++)
		{
			try {
				double v = getOilByCANData2(b1[i], OilBoxFactory.DFA1070SDJ41D6);
				System.out.println(v);
			} catch (Exception e) {


				System.out.println(e.getMessage());
			}
			
		}
		for(int i =0; i< 37;i++)
		{
			try {
				double v = getOilByCANData2(b[i], OilBoxFactory.DFA1070SJ12N5);
				System.out.println(v);
			} catch (Exception e) {

				//index = 0;
				System.out.println(e.getMessage());
			}
			
		}

	}
}
