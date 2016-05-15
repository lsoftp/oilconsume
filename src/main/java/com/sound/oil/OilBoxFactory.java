package com.sound.oil;

public class OilBoxFactory {

	// oilbox type,按照底盘类型来分
	public static final int DFA1070SDJ41D6=1;
    public static final int DFA1070SJ12N5=2;
    public static final int DFA1070SJ35D6=3;
    public static final int DFA1080SJ11D3=4;
    public static final int DFD1O32GJ=5;
    public static final int DFL1100BX7=6;
    public static final int DFL1120B3=7;
    public static final int DFL1160BX1V=8;
    public static final int DFL1160BX4=9;
    public static final int DFL1160BX5=10;
    public static final int DFL1160BXB=11;
	public static final int DFL1250A11=12;
    public static final int DFL1250A13=13;
    public static final int EQ1160GD5NJ=14;
    public static final int EQ1160ZZ4GJ=15;
    public static final int EG3160GNJ_50=16;
    public static final int HFC1073P83K1C3=17;
    public static final int QL1060A1HAY=18;
    public static final int QL10703HARY=19;
    public static final int QL1070A1ARY=20;
    public static final int QL11009KARY=21;
    public static final int QL11009LARY=22;
    public static final int SC1022DB4N4=23;
    public static final int SC1026DAN4=24;
    public static final int SC1034DD44=25;

	
	

	/**
	 * @param oilBoxType
	 * @return
	 * @throws Exception
	 */
	public static OilBox createOilBox(int oilBoxType) throws Exception {
		OilBox ob=null;
		
		switch(oilBoxType) {
		case DFA1070SDJ41D6:
			ob = new BasalAreaOilBox(60);
			break;  
		case DFA1070SJ12N5:
			ob = new CylinderOilBox(2.5, 10);
			break;
		case DFA1070SJ35D6:
			ob = new IrregularOilBox(10);
			break;
		case DFA1080SJ11D3:
			ob = new OilBox10(10,3,10,1);
			break;
		case DFD1O32GJ:  
		case DFL1100BX7: 
		case DFL1120B3:  
		case DFL1160BX1V:
		case DFL1160BX4: 
		case DFL1160BX5:
			ob = new OilBox10(5.79,6.11,4.9,1.3);
			break;
		case DFL1160BXB:
		case DFL1250A11:
			ob = new OilBox10(11.55,6.07,5.86,1.225);
			break;
		case DFL1250A13:
		case EQ1160GD5NJ:
		case EQ1160ZZ4GJ:
		case EG3160GNJ_50:
		case HFC1073P83K1C3:
		case QL1060A1HAY:
		case QL10703HARY:
		case QL1070A1ARY:
		case QL11009KARY:
		case QL11009LARY:
		case SC1022DB4N4:
		case SC1026DAN4:
		case SC1034DD44:
			ob = new BasalAreaOilBox(25.84538662);
			break;
		default:
			throw new Exception("invalid Oil box type:" + oilBoxType );
		}
		return ob;
	}




}
