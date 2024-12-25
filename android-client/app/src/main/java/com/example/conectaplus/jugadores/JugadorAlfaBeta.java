package com.example.conectaplus.jugadores;

import java.util.List;

import com.example.conectaplus.mundoadversario.EstadoJuego;


/**
 * Jugador que elige el siguiente movimiento evaluando los sucesores
 * de la posición actual a una profundidad dada, y propagando los
 * valores mediante la estrategia Alfa-Beta.
 * 
 * Se incluye un constructor con semilla para que la ejecución sea repetible.
 * 
 * @author Lorenzo Mandow
 * @versión: 2018-10-03
 *
 */
public class JugadorAlfaBeta<E extends EstadoJuego<E>> extends JugadorEvaluar<E> {

    protected int profMax;            // Profundidad máxima de búsqueda.
    
    /**
     * Constructor.
     * 
     * @param ev                Evaluador.
     * @param profundidadMaxima Profundidad máxima de búsqueda.
     */
    public JugadorAlfaBeta(Evaluador<E> ev, int profundidadMaxima) {
        super(ev);
        this.profMax = profundidadMaxima;
    }
    
    public JugadorAlfaBeta(Evaluador<E> ev, int profundidadMaxima, long semilla) {
        super(ev, semilla);
        this.profMax = profundidadMaxima;
    }
    
    
    @Override
    public E mueve(E e) {
    	
    	boolean miTurno = e.turno1();
    	
        VE<E> resultado = negamaxAB(e, this.profMax, miTurno, 1);
        
       // System.out.println("Evaluación del movimiento: " + resultado.v());
        
        return  resultado.e();
    }
    
    /**
     * 
     */
    
    public VE<E> negamaxAB (E e, int prof, boolean miTurno, int signo) {
        return negamaxAB(e, prof, miTurno, signo, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
    
    public VE<E> negamaxAB (E e, int prof, boolean miTurno, int signo, double alfa, double beta) {
        E mejorE = null;
        double mejorV = Double.NEGATIVE_INFINITY;
        double v2;
        
        if (prof == 0 || e.ganaActual() || e.ganaOtro() || e.agotado()) {
            mejorV = signo*this.evaluador.evalua(e, miTurno);
        } else {
            for (E e2 : ordenar(e.calculaSucesores())) {
                v2 = -(negamaxAB (e2, prof-1, miTurno, -signo, -beta, -alfa).v());
                if ((v2 > mejorV) || (mejorE == null)) {
                    mejorV = v2;
                    mejorE = e2;
                     if (v2 >= beta){
                         return new VE<E>(mejorV, mejorE);
                    }
                    if (v2 >  alfa){
                        alfa = v2;
                    }
                }
            }
        }
        return new VE<E>(mejorV, mejorE);
    }
  
    protected List<E> ordenar (List<E> l) {
        return super.barajar(l);    
    }
}
