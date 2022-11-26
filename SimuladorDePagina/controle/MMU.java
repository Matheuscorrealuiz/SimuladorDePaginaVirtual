package controle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import modelo.Frame;
import modelo.Pagina;

public class MMU {
	
	public static int Kb = 1024;

	static Frame[] memoriaPrincipal;

	static Pagina[] memoriaVirtual;

	public static Integer[][] tabelaDePaginas;
	public int quantidadeColunas = 3;
	public static int moldura = 0;
	public final static int BIT_PRESENTE = 1;
	public final static int BIT_MODIFICADA = 2;

	static int[][] bitsReferencia;
	private final static int QUANTIDADE_BITS_REFERENCIA = 8;
	
	private static int posicaoSolicitada;

	private static int tamanhoPagina;

	public MMU(int tamanhoDaMemoriaVirtual, int tamanhoDaMemoriaFisica, int tamanhoDaPagina) {

		memoriaVirtual = new Pagina[tamanhoDaMemoriaVirtual];
		tabelaDePaginas = new Integer[tamanhoDaMemoriaVirtual][quantidadeColunas];

		memoriaPrincipal = new Frame[tamanhoDaMemoriaFisica];
		bitsReferencia = new int[tamanhoDaMemoriaFisica][QUANTIDADE_BITS_REFERENCIA];
		
		tamanhoPagina = tamanhoDaPagina;

		for (int i = 0; i < tamanhoDaMemoriaVirtual; i++) {
			tabelaDePaginas[i][BIT_PRESENTE] = 0;
			tabelaDePaginas[i][BIT_MODIFICADA] = 0;
			tabelaDePaginas[i][moldura] = -1;
		}
	}

	public static boolean mapearMemoria(int posicao) {
		boolean mapear;
		posicaoSolicitada = posicao;
		
		System.out.println("Caminho lógico solicitado " + posicaoSolicitada * tamanhoPagina * Kb);
		
		Pagina pagina = memoriaVirtual[posicaoSolicitada];

		if (tabelaDePaginas[posicaoSolicitada][BIT_PRESENTE] == 1) {
			mapear = true;
			System.out.println("Página presente na memória principal na posição "
					+ tabelaDePaginas[posicaoSolicitada][moldura]);
			bitsReferencia[tabelaDePaginas[posicaoSolicitada][moldura]][0] = 1;

		} else {
			mapear = false;
			System.out.println("Página ausente na memória principal!");
			carregaPagina(pagina);

		}
		int posicaoFisica = tabelaDePaginas[posicaoSolicitada][moldura];

		System.out.println("Caminho físico solicitado "
				+ posicaoFisica * tamanhoPagina * Kb);

		return mapear;
	}

	private static void carregaPagina(Pagina pagina) {

		int frameDisponivel = procuraFrameDisponivel();

		try {
			FileInputStream arquivoLeitura = new FileInputStream(pagina.getCaminhoArquivo());
			ObjectInputStream objLeitura = new ObjectInputStream(arquivoLeitura);

			memoriaPrincipal[frameDisponivel] = new Frame();
			memoriaPrincipal[frameDisponivel].setConteudo(objLeitura.readObject());

			arquivoLeitura.close();
			objLeitura.close();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		tabelaDePaginas[posicaoSolicitada][moldura] = frameDisponivel;

		System.out.println("Página carregada na posição " + frameDisponivel);

		bitsReferencia[frameDisponivel][0] = 1;
	}

	private static int procuraFrameDisponivel() {
		System.out.println("Procurando frame disponível...");
		for (int i = 0; i < memoriaPrincipal.length; i++) {
			if (memoriaPrincipal[i] == null) {
				System.out.println("Frame " + i + " disponível!");
				return i;
			}
		}
		System.out.println("Não há frames disponíveis!");
		int frameLiberado = liberaUmFrame();
		return frameLiberado;
	}

	private static int liberaUmFrame() {
		int[] valores = new int[memoriaPrincipal.length];

		int minimo = valores[0];
		int posicaoLiberada = 0;
		for (int i = 1; i < valores.length; i++) {
			if (valores[i] < minimo) {
				minimo = valores[i];
				posicaoLiberada = i;
			}
		}
		System.out.println("Posição liberada " + posicaoLiberada);

		int posicaoPaginaRemovida = -1;
		for (int i = 0; i < tabelaDePaginas.length; i++) {
			if (tabelaDePaginas[i][moldura] == posicaoLiberada) {
				posicaoPaginaRemovida = i;
			}
		}

		if (tabelaDePaginas[posicaoPaginaRemovida][BIT_MODIFICADA] == 1) {
			salvaConteudoDaPaginaModificada(memoriaPrincipal[posicaoLiberada].getConteudo(),
					memoriaVirtual[posicaoPaginaRemovida].getCaminhoArquivo());

			tabelaDePaginas[posicaoPaginaRemovida][BIT_MODIFICADA] = 0;
		}
		memoriaPrincipal[posicaoLiberada] = null;

		tabelaDePaginas[posicaoPaginaRemovida][BIT_PRESENTE] = 0;
		tabelaDePaginas[posicaoPaginaRemovida][moldura] = -1;

		for (int i = 0; i < QUANTIDADE_BITS_REFERENCIA; i++)
			bitsReferencia[posicaoLiberada][i] = 0;

		System.out.println("Frame " + posicaoLiberada + " liberado!");
		return posicaoLiberada;

	}

	private static void salvaConteudoDaPaginaModificada(Object conteudoPagina, String caminhoArquivoDePagina) {

		try {
			FileOutputStream arquivo = new FileOutputStream(caminhoArquivoDePagina);
			ObjectOutputStream geradorDeArquivo = new ObjectOutputStream(arquivo);

			geradorDeArquivo.writeObject(conteudoPagina);
			geradorDeArquivo.flush();

			geradorDeArquivo.close();
			arquivo.close();

			System.out.println("O conteúdo foi gravado no arquivo " + caminhoArquivoDePagina + " com sucesso");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}