package com.example.conectaplus.jugadores;

import com.example.conectaplus.mundoadversario.EstadoJuegoAprox;


/**
* Jugador que evalúa los sucesores inmediatos usando una función de evaluación lineal.
* 
* Se incluyen constructores con semilla para que la ejecución sea repetible.
* 
* @author Lorenzo Mandow
* @param <E>  Clase de estados del juego a los que jugará el jugador
* 
* @versión: 2019-06-28
*
*/

public class JugadorEvaluarLineal<E extends EstadoJuegoAprox<E>> extends JugadorEntrenable<E> {
    
    public JugadorEvaluarLineal(E estadoIni) {
    	super(new EvaluadorAproxLineal<E>(estadoIni.codifica().length));
    }
    
    public JugadorEvaluarLineal(E estadoIni, double alfa) {
    	super(new EvaluadorAproxLineal<E>(estadoIni.codifica().length, alfa));
    }
    
    public JugadorEvaluarLineal(E estadoIni, long semilla) {
    	super(new EvaluadorAproxLineal<E>(estadoIni.codifica().length), semilla);
    }
    
    public JugadorEvaluarLineal(E estadoIni, double alfa, long semilla) {
    	super(new EvaluadorAproxLineal<E>(estadoIni.codifica().length, alfa), semilla);
    }
    
    
    
    public double[] consultarPesos(){
    	return ((EvaluadorAproxLineal<E>) this.evaluador).consultarPesos();
    }
    
}
