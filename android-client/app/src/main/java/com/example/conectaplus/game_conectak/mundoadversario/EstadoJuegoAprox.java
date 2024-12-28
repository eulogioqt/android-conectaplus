package com.example.conectaplus.game_conectak.mundoadversario;

/**
 *  
 * @author Lorenzo.Mandow
 * @version 2017-11-07
 * 
 * Interfaz para los juegos que usen aproximadores de funciones.
 * Incorpora un método para codificar la representación del estado en un
 * vector de características. Esto se usa, por ejemplo, en las funciones
 * de evaluación entrenables que emplean aproximadores de funciones
 * (lineales, redes neuronales, etc.).
 *
 */
public interface EstadoJuegoAprox<E extends EstadoJuegoAprox<E>> extends EstadoJuego<E> {
	
	/**
	 * La codificación del estado mediante un aproximador lineal debe
	 * incluir siempre un término independiente (por ejemplo, un
	 * elemento del array que siempre sea 1).
	 * 
	 * @return Un array de enteros con la codificación del estado
	 */
	
	public int[] codifica ();

}
