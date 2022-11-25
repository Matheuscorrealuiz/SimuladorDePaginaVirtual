package controle;

public class Processador {

	public static boolean instrucaoDeAcesso(int posicaoSolicitada) {

		boolean posicao = MMU.mapeamento(posicaoSolicitada);

		return posicao;
	}
}
