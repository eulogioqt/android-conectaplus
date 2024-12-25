package com.example.conectaplus.jugadores;


import java.util.Map;

import com.example.conectaplus.mundoadversario.EstadoJuego;
import com.example.conectaplus.mundosolitario.OverrideHashCode;

import java.util.Hashtable;

/**
 * Clase EvaluadorTV.
 * Para aprendizaje con refuerzo utilizando una tabla de valor. La tabla se implementa mediante una
 * tabla hash indexada por EstadoJuego que contiene las estimaciones de valor correspondientes.
 * 
 * @author Lorenzo Mandow
 * @version 2018-10-03
 */
public class EvaluadorTV<E extends OverrideHashCode & EstadoJuego<E>> extends EvaluadorEntrenable<E>{ 

    public Map<E,Double> tv;   //tabla de valor
    
    public EvaluadorTV() {
    	super(); 
        this.tv = new Hashtable<E,Double>();
    }
    
    public EvaluadorTV(double alfa) {
        this();   //llamada al otro constructor
        this.alfa = alfa;
    }

    /**
     * Valor del estado en la tabla. Si no está devolvemos EMPATE.
     * En este caso el valor de miTurno es irrelevante.
     */
    @Override
    protected double evaluacion(E estado, boolean miTurno) {
        Double resultado = tv.get(estado);  //recompensa esperada
        if (resultado != null){
            return resultado;
        } else {                    //el estado no está en la tabla
            return EMPATE;          // valor por defecto
        }// if resultado
    }
    
    /**
     * Actualización del valor de estado tras la transición de estado -> sucesor
     * 
     * El estado sucesor podría ser un estado final del juego. En tal caso nos interesaría conocer
     * su recompensa final (victoria, derrota o empate). Por ese motivo calculamos su valor llamando
     * al método evalua (que comprueba si es final, y en caso contrario, llama a evaluacion).
     */
    @Override
    public void actualizaDT(E estado, E sucesor, boolean miTurno){
    	
        double valorE = this.evaluacion(estado, miTurno);  //sabemos que no es final, luego llamamos a evaluacion
        double valorSuc = this.evalua(sucesor, miTurno);   //podría ser final, luego llamamos a evalua
     
        this.tv.put(estado, valorE + this.alfa * (valorSuc - valorE)); 
    }
}
