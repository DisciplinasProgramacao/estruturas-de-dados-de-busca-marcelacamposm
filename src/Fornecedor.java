public class Fornecedor {

    private static int ultimoID = 10_000;
    
    private int documento;
    private String nome;
    private Lista<Produto> produtos;

    public Fornecedor(String nome) {
        if (nome == null || nome.trim().split("\\s+").length < 2) {
            throw new IllegalArgumentException("Nome deve conter pelo menos duas palavras.");
        }
        this.nome = nome;
        this.documento = ultimoID++;
        this.produtos = new Lista<>();
    }

    public void adicionarProduto(Produto novo) {
        if (novo == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo.");
        }
        produtos.inserirFinal(novo);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fornecedor: ").append(nome)
          .append(" | Documento: ").append(documento)
          .append(" | Produtos: ");
        
        if (produtos.vazia()) {
            sb.append("Nenhum produto cadastrado");
        } else {
            sb.append(produtos.toString());
        }
        
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return documento;
    }
}
