/**
 * 
 */
package com.sound.oil;

/**
 * @author Administrator
 *
 */
public class OilBox10 implements OilBox {

	/**
	 * 
	 */
	
	double l,w,h1,r;
	public OilBox10(double l, double w, double h, double r) {
		super();
		this.l = l;
		this.w = w;
		this.h1 = h;
		this.r = r;
	}

	public OilBox10() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.sound.oil.OilBox#getVolume(double)
	 */
	@Override
	public double getVolume(double h) throws Exception {
		if((h < 0) || (h > (h1+0.05))){
			throw new Exception("radius is " + r + "  invalid height:" + h );
		}
		double v = 0;
		if(h<=r){	
			 v =  (Math.PI * r * r/2 +(h-r)*Math.sqrt(2*r*h-h*h)
					+ r * r * Math.asin((h-r)/r)) * l + (w-2*r)*h*l;
		}else if((h > r)&&(h <= (h1-r))){
			v = (Math.PI * r * r/2 + (w-2*r)*r)*l + (h-r)*w*l;
		}else if((h>= h1-r) &&(h <=(h1+0.05))){
			double h2 =0;
			if(h<=h1){
				h2 = h-(h1-2*r);
			}else{
				h2=2*r;
			}
			v =  (Math.PI * r * r/2 +(h2-r)*Math.sqrt(2*r*h2-h2*h2)
					+ r * r * Math.asin((h2-r)/r)) * l + (w-2*r)*h2*l
					+w*l*(h1-2*r);
		}
		return v;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OilBox10 [l=" + l + ", w=" + w + ", h1=" + h1 + ", r=" + r
				+ "]";
	}

}
