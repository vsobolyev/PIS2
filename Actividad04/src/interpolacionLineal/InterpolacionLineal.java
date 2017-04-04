package interpolacionLineal;

import java.util.Locale;
import polinomio.Polinomio;
/**
 * El numero de decimales que se usan durante las operaciones es 15 (16 se usa para redondeo)
 * @author Usuario
 *
 */
public class InterpolacionLineal {
	private Polinomio polinomio;			//polinomio que alberga la funcion
	private int precision = 5; 				//numero de digitos de precisi�n a mostrar.
	private Double x1, x2;					//valores del intervalo [x1,x2]
	private Double xR = Double.NaN;			//Variable que almacenara la evaluacion de la ecuacion.
	private Double fXr = Double.NaN;		//evaluaci�n fxR = f(xR)
	private Double E = Double.NaN;			//Valor de precision
	private Integer it = 0; 				//numero de iteraci�nes
	private Double error = Double.NaN; 		//precision obtenida
	private int iMax = 40; 					//numero maximo de iteraci�nes para el caso de divergencia

	
	public InterpolacionLineal(){
		initDefaultFunc();
	}
	public InterpolacionLineal(Double x1, Double x2, Double E){
		initDefaultFunc();
		this.setParameters(x1, x2, E);
	}
	
	/**
	 * Establece el error admisible
	 * @param E - valor del error admisible
	 */
	public void setError(Double E){
		this.E = Math.abs(E);
		this.verifyPrecision();
	}
	public Double getError(){
		return this.E;
	}
	
	/**
	 * Establece el intervalo del calculo [x1,x2]
	 * @param x1 - valor a la izquierda
	 * @param x2 - valor a la derecha
	 */
	public void setIterval(Double x1, Double x2){
		initDefaultFunc();
		this.x1 = x1;
		this.x2 = x2;
	}
	
	public String getPolinomio(){
		return this.polinomio.toString();
	}
	
	/**
	 * Returns an array of both positions [x1,x2]
	 * @return Double[]{x1,x2}
	 */
	public Double[] getInterval(){
		return new Double[]{x1,x2};
	}
	
	private void verifyPrecision(){
		//para lograr representacion no cientifica, mostramos numero con ceros a la derecha y los borramos
		String[] p = ((String.format(Locale.US, "%.32f", this.E)).replaceAll("0+$", "")).split("\\.");
		if(p.length>1){
			if(p[1].length() > this.precision){
				this.precision = p[1].length();
			}
		}
	}
	/**
	 * set precision para la representaci�n de los datos
	 * @param precision - entero que indica el numero de digitos de precision
	 */
	public void setPrecision(int precision){
		this.precision = precision;
	}
	public int getPrecision(){
		return this.precision;
	}
	
	/**
	 * Establece el intervalo [x1,x2] y el error admisible
	 * @param x1 - valor a la izquierda
	 * @param x2 - valor a la derecha
	 * @param E - valor del error admisible
	 */
	public void setParameters(Double x1, Double x2, Double E){
		this.setIterval(x1, x2);
		this.setError(E);
	}
	
	/**
	 * Realiza la busqueda de la ra�z
	 */
	public void calcular(){
		it = 0;
		Double fX1 = 0.0;
		Double fX2 = 0.0;
		error = 0.0;
		
		while (it < iMax){
			fX1 = this.evaluatePolinoimo(this.x1);
			fX2 = this.evaluatePolinoimo(this.x2);
			
			xR = (x2 - ((x2-x1)*fX2) / (fX2-fX1)) ;

			fXr = this.evaluatePolinoimo(xR);
			
			error = Math.abs(fXr);

			//System.out.println(this.toString());
			
			if(error <= E){
				//System.out.println("Error admisible");
				break;
			}
			
			if((fX1*fXr) <0 ){
				x2 = xR;
			}
			if((fX1*fXr) >0 ){
				x1 = xR;
			}
			if((fX1*fXr) == 0 ){
				//System.out.println("Raiz en xR");
				break;
			}
			
			it++;
		}
	}
	
	public boolean verificarBolzano(){
		boolean bolzano = false;
		
		if(x1 != null && x2 != null){
			
			Double fX1 = this.evaluatePolinoimo(x1);
			Double fX2 = this.evaluatePolinoimo(x2);
			
			if(fX1 < 0 && fX2 > 0){
				bolzano = true;
			}
			if(fX1 > 0 && fX2 < 0){
				bolzano = true;
			}
			
		}
		
		return bolzano;
	}
	
	public String toString(){
		String ret = "";
		ret = "Iteraci�nes: " + it;
		ret += "\n Intervalo [" + this.adaptarPrecision(this.x1) + "," + this.adaptarPrecision(this.x2)+  "]";
		ret += "\n error = " + this.adaptarPrecision(error);
		ret += "\n Xr = " + this.adaptarPrecision(xR);
		ret += "\n f(Xr) = " + this.adaptarPrecision(fXr);
		return ret;
	}
	
	/**
	 * Adapta la precision deseada a los calculos
	 * @param val - valor cuya precision queremos adaptar
	 * @return devuelve valor con precision "precision"
	 */
	private Double adaptarPrecision(Double val){
		val = Double.valueOf(String.format(Locale.US, "%." + this.precision + "f", val));
		return val;
	}
	
	/**
	 * Llama el metodo del polinomio para la evaluaci�n
	 * @param val - punto a evaluar
	 * @return - resultado para dicho punto
	 */
	private Double evaluatePolinoimo(Double val){
		return this.polinomio.evaluatePolinoimo(val);
	}
	
	/**
	 * establece el numero de iteraci�nes que ha de ejecutar el programa
	 * @param n
	 */
	public void setNumeroIteraciones(int n){
		
		if(n < 1){
			System.out.println("Numero no puede ser negativo");
		}else{
			this.iMax = n;
		}
	}
	public Integer getNumeroIteraciones(){
		return this.iMax;
	}
	/**
	 * Funcion que inicializa la funci�n que se analiza
	 * f(x) = x^5 - x^4 + x^3 - 3
	 */
	private void initDefaultFunc(){
		polinomio = new Polinomio();
		polinomio.setMonomio(5, 1);
		polinomio.setMonomio(4, -1);
		polinomio.setMonomio(3, 1);
		polinomio.setMonomio(0, -3);
	}
}
