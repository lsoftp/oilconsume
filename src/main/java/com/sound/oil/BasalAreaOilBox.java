package com.sound.oil;

/**
 * 油箱  底面积x高 
 */
public class BasalAreaOilBox implements OilBox {

	/**
	 * 底面积, 单位:平方分米
	 */
	double basalArea;  
	
	public BasalAreaOilBox() {
		this(1);
	}
	
	public BasalAreaOilBox(double basalArea) {
		this.basalArea = basalArea;
	}

	@Override
	public double getVolume(double h) throws Exception {
		if((h < 0)|| (h > 10)){
			throw new Exception("invalid height:" + h);
		}
			
		double v = this.basalArea * h;
		return v;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BasalAreaOilBox [basalArea=" + basalArea + "]";
	}
}
