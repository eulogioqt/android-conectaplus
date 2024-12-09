package com.example.conectaplus.jugadores;

import mundoadversario.EstadoJuego;

    /**
     * Guarda una valoración double asociada a un objeto EstadoJuego.
     * 
     * @author Lorenzo Mandow
     * @version 2018-10-03
     */
    public class VE<E extends EstadoJuego<E>> {
       private double valor;
       private E estado;
             
       public VE (double v, E e){
           this.estado = e;
           this.valor = v;
       }
       /**
        * @return el valor asociado al objeto.
        */
       public double v(){
           return this.valor;
        }
       
       /**
        * @return el estado asociado al objeto. 
        */
       public E e() {
           return this.estado;
       }
    }
