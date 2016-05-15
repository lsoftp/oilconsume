package com.sound.oil;



import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class OilConsume {
	List<OilData> data = new ArrayList<OilData>();
	int interval = 1;
	//如果大于这个值，便认为有偷油发生
	double oilConsumeThresold = 800;
	
	public void createTestData() throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date start = df.parse("2016-01-12 00:00:00");
		
		this.interval = 5*60;
		
		Random r = new Random();
		// 生成每隔 5 分钟的一天数据
		for(int i=0;i<=12*24;i++) {
			OilData od = new OilData(start.getTime() + i* 5*60000, Math.abs(r.nextDouble()*7) );
			data.add(od);
		}
	}
	
	/**
	 * print oil data
	 */
	public void print() {
		for(int i=0;i<data.size();i++) {
			System.out.println(data.get(i));
		}
	}
	
	// 找到第一个 <t0 的时间点
	protected int findIdxBefore(Date t0) throws Exception {
		int i;
		for(i=0;i<data.size();i++) {
			OilData od = data.get(i);
			if(od.getTime().getTime()> t0.getTime()) { 
				if(i>0) {
					i--;
				}
				return i;
			}
			else if (od.getTime().getTime()== t0.getTime()) {
				return i;
			}
		}
		throw new Exception("没有此日期的数据");
	}

	/**
	 * 找到第一个 >t1 的时间点
	 * @param t1
	 * @return
	 * @throws Exception
	 */
	protected int findIdxAfter(Date t1) throws Exception {
		int i;
		for(i=data.size()-1;i>=0;i--) {
			OilData od = data.get(i);
			if(od.getTime().getTime()< t1.getTime()) { 
				if(i<data.size()-1) {
					i++;
				}
				return i;
			}
			else if (od.getTime().getTime()== t1.getTime()) {
				return i;
			}
		}
		throw new Exception("没有此日期的数据");
	}
	
	public void checkDateInRange(Date t) throws Exception {
		if(this.data.size()<1) {
			throw new Exception("没有此日期的数据");
		}
		if(t.getTime() < this.data.get(0).getTime().getTime()) {
			throw new Exception("没有此日期的数据");
		}
		if(t.getTime() > this.data.get(this.data.size()-1).getTime().getTime()) {
			throw new Exception("没有此日期的数据");
		}
	}
	
	/**
	 * calculate oil consumation in the date range
	 * @param ob
	 * @param t0
	 * @param t1
	 * @return
	 * @throws Exception
	 */
	public double calOilConsume(OilBox ob, String t0, String t1) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date start = df.parse(t0);
		Date end = df.parse(t1);
		
		double ret = calOilConsume(ob, start, end);
		return ret;
	}

	
	
	/**
	 * calculate oil consume
	 * t1 must > t0
	 * @param t0
	 * @param t1
	 * @return
	 */
	public double calOilConsume(OilBox ob, Date t0, Date t1) throws Exception {
		if(this.data.size()<1) {
			return 0;
		}
		checkDateInRange(t0);
		checkDateInRange(t1);
		
		int s = findIdxBefore(t0);
		int e = findIdxAfter(t1);
		
		if(s==e) {
			return 0;
		}

		double consume = 0;
		OilData od0, od1;
		for(int i=s+1;i<=e;i++) {
			od0 = data.get(i-1);
			od1 = data.get(i);
			if(od1.h < od0.h) {
				consume += ob.getVolume(od0.h)-ob.getVolume(od1.h);
			}
		}
		
		//double consumedVolume = ob.getVolume(consume);
		
		System.out.println("cv:" + consume + " " + s + " " + e);
		
		long s0 = this.data.get(s).time.getTime();
		long s1 = this.data.get(e).time.getTime();
		if(s0==s1) {
			return 0;
		}
		
		double t = (double)(s1-s0)/1000/3600;
		double ret = consume / t;
		return ret;
	}

	/**
	 * @param ob
	 * @param t0
	 * @param t1
	 * @return
	 * @throws Exception
	 */
	public double calOilAdded(OilBox ob, Date t0, Date t1) throws Exception {
		if(this.data.size()<1) {
			return 0;
		}
		checkDateInRange(t0);
		checkDateInRange(t1);
		
		int s = findIdxBefore(t0);
		int e = findIdxAfter(t1);
		
		if(s==e) {
			return 0;
		}

		double added = 0;
		OilData od0, od1;
		for(int i=s+1;i<=e;i++) {
			od0 = data.get(i-1);
			od1 = data.get(i);
			if(od1.h > od0.h) {
				added += ob.getVolume(od1.h)-ob.getVolume(od0.h);
			}
		}
		
		
		System.out.println("cv:" + added + " " + s + " " + e);
		
		long s0 = this.data.get(s).time.getTime();
		long s1 = this.data.get(e).time.getTime();
		if(s0==s1) {
			return 0;
		}
		
		//double t = (double)(s1-s0)/1000/3600;
		//double ret = added / t;
		return added;
	}

	/**
	 * calculate oid added during the time span
	 * @param ob
	 * @param t0
	 * @param t1
	 * @return
	 * @throws ParseException
	 * @throws Exception
	 */
	public double calOilAdd(OilBox ob, String t0, String t1)
			throws ParseException, Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date start = df.parse(t0);
		Date end = df.parse(t1);
		double ret = calOilAdded(ob, start, end);
		return ret;
	}




	


	public static void main(String[] args) throws Exception {
//		testGetOilByCANData();



				 
		try{

			OilFromCAN.testGetOilByCANData();
			OilFrom232.testGetOilBy232();
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
		
		OilConsume oc = new OilConsume();
		oc.createTestData();
		//oc.print();
		
		
		OilBox ob = OilBoxFactory.createOilBox(1);
		
		double x = oc.calOilConsume(ob, "2016-01-12 00:00:00", "2016-01-12 00:11:00");
		System.out.printf("%s  oilbox oil consume: %.3f l/h\n",ob,  x);
		x = oc.calOilAdd(ob, "2016-01-12 00:00:00", "2016-01-13 00:00:00");
		System.out.printf("%s  oilbox oil added: %.3f \n",ob,  x);
		ob = OilBoxFactory.createOilBox(4);
		double volume = ob.getVolume(0.5);
		System.out.printf("saving candata, oilBoxType into DB,  %.3f %.3f %d\n",0.5, volume, 4);
		volume = ob.getVolume(1);
		System.out.printf("saving candata, oilBoxType into DB,  %.3f %.3f %d\n",1.0, volume, 4);
		volume = ob.getVolume(1.5);
		System.out.printf("saving candata, oilBoxType into DB,  %.3f %.3f %d\n",1.5, volume, 4);
		volume = ob.getVolume(2);
		System.out.printf("saving candata, oilBoxType into DB,  %.3f %.3f %d\n",2.0, volume, 4);
		volume = ob.getVolume(2.5);
		System.out.printf("saving candata, oilBoxType into DB,  %.3f %.3f %d\n",2.5, volume, 4);
		volume = ob.getVolume(3);
		System.out.printf("saving candata, oilBoxType into DB,  %.3f %.3f %d\n",3.0, volume, 4);
		double v= Math.acos(0.5)*1*1*10-Math.tan(Math.acos(0.5))*0.5*0.5*10+1*0.5*10;
		System.out.printf("saving candata, oilBoxType into DB,  %.3f %.3f %d\n",0.5, v, 4);
	}
	
}

