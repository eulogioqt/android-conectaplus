package com.example.conectaplus.game.jugadores;

import com.example.conectaplus.game.mundoadversario.EstadoJuego;

/**
 * Esquema para la clase de evaluadores entrenables mediante aprendizaje con refuerzo. Ejemplos son
 * la evaluación con tablas de valor o aproximadores lineales de funciones. 
 * 
 * Se incluye la tasa de aprendizaje alfa, correspondiente al método de aprendizaje por
 * diferencias temporales.
 * 
 * @author Lorenzo Mandow
 * @version 2018-10-03
 */
public abstract class EvaluadorEntrenable<E extends EstadoJuego<E>> extends Evaluador<E> {
	
    //Tasa de aprendizaje
    protected double alfa = 0.05;

    
    /**
     * Método para el entrenamiento por diferencias temporales en la transición
     * estado -> sucesor.
     * @param estado   - estado  cuyo valor se va a actualizar
     * @param sucesor  - sucesor alcanzado del estado a actualizar
     * @param miTurno  - turno del jugador que está aprendiendo (true - primer jugador,
     * false - segundo jugador). Necesario para calcular el valor de los estados finales.
     */
    public abstract void actualizaDT(E estado, E sucesor, boolean miTurno);
    
    /**
     * Método para la actualización del valor de alfa. Se puede emplear para implementar
     * técnicas de enfriamiento.
     */
    public void actualizarAlfa(double alfa){
    	this.alfa = alfa;
    }
    
    public double alfa() {
    	return alfa;
    }
}
