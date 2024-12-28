package com.example.conectaplus.game_conectak.conectak;

/**
 * @author L.Mandow (2023-10-16)
 * 
 * Contenedor de un tipo de juego del Conecta-k, definido por:
 * - número de filas
 * - número de columnas
 * - longitud ganadora
 */
public class TipoJuego {

	int nFil;
	int nCol;
	int k;
	
	public TipoJuego(int nFil, int nCol, int k) {
		this.nFil = nFil;
		this.nCol = nCol;
		this.k = k;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + k;
		result = prime * result + nCol;
		result = prime * result + nFil;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoJuego other = (TipoJuego) obj;
		if (k != other.k)
			return false;
		if (nCol != other.nCol)
			return false;
		if (nFil != other.nFil)
			return false;
		return true;
	}
	
	
}
