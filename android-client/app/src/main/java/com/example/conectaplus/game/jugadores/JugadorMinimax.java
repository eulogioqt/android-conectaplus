package com.example.conectaplus.game.jugadores;

import java.util.List;

import com.example.conectaplus.game.mundoadversario.EstadoJuego;


/**
 * Jugador que elige el siguiente movimiento evaluando los sucesores
 * de la posición actual a una profundidad dada, y propagando los
 * valores mediante la estrategia MiniMax.
 * 
 * Se incluye un constructor con semilla para que la ejecución sea repetible.
 * 
 * @author Lorenzo Mandow
 * 
 * @versión: 2019-06-28 L. Mandow
 *
 */
public class JugadorMinimax<E extends EstadoJuego<E>> extends JugadorEvaluar<E>{ 
    protected int profMax;            // Profundidad máxima de búsqueda.
    
    /**
     * Constructor.
     * 
     * @param ev                Evaluador.
     * @param profundidadMaxima Profundidad máxima de búsqueda.
     */
    public JugadorMinimax(Evaluador<E> ev, int profundidadMaxima) {
        super(ev);
        this.profMax = profundidadMaxima;
    }
    
    
    public JugadorMinimax(Evaluador<E> ev, int profundidadMaxima, long semilla) {
        super(ev, semilla);
        this.profMax = profundidadMaxima;
    }
    
    @Override
    public E mueve(E e) {
    	
    	boolean miTurno = e.turno1();
    	
        VE<E> resultado = negamax(e, this.profMax, miTurno, 1);
        
        //System.out.println("Evaluación del movimiento: " + resultado.v());
        
        return resultado.e();
    }
    
  
    /**
     * Realiza una evaluación MINIMAX de un estado dado a una profundidad dada.
     * El cálculo se realiza mediante una función recursiva por la cola que implementa
     * el método de cálculo conocido como NEGAMAX: en cada nivel se calcula siempre el
     * máximo de los sucesores cambiado de signo, pero las evaluaciones se van cambiando
     * de signo según la profundidad, de modo que en los niveles MIN, calcular el máximo 
     * cambiado de signo de las evaluaciones cambiadas de signo equivale a calcular el mínimo.
     * 
     * @param e     Estado del juego.
     * @param prof  Profundiad de la búsqueda.
     * @param miTurno  true si MAX es el primer jugador, false si es el segundo.
     * @param signo Tomará inicialmente el valor 1, e irá alternando de signo con la profundidad.
     * @return      Objeto VE con el mejor estado sucesor y su valoración minimax.
     */
    
    public VE<E> negamax (E e, int prof, boolean miTurno, int signo) {
        E mejorE = null;
        double mejorV = Double.NEGATIVE_INFINITY;
        double v2;
        
        if (prof == 0 || e.ganaActual() || e.ganaOtro() || e.agotado()) {
            mejorV = signo*this.evaluador.evalua(e, miTurno);
        } else {
            for (E e2 : ordenar(e.calculaSucesores())) {
                v2 = -(negamax(e2, prof-1, miTurno, -signo).v());
                if ((v2 > mejorV) || (mejorE == null)) {
                    mejorV = v2;
                    mejorE = e2;
                }
            }
        }
        return new VE<E>(mejorV, mejorE);
    }
    
    protected List<E> ordenar (List<E> l) {
        return super.barajar(l);    
    }
}
