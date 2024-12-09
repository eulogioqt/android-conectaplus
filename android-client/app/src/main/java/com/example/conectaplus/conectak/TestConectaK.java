package com.example.conectaplus.conectak;

import java.util.List;


import jugadores.Jugador;
import jugadores.JugadorAleatorio;
import jugadores.JugadorEvaluar;
import jugadores.JugadorEvaluarTV;
import mundoadversario.Juego;

public class TestConectaK {

	public static void main(String[] args) {
		
		
//		aleatorio();
//		aleatorioHumano();
//		evaluarHumano();
		evaluarTVHumano();
	}
	
    
    public static void aleatorio(){
        ConectaK e = new ConectaK(3,3,3);
        Jugador j1 = new JugadorAleatorio<ConectaK>();
        Jugador j2 = new JugadorAleatorio<ConectaK>();
        Juego juego1 = new Juego(j1, j2, e);
        
        verPartida(juego1);
    }
    
    public static void aleatorioHumano(){
        ConectaK e = new ConectaK(3,3,3);
        Jugador j1 = new JugadorAleatorio<ConectaK>();
        Jugador j2 = new JugadorHumanoCK();
        Juego juego1 = new Juego(j1, j2, e);
        
        verPartida(juego1);       
    }
    
    
    public static void evaluarHumano(){
        ConectaK e = new ConectaK(3,3,3);
        Jugador j1 = new JugadorEvaluar(new EvaluadorCK());
        Jugador j2 = new JugadorHumanoCK();
        Juego juego1 = new Juego(j1, j2, e);
        
        verPartida(juego1);       
    }
    
    public static void evaluarTVHumano(){
        ConectaK e = new ConectaK(3,3,3);
        JugadorEvaluarTV<ConectaK> j1 = new JugadorEvaluarTV();
        
        
        //entrenamos
        double epsilon = 0.1;//probabilidad de exploración
        int nPartidas = 5000;
        Jugador ja = new JugadorAleatorio();
        System.out.println("Entrenando...");
        for(int i = 0; i< nPartidas; i++){
        	j1.aprendeTurno1(ja, e, epsilon);
        }
        System.out.println("OK");
        
        //jugamos
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
