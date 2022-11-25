package main;

import controle.SO;

public class Main {

	public static void main(String[] args) {
		//Tamanhos representados em KB
		int tamanhoPagina = 8;
		int tamanhoMemoriaFisica = 64;
		int tamanhoMemoriaVirtual = 1024;

		int numeroPosicoesMemoriaFisica = (Integer) tamanhoMemoriaFisica / tamanhoPagina;
		int numeroPosicoesMemoriaVirtual = (Integer) tamanhoMemoriaVirtual / tamanhoPagina;

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		SO sistema = new SO(numeroPosicoesMemoriaVirtual, numeroPosicoesMemoriaFisica, tamanhoPagina);
		Thread threadSistema = new Thread(sistema);

		threadSistema.start();
	}
}