package com.example.conectaplus.jugadores;


import mundoadversario.EstadoJuegoAprox;

/**
 * Para aprendizaje con refuerzo utilizando un aproximador lineal de la función de
 * evaluación. La función se almacena internamente como un vector con los pesos, uno
 * por característica codificada del estado.
 * 
 * Los espacios de estados que utilicen aproximadores lineales deben implementar
 * EstadoJuegoAprox, que define una función de codificación de los estados en forma
 * de un vector de características. El vector de pesos debe tener la misma longitud
 * que el vector de características.
 * 
 * En principio la convergencia no está garantizada para valores constantes de alfa,
 * por lo que puede ser necesario reducir progresivamente su valor. Para problemas
 * sencillos puede ser suficiente utilizar valores pequeños de alfa.
 * 
 * @author Lorenzo Mandow
 * @version 2018-10-03
 */
public class EvaluadorAproxLineal<E extends EstadoJuegoAprox<E>> extends EvaluadorEntrenable<E> {

    public double[] pesos = null; 
    int longitud = -1;

    /**
     * @param n - número de valores que tendrá el vector que codifica los estados.
     */
    public EvaluadorAproxLineal(int n) {
    	super();   
    	this.longitud = n;
        this.pesos = new double[n];
        
//        System.out.println("EvaluadorAproxLineal (constructor 1) - Creado un objeto con " + n + " pesos y alfa: " + this.alfa);
    }
    
    /**
     * @param n - número de valores que tendrá el vector que codifica los estados.
     * @param alfa - tasa de aprendizaje
     */
    public EvaluadorAproxLineal(int n, double alfa) {
    	this(n);  //lamada al otro constructor
    	this.alfa = alfa;
    	
//    	System.out.println("EvaluadorAproxLineal (constructor 2) - Creado un objeto con " + n + " pesos y alfa: " + this.alfa);
    	
    }

    /**
     * @return copia de los pesos que definen actualmente la función lineal.
     */
    public double[] consultarPesos(){
    	return pesos.clone();   	
    }
    

    /**
     * En este método el valor de miTurno es irrelevante.
     * @return valor de la función lineal para el estado proporcionado.
     */
    @Override
    protected double evaluacion(E estado, boolean miTurno) {
    	
    	return productoEscalar(estado.codifica(), this.pesos);
  	}
    
    
   
    /**
     * Actualización de los pesos de la función tras la transición de estado -> sucesor
     * Se emplea el método del semigradiente estocástico TD(0).
     *
     * El estado sucesor podría ser un estado final del juego. En tal caso nos interesaría conocer
     * su recompensa final (victoria, derrota o empate). Por ese motivo calculamos su valor llamando
     * al método evalua (que comprueba si es final, y en caso contrario, llama a evaluacion).
     */
    @Override
    public void actualizaDT(E estado, E sucesor, boolean miTurno){
    	
      	//actualización de los pesos por diferencias temporales, método TD(0):
    	int[] codigo = estado.codifica();
    	double delta = alfa * (this.evalua(sucesor, miTurno) - this.evaluacion(estado, miTurno));
    	for (int i = 0; i < longitud; i++){
    		pesos[i] = pesos[i] + delta * codigo[i]; 
    	}// for
    }
    
    /**
     * @return producto escalar de dos vectores
     */
    private double productoEscalar(int[] codigo, double[] pesos){
    	double valor = 0;
    	
		for(int i = 0; i < pesos.length; i++){
			valor = valor + pesos[i] * codigo[i];
		}//for i
	
		return valor;
    }
	
}
