package com.example.conectaplus.jugadores;

import com.example.conectaplus.mundoadversario.EstadoJuego;

/**
 * Interfaz Evaluador que deben implementar los objetos evaluadores heurísticos
 * de un juego dado.
 * 
 * IMPORTANTE: El valor de la función de evaluación calculado por el método
 * evaluacion debe estar comprendido entre los valores de VICTORIA Y DERROTA.
 * Estos valores no pueden ser infinitos si se utiliza aprendizaje por refuerzo.
 * 
 * @author Lorenzo Mandow
 * 
 * @versión: 2018-10-03
 *           2021-09-14  El evaluador guarda el último estado final evaluado
 *
 */
public abstract class Evaluador<E extends EstadoJuego<E>> {

	public static double VICTORIA = 1000; 
    public static double EMPATE = 0;      
    public static double DERROTA = -1000; 
    
    public E e;  //ultimo estado final evaluado con el método evalúa(para consulta y depuración)
	
	/**
	 * Función de evaluación válida para cualquier juego que implemente la interfaz EstadoJuego. 
	 *  
	 * @param e2			Estado del juego.
	 * @param miTurno       true si es el primer jugador, false si es el segundo
	 * @return				VICTORIA si el estado es final y ganador para MAX.
	 * 						DERROTA si el estado es final y perdedor para MAX.
	 *                      EMPATE si el estado es final y no ganó nadie.
	 * 						El valor de la función heurística en otro caso.
	 */
	public double evalua (E e2, boolean miTurno) {
		
		this.e = e2; //guardamos el último estado evaluado (para consulta y depuración)
		
		boolean ganaActual = e2.ganaActual();  //true si gana el jugador al que le toca
		boolean ganaOtro   = e2.ganaOtro();
		
		boolean soyActual = e2.turno1() == miTurno;  //true si soy el jugador al que le toca
		
		if (ganaActual && soyActual || ganaOtro   && !soyActual) {   //gana max
			return VICTORIA;
		} else if (ganaActual && !soyActual || ganaOtro   && soyActual) {  //gana min
			return DERROTA;
		} else if (e2.agotado()) {
			return EMPATE;
		} else {
			return  this.evaluacion(e2, miTurno);
		}// if else else
	}
	
	/**
	 * Devuelve el resultado de evaluar un estado del juego.
	 * El jugador para el que se realiza la evaluación será el correspondiente
	 * al indicado por miTurno. Esto es necesario, porque el jugador puede querer
	 * evaluar posiciones del juego correspondientes a su turno o al contrario. 
	 * Por ejemplo, dependiendo de si la profundidad a la que explora el árbol
	 * del juego es par o impar.
	 * 
	 * IMPORTANTE: El valor devuelto debe estar comprendido entre los valores
	 *  de VICTORIA Y DERROTA.
	 * 
	 * NOTA: Por motivos de eficiencia lo lógico es que no se proporcione
	 * una copia del estado. Por ese motivo, el método evaluacion no debe
	 * modificar destructivamente el estado recibido.
	 * 
	 * @param estado	Estado del juego.
	 * @param miTurno   Turno del jugador que realiza la evaluación (true -> primer
	 * jugador; false -> segundo jugador).
	 * @return			Evaluación del estado.
	 */
	protected abstract double evaluacion(E estado, boolean miTurno);
	
}
