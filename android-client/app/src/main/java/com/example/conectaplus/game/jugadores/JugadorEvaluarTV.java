package com.example.conectaplus.game.jugadores;

import com.example.conectaplus.game.mundoadversario.EstadoJuego;
import com.example.conectaplus.game.mundosolitario.OverrideHashCode;

/**
 * Jugador que elige el siguiente movimiento evaluando los sucesores
 * inmediatos a la posición actual usando una Tabla de Valor calculada
 * mediante apriendizaje con refuerzo.
 * 
 * Se incluyen constructores con semilla para que la ejecución sea repetible.
 * 
 * @author Lorenzo Mandow
 * @versión: 2019-06-28
 * */
public class JugadorEvaluarTV<E extends OverrideHashCode & EstadoJuego<E>> extends JugadorEntrenable<E> {
    
    public JugadorEvaluarTV(){
        super(new EvaluadorTV<E>());
    }
    
    public JugadorEvaluarTV(double alfa) {
        super(new EvaluadorTV<E>(alfa));
    }
    
    public JugadorEvaluarTV(long semilla){
        super(new EvaluadorTV<E>(), semilla);
    }
    
    public JugadorEvaluarTV(double alfa, long semilla) {
        super(new EvaluadorTV<E>(alfa), semilla);
    }
}
