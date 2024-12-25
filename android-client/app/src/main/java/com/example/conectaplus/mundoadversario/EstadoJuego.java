package com.example.conectaplus.mundoadversario;

import com.example.conectaplus.mundosolitario.RepresentacionEstado;

/**
 * Representación de los estados de un juego bipersonal de suma cero.
 * 
 * @author Lorenzo Mandow
 * @version: 2018-10-02
 *
 */
public interface EstadoJuego<E extends EstadoJuego<E>> extends RepresentacionEstado<E> {
	
	/**
	 * @return true si es el turno del primer jugador, false en otro caso.
	 */
	public boolean turno1 ();

	/**
	 * @return true si el estado actual es final del juego, y gana el jugador del turno
	 * actual; false en otro caso.
	 */
	public boolean ganaActual();
	
	/**
	 * @return true si el estado actual es final del juego, y gana el jugador contrario
	 * al del turno actual; false en otro caso.
	 */
	
	public boolean ganaOtro();
	
	/**
	 * @return true si el juego ha terminado (no se pueden hacer más movimientos), tal vez porque alguien ganó,
	 * o porque hay tablas o empate; false en otro caso.
	 */
	public boolean agotado ();
	
	/**
	 * Muestra por pantalla el estado del juego de forma legible.
	 */
	public void ver ();
	
	
}
