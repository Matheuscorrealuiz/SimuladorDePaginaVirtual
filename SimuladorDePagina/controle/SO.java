package controle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import modelo.Pagina;

public class SO implements Runnable {

	private static int tamanhoVirtual;

	public SO(int tamanhoMemoriaVirtual, int tamanhoMemoriaFisica, int tamanhoPagina) {
		tamanhoVirtual = tamanhoMemoriaVirtual;
		new MMU(tamanhoMemoriaVirtual, tamanhoMemoriaFisica, tamanhoPagina);
		geraArquivos(tamanhoMemoriaVirtual);

	}

	public static void iniciaSO() {

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Random random = new Random();
		int posicaoSolicitada = random.nextInt(tamanhoVirtual);
		boolean posicaoRetorno = Processador.instrucaoDeAcesso(posicaoSolicitada);

		System.out.println("Posicao solicitada para a memoria virtual " + posicaoSolicitada);

	}

	private static void geraArquivos(int tamanhoMemoriaVirtual) {

		for (int i = 0; i < tamanhoMemoriaVirtual; i++) {
			try {
				
				FileOutputStream arquivo = new FileOutputStream("SimuladorDePagina/arquivos/" + i + ".txt");
				ObjectOutputStream geradorDeArquivo = new ObjectOutputStream(arquivo);

				Pagina pagina = new Pagina();

				pagina.setCaminhoArquivo("SimuladorDePagina/src/arquivos/" + i + ".txt");

				MMU.memoriaVirtual[i] = pagina;
				
				geradorDeArquivo.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized void run() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (true) {
			iniciaSO();
		}
	}

}
