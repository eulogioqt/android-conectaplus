package com.example.conectaplus.conectak;

import java.util.Arrays;
import java.util.List;


import jugadores.Jugador;
import jugadores.JugadorAleatorio;
import jugadores.JugadorEvaluar;
import jugadores.JugadorEvaluarLineal;
import jugadores.JugadorEvaluarLinealDecay;
import jugadores.JugadorEvaluarTV;
import mundoadversario.Juego;

public class TestConectaKAprox {

	public static void main(String[] args) {
		
		
		evaluarAproxHumano();
	}
	
    
    public static void evaluarAproxHumano(){
        ConectaK e = new ConectaK(3,3,3);
        double alfa = 0.01;
        JugadorEvaluarLinealDecay<ConectaK> j1 = new JugadorEvaluarLinealDecay(e, alfa);
        
        
        //entrenamos
        double epsilon = 0.1;//probabilidad de exploración
        int nPartidas = 600;
        Jugador ja = new JugadorAleatorio();
        System.out.println("Entrenando...");
        for(int i = 0; i< nPartidas; i++){
        	j1.aprendeTurno1(ja, e, epsilon);
            System.out.println("Pesos: " + Arrays.toString(j1.consultarPesos()));
        }
        System.out.println("OK");
        
//        //jugamos
        Jugador j2 = new JugadorHumanoCK();
        Juego juego1 = new Juego(j1, j2, e);
        
        verPartida(juego1);
    }
    
      
public static void verPartida (Juego juego){
        int i =  juego.jugarPartida(true);
        
        if (i == 0){
            System.out.println("Empate.");
        } else if (i == 1) {
            System.out.println("Gana el primer jugador.");
        } else {
            System.out.println("Gana el segundo jugador.");
        }
    }

	
	
	

}
