package com.sound.oil;

/**
 * 圆筒油箱 
 */
public class CylinderOilBox implements OilBox {
	
	/**
	 * radius, 单位:分米
	 */
	double r, len;  
	
	public CylinderOilBox(double radius, double l) {
		this.r = radius;
		len = l;
	}

	
	@Override
	public double getVolume(double h) throws Exception {
		if((h < 0) || (h > 2*r)){
			throw new Exception("radius is " + r + "  invalid height:" + h );
		}
			
		double v =  (Math.PI * r * r/2 +(h-r)*Math.sqrt(2*r*h-h*h)
					+ r * r * Math.asin((h-r)/r)) * len;
		return v;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CylinderOilBox [r=" + r + ", len=" + len + "]";
	}
	
}
