import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ABB<K, V> implements IMapeamento<K, V>{

	private No<K, V> raiz; // referência à raiz da árvore.
	private Comparator<K> comparador; //comparador empregado para definir "menores" e "maiores".
	private int tamanho;
	private long comparacoes;
	private long inicio;
	private long termino;
	
	/**
	 * Método auxiliar para inicialização da árvore binária de busca.
	 * 
	 * Este método define a raiz da árvore como {@code null} e seu tamanho como 0.
	 * Utiliza o comparador fornecido para definir a organização dos elementos na árvore.
	 * @param comparador o comparador para organizar os elementos da árvore.
	 */
	private void init(Comparator<K> comparador) {
		raiz = null;
		tamanho = 0;
		this.comparador = comparador;
	}

	/**
	 * Construtor da classe.
	 * O comparador padrão de ordem natural será utilizado.
	 */ 
	@SuppressWarnings("unchecked")
	public ABB() {
	    init((Comparator<K>) Comparator.naturalOrder());
	}

	/**
	 * Construtor da classe.
	 * Esse construtor cria uma nova árvore binária de busca vazia.
	 *  
	 * @param comparador o comparador a ser utilizado para organizar os elementos da árvore.  
	 */
	public ABB(Comparator<K> comparador) {
	    init(comparador);
	}

    /**
     * Construtor da classe.
     * Esse construtor cria uma nova árvore binária a partir de uma outra árvore binária de busca,
     * com os mesmos itens, mas usando uma nova chave.
     * @param original a árvore binária de busca original.
     * @param funcaoChave a função que irá extrair a nova chave de cada item para a nova árvore.
     */
    public ABB(ABB<?, V> original, Function<V, K> funcaoChave) {
        ABB<K, V> nova = new ABB<>();
        nova = copiarArvore(original.raiz, funcaoChave, nova);
        this.raiz = nova.raiz;
    }
    
    /**
     * Recursivamente, copia os elementos da árvore original para esta, num processo análogo ao caminhamento em ordem.
     * @param <T> Tipo da nova chave.
     * @param raizArvore raiz da árvore original que será copiada.
     * @param funcaoChave função extratora da nova chave para cada item da árvore.
     * @param novaArvore Nova árvore. Parâmetro usado para permitir o retorno da recursividade.
     * @return A nova árvore com os itens copiados e usando a chave indicada pela função extratora.
     */
    private <T> ABB<T, V> copiarArvore(No<?, V> raizArvore, Function<V, T> funcaoChave, ABB<T, V> novaArvore) {
    	
        if (raizArvore != null) {
    		novaArvore = copiarArvore(raizArvore.getEsquerda(), funcaoChave, novaArvore);
            V item = raizArvore.getItem();
            T chave = funcaoChave.apply(item);
    		novaArvore.inserir(chave, item);
    		novaArvore = copiarArvore(raizArvore.getDireita(), funcaoChave, novaArvore);
    	}
        return novaArvore;
    }
    
    /**
	 * Método booleano que indica se a árvore está vazia ou não.
	 * @return
	 * verdadeiro: se a raiz da árvore for null, o que significa que a árvore está vazia.
	 * falso: se a raiz da árvore não for null, o que significa que a árvore não está vazia.
	 */
	public Boolean vazia() {
	    return (this.raiz == null);
	}
    
    @Override
    /**
     * Método que encapsula a pesquisa recursiva de itens na árvore.
     * @param chave a chave do item que será pesquisado na árvore.
     * @return o valor associado à chave.
     */
	public V pesquisar(K chave) {
    	comparacoes = 0;
    	inicio = System.nanoTime();
    	V procurado = pesquisar(raiz, chave);
    	termino = System.nanoTime();
    	return procurado;
	}
    
    private V pesquisar(No<K, V> raizArvore, K procurado) {
    	
    	int comparacao;
    	
    	comparacoes++;
    	if (raizArvore == null)
    		/// Se a raiz da árvore ou sub-árvore for null, a árvore/sub-árvore está vazia e então o item não foi encontrado.
    		throw new NoSuchElementException("O item não foi localizado na árvore!");
    	
    	comparacao = comparador.compare(procurado, raizArvore.getChave());
    	
    	if (comparacao == 0)
    		/// O item procurado foi encontrado.
    		return raizArvore.getItem();
    	else if (comparacao < 0)
    		/// Se o item procurado for menor do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore esquerda.    
    		return pesquisar(raizArvore.getEsquerda(), procurado);
    	else
    		/// Se o item procurado for maior do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore direita.
    		return pesquisar(raizArvore.getDireita(), procurado);
    }
    
    @Override
    /**
     * Método que encapsula a adição recursiva de itens à árvore, associando-o à chave fornecida.
     * @param chave a chave associada ao item que será inserido na árvore.
     * @param item o item que será inserido na árvore.
     * 
     * @return o tamanho atualizado da árvore após a execução da operação de inserção.
     */
    public int inserir(K chave, V item) {
    	raiz = inserir(raiz, chave, item);
        return tamanho;
    }
    
    /**
     * Método recursivo auxiliar para inserção de um novo nó na árvore.
     * @param raizArvore a raiz da árvore ou sub-árvore atual.
     * @param chave a chave associada ao item que será inserido.
     * @param item o item que será inserido.
     * @return a raiz atualizada da árvore ou sub-árvore.
     */
    private No<K, V> inserir(No<K, V> raizArvore, K chave, V item) {
    	
    	int comparacao;
    	
    	if (raizArvore == null) {
    		/// Se a árvore/sub-árvore está vazia, cria um novo nó e incrementa o tamanho.
    		tamanho++;
    		return new No<K, V>(chave, item);
    	}
    	
    	comparacao = comparador.compare(chave, raizArvore.getChave());
    	
    	if (comparacao < 0) {
    		/// Se a chave é menor, insere na sub-árvore esquerda.
    		raizArvore.setEsquerda(inserir(raizArvore.getEsquerda(), chave, item));
    	} else if (comparacao > 0) {
    		/// Se a chave é maior, insere na sub-árvore direita.
    		raizArvore.setDireita(inserir(raizArvore.getDireita(), chave, item));
    	} else {
    		/// Se a chave já existe, atualiza o item.
    		raizArvore.setItem(item);
    	}
    	
    	return raizArvore;
    }

    @Override 
    public String toString(){
    	return percorrer();
    }

    @Override
    public String percorrer() {
    	return caminhamentoEmOrdem();
    }

    public String caminhamentoEmOrdem() {
    	StringBuilder resultado = new StringBuilder();
    	caminhamentoEmOrdem(raiz, resultado);
    	return resultado.toString();
    }
    
    /**
     * Método recursivo auxiliar para percorrer a árvore em ordem (in-order traversal).
     * @param raizArvore a raiz da árvore ou sub-árvore atual.
     * @param resultado o StringBuilder que acumula a representação em string dos itens.
     */
    private void caminhamentoEmOrdem(No<K, V> raizArvore, StringBuilder resultado) {
    	if (raizArvore != null) {
    		/// Percorre a sub-árvore esquerda.
    		caminhamentoEmOrdem(raizArvore.getEsquerda(), resultado);
    		/// Visita o nó atual.
    		resultado.append(raizArvore.getItem().toString()).append("\n");
    		/// Percorre a sub-árvore direita.
    		caminhamentoEmOrdem(raizArvore.getDireita(), resultado);
    	}
    }

    @Override
    /**
     * Método que encapsula a remoção recursiva de um item da árvore.
     * @param chave a chave do item que deverá ser localizado e removido da árvore.
     * @return o valor associado ao item removido.
     */
    public V remover(K chave) {
    	V[] itemRemovido = (V[]) new Object[1];
    	raiz = remover(raiz, chave, itemRemovido);
    	return itemRemovido[0];
    }
    
    /**
     * Método recursivo auxiliar para remover um nó da árvore.
     * @param raizArvore a raiz da árvore ou sub-árvore atual.
     * @param chave a chave do item que será removido.
     * @param itemRemovido array usado para retornar o item removido.
     * @return a raiz atualizada da árvore ou sub-árvore.
     */
    private No<K, V> remover(No<K, V> raizArvore, K chave, V[] itemRemovido) {
    	
    	int comparacao;
    	
    	if (raizArvore == null) {
    		/// Se a árvore/sub-árvore está vazia, o item não foi encontrado.
    		throw new NoSuchElementException("O item não foi localizado na árvore!");
    	}
    	
    	comparacao = comparador.compare(chave, raizArvore.getChave());
    	
    	if (comparacao < 0) {
    		/// Se a chave é menor, busca na sub-árvore esquerda.
    		raizArvore.setEsquerda(remover(raizArvore.getEsquerda(), chave, itemRemovido));
    	} else if (comparacao > 0) {
    		/// Se a chave é maior, busca na sub-árvore direita.
    		raizArvore.setDireita(remover(raizArvore.getDireita(), chave, itemRemovido));
    	} else {
    		/// Nó encontrado! Guarda o item para retornar.
    		itemRemovido[0] = raizArvore.getItem();
    		tamanho--;
    		
    		/// Caso 1: Nó folha (sem filhos).
    		if (raizArvore.getEsquerda() == null && raizArvore.getDireita() == null) {
    			return null;
    		}
    		
    		/// Caso 2: Nó com apenas um filho à direita.
    		if (raizArvore.getEsquerda() == null) {
    			return raizArvore.getDireita();
    		}
    		
    		/// Caso 3: Nó com apenas um filho à esquerda.
    		if (raizArvore.getDireita() == null) {
    			return raizArvore.getEsquerda();
    		}
    		
    		/// Caso 4: Nó com dois filhos.
    		/// Encontra o sucessor in-order (menor nó da sub-árvore direita).
    		No<K, V> sucessor = encontrarMinimo(raizArvore.getDireita());
    		
    		/// Substitui os dados do nó atual pelos dados do sucessor.
    		raizArvore.setChave(sucessor.getChave());
    		raizArvore.setItem(sucessor.getItem());
    		
    		/// Remove o sucessor da sub-árvore direita.
    		raizArvore.setDireita(removerMinimo(raizArvore.getDireita()));
    	}
    	
    	return raizArvore;
    }
    
    /**
     * Método auxiliar para encontrar o nó com a menor chave em uma árvore ou sub-árvore.
     * @param raizArvore a raiz da árvore ou sub-árvore.
     * @return o nó com a menor chave.
     */
    private No<K, V> encontrarMinimo(No<K, V> raizArvore) {
    	if (raizArvore.getEsquerda() == null) {
    		return raizArvore;
    	}
    	return encontrarMinimo(raizArvore.getEsquerda());
    }
    
    /**
     * Método auxiliar para remover o nó com a menor chave em uma árvore ou sub-árvore.
     * @param raizArvore a raiz da árvore ou sub-árvore.
     * @return a raiz atualizada da árvore ou sub-árvore após a remoção.
     */
    private No<K, V> removerMinimo(No<K, V> raizArvore) {
    	if (raizArvore.getEsquerda() == null) {
    		return raizArvore.getDireita();
    	}
    	raizArvore.setEsquerda(removerMinimo(raizArvore.getEsquerda()));
    	return raizArvore;
    }

	@Override
	public int tamanho() {
		return tamanho;
	}
	
	@Override
	public long getComparacoes() {
		return comparacoes;
	}

	@Override
	public double getTempo() {
		return (termino - inicio) / 1_000_000;
	}
}