package com.sound.oil;

public class IrregularOilBox implements OilBox {
	double area;
	
	
	public IrregularOilBox() {
		this(1);
		// TODO Auto-generated constructor stub
	}


	public IrregularOilBox(double area) {
		super();
		this.area = area;
	}


	@Override
	public double getVolume(double h) throws Exception {
		if((h < 0)|| (h > 10)){
			throw new Exception("invalid height:" + h);
		}
			
		double v = area * h;
		return v;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IrregularOilBox [area=" + area + "]";
	}

}
