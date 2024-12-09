package com.example.conectaplus.conectak;


/**
 * Clase que representa un movimiento realizado durante el juego.
 * 
 * @author José Miguel Horcas Aguilera
 *
 */
public class Movimiento {

	private int f;				// Fila.
	private int c;				// Columna.
	
	/**
	 * Constructor.
	 * 
	 * @param f	Fila.
	 * @param c	Columna.
	 */
	public Movimiento (int f, int c) {
		this.f = f;
		this.c = c;
	}
	
	/**
	 * Selector.
	 * 
	 * @return Fila.
	 */
	public int f () {
		return f;
	}
	
	/**
	 * Selector.
	 * 
	 * @return Columna.
	 */
	public int c () {
		return c;
	}
	
	public String toString () {
		return "(" + f + "," + c + ")";
	}
}
