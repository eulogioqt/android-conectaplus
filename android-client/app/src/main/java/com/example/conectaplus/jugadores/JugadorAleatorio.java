package com.example.conectaplus.jugadores;

import java.util.List;
import java.util.Random;

import com.example.conectaplus.mundoadversario.EstadoJuego;

/**
 * Jugador aleatorio.
 * Este jugador puede jugar a cualquier juego que implemente la interfaz EstadoJuego.
 * 
 * Se incluye un constructor con semilla para que la ejecución sea repetible.
 * 
 * @author (2013) José Miguel Horcas Aguilera, Lorenzo Mandow
 * @param <E>
 *
 * @versión: 2019-06-28  L.Mandow
 *
 */
public class JugadorAleatorio<E extends EstadoJuego<E>> implements Jugador<E> {

	private Random rd = new Random();
	
	public JugadorAleatorio(){
		//nada que hacer, la semilla es arbitraria
	}
	
	public JugadorAleatorio (long semilla){
		rd.setSeed(semilla);
	}
	
	@Override
	public E mueve(E e) {
		List<E> lh = e.calculaSucesores();
		int n = rd.nextInt(lh.size());
		return lh.get(n);
	}
}
