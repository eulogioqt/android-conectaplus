package com.example.conectaplus.jugadores;

import jugadores.EvaluadorAproxLineal;
import mundoadversario.EstadoJuegoAprox;

/**
 * Evaluador para aproximación lineal que incluye un decaimiento del valor de alfa (tasa de aprendizaje).
 * 
 * Cada 'pasos' veces que se llama a actualizaDT, alfa se multiplica por 'ratio'.
 * 
 * 
 * @author Lorenzo Mandow
 * @versión: 2020-11-13
 * */


public class EvaluadorAproxLinealDecay<E extends EstadoJuegoAprox<E>> extends EvaluadorAproxLineal<E> {
	
	double ratio = 0.99; //ratio multiplicadora de alfa para el decay
	int pasos = 10; // número de pasos (llamadas a actualizaDT para aplicar el decay)
	
	int cont = 0;  //contador de pasos

	public EvaluadorAproxLinealDecay(int n, double alfa) {
		super(n, alfa);
	}
	
	public EvaluadorAproxLinealDecay(int n, double alfa, double ratio, int pasos) {
		super(n, alfa);
				
		this.ratio = ratio;
		this.pasos = pasos;
	}
	
	@Override
    public void actualizaDT(E estado, E sucesor, boolean miTurno){
		//decaimiento de alfa si es necesario.
		this.cont++;
		if (this.cont % this.pasos == 0) {
			this.alfa = this.alfa * ratio;
		}
		super.actualizaDT(estado, sucesor, miTurno);
		
	}

}
