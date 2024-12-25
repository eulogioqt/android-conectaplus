package com.example.conectaplus.jugadores;

import com.example.conectaplus.mundoadversario.EstadoJuego;

/**
 * @author Lorenzo Mandow
 * @version: 2018-10-03
 * 
 * Representación de un jugador.
 *
 */
public interface Jugador<E extends EstadoJuego<E>> {

	/**
	 * Se supone que los jugadores son legales, en el sentido de que devolverán
	 * un sucesor legal del estado e.
	 * 
	 * El jugardor debe jugar según el turno que toque en el estado del juego e.
	 * 
	 * @param e Estado.
	 * @return	Estado resultante de que el jugador mueva en el estado e según el turno que toca.
	 */
	public E mueve (E e);
	
}
