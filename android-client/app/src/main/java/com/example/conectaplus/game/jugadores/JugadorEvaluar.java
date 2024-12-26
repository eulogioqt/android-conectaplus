package com.example.conectaplus.game.jugadores;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.example.conectaplus.game.mundoadversario.EstadoJuego;

/**
 * Jugador que elige el siguiente movimiento evaluando los sucesores
 * inmediatos a la posición actual.
 * 
 * El jugador dispone de un generador de números aleatorios que le permite
 * desempatar aleatoriamente en caso de empate entre varias acciones.
 * 
 * Se incluye un constructor con semilla para que la ejecución sea repetible.
 * 
 * @author Lorenzo Mandow
 * @param <E>  Clase de estados del juego a los que jugará el jugador
 * 
 * @versión: 2019-06-28 L. Mandow
 *
 */
public class JugadorEvaluar<E extends EstadoJuego<E>> implements Jugador<E> {
	
	private Random rd = new Random();
	public Evaluador<E> evaluador;
	
	public JugadorEvaluar(Evaluador<E> ev) {
		evaluador = ev;
	}

	public JugadorEvaluar(Evaluador<E> ev, long semilla) {
		this(ev);
		rd.setSeed(semilla);
	}

	
	/**
	 * Genera todos los hijos del estado recibido, y devuelve el que reciba una mayor
	 * evaluación. La lista de hijos se baraja antes de analizarla para evitar un comportamiento
	 * determinista, es decir, si hay varios hijos con la evaluación óptima, distintas
	 * llamada al método pueden devolver distintos hijos óptimos.
	 * 
	 * @param e el estado del juego a partir del cual debe elegirse el movimiento.
	 * 
	 * @return el estado del juego resultante a la realización del movimiento elegido.
	 */
	@Override
	public E mueve(E e) {
		
		boolean miTurno = e.turno1();
		
		E mejorE = null;
		double mejorV = Double.NEGATIVE_INFINITY;
		double v2;
		
		for (E e2 : barajar(e.calculaSucesores())) {
			v2 = evaluador.evalua(e2, miTurno);
			if ((v2 > mejorV)|| (mejorE == null)) {
				mejorV = v2;
				mejorE = e2;
			}
		}
		return mejorE;
	}
	
	/**
	 * Baraja aleatoriamente una lista de EstadoJuego.
	 * Utilizamos el propio generador de números aleatorios del jugador
	 * para que sea repetible usando la misma semilla.
	 * 
	 * @param list Lista de EstadoJuego.
	 * @return	Lista barajada aleatoriamente.
	 */
	List<E> barajar (List<E> list) {
		Collections.shuffle(list, this.rd);
		return list;
	}
}
