package com.example.conectaplus.game_conectak.conectak;

import java.io.*;
import com.example.conectaplus.game_conectak.jugadores.Jugador;

/**
 * 
 * @author José Miguel Horcas Aguilera, Lorenzo Mandow (2010)
 * @author Lorenzo Mandow (2013-10-21, 2023-10-30)
 * @version 
 *
 */
public class JugadorHumanoCK implements Jugador<ConectaK> {

	@Override
	public ConectaK mueve(ConectaK e) {
		ConectaK estado = (ConectaK) e;
		int c;
		
		do {
			c = pedirMovimiento();
		} while (!estado.columnaLibre(c));
		return estado.calcularSucNth(c);
	}

	/**
	 * Pide una columna sobre la que soltar una ficha.
	 * 
	 * @return Columna.
	 */
	private int pedirMovimiento () {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
		String linea;
		
		System.out.println("¿En qué columna soltamos la ficha?");
		try {
			linea = br.readLine(); 
		} catch (IOException e) {
			return -1;
		}
		return Integer.parseInt(linea);
	}
}
