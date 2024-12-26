package com.example.conectaplus.game.jugadores;

import com.example.conectaplus.game.mundoadversario.EstadoJuegoAprox;

/**
* Jugador que evalúa los sucesores inmediatos usando una función de evaluación lineal
* que incluye decaimiento para el valor de alfa.
* 
* Se incluyen constructores con semilla para que la ejecución sea repetible.
* 
* @author Lorenzo Mandow
* @param <E>  Clase de estados del juego a los que jugará el jugador
* 
* @versión: 2020-11-13
*
*/

public class JugadorEvaluarLinealDecay<E extends EstadoJuegoAprox<E>> extends JugadorEntrenable<E> {
    
	 public JugadorEvaluarLinealDecay(E estadoIni, double alfa) {
	    	super(new EvaluadorAproxLinealDecay<E>(estadoIni.codifica().length, alfa));
	    }
    
    public JugadorEvaluarLinealDecay(E estadoIni, double alfa, double ratio, int pasos) {
    	super(new EvaluadorAproxLinealDecay<E>(estadoIni.codifica().length, alfa, ratio, pasos));
    }
    
    
    public JugadorEvaluarLinealDecay(E estadoIni, double alfa, long semilla, double ratio, int pasos) {
    	super(new EvaluadorAproxLinealDecay<E>(estadoIni.codifica().length, alfa, ratio, pasos), semilla);
    }
    
    
    
    public double[] consultarPesos(){
    	return ((EvaluadorAproxLinealDecay<E>) this.evaluador).consultarPesos();
    }
    
}
